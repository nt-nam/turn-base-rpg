package com.pxworld.simulation

import com.pxworld.content.balance.SeedRange
import com.pxworld.domain.battle.GridCell
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimulationArgumentsTest {

    private fun request(vararg arguments: String): SimulationRequest =
        (SimulationArguments.parse(arguments.toList()) as CommandLine.Run).request

    private fun failure(vararg arguments: String): String =
        assertThrows<UsageException> { SimulationArguments.parse(arguments.toList()) }.message.orEmpty()

    @Test
    fun `no arguments simulate every encounter with the defaults`() {
        assertEquals(SimulationRequest(), request())
        assertEquals(SeedRange(1, 200), request().seeds)
    }

    @Test
    fun `every option is read in both spellings`() {
        val parsed = request(
            "--encounter", "encounter.ashwaste_01.e0", "--seeds=50", "--seed-start", "900", "--level", "4", "--stars=2",
            "--format", "json", "--content", "other/content", "--heroes", "hero.aldric:3,mirae",
        )
        assertEquals(EncounterSelection.Only("encounter.ashwaste_01.e0"), parsed.encounter)
        assertEquals(SeedRange(900, 50), parsed.seeds)
        assertEquals(4, parsed.defaultLevel)
        assertEquals(2, parsed.defaultStar)
        assertEquals(OutputFormat.JSON, parsed.format)
        assertEquals("other/content", parsed.contentDirectory)
        assertEquals(LineupSelection.Heroes(listOf(HeroRequest("hero.aldric", level = 3), HeroRequest("hero.mirae"))), parsed.lineup)
    }

    @Test
    fun `hero specs carry level, stars, placement and equipment`() {
        val parsed = request("--heroes", "aldric:5:2@0-1+sword_001+equip.armor_008,nyx:1:1")
        assertEquals(
            listOf(
                HeroRequest("hero.aldric", level = 5, star = 2, cell = GridCell(lane = 0, depth = 1), equipment = listOf("equip.sword_001", "equip.armor_008")),
                HeroRequest("hero.nyx", level = 1, star = 1),
            ),
            (parsed.lineup as LineupSelection.Heroes).heroes,
        )
    }

    @Test
    fun `help wins over everything else`() {
        assertEquals(CommandLine.ShowUsage, SimulationArguments.parse(listOf("--seeds", "3", "--help")))
        assertTrue("--encounter" in SimulationArguments.USAGE && "--heroes" in SimulationArguments.USAGE)
    }

    @Test
    fun `malformed arguments are explained`() {
        assertEquals("unknown option --speed", failure("--speed", "2"))
        assertEquals("unexpected argument 'aldric'", failure("aldric"))
        assertEquals("--seeds needs a value", failure("--seeds"))
        assertEquals("--seeds needs a value", failure("--seeds", "--level", "3"))
        assertEquals("--seeds must be a whole number, was 'many'", failure("--seeds", "many"))
        assertEquals("--seeds must be in 1..100000, was 0", failure("--seeds", "0"))
        assertEquals("--level must be in 1..60, was 61", failure("--level", "61"))
        assertEquals("--stars must be in 0..5, was 6", failure("--stars", "6"))
        assertEquals("--format must be table or json, was 'xml'", failure("--format", "xml"))
        assertEquals("--level given more than once", failure("--level", "2", "--level=3"))
        assertEquals("--heroes has an empty entry in 'aldric,,nyx'", failure("--heroes", "aldric,,nyx"))
        assertEquals("level in 'aldric:0' must be in 1..60, was 0", failure("--heroes", "aldric:0"))
        assertEquals("placement in 'aldric@1' must be @lane-depth, e.g. @1-0", failure("--heroes", "aldric@1"))
        assertEquals("lane in 'aldric@3-0' must be in 0..2, was 3", failure("--heroes", "aldric@3-0"))
        assertEquals("hero spec 'aldric:1:0:2' must look like heroId[:level[:stars]][@lane-depth][+equipmentId...]", failure("--heroes", "aldric:1:0:2"))
        assertEquals("hero spec 'aldric+' has an empty equipment entry", failure("--heroes", "aldric+"))
    }
}
