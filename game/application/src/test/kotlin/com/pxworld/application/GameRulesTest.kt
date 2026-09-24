package com.pxworld.application

import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.progression.CheckinProgress
import com.pxworld.domain.progression.EquipmentInstance
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Inventory
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.progression.PlayerProfile
import com.pxworld.domain.progression.WorldPosition
import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatKind
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private object FakeCatalog : ContentCatalog {
    override fun heroExists(heroId: String) = heroId in setOf("hero.aldric", "hero.selene")
    override fun heroBaseStats(heroId: String) = StatBlock.of(StatKind.HP to 1000, StatKind.ATTACK to 150, StatKind.SPEED to 104)
    override fun recruitableHeroes() = listOf("hero.aldric", "hero.selene")
    override fun recruitPrice() = Price(Currencies.GEM, 5)
    override fun itemExperience(itemId: String) = if (itemId == "item.food_t1") 100L else null
    override fun itemPrice(itemId: String) = if (itemId == "item.food_t1") Price(Currencies.GOLD, 100) else null
    override fun equipmentSlot(equipmentId: String) = if (equipmentId.startsWith("equip.sword")) EquipmentSlot.WEAPON else EquipmentSlot.ARMOR
    override fun equipmentStats(equipmentId: String) = StatBlock.of(StatKind.ATTACK to 100)
    override fun equipmentPrice(equipmentId: String) = Price(Currencies.GEM, 50)
    override fun encounter(encounterId: String) = EncounterSummary(encounterId, highestEnemyLevel = 2, enemyCount = 2, rewards = listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 30)))
    override fun checkinRewards(tableId: String, day: Int) = listOf(Grant(GrantKind.CURRENCY, if (day % 2 == 0) Currencies.GEM else Currencies.GOLD, 100L * day))
    override fun checkinLength(tableId: String) = 3
    override fun itemCategory(itemId: String) = itemId.substringAfter("item.").substringBefore("_")
    override fun quests() = listOf(
        QuestSummary("quest.side.hunt", "defeat_enemies", null, 3, listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 50))),
        QuestSummary("quest.side.food", "collect_item_category", "food", 2, listOf(Grant(GrantKind.CURRENCY, Currencies.GEM, 1))),
        QuestSummary("quest.side.travel", "reach_map", "map.ashwaste_01", 1, listOf(Grant(GrantKind.ITEM, "item.food_t1", 1))),
    )
    override fun achievements() = listOf(
        AchievementSummary(
            "achievement.victor", Counters.BATTLES_WON,
            listOf(AchievementTier(1, listOf(Grant(GrantKind.CURRENCY, Currencies.GEM, 10))), AchievementTier(3, listOf(Grant(GrantKind.CURRENCY, Currencies.GEM, 40)))),
        ),
    )
    override fun startingGrants() = listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 300))
    override fun starterHeroes() = listOf("hero.aldric", "hero.selene")
    override fun startingMap() = "map.dawnvillage_01"
    override fun defaultCheckinTable() = "checkin.standard_30"
}

class GameRulesTest {

    private val rules = GameRules(FakeCatalog)

    private fun newGame(): GameState = GameState(
        profile = PlayerProfile("tester", starterHeroId = "hero.aldric"),
        heroes = listOf(OwnedHero("hero-1", "hero.aldric")),
        lineup = Lineup(mapOf(GridCell(1, 0) to "hero-1"), capacity = 2),
        checkin = CheckinProgress("checkin.standard_30"),
        position = WorldPosition("map.dawnvillage_01"),
        nextInstanceNumber = 2,
    )

    private fun rich(state: GameState = newGame()): GameState =
        rules.grant(state, listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 1000), Grant(GrantKind.CURRENCY, Currencies.GEM, 100)), LedgerReason("test")).state

    @Test
    fun `equipment bonuses reach hero stats`() {
        val bought = rules.buyEquipment(rich(), "equip.sword_000").state
        val sword = bought.inventory.equipment.single()
        val before = rules.heroStats(bought, "hero-1")[StatKind.ATTACK]
        val equipped = rules.equip(bought, "hero-1", sword.instanceId).state
        assertEquals(before + 100, rules.heroStats(equipped, "hero-1")[StatKind.ATTACK])
    }

    @Test
    fun `equipping a second weapon swaps out the first`() {
        var state = rules.buyEquipment(rich(), "equip.sword_000").state
        state = rules.buyEquipment(state, "equip.sword_001").state
        val (first, second) = state.inventory.equipment
        state = rules.equip(state, "hero-1", first.instanceId).state
        val swap = rules.equip(state, "hero-1", second.instanceId)
        assertEquals(listOf(second.instanceId), swap.state.inventory.equippedOn("hero-1").map { it.instanceId })
        assertTrue(swap.events.contains(GameEvent.EquipmentUnequipped("hero-1", first.instanceId)))
    }

    @Test
    fun `shop purchase debits wallet, writes ledger and adds items`() {
        val result = rules.buyItem(rich(), "item.food_t1", 3)
        assertEquals(700, result.state.wallet.balance(Currencies.GOLD))
        assertEquals(3, result.state.inventory.quantity("item.food_t1"))
        assertEquals(LedgerReason("shop_item", "item.food_t1"), result.state.ledgerTail.last().reason)
    }

    @Test
    fun `cannot buy without enough currency`() {
        assertThrows<GameRuleViolation> { rules.buyItem(newGame(), "item.food_t1", 1) }
    }

    @Test
    fun `check-in actually grants its reward once per day and cycles`() {
        var state = newGame()
        state = rules.claimCheckin(state, todayEpochDay = 100).state
        assertEquals(100, state.wallet.balance(Currencies.GOLD))
        assertThrows<GameRuleViolation> { rules.claimCheckin(state, todayEpochDay = 100) }
        state = rules.claimCheckin(state, todayEpochDay = 101).state
        state = rules.claimCheckin(state, todayEpochDay = 102).state
        val fourth = rules.claimCheckin(state, todayEpochDay = 103)
        assertTrue(fourth.events.contains(GameEvent.CheckinClaimed("checkin.standard_30", 1)))
    }

    @Test
    fun `experience item levels a hero and is consumed`() {
        val stocked = rules.grant(newGame(), listOf(Grant(GrantKind.ITEM, "item.food_t1", 2)), LedgerReason("test")).state
        val used = rules.useExperienceItem(stocked, "item.food_t1", "hero-1", 2)
        assertEquals(2, used.state.hero("hero-1").level)
        assertEquals(100, used.state.hero("hero-1").experience)
        assertEquals(0, used.state.inventory.quantity("item.food_t1"))
    }

    @Test
    fun `recruit costs gems and adds a hero deterministically`() {
        val first = rules.recruit(rich(), seed = 9)
        val second = rules.recruit(rich(), seed = 9)
        assertEquals(first.state, second.state)
        assertEquals(95, first.state.wallet.balance(Currencies.GEM))
        assertEquals(2, first.state.heroes.size)
        assertEquals(1, first.state.stats.value(Counters.HEROES_RECRUITED))
        assertEquals(5, first.state.stats.value(Counters.GEMS_SPENT))
    }

    @Test
    fun `star raise refuses to consume a hero standing in the lineup`() {
        var state = rules.grant(newGame(), listOf(Grant(GrantKind.HERO, "hero.aldric", 1)), LedgerReason("test")).state
        val copy = state.heroes.last().instanceId
        state = rules.placeInLineup(state, GridCell(0, 0), copy).state
        assertThrows<GameRuleViolation> { rules.raiseStar(state, "hero-1", copy) }
        state = rules.placeInLineup(state, GridCell(0, 0), null).state
        val raised = rules.raiseStar(state, "hero-1", copy).state
        assertEquals(1, raised.hero("hero-1").star)
        assertEquals(1, raised.heroes.size)
    }

    @Test
    fun `lineup respects capacity and keeps each hero in one cell`() {
        var state = rules.grant(newGame(), listOf(Grant(GrantKind.HERO, "hero.selene", 2)), LedgerReason("test")).state
        val (second, third) = state.heroes.drop(1).map { it.instanceId }
        state = rules.placeInLineup(state, GridCell(0, 2), second).state
        assertThrows<GameRuleViolation> { rules.placeInLineup(state, GridCell(2, 2), third) }
        state = rules.placeInLineup(state, GridCell(2, 2), second).state
        assertEquals(setOf(GridCell(1, 0), GridCell(2, 2)), state.lineup.cells.keys)
    }

    @Test
    fun `victory grants rewards, experience and counters`() {
        val result = rules.finishBattle(newGame(), "encounter.test", BattleOutcome.VICTORY, enemiesDefeated = 2)
        assertEquals(30, result.state.wallet.balance(Currencies.GOLD))
        assertEquals(70, result.state.hero("hero-1").experience)
        assertEquals(1, result.state.stats.value(Counters.BATTLES_WON))
        assertEquals(2, result.state.stats.value(Counters.ENEMIES_DEFEATED))
        assertEquals(30, result.state.stats.value(Counters.GOLD_EARNED))
    }

    @Test
    fun `defeat gives reduced experience and no rewards`() {
        val result = rules.finishBattle(newGame(), "encounter.test", BattleOutcome.DEFEAT, enemiesDefeated = 1)
        assertEquals(0, result.state.wallet.balance(Currencies.GOLD))
        assertEquals(30, result.state.hero("hero-1").experience)
        assertEquals(0, result.state.stats.value(Counters.BATTLES_WON))
    }

    @Test
    fun `state invariants reject impossible saves`() {
        assertThrows<IllegalArgumentException> {
            newGame().copy(lineup = Lineup(mapOf(GridCell(0, 0) to "ghost"), capacity = 1))
        }
        assertThrows<IllegalArgumentException> {
            newGame().copy(inventory = Inventory(equipment = listOf(EquipmentInstance("e1", "equip.sword_000", EquipmentSlot.WEAPON, equippedBy = "ghost"))))
        }
    }

    private val quests = QuestTracker(FakeCatalog)

    @Test
    fun `new game starts with starter hero, starting grants and every quest tracked`() {
        val state = NewGame(FakeCatalog, rules, quests).create("  Linh  ", "hero.selene")
        assertEquals("Linh", state.profile.name)
        assertEquals(listOf("hero.selene"), state.heroes.map { it.heroId })
        assertEquals(300, state.wallet.balance(Currencies.GOLD))
        assertEquals(LineupCapacity.STARTING, state.lineup.capacity)
        assertEquals(3, state.quests.size)
        assertThrows<GameRuleViolation> { NewGame(FakeCatalog, rules, quests).create("x", "hero.aldric") }
        assertThrows<GameRuleViolation> { NewGame(FakeCatalog, rules, quests).create("Linh", "hero.nobody") }
    }

    @Test
    fun `quest tracker reacts to battles, items and travel, then rewards can be claimed once`() {
        var state = NewGame(FakeCatalog, rules, quests).create("Linh", "hero.aldric")
        state = quests.react(rules.finishBattle(state, "encounter.test", BattleOutcome.VICTORY, enemiesDefeated = 2)).state
        assertEquals(2, state.quests.single { it.questId == "quest.side.hunt" }.progress)
        val completing = quests.react(rules.finishBattle(state, "encounter.test", BattleOutcome.DEFEAT, enemiesDefeated = 5))
        assertTrue(completing.events.contains(GameEvent.QuestCompleted("quest.side.hunt")))
        state = completing.state
        assertEquals(3, state.quests.single { it.questId == "quest.side.hunt" }.progress)
        state = quests.react(rules.grant(state, listOf(Grant(GrantKind.ITEM, "item.food_t2", 2)), LedgerReason("test"))).state
        assertTrue(state.quests.single { it.questId == "quest.side.food" }.completed)
        state = quests.react(rules.enterMap(state, "map.ashwaste_01", 10, 10, 0)).state
        assertTrue(state.quests.single { it.questId == "quest.side.travel" }.completed)
        val gold = state.wallet.balance(Currencies.GOLD)
        state = rules.claimQuest(state, "quest.side.hunt").state
        assertEquals(gold + 50, state.wallet.balance(Currencies.GOLD))
        assertThrows<GameRuleViolation> { rules.claimQuest(state, "quest.side.hunt") }
    }

    @Test
    fun `achievement tiers are claimed in order once their counter is reached`() {
        var state = rules.finishBattle(newGame(), "encounter.test", BattleOutcome.VICTORY, 1).state
        state = rules.claimAchievementTier(state, "achievement.victor").state
        assertEquals(10, state.wallet.balance(Currencies.GEM))
        assertThrows<GameRuleViolation> { rules.claimAchievementTier(state, "achievement.victor") }
    }

    @Test
    fun `lineup capacity grows with profile level`() {
        assertEquals(3, LineupCapacity.forProfileLevel(1))
        assertEquals(4, LineupCapacity.forProfileLevel(6))
        assertEquals(9, LineupCapacity.forProfileLevel(60))
    }
}
