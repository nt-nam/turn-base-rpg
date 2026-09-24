package com.pxworld.application

import com.pxworld.domain.PERMILLE
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.PlayerJournal
import com.pxworld.domain.progression.RecruitRecord
import com.pxworld.domain.stats.StatBlock

object EconomyTuning {
    const val SELL_DIVISOR: Long = 4
    const val SALVAGE_GOLD_PER_PRICE_UNIT: Long = 5
    const val UPGRADE_GOLD_PER_LEVEL: Long = 100
    const val MAX_EQUIPMENT_LEVEL: Int = 10
    const val EQUIPMENT_GROWTH_PERMILLE: Int = 100
    const val DISMISS_GOLD_PER_STAR: Long = 150
    const val DISMISS_GOLD_BASE: Long = 100
    const val GOLD_PER_GEM: Long = 100
    const val IDLE_CAP_HOURS: Long = 12
    const val IDLE_GOLD_BASE_PER_HOUR: Long = 50
    const val IDLE_GOLD_PER_LEVEL_PER_HOUR: Long = 10
    const val MILLIS_PER_HOUR: Long = 3_600_000
}

class CollectionRules(private val catalog: ContentCatalog, private val rules: GameRules) {

    fun equipmentBonus(state: GameState, heroInstanceId: String): StatBlock =
        state.inventory.equippedOn(heroInstanceId).fold(StatBlock.EMPTY) { total, equip ->
            val scale = PERMILLE + EconomyTuning.EQUIPMENT_GROWTH_PERMILLE * (equip.level - 1)
            total + catalog.equipmentStats(equip.equipmentId).map { _, value -> (value.toLong() * scale / PERMILLE).toInt() }
        }

    fun sellValue(itemId: String): Price? =
        catalog.itemPrice(itemId)?.let { Price(it.currency, maxOf(1L, it.amount / EconomyTuning.SELL_DIVISOR)) }

    fun sellItem(state: GameState, itemId: String, quantity: Long): Transition {
        val value = sellValue(itemId) ?: throw GameRuleViolation("$itemId cannot be sold")
        if (quantity <= 0 || state.inventory.quantity(itemId) < quantity) throw GameRuleViolation("not enough $itemId to sell")
        val removed = state.copy(inventory = state.inventory.removeItem(itemId, quantity))
        val paid = rules.grant(removed, listOf(Grant(GrantKind.CURRENCY, value.currency, value.amount * quantity)), LedgerReason("sell_item", itemId))
        return Transition(paid.state, listOf(GameEvent.ItemsConsumed(itemId, quantity)) + paid.events)
    }

    fun salvageValue(equipmentId: String): Price {
        val price = catalog.equipmentPrice(equipmentId)?.amount ?: 10L
        return Price(Currencies.GOLD, price * EconomyTuning.SALVAGE_GOLD_PER_PRICE_UNIT)
    }

    fun salvageEquipment(state: GameState, equipmentInstanceId: String): Transition {
        val equip = state.inventory.equipmentInstance(equipmentInstanceId)
        if (equip.equippedBy != null) throw GameRuleViolation("unequip before salvaging")
        val removed = state.copy(inventory = state.inventory.copy(equipment = state.inventory.equipment.filter { it.instanceId != equipmentInstanceId }))
        val value = salvageValue(equip.equipmentId)
        return rules.grant(removed, listOf(Grant(GrantKind.CURRENCY, value.currency, value.amount * equip.level)), LedgerReason("salvage", equip.equipmentId))
    }

    fun upgradeCost(level: Int): Price = Price(Currencies.GOLD, EconomyTuning.UPGRADE_GOLD_PER_LEVEL * level)

    fun upgradeEquipment(state: GameState, equipmentInstanceId: String): Transition {
        val equip = state.inventory.equipmentInstance(equipmentInstanceId)
        if (equip.level >= EconomyTuning.MAX_EQUIPMENT_LEVEL) throw GameRuleViolation("equipment is at max level")
        val cost = upgradeCost(equip.level)
        val change = try {
            state.wallet.debit(cost.currency, cost.amount, LedgerReason("upgrade_equipment", equip.equipmentId))
        } catch (shortfall: com.pxworld.domain.economy.InsufficientFunds) {
            throw GameRuleViolation(shortfall.message ?: "insufficient funds")
        }
        val upgraded = state.copy(
            wallet = change.wallet,
            ledgerTail = (state.ledgerTail + change.entry).takeLast(GameState.LEDGER_TAIL_SIZE),
            inventory = state.inventory.copy(equipment = state.inventory.equipment.map { if (it.instanceId == equipmentInstanceId) it.copy(level = it.level + 1) else it }),
        )
        return Transition(upgraded, listOf(GameEvent.CurrencyChanged(change.entry)))
    }

    fun dismissValue(star: Int): Price = Price(Currencies.GOLD, EconomyTuning.DISMISS_GOLD_BASE + EconomyTuning.DISMISS_GOLD_PER_STAR * star)

    fun dismissHero(state: GameState, heroInstanceId: String): Transition {
        val hero = state.hero(heroInstanceId)
        if (state.heroes.size <= 1) throw GameRuleViolation("you cannot dismiss your last hero")
        if (state.lineup.contains(heroInstanceId)) throw GameRuleViolation("remove the hero from the lineup first")
        if (hero.locked) throw GameRuleViolation("hero is locked")
        val released = state.inventory.equipment.map { if (it.equippedBy == heroInstanceId) it.copy(equippedBy = null) else it }
        val removed = state.copy(
            heroes = state.heroes.filter { it.instanceId != heroInstanceId },
            inventory = state.inventory.copy(equipment = released),
            journal = state.journal.copy(lineupPresets = state.journal.lineupPresets.mapValues { (_, cells) -> cells.filterValues { it != heroInstanceId } }),
        )
        val value = dismissValue(hero.star)
        return rules.grant(removed, listOf(Grant(GrantKind.CURRENCY, value.currency, value.amount)), LedgerReason("dismiss", hero.heroId))
    }

    fun toggleLock(state: GameState, heroInstanceId: String): Transition {
        state.hero(heroInstanceId)
        return Transition(state.copy(heroes = state.heroes.map { if (it.instanceId == heroInstanceId) it.copy(locked = !it.locked) else it }), emptyList())
    }

    fun exchangeGems(state: GameState, gems: Long): Transition {
        if (gems <= 0) throw GameRuleViolation("exchange amount must be positive")
        val change = try {
            state.wallet.debit(Currencies.GEM, gems, LedgerReason("exchange"))
        } catch (shortfall: com.pxworld.domain.economy.InsufficientFunds) {
            throw GameRuleViolation(shortfall.message ?: "insufficient funds")
        }
        val paid = state.copy(
            wallet = change.wallet,
            ledgerTail = (state.ledgerTail + change.entry).takeLast(GameState.LEDGER_TAIL_SIZE),
            stats = state.stats.increment(Counters.GEMS_SPENT, gems),
        )
        val gained = rules.grant(paid, listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, gems * EconomyTuning.GOLD_PER_GEM)), LedgerReason("exchange"))
        return Transition(gained.state, listOf(GameEvent.CurrencyChanged(change.entry)) + gained.events)
    }

    fun idleRewardPreview(state: GameState, nowMillis: Long): Long {
        val since = state.journal.lastIdleClaimMillis ?: return 0
        val hours = ((nowMillis - since) / EconomyTuning.MILLIS_PER_HOUR).coerceIn(0, EconomyTuning.IDLE_CAP_HOURS)
        return hours * (EconomyTuning.IDLE_GOLD_BASE_PER_HOUR + EconomyTuning.IDLE_GOLD_PER_LEVEL_PER_HOUR * state.profile.level)
    }

    fun claimIdleRewards(state: GameState, nowMillis: Long): Transition {
        val gold = idleRewardPreview(state, nowMillis)
        val stamped = state.copy(journal = state.journal.copy(lastIdleClaimMillis = nowMillis))
        if (gold == 0L) {
            if (state.journal.lastIdleClaimMillis != null) throw GameRuleViolation("nothing accumulated yet")
            return Transition(stamped, emptyList())
        }
        return rules.grant(stamped, listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, gold)), LedgerReason("idle"))
    }

    fun savePreset(state: GameState, name: String): Transition {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) throw GameRuleViolation("preset needs a name")
        if (trimmed !in state.journal.lineupPresets && state.journal.lineupPresets.size >= PlayerJournal.MAX_PRESETS) throw GameRuleViolation("at most ${PlayerJournal.MAX_PRESETS} presets")
        return Transition(state.copy(journal = state.journal.copy(lineupPresets = state.journal.lineupPresets + (trimmed to state.lineup.cells))), emptyList())
    }

    fun applyPreset(state: GameState, name: String): Transition {
        val cells = state.journal.lineupPresets[name] ?: throw GameRuleViolation("no preset $name")
        val owned = state.heroes.map { it.instanceId }.toSet()
        val usable = cells.filterValues { it in owned }.entries.take(state.lineup.capacity).associate { it.key to it.value }
        if (usable.isEmpty()) throw GameRuleViolation("preset has no available heroes")
        val lineup = Lineup(usable, state.lineup.capacity)
        return Transition(state.copy(lineup = lineup), listOf(GameEvent.LineupChanged(lineup.cells.values.toList())))
    }

    fun deletePreset(state: GameState, name: String): Transition =
        Transition(state.copy(journal = state.journal.copy(lineupPresets = state.journal.lineupPresets - name)), emptyList())

    fun record(transition: Transition, nowMillis: Long): Transition {
        var journal = transition.state.journal
        for (event in transition.events) {
            journal = when (event) {
                is GameEvent.MapEntered -> journal.copy(visitedMaps = journal.visitedMaps + event.mapId)
                is GameEvent.ItemsGained -> journal.copy(seenItems = journal.seenItems + event.itemId)
                is GameEvent.HeroRecruited -> journal.copy(recruitHistory = (journal.recruitHistory + RecruitRecord(event.heroId, nowMillis)).takeLast(PlayerJournal.RECRUIT_HISTORY_SIZE))
                is GameEvent.BattleFinished -> journal.copy(seenEnemies = journal.seenEnemies + catalog.encounter(event.encounterId).enemyIds)
                else -> journal
            }
        }
        val position = transition.state.position.mapId
        if (position !in journal.visitedMaps) journal = journal.copy(visitedMaps = journal.visitedMaps + position)
        return if (journal == transition.state.journal) transition else Transition(transition.state.copy(journal = journal), transition.events)
    }

    fun fastTravel(state: GameState, mapId: String): Transition {
        if (mapId !in state.journal.visitedMaps) throw GameRuleViolation("you have not visited $mapId yet")
        return rules.enterMap(state, mapId, -1, -1, 0)
    }

    fun markTutorial(state: GameState, tutorialId: String): Transition {
        if (tutorialId in state.journal.tutorialsSeen) return Transition(state, emptyList())
        val marked = state.copy(journal = state.journal.copy(tutorialsSeen = state.journal.tutorialsSeen + tutorialId))
        return if (tutorialId == TUTORIAL_COMPLETE) rules.grant(marked, TUTORIAL_REWARD, LedgerReason("tutorial")) else Transition(marked, emptyList())
    }

    fun addPlayTime(state: GameState, seconds: Long): GameState =
        state.copy(journal = state.journal.copy(playSeconds = state.journal.playSeconds + seconds))

    companion object {
        const val TUTORIAL_COMPLETE: String = "tutorial_reward"
        val TUTORIAL_REWARD: List<Grant> = listOf(Grant(GrantKind.CURRENCY, Currencies.GEM, 10), Grant(GrantKind.ITEM, "item.food_t1", 3))
    }
}
