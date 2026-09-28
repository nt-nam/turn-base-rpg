package com.pxworld.content.balance

import com.pxworld.content.ContentLoader
import com.pxworld.content.LineupSlot
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.domain.battle.BattleSide
import com.pxworld.domain.battle.GridCell
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EncounterSimulatorTest {

    private val bundle = ContentLoader.load(ContentCompilation.readTree(File(System.getProperty("contentDir"))))
    private val simulator = EncounterSimulator(bundle)
    private val encounter = bundle.encounters.first { it.enemies.size > 1 }
    private val lineup = ContentCompilation.referenceLineup(bundle, level = encounter.recommendedLevel)

    @Test
    fun `the same seeds give the same result`() {
        val seeds = SeedRange(first = 40, count = 25)
        assertEquals(simulator.simulate(encounter.id, lineup, seeds), simulator.simulate(encounter.id, lineup, seeds))
    }

    @Test
    fun `outcomes and rounds account for every battle`() {
        val result = simulator.simulate(encounter.id, lineup, SeedRange(first = 1, count = 30))
        assertEquals(30, result.victories + result.draws + result.defeats)
        assertEquals(30, result.sortedRounds.size)
        assertEquals(result.sortedRounds.sorted(), result.sortedRounds)
        assertEquals(1000, result.winPermille + result.drawPermille + result.lossPermille)
    }

    @Test
    fun `damage dealt by one side is damage taken by the other`() {
        val result = simulator.simulate(encounter.id, lineup, SeedRange(first = 1, count = 20))
        fun total(side: BattleSide, measure: (UnitTotals) -> Long) = result.units.filter { it.unit.id.side == side }.sumOf(measure)
        assertEquals(total(BattleSide.ALLY) { it.damageDealt }, total(BattleSide.ENEMY) { it.damageTaken })
        assertEquals(total(BattleSide.ENEMY) { it.damageDealt }, total(BattleSide.ALLY) { it.damageTaken })
        assertTrue(total(BattleSide.ALLY) { it.damageDealt } > 0)
        assertEquals(lineup.size + encounter.enemies.size, result.units.size)
        assertEquals(encounter.enemies.map { it.enemy }, result.units.filter { it.unit.id.side == BattleSide.ENEMY }.map { it.unit.contentId })
    }

    @Test
    fun `defeated enemies are counted once per battle`() {
        val result = simulator.simulate(encounter.id, lineup, SeedRange(first = 1, count = 20))
        val enemies = result.units.filter { it.unit.id.side == BattleSide.ENEMY }
        assertTrue(enemies.all { it.deaths <= result.battles })
        assertTrue(enemies.sumOf { it.deaths } >= result.victories * encounter.enemies.size)
    }

    @Test
    fun `percentiles use the nearest rank`() {
        val result = simulator.simulate(encounter.id, listOf(LineupSlot(lineup.first().heroId, 1, 0, GridCell(1, 0))), SeedRange(first = 1, count = 10))
        assertEquals(result.sortedRounds[8], result.roundsAtPercentile(90))
        assertEquals(result.sortedRounds[9], result.roundsAtPercentile(100))
        assertEquals(result.sortedRounds[0], result.roundsAtPercentile(1))
    }

    @Test
    fun `unknown encounters are rejected`() {
        assertThrows<IllegalArgumentException> { simulator.simulate("encounter.nowhere.e9", lineup) }
    }
}
