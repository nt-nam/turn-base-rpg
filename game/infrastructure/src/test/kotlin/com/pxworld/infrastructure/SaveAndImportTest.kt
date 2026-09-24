package com.pxworld.infrastructure

import com.pxworld.application.Counters
import com.pxworld.application.Currencies
import com.pxworld.application.GameRules
import com.pxworld.content.ContentBundleCatalog
import com.pxworld.content.ContentLoader
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.stats.StatKind
import com.pxworld.infrastructure.legacy.LegacyV1Importer
import com.pxworld.infrastructure.save.CorruptSave
import com.pxworld.infrastructure.save.FileSaveStore
import com.pxworld.infrastructure.save.SaveCodec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveAndImportTest {

    private val bundle = ContentLoader.load(ContentCompilation.readTree(File(System.getProperty("contentDir"))))
    private val importer = LegacyV1Importer(bundle)
    private val legacySaves = File(System.getProperty("legacySavesDir"))
    private val rules = GameRules(ContentBundleCatalog(bundle))

    private fun imported(name: String) = importer.import(File(legacySaves, name))

    @Test
    fun `every legacy fixture imports into a valid state`() {
        val folders = legacySaves.listFiles { file -> file.isDirectory }.orEmpty()
        assertTrue(folders.size >= 3)
        folders.forEach { folder -> importer.import(folder) }
    }

    @Test
    fun `rich legacy save keeps heroes, equipment, currency, items and progress`() {
        val (state, notes) = imported("y_desktop")
        assertEquals("y", state.profile.name)
        assertEquals(3, state.heroes.size)
        assertEquals(listOf("hero.aldric", "hero.selene", "hero.borin"), state.heroes.map { it.heroId })
        assertEquals(2, state.heroes.first().level)
        assertEquals(40, state.heroes.first().experience)
        assertEquals(240, state.wallet.balance(Currencies.GOLD))
        assertEquals(264, state.wallet.balance(Currencies.GEM))
        assertEquals(1, state.inventory.quantity("item.food_t1"))
        val sword = state.inventory.equipment.single { it.equipmentId == "equip.sword_000" }
        assertEquals(EquipmentSlot.WEAPON, sword.slot)
        assertEquals(state.heroes.first().instanceId, sword.equippedBy)
        assertEquals(mapOf(GridCell(lane = 1, depth = 1) to state.heroes.first().instanceId), state.lineup.cells)
        assertEquals("map.ashwaste_01", state.position.mapId)
        assertEquals(1, state.checkin.claimedDays)
        assertEquals(3, state.stats.value(Counters.ENEMIES_DEFEATED))
        assertEquals(1, state.stats.value(Counters.BATTLES_WON))
        assertEquals(emptyList(), notes)
    }

    @Test
    fun `imported equipment now actually raises hero stats`() {
        val (state) = imported("y_desktop")
        val heroId = state.heroes.first().instanceId
        val withSword = rules.heroStats(state, heroId)[StatKind.ATTACK]
        val withoutSword = rules.heroStats(rules.unequip(state, state.inventory.equippedOn(heroId).single().instanceId).state, heroId)[StatKind.ATTACK]
        assertEquals(100, withSword - withoutSword)
    }

    @Test
    fun `save round trip is byte-for-byte stable`() {
        val (state) = imported("y_desktop")
        val played = rules.finishBattle(state, "encounter.ashwaste_01.e1", BattleOutcome.VICTORY, 1).state
        val encoded = SaveCodec.encode(played)
        assertEquals(played, SaveCodec.decode(encoded))
        assertEquals(encoded, SaveCodec.encode(SaveCodec.decode(encoded)))
    }

    @Test
    fun `tampered save is rejected by checksum`() {
        val encoded = SaveCodec.encode(imported("y_desktop").state)
        val tampered = encoded.replace("\"currency.gold\": 240", "\"currency.gold\": 999999")
        assertTrue(tampered != encoded)
        assertThrows<CorruptSave> { SaveCodec.decode(tampered) }
    }

    @Test
    fun `file store rotates backups and recovers from a corrupt primary`(@TempDir directory: File) {
        val store = FileSaveStore(directory)
        val (first) = imported("y_desktop")
        store.save("slot_y", first)
        val second = rules.finishBattle(first, "encounter.ashwaste_01.e1", BattleOutcome.VICTORY, 1).state
        store.save("slot_y", second)
        assertEquals(second, store.load("slot_y"))
        File(directory, "slot_y.save.json").writeText("{ broken")
        assertEquals(first, store.load("slot_y"))
        assertEquals(listOf("slot_y"), store.slots())
    }

    @Test
    fun `v2 saves written before the journal existed still load`() {
        val fixture = File(legacySaves.parentFile, "save_v2_before_journal.save.json")
        val state = SaveCodec.decode(fixture.readText())
        assertEquals("Agent Tester", state.profile.name)
        assertTrue(state.journal.visitedMaps.isEmpty())
        assertEquals(state, SaveCodec.decode(SaveCodec.encode(state)))
    }
}
