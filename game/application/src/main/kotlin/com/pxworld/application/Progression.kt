package com.pxworld.application

import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.progression.CheckinProgress
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.progression.PlayerProfile
import com.pxworld.domain.progression.QuestProgress
import com.pxworld.domain.progression.WorldPosition

object LineupCapacity {
    const val STARTING: Int = 3
    const val MAXIMUM: Int = 9
    const val LEVELS_PER_EXTRA_SLOT: Int = 5

    fun forProfileLevel(level: Int): Int = minOf(MAXIMUM, STARTING + (level - 1) / LEVELS_PER_EXTRA_SLOT)
}

class QuestTracker(private val catalog: ContentCatalog) {

    fun startAll(state: GameState): GameState {
        val known = state.quests.map { it.questId }.toSet()
        val added = catalog.quests().filter { it.id !in known }.map { QuestProgress(it.id) }
        return if (added.isEmpty()) state else state.copy(quests = state.quests + added)
    }

    fun react(transition: Transition): Transition {
        val quests = catalog.quests().associateBy { it.id }
        var state = startAll(transition.state)
        val extra = mutableListOf<GameEvent>()
        for (event in transition.events) {
            val completedIds = state.quests.filter { it.completed }.map { it.questId }.toSet()
            val updated = state.quests.map { progress ->
                val quest = quests[progress.questId]
                if (quest == null || progress.completed) return@map progress
                if (quest.requires != null && quest.requires !in completedIds) return@map progress
                val gained = contribution(quest, event)
                if (gained == 0) return@map progress
                val value = minOf(quest.count, progress.progress + gained)
                extra += GameEvent.QuestProgressed(quest.id, value, quest.count)
                if (value >= quest.count) extra += GameEvent.QuestCompleted(quest.id)
                progress.copy(progress = value, completed = value >= quest.count)
            }
            state = state.copy(quests = updated)
        }
        return Transition(state, transition.events + extra)
    }

    private fun contribution(quest: QuestSummary, event: GameEvent): Int = when (quest.objectiveKind) {
        "win_encounter" -> if (event is GameEvent.BattleFinished && event.outcome == BattleOutcome.VICTORY && event.encounterId == quest.target) 1 else 0
        "defeat_enemies" -> if (event is GameEvent.BattleFinished) event.enemiesDefeated else 0
        "collect_item" -> if (event is GameEvent.ItemsGained && event.itemId == quest.target) event.quantity.toInt() else 0
        "collect_item_category" -> if (event is GameEvent.ItemsGained && catalog.itemCategory(event.itemId) == quest.target) event.quantity.toInt() else 0
        "reach_map" -> if (event is GameEvent.MapEntered && event.mapId == quest.target) 1 else 0
        "talk_to_npc" -> if (event is GameEvent.NpcTalked && event.npcId == quest.target) 1 else 0
        else -> 0
    }
}

fun GameState.isQuestActive(questId: String, catalog: ContentCatalog): Boolean {
    val quest = catalog.quests().firstOrNull { it.id == questId } ?: return false
    val progress = quests.firstOrNull { it.questId == questId } ?: return false
    val prerequisiteDone = quest.requires == null || quests.any { it.questId == quest.requires && it.completed }
    return prerequisiteDone && !progress.completed
}

class NewGame(private val catalog: ContentCatalog, private val rules: GameRules, private val quests: QuestTracker) {

    fun create(playerName: String, starterHeroId: String): GameState {
        val name = playerName.trim()
        if (name.length !in NAME_LENGTH) throw GameRuleViolation("name must be ${NAME_LENGTH.first}..${NAME_LENGTH.last} characters")
        if (starterHeroId !in catalog.starterHeroes()) throw GameRuleViolation("$starterHeroId is not a starter hero")
        val blank = GameState(
            profile = PlayerProfile(name, starterHeroId = starterHeroId),
            heroes = listOf(OwnedHero("hero-1", starterHeroId)),
            lineup = Lineup(mapOf(GridCell(lane = 1, depth = 0) to "hero-1"), LineupCapacity.STARTING),
            checkin = CheckinProgress(catalog.defaultCheckinTable()),
            position = WorldPosition(catalog.startingMap()),
            nextInstanceNumber = 2,
        )
        return quests.startAll(rules.grant(blank, catalog.startingGrants(), LedgerReason("new_game")).state)
    }

    companion object {
        val NAME_LENGTH: IntRange = 2..16
    }
}
