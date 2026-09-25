package com.pxworld.web

import com.badlogic.gdx.files.FileHandle
import com.pxworld.application.SaveRepository
import com.pxworld.domain.progression.GameState
import com.pxworld.infrastructure.save.CorruptSave
import com.pxworld.infrastructure.save.SaveCodec

class BrowserSaveStore(private val directory: FileHandle) : SaveRepository {

    override fun slots(): List<String> =
        directory.list()
            .filter { !it.isDirectory && it.name().endsWith(SAVE_SUFFIX) }
            .map { it.name().removeSuffix(SAVE_SUFFIX) }
            .sorted()

    override fun save(slot: String, state: GameState) {
        require(SLOT_PATTERN.matches(slot)) { "slot names are lowercase letters, digits and underscores" }
        val encoded = SaveCodec.encode(state)
        val target = file(slot)
        if (target.exists()) backup(slot).writeString(target.readString(ENCODING), false, ENCODING)
        target.writeString(encoded, false, ENCODING)
    }

    override fun load(slot: String): GameState {
        val failures = mutableListOf<String>()
        for (candidate in listOf(file(slot), backup(slot)).filter { it.exists() }) {
            try {
                return SaveCodec.decode(candidate.readString(ENCODING))
            } catch (corrupt: CorruptSave) {
                failures += "${candidate.name()}: ${corrupt.message}"
            }
        }
        throw CorruptSave(if (failures.isEmpty()) "no save for slot $slot" else failures.joinToString("; "))
    }

    override fun export(slot: String): String = file(slot).takeIf { it.exists() }?.readString(ENCODING) ?: SaveCodec.encode(load(slot))

    override fun decode(exported: String): GameState = SaveCodec.decode(exported)

    override fun delete(slot: String) {
        listOf(file(slot), backup(slot)).filter { it.exists() }.forEach { it.delete() }
    }

    private fun file(slot: String) = directory.child("$slot$SAVE_SUFFIX")

    private fun backup(slot: String) = directory.child("$slot$BACKUP_SUFFIX")

    companion object {
        private const val SAVE_SUFFIX = ".save.json"
        private const val BACKUP_SUFFIX = ".1.save.json.bak"
        private const val ENCODING = "UTF-8"
        private val SLOT_PATTERN = Regex("^[a-z0-9_]{1,32}$")
    }
}
