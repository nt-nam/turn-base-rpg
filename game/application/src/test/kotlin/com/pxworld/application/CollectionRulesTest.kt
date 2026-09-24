package com.pxworld.application

import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.progression.CheckinProgress
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.progression.PlayerProfile
import com.pxworld.domain.progression.WorldPosition
import com.pxworld.domain.stats.StatKind
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CollectionRulesTest {

    private val catalog = object : ContentCatalog by FakeCatalogDelegate.catalog {}
    private val rules = GameRules(catalog)
    private val collection = CollectionRules(catalog, rules)

    private fun game(): GameState = rules.grant(
        GameState(
            profile = PlayerProfile("tester", starterHeroId = "hero.aldric"),
            heroes = listOf(OwnedHero("hero-1", "hero.aldric"), OwnedHero("hero-2", "hero.selene")),
            lineup = Lineup(mapOf(GridCell(1, 0) to "hero-1"), capacity = 3),
            checkin = CheckinProgress("checkin.standard_30"),
            position = WorldPosition("map.dawnvillage_01"),
            nextInstanceNumber = 3,
        ),
        listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 1000), Grant(GrantKind.CURRENCY, Currencies.GEM, 200), Grant(GrantKind.ITEM, "item.food_t1", 4)),
        LedgerReason("test"),
    ).state

    @Test
    fun `selling returns a quarter of the shop price`() {
        val sold = collection.sellItem(game(), "item.food_t1", 2).state
        assertEquals(1050, sold.wallet.balance(Currencies.GOLD))
        assertEquals(2, sold.inventory.quantity("item.food_t1"))
        assertThrows<GameRuleViolation> { collection.sellItem(sold, "item.food_t1", 5) }
    }

    @Test
    fun `upgrading equipment costs gold and scales its stats`() {
        var state = rules.buyEquipment(game(), "equip.sword_000").state
        val sword = state.inventory.equipment.single().instanceId
        state = rules.equip(state, "hero-1", sword).state
        val before = collection.equipmentBonus(state, "hero-1")[StatKind.ATTACK]
        state = collection.upgradeEquipment(state, sword).state
        assertEquals(900, state.wallet.balance(Currencies.GOLD))
        assertEquals(before + before / 10, collection.equipmentBonus(state, "hero-1")[StatKind.ATTACK])
        assertEquals(rules.heroStats(state, "hero-1")[StatKind.ATTACK] - rules.heroStats(rules.unequip(state, sword).state, "hero-1")[StatKind.ATTACK], 110)
    }

    @Test
    fun `worn equipment cannot be salvaged`() {
        var state = rules.buyEquipment(game(), "equip.sword_000").state
        val sword = state.inventory.equipment.single().instanceId
        state = rules.equip(state, "hero-1", sword).state
        assertThrows<GameRuleViolation> { collection.salvageEquipment(state, sword) }
        state = collection.salvageEquipment(rules.unequip(state, sword).state, sword).state
        assertTrue(state.inventory.equipment.isEmpty())
        assertEquals(1250, state.wallet.balance(Currencies.GOLD))
    }

    @Test
    fun `dismissing protects the lineup, locked heroes and the last hero`() {
        val state = game()
        assertThrows<GameRuleViolation> { collection.dismissHero(state, "hero-1") }
        val locked = collection.toggleLock(state, "hero-2").state
        assertThrows<GameRuleViolation> { collection.dismissHero(locked, "hero-2") }
        val dismissed = collection.dismissHero(state, "hero-2").state
        assertEquals(1, dismissed.heroes.size)
        assertEquals(1100, dismissed.wallet.balance(Currencies.GOLD))
    }

    @Test
    fun `gems exchange into gold and count as spent`() {
        val exchanged = collection.exchangeGems(game(), 3).state
        assertEquals(197, exchanged.wallet.balance(Currencies.GEM))
        assertEquals(1300, exchanged.wallet.balance(Currencies.GOLD))
        assertEquals(3, exchanged.stats.value(Counters.GEMS_SPENT))
    }

    @Test
    fun `idle rewards accumulate per hour up to a cap`() {
        val started = collection.claimIdleRewards(game(), nowMillis = 0).state
        assertEquals(0, collection.idleRewardPreview(started, 30 * 60_000))
        assertEquals(3 * 60, collection.idleRewardPreview(started, 3 * EconomyTuning.MILLIS_PER_HOUR))
        assertEquals(12 * 60, collection.idleRewardPreview(started, 48 * EconomyTuning.MILLIS_PER_HOUR))
        val claimed = collection.claimIdleRewards(started, 2 * EconomyTuning.MILLIS_PER_HOUR).state
        assertEquals(1120, claimed.wallet.balance(Currencies.GOLD))
    }

    @Test
    fun `lineup presets save and restore owned heroes`() {
        var state = rules.placeInLineup(game(), GridCell(0, 2), "hero-2").state
        state = collection.savePreset(state, "raid").state
        state = rules.placeInLineup(state, GridCell(0, 2), null).state
        state = collection.applyPreset(state, "raid").state
        assertEquals(setOf("hero-1", "hero-2"), state.lineup.cells.values.toSet())
    }

    @Test
    fun `journal records visited maps, seen items, recruits and enemies`() {
        val battle = rules.finishBattle(game(), "encounter.test", com.pxworld.domain.battle.BattleOutcome.VICTORY, 1)
        val recorded = collection.record(collection.record(battle, 5).let { rules.enterMap(it.state, "map.ashwaste_01", 1, 1, 0) }, 6).state
        assertTrue("map.ashwaste_01" in recorded.journal.visitedMaps)
        assertTrue("map.dawnvillage_01" in recorded.journal.visitedMaps)
        val recruited = collection.record(rules.recruit(recorded, 3), 7).state
        assertNotNull(recruited.journal.recruitHistory.singleOrNull())
        assertThrows<GameRuleViolation> { collection.fastTravel(recruited, "map.mistgarden_01") }
    }
}

object FakeCatalogDelegate {
    val catalog: ContentCatalog = FakeCatalogHolder.value
}
