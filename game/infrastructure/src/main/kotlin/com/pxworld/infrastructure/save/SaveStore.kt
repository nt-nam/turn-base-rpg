package com.pxworld.infrastructure.save

import com.pxworld.application.SaveRepository
import com.pxworld.domain.progression.GameState
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

class CorruptSave(message: String) : IllegalStateException(message)

object SaveCodec {

    const val SCHEMA_VERSION: Int = 2

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = false
    }

    fun encode(state: GameState): String {
        val document = SaveGameMapper.toDocument(state)
        val body = json.encodeToString(SaveGameDocument.serializer(), document)
        return json.encodeToString(SaveEnvelope.serializer(), SaveEnvelope(SCHEMA_VERSION, sha256(body), document)) + "\n"
    }

    fun decode(text: String): GameState {
        val envelope = try {
            json.decodeFromString(SaveEnvelope.serializer(), text)
        } catch (failure: IllegalArgumentException) {
            throw CorruptSave("unreadable save: ${failure.message}")
        }
        if (envelope.schemaVersion != SCHEMA_VERSION) throw CorruptSave("unsupported save schema ${envelope.schemaVersion}")
        val body = json.encodeToString(SaveGameDocument.serializer(), envelope.state)
        if (sha256(body) != envelope.checksum) throw CorruptSave("checksum mismatch")
        return try {
            SaveGameMapper.toState(envelope.state)
        } catch (violation: IllegalArgumentException) {
            throw CorruptSave("save violates game invariants: ${violation.message}")
        }
    }

    private fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
}

class FileSaveStore(private val directory: File, private val keptBackups: Int = 3) : SaveRepository {

    override fun slots(): List<String> =
        directory.listFiles { file -> file.isFile && file.name.endsWith(SAVE_SUFFIX) }
            .orEmpty()
            .map { it.name.removeSuffix(SAVE_SUFFIX) }
            .sorted()

    override fun save(slot: String, state: GameState) {
        require(SLOT_PATTERN.matches(slot)) { "slot names are lowercase letters, digits and underscores" }
        directory.mkdirs()
        val target = file(slot)
        val temporary = File(directory, "$slot$SAVE_SUFFIX.tmp")
        FileOutputStream(temporary).use { stream ->
            stream.write(SaveCodec.encode(state).toByteArray(Charsets.UTF_8))
            stream.fd.sync()
        }
        rotateBackups(slot)
        try {
            Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        } catch (unsupported: AtomicMoveNotSupportedException) {
            Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    override fun load(slot: String): GameState {
        val candidates = listOf(file(slot)) + (1..keptBackups).map { backup(slot, it) }
        val failures = mutableListOf<String>()
        for (candidate in candidates.filter { it.isFile }) {
            try {
                return SaveCodec.decode(candidate.readText())
            } catch (corrupt: CorruptSave) {
                failures += "${candidate.name}: ${corrupt.message}"
            }
        }
        throw CorruptSave(if (failures.isEmpty()) "no save for slot $slot" else failures.joinToString("; "))
    }

    override fun delete(slot: String) {
        (listOf(file(slot)) + (1..keptBackups).map { backup(slot, it) }).forEach { it.delete() }
    }

    private fun rotateBackups(slot: String) {
        val current = file(slot)
        if (!current.isFile) return
        backup(slot, keptBackups).delete()
        for (index in keptBackups - 1 downTo 1) {
            val from = backup(slot, index)
            if (from.isFile) Files.move(from.toPath(), backup(slot, index + 1).toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        Files.copy(current.toPath(), backup(slot, 1).toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    private fun file(slot: String) = File(directory, "$slot$SAVE_SUFFIX")

    private fun backup(slot: String, index: Int) = File(directory, "$slot.$index$SAVE_SUFFIX.bak")

    companion object {
        private const val SAVE_SUFFIX = ".save.json"
        private val SLOT_PATTERN = Regex("^[a-z0-9_]{1,32}$")
    }
}
