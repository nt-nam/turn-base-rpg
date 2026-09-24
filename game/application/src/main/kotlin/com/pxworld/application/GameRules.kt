package com.pxworld.application

import com.pxworld.domain.PERMILLE
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.InsufficientFunds
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.progression.EquipmentInstance
import com.pxworld.domain.progression.ExperienceCurve
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.progression.WorldPosition
import com.pxworld.domain.random.Pcg32
import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatFormula

object Counters {
    const val HEROES_RECRUITED = "heroes_recruited"
    const val ENEMIES_DEFEATED = "enemies_defeated"
    const val BATTLES_WON = "battles_won"
    const val GOLD_EARNED = "gold_earned"
    const val GEMS_SPENT = "gems_spent"
    const val EQUIPMENT_OBTAINED = "equipment_obtained"
}

object Currencies {
    const val GOLD = "currency.gold"
    const val GEM = "currency.gem"
}

class GameRules(private val catalog: ContentCatalog) {

    fun heroStats(state: GameState, heroInstanceId: String): StatBlock {
        val hero = state.hero(heroInstanceId)
        val equipmentBonus = state.inventory.equippedOn(heroInstanceId)
            .fold(StatBlock.EMPTY) { total, equip -> total + catalog.equipmentStats(equip.equipmentId) }
        return StatFormula.finalStats(catalog.heroBaseStats(hero.heroId), hero.level, hero.star, equipmentBonus, StatBlock.EMPTY)
    }

    fun equip(state: GameState, heroInstanceId: String, equipmentInstanceId: String): Transition {
        state.hero(heroInstanceId)
        val target = state.inventory.equipmentInstance(equipmentInstanceId)
        val events = mutableListOf<GameEvent>()
        val updated = state.inventory.equipment.map { equip ->
            when {
                equip.instanceId == equipmentInstanceId -> equip.copy(equippedBy = heroInstanceId)
                equip.equippedBy == heroInstanceId && equip.slot == target.slot -> {
                    events += GameEvent.EquipmentUnequipped(heroInstanceId, equip.instanceId)
                    equip.copy(equippedBy = null)
                }
                else -> equip
            }
        }
        target.equippedBy?.takeIf { it != heroInstanceId }?.let { events += GameEvent.EquipmentUnequipped(it, equipmentInstanceId) }
        events += GameEvent.EquipmentEquipped(heroInstanceId, equipmentInstanceId)
        return Transition(state.copy(inventory = state.inventory.copy(equipment = updated)), events)
    }

    fun unequip(state: GameState, equipmentInstanceId: String): Transition {
        val target = state.inventory.equipmentInstance(equipmentInstanceId)
        val owner = target.equippedBy ?: return Transition(state, emptyList())
        val updated = state.inventory.equipment.map { if (it.instanceId == equipmentInstanceId) it.copy(equippedBy = null) else it }
        return Transition(state.copy(inventory = state.inventory.copy(equipment = updated)), listOf(GameEvent.EquipmentUnequipped(owner, equipmentInstanceId)))
    }

    fun useExperienceItem(state: GameState, itemId: String, heroInstanceId: String, quantity: Long = 1): Transition {
        val perItem = catalog.itemExperience(itemId) ?: throw GameRuleViolation("$itemId cannot be used on heroes")
        if (quantity <= 0) throw GameRuleViolation("quantity must be positive")
        if (state.inventory.quantity(itemId) < quantity) throw GameRuleViolation("not enough $itemId")
        val consumed = state.copy(inventory = state.inventory.removeItem(itemId, quantity))
        val (leveled, levelEvents) = grantHeroExperience(consumed, heroInstanceId, perItem * quantity)
        return Transition(leveled, listOf(GameEvent.ItemsConsumed(itemId, quantity)) + levelEvents)
    }

    fun buyItem(state: GameState, itemId: String, quantity: Long): Transition {
        val price = catalog.itemPrice(itemId) ?: throw GameRuleViolation("$itemId is not sold")
        if (quantity <= 0) throw GameRuleViolation("quantity must be positive")
        val (paid, payment) = spend(state, price.currency, price.amount * quantity, LedgerReason("shop_item", itemId))
        return Transition(paid.copy(inventory = paid.inventory.addItem(itemId, quantity)), payment + GameEvent.ItemsGained(itemId, quantity))
    }

    fun buyEquipment(state: GameState, equipmentId: String): Transition {
        val price = catalog.equipmentPrice(equipmentId) ?: throw GameRuleViolation("$equipmentId is not sold")
        val (paid, payment) = spend(state, price.currency, price.amount, LedgerReason("shop_equipment", equipmentId))
        val gained = addEquipment(paid, equipmentId)
        return Transition(gained.state, payment + gained.events)
    }

    fun claimCheckin(state: GameState, todayEpochDay: Long): Transition {
        val progress = state.checkin
        val lastClaim = progress.lastClaimEpochDay
        if (lastClaim != null && lastClaim >= todayEpochDay) throw GameRuleViolation("already claimed today")
        val length = catalog.checkinLength(progress.tableId)
        val day = progress.claimedDays % length + 1
        val rewards = catalog.checkinRewards(progress.tableId, day) ?: throw GameRuleViolation("no rewards for day $day")
        val claimed = state.copy(checkin = progress.copy(claimedDays = progress.claimedDays + 1, lastClaimEpochDay = todayEpochDay))
        val granted = grant(claimed, rewards, LedgerReason("checkin", "${progress.tableId}#$day"))
        return Transition(granted.state, granted.events + GameEvent.CheckinClaimed(progress.tableId, day))
    }

    fun recruit(state: GameState, seed: Long): Transition {
        val pool = catalog.recruitableHeroes()
        if (pool.isEmpty()) throw GameRuleViolation("recruit pool is empty")
        val price = catalog.recruitPrice()
        val (paid, payment) = spend(state, price.currency, price.amount, LedgerReason("recruit"))
        val pick = pool[Pcg32.seeded(seed xor paid.nextInstanceNumber).nextBelow(pool.size).value]
        val (instanceId, allocated) = paid.allocateInstanceId("hero")
        val recruited = allocated.copy(
            heroes = allocated.heroes + OwnedHero(instanceId, pick),
            stats = allocated.stats.increment(Counters.HEROES_RECRUITED),
        )
        return Transition(recruited, payment + GameEvent.HeroRecruited(instanceId, pick))
    }

    fun raiseStar(state: GameState, targetInstanceId: String, fodderInstanceId: String): Transition {
        if (targetInstanceId == fodderInstanceId) throw GameRuleViolation("a hero cannot consume itself")
        val target = state.hero(targetInstanceId)
        val fodder = state.hero(fodderInstanceId)
        if (target.heroId != fodder.heroId) throw GameRuleViolation("star raise needs two copies of the same hero")
        if (target.star != fodder.star) throw GameRuleViolation("star raise needs heroes of equal star")
        if (target.star >= StatFormula.MAX_STAR) throw GameRuleViolation("hero is already at max star")
        if (state.lineup.contains(fodderInstanceId)) throw GameRuleViolation("remove the consumed hero from the lineup first")
        if (fodder.locked) throw GameRuleViolation("consumed hero is locked")
        val released = state.inventory.equipment.map { if (it.equippedBy == fodderInstanceId) it.copy(equippedBy = null) else it }
        val raised = state.copy(
            heroes = state.heroes.filter { it.instanceId != fodderInstanceId }.map { if (it.instanceId == targetInstanceId) it.copy(star = it.star + 1) else it },
            inventory = state.inventory.copy(equipment = released),
        )
        return Transition(raised, listOf(GameEvent.HeroStarRaised(targetInstanceId, target.star + 1, fodderInstanceId)))
    }

    fun placeInLineup(state: GameState, cell: GridCell, heroInstanceId: String?): Transition {
        val withoutCell = state.lineup.cells - cell
        val cells = if (heroInstanceId == null) {
            withoutCell
        } else {
            state.hero(heroInstanceId)
            withoutCell.filterValues { it != heroInstanceId } + (cell to heroInstanceId)
        }
        if (cells.size > state.lineup.capacity) throw GameRuleViolation("lineup is full (${state.lineup.capacity})")
        if (cells.isEmpty()) throw GameRuleViolation("lineup needs at least one hero")
        val lineup = Lineup(cells, state.lineup.capacity)
        return Transition(state.copy(lineup = lineup), listOf(GameEvent.LineupChanged(lineup.cells.values.toList())))
    }

    fun finishBattle(state: GameState, encounterId: String, outcome: BattleOutcome, enemiesDefeated: Int): Transition {
        val encounter = catalog.encounter(encounterId)
        val events = mutableListOf<GameEvent>(GameEvent.BattleFinished(encounterId, outcome, enemiesDefeated))
        var current = state.copy(stats = state.stats.increment(Counters.ENEMIES_DEFEATED, enemiesDefeated.toLong()))
        if (outcome == BattleOutcome.VICTORY) {
            current = current.copy(stats = current.stats.increment(Counters.BATTLES_WON))
            val granted = grant(current, encounter.rewards, LedgerReason("battle", encounterId))
            current = granted.state
            events += granted.events
        }
        val experience = encounter.highestEnemyLevel * 100L * (if (outcome == BattleOutcome.VICTORY) VICTORY_EXPERIENCE_PERMILLE else DEFEAT_EXPERIENCE_PERMILLE) / PERMILLE
        for (heroInstanceId in current.lineup.cells.values.distinct()) {
            val (leveled, levelEvents) = grantHeroExperience(current, heroInstanceId, experience)
            current = leveled
            events += levelEvents
        }
        val (profileLevel, profileExperience) = ExperienceCurve.addExperience(current.profile.level, current.profile.experience, experience)
        if (profileLevel > current.profile.level) events += GameEvent.ProfileLeveledUp(profileLevel)
        current = current.copy(
            profile = current.profile.copy(level = profileLevel, experience = profileExperience),
            lineup = current.lineup.copy(capacity = maxOf(current.lineup.capacity, LineupCapacity.forProfileLevel(profileLevel))),
        )
        return Transition(current, events)
    }

    fun enterMap(state: GameState, mapId: String, x: Int, y: Int, spawnIndex: Int): Transition {
        val moved = state.copy(position = WorldPosition(mapId, x, y, spawnIndex))
        val events = if (mapId != state.position.mapId) listOf<GameEvent>(GameEvent.MapEntered(mapId)) else emptyList()
        return Transition(moved, events)
    }

    fun claimQuest(state: GameState, questId: String): Transition {
        val progress = state.quests.firstOrNull { it.questId == questId } ?: throw GameRuleViolation("quest $questId not started")
        if (!progress.completed) throw GameRuleViolation("quest $questId is not complete")
        if (progress.claimed) throw GameRuleViolation("quest $questId already claimed")
        val quest = catalog.quests().first { it.id == questId }
        val marked = state.copy(quests = state.quests.map { if (it.questId == questId) it.copy(claimed = true) else it })
        val granted = grant(marked, quest.rewards, LedgerReason("quest", questId))
        return Transition(granted.state, granted.events + GameEvent.QuestRewardClaimed(questId))
    }

    fun claimAchievementTier(state: GameState, achievementId: String): Transition {
        val achievement = catalog.achievements().firstOrNull { it.id == achievementId } ?: throw GameRuleViolation("unknown achievement $achievementId")
        val claimed = state.claimedAchievementTiers[achievementId] ?: 0
        val tier = achievement.tiers.getOrNull(claimed) ?: throw GameRuleViolation("all tiers claimed")
        if (state.stats.value(achievement.counter) < tier.target) throw GameRuleViolation("tier ${claimed + 1} not reached")
        val marked = state.copy(claimedAchievementTiers = state.claimedAchievementTiers + (achievementId to claimed + 1))
        val granted = grant(marked, tier.rewards, LedgerReason("achievement", "$achievementId#${claimed + 1}"))
        return Transition(granted.state, granted.events + GameEvent.AchievementTierClaimed(achievementId, claimed + 1))
    }

    fun grant(state: GameState, grants: List<Grant>, reason: LedgerReason): Transition {
        var current = state
        val events = mutableListOf<GameEvent>()
        for (item in grants) {
            when (item.kind) {
                GrantKind.CURRENCY -> {
                    val change = current.wallet.credit(item.id, item.quantity, reason)
                    val stats = if (item.id == Currencies.GOLD) current.stats.increment(Counters.GOLD_EARNED, item.quantity) else current.stats
                    current = current.copy(wallet = change.wallet, ledgerTail = (current.ledgerTail + change.entry).takeLast(GameState.LEDGER_TAIL_SIZE), stats = stats)
                    events += GameEvent.CurrencyChanged(change.entry)
                }
                GrantKind.ITEM -> {
                    current = current.copy(inventory = current.inventory.addItem(item.id, item.quantity))
                    events += GameEvent.ItemsGained(item.id, item.quantity)
                }
                GrantKind.EQUIPMENT -> repeat(item.quantity.toInt()) {
                    val gained = addEquipment(current, item.id)
                    current = gained.state
                    events += gained.events
                }
                GrantKind.HERO -> repeat(item.quantity.toInt()) {
                    if (!catalog.heroExists(item.id)) throw GameRuleViolation("unknown hero ${item.id}")
                    val (instanceId, allocated) = current.allocateInstanceId("hero")
                    current = allocated.copy(heroes = allocated.heroes + OwnedHero(instanceId, item.id), stats = allocated.stats.increment(Counters.HEROES_RECRUITED))
                    events += GameEvent.HeroRecruited(instanceId, item.id)
                }
            }
        }
        return Transition(current, events)
    }

    private fun spend(state: GameState, currency: String, amount: Long, reason: LedgerReason): Pair<GameState, List<GameEvent>> {
        val change = try {
            state.wallet.debit(currency, amount, reason)
        } catch (shortfall: InsufficientFunds) {
            throw GameRuleViolation(shortfall.message ?: "insufficient funds")
        }
        val stats = if (currency == Currencies.GEM) state.stats.increment(Counters.GEMS_SPENT, amount) else state.stats
        val paid = state.copy(wallet = change.wallet, ledgerTail = (state.ledgerTail + change.entry).takeLast(GameState.LEDGER_TAIL_SIZE), stats = stats)
        return paid to listOf(GameEvent.CurrencyChanged(change.entry))
    }

    private fun addEquipment(state: GameState, equipmentId: String): Transition {
        val (instanceId, allocated) = state.allocateInstanceId("equip")
        val instance = EquipmentInstance(instanceId, equipmentId, catalog.equipmentSlot(equipmentId))
        val gained = allocated.copy(
            inventory = allocated.inventory.copy(equipment = allocated.inventory.equipment + instance),
            stats = allocated.stats.increment(Counters.EQUIPMENT_OBTAINED),
        )
        return Transition(gained, listOf(GameEvent.EquipmentGained(instanceId, equipmentId)))
    }

    private fun grantHeroExperience(state: GameState, heroInstanceId: String, amount: Long): Pair<GameState, List<GameEvent>> {
        val hero = state.hero(heroInstanceId)
        val (level, experience) = ExperienceCurve.addExperience(hero.level, hero.experience, amount)
        val updated = state.copy(heroes = state.heroes.map { if (it.instanceId == heroInstanceId) it.copy(level = level, experience = experience) else it })
        val events = if (level > hero.level) listOf<GameEvent>(GameEvent.HeroLeveledUp(heroInstanceId, level)) else emptyList()
        return updated to events
    }

    companion object {
        const val VICTORY_EXPERIENCE_PERMILLE: Long = 350
        const val DEFEAT_EXPERIENCE_PERMILLE: Long = 150
    }
}
