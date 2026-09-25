package com.pxworld.simulation

import com.pxworld.content.ContentLoader
import com.pxworld.content.balance.SeedRange
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.domain.battle.GridCell
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SimulationRunnerTest {

    private val bundle = ContentLoader.load(ContentCompilation.readTree(File(System.getProperty("contentDir"))))
    private val runner = SimulationRunner(bundle)
    private val encounter = bundle.encounters.first()

    private fun heroes(vararg heroes: HeroRequest) = SimulationRequest(
        encounter = EncounterSelection.Only(encounter.id),
        lineup = LineupSelection.Heroes(heroes.toList()),
        seeds = SeedRange(1, 20),
    )

    private fun failure(request: SimulationRequest): String = assertThrows<UsageException> { runner.plan(request) }.message.orEmpty()

    @Test
    fun `explicit heroes default to the encounter level and are placed by class`() {
        val job = runner.plan(heroes(HeroRequest("hero.mirae"), HeroRequest("hero.borin", level = 7, star = 1))).single()
        assertEquals(encounter.id, job.encounterId)
        assertEquals(listOf(encounter.recommendedLevel, 7), job.members.map { it.level })
        assertEquals(listOf(0, 1), job.members.map { it.star })
        assertEquals(listOf(GridCell(2, 2), GridCell(1, 0)), job.members.map { it.cell })
    }

    @Test
    fun `request defaults apply to heroes without their own level and stars`() {
        val request = heroes(HeroRequest("hero.aldric"), HeroRequest("hero.nyx", level = 2)).copy(defaultLevel = 9, defaultStar = 3)
        assertEquals(listOf(9 to 3, 2 to 3), runner.plan(request).single().members.map { it.level to it.star })
    }

    @Test
    fun `runs are deterministic and equipment changes the outcome`() {
        val bare = heroes(HeroRequest("hero.nyx", level = 1, cell = GridCell(1, 0)))
        assertEquals(runner.run(bare), runner.run(bare))
        val armed = heroes(HeroRequest("hero.nyx", level = 1, cell = GridCell(1, 0), equipment = listOf("equip.sword_001")))
        assertNotEquals(runner.run(bare).runs.single().result.units, runner.run(armed).runs.single().result.units)
        assertEquals(20, runner.run(armed).runs.single().result.battles)
    }

    @Test
    fun `unknown ids and impossible lineups are usage errors`() {
        assertTrue(failure(SimulationRequest(encounter = EncounterSelection.Only("encounter.nowhere.e1"))).startsWith("unknown encounter encounter.nowhere.e1; known: "))
        assertTrue(failure(heroes(HeroRequest("hero.nobody"))).startsWith("unknown hero hero.nobody; known: "))
        assertEquals("hero.aldric is listed more than once", failure(heroes(HeroRequest("hero.aldric"), HeroRequest("hero.aldric"))))
        assertEquals(
            "hero.aldric and hero.nyx are both placed at 1-0",
            failure(heroes(HeroRequest("hero.aldric", cell = GridCell(1, 0)), HeroRequest("hero.nyx", cell = GridCell(1, 0)))),
        )
        assertEquals("unknown equipment equip.nothing for hero.aldric", failure(heroes(HeroRequest("hero.aldric", equipment = listOf("equip.nothing")))))
        assertEquals(
            "hero.aldric wears more than one weapon item",
            failure(heroes(HeroRequest("hero.aldric", equipment = listOf("equip.sword_000", "equip.sword_001")))),
        )
    }
}
