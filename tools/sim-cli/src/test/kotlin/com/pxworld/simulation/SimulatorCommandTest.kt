package com.pxworld.simulation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimulatorCommandTest {

    private val content = System.getProperty("contentDir")

    private data class Outcome(val status: Int, val output: String, val errors: String)

    private fun execute(vararg arguments: String): Outcome {
        val output = StringBuilder()
        val errors = StringBuilder()
        val status = SimulatorCommand.execute(arguments.toList(), { output.appendLine(it) }, { errors.appendLine(it) })
        return Outcome(status, output.toString(), errors.toString())
    }

    @Test
    fun `table output names the encounter, the rates and every unit`() {
        val outcome = execute("--content", content, "--encounter", "encounter.dawnvillage_01.e0", "--heroes", "aldric:1@1-0", "--seeds", "20")
        assertEquals(SimulatorCommand.SUCCESS, outcome.status, outcome.errors)
        val lines = outcome.output.lines()
        assertEquals("encounter.dawnvillage_01.e0  lineup custom  seeds 1..20", lines[0])
        assertTrue(lines[1].trim().startsWith("win "), lines[1])
        assertTrue(lines.any { it.trim().startsWith("ally#0") && "hero.aldric" in it && "1-0" in it })
        assertTrue(lines.any { it.trim().startsWith("enemy#0") })
    }

    @Test
    fun `json output is machine readable and consistent`() {
        val outcome = execute("--content", content, "--encounter", "encounter.dawnvillage_01.e0", "--heroes", "aldric:1", "--seeds", "20", "--format", "json")
        assertEquals(SimulatorCommand.SUCCESS, outcome.status, outcome.errors)
        val document = Json.parseToJsonElement(outcome.output).jsonObject
        assertEquals(20, document.getValue("seeds").jsonPrimitive.int)
        val run = document.getValue("runs").jsonArray.single().jsonObject
        assertEquals(20, listOf("victories", "draws", "defeats").sumOf { run.getValue(it).jsonPrimitive.int })
        assertEquals("hero.aldric", run.getValue("heroes").jsonArray.single().jsonObject.getValue("hero").jsonPrimitive.content)
        assertEquals(2, run.getValue("units").jsonArray.size)
    }

    @Test
    fun `invalid arguments and unknown ids exit with a usage failure`() {
        val unknownHero = execute("--content", content, "--heroes", "nobody")
        assertEquals(SimulatorCommand.USAGE_FAILURE, unknownHero.status)
        assertTrue(unknownHero.errors.startsWith("error: unknown hero hero.nobody"), unknownHero.errors)
        assertEquals("", unknownHero.output)

        val badOption = execute("--turbo")
        assertEquals(SimulatorCommand.USAGE_FAILURE, badOption.status)
        assertTrue("unknown option --turbo" in badOption.errors)

        val missingContent = execute("--content", "does/not/exist")
        assertEquals(SimulatorCommand.USAGE_FAILURE, missingContent.status)
        assertTrue("does not exist" in missingContent.errors)
    }

    @Test
    fun `help prints usage and succeeds`() {
        val outcome = execute("--help")
        assertEquals(SimulatorCommand.SUCCESS, outcome.status)
        assertTrue(outcome.output.startsWith("usage: sim-cli"))
    }
}
