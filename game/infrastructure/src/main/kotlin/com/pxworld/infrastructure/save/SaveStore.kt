package com.pxworld.infrastructure.save

import com.pxworld.application.SaveRepository
import com.pxworld.domain.progression.GameState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
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
        explicitNulls = true
    }

    fun encode(state: GameState): String {
        val body = json.encodeToJsonElement(SaveGameDocument.serializer(), SaveGameMapper.toDocument(state))
        val envelope = JsonObject(
            linkedMapOf(
                "schemaVersion" to JsonPrimitive(SCHEMA_VERSION),
                "checksum" to JsonPrimitive(sha256(body.toString())),
                "state" to body,
            ),
        )
        return json.encodeToString(JsonElement.serializer(), envelope) + "\n"
    }

    fun decode(text: String): GameState {
        val root = try {
            Json.parseToJsonElement(text) as? JsonObject ?: throw CorruptSave("save root is not an object")
        } catch (failure: IllegalArgumentException) {
            throw CorruptSave("unreadable save: ${failure.message}")
        }
        val version = (root["schemaVersion"] as? JsonPrimitive)?.intOrNull
        if (version != SCHEMA_VERSION) throw CorruptSave("unsupported save schema $version")
        val body = root["state"] ?: throw CorruptSave("save has no state")
        val checksum = (root["checksum"] as? JsonPrimitive)?.contentOrNull
        val document = try {
            json.decodeFromJsonElement(SaveGameDocument.serializer(), body)
        } catch (failure: IllegalArgumentException) {
            throw CorruptSave("unreadable state: ${failure.message}")
        }
        val matchesStoredTree = sha256(body.toString()) == checksum
        val matchesFirstFormat = sha256(json.encodeToString(JsonElement.serializer(), body)) == checksum
        if (!matchesStoredTree && !matchesFirstFormat) throw CorruptSave("checksum mismatch")
        return try {
            SaveGameMapper.toState(document)
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

    override fun export(slot: String): String = file(slot).takeIf { it.isFile }?.readText() ?: SaveCodec.encode(load(slot))

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
