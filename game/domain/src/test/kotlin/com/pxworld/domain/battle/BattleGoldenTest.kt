package com.pxworld.domain.battle

import com.pxworld.domain.battle.BattleFixtures.hero
import com.pxworld.domain.battle.BattleFixtures.rules
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import kotlin.test.assertEquals

class BattleGoldenTest {

    private val scenarios: Map<String, BattleSetup> = mapOf(
        "balanced_seed_1" to BattleFixtures.balancedBattle(1),
        "balanced_seed_2024" to BattleFixtures.balancedBattle(2024),
        "tank_wall_vs_assassins" to BattleSetup(
            seed = 77,
            allies = listOf(hero("tank", 0, 0), hero("tank", 2, 0), hero("support", 1, 2)),
            enemies = listOf(hero("assassin", 0, 0), hero("assassin", 1, 0), hero("assassin", 2, 0)),
            rules = rules,
        ),
        "mage_burn_squad" to BattleSetup(
            seed = 3131,
            allies = listOf(hero("mage", 0, 2), hero("mage", 2, 2), hero("warrior", 1, 0)),
            enemies = listOf(hero("tank", 1, 0), hero("ranger", 0, 1), hero("support", 2, 2)),
            rules = rules,
        ),
        "star_gap_underdog" to BattleSetup(
            seed = 555,
            allies = listOf(hero("warrior", 1, 0, level = 5, star = 0), hero("ranger", 1, 2, level = 5, star = 0)),
            enemies = listOf(hero("warrior", 1, 0, level = 5, star = 3), hero("ranger", 1, 2, level = 5, star = 3)),
            rules = rules,
        ),
        "boss_solo" to BattleSetup(
            seed = 9001,
            allies = BattleFixtures.balancedAllies,
            enemies = listOf(hero("tank", 1, 1, level = 25, star = 4, name = "ash_scorpion_queen")),
            rules = rules,
        ),
    )

    @TestFactory
    fun `auto battle event logs match golden files`(): List<DynamicTest> = scenarios.map { (name, setup) ->
        DynamicTest.dynamicTest(name) {
            val actual = BattleEventLog.render(BattleEngine.runAuto(setup).events)
            val golden = File(System.getProperty("goldenDir"), "$name.txt")
            if (System.getProperty("updateGolden") == "true" || !golden.exists()) {
                golden.parentFile.mkdirs()
                golden.writeText(actual)
            }
            assertEquals(golden.readText(), actual, "golden mismatch for $name; rerun with -DupdateGolden=true after review")
        }
    }
}
