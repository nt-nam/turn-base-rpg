package com.pxworld.content

import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.content.compiler.LegacyAssetExistence
import com.pxworld.domain.battle.BattleEngine
import com.pxworld.domain.battle.GridCell
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContentTest {

    private val contentDir = File(System.getProperty("contentDir"))
    private val assets = LegacyAssetExistence(File(System.getProperty("legacyAssetsDir")))
    private val tree = ContentCompilation.readTree(contentDir)
    private val bundle = ContentLoader.load(tree)

    @Test
    fun `migrated content has no validation errors`() {
        val errors = ContentValidator.validate(bundle, assets).filter { it.severity == IssueSeverity.ERROR }
        assertEquals(emptyList(), errors)
    }

    @Test
    fun `migrated content keeps the legacy record counts`() {
        assertEquals(6, bundle.heroes.size)
        assertEquals(166, bundle.equipment.size)
        assertEquals(52, bundle.items.size)
        assertEquals(30, bundle.checkinTables.single().days.size)
        assertEquals(10, bundle.encounters.size)
    }

    @Test
    fun `strict parsing rejects unknown fields and trailing commas`() {
        val withUnknownField = tree + ("currencies/extra.json" to """[{"id":"currency.x","name":"a","description":"b","icon":"c","surprise":1}]""")
        assertThrows<ContentFormatException> { ContentLoader.load(withUnknownField) }
        val withTrailingComma = tree + ("currencies/extra.json" to """[{"id":"currency.x","name":"a","description":"b","icon":"c",}]""")
        assertThrows<ContentFormatException> { ContentLoader.load(withTrailingComma) }
    }

    @Test
    fun `validator catches dangling references`() {
        val broken = bundle.copy(
            encounters = bundle.encounters + bundle.encounters.first().copy(
                id = "encounter.test.broken",
                mapObjectId = 99,
                enemies = listOf(EncounterEnemyRecord("enemy.ghost", 1, 0, CellRecord(0, 0))),
                rewards = listOf(RewardRecord(RewardKindName.ITEM, "item.missing", 1)),
            ),
        )
        val messages = ContentValidator.validate(broken, assets).filter { it.recordId == "encounter.test.broken" }.map { it.message }
        assertTrue(messages.any { "unknown enemy enemy.ghost" in it })
        assertTrue(messages.any { "unknown item item.missing" in it })
    }

    @Test
    fun `every encounter assembles into a playable deterministic battle`() {
        val assembler = BattleContentAssembler(bundle)
        val lineup = ContentCompilation.referenceLineup(bundle, level = 5)
        bundle.encounters.forEach { encounter ->
            val first = BattleEngine.runAuto(assembler.battle(7, lineup, encounter.id))
            val second = BattleEngine.runAuto(assembler.battle(7, lineup, encounter.id))
            assertEquals(first.events, second.events)
            assertTrue(first.finalState.isOver)
        }
    }

    @Test
    fun `starter hero alone can fight the first village encounter`() {
        val assembler = BattleContentAssembler(bundle)
        val starter = bundle.heroes.first { it.starter }
        val lineup = listOf(LineupSlot(starter.id, level = 1, star = 0, cell = GridCell(1, 0)))
        val record = BattleEngine.runAuto(assembler.battle(1, lineup, "encounter.dawnvillage_01.e0"))
        assertTrue(record.finalState.isOver)
    }

    @Test
    fun `content pack is byte-for-byte reproducible`() {
        val again = ContentLoader.load(ContentCompilation.readTree(contentDir))
        assertEquals(ContentCompilation.sha256(ContentCompilation.packJson(bundle)), ContentCompilation.sha256(ContentCompilation.packJson(again)))
    }
}
