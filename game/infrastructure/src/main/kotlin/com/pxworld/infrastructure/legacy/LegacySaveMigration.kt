package com.pxworld.infrastructure.legacy

import com.pxworld.application.SaveRepository
import java.io.File
import java.security.MessageDigest
import java.text.Normalizer

data class MigrationOutcome(val imported: List<String>, val skipped: List<String>, val notes: Map<String, List<String>>)

class LegacySaveMigration(private val importer: LegacyV1Importer, private val saves: SaveRepository) {

    fun run(legacyRoots: List<File>, markerDirectory: File): MigrationOutcome {
        val imported = mutableListOf<String>()
        val skipped = mutableListOf<String>()
        val notes = mutableMapOf<String, List<String>>()
        markerDirectory.mkdirs()
        legacyFolders(legacyRoots).forEach { folder ->
            val marker = File(markerDirectory, markerName(folder))
            if (marker.isFile) {
                skipped += folder.path
                return@forEach
            }
            runCatching { importer.import(folder) }
                .onSuccess { result ->
                    val slot = uniqueSlot("legacy_${slug(result.state.profile.name)}", imported)
                    saves.save(slot, result.state)
                    marker.writeText(slot)
                    imported += slot
                    notes[slot] = result.notes
                }
                .onFailure { failure ->
                    skipped += folder.path
                    notes[folder.path] = listOf("import failed: ${failure.message}")
                }
        }
        return MigrationOutcome(imported, skipped, notes)
    }

    private fun legacyFolders(roots: List<File>): List<File> =
        roots.map { File(it, "select") }
            .filter { it.isDirectory }
            .flatMap { select -> select.listFiles { file -> file.isDirectory && File(file, "info.json").isFile }.orEmpty().sortedBy { it.name } }
            .distinctBy { it.canonicalPath }

    private fun uniqueSlot(base: String, pending: List<String>): String {
        val taken = saves.slots().toSet() + pending
        var candidate = base.take(MAX_SLOT_LENGTH)
        var suffix = 2
        while (candidate in taken) candidate = "${base.take(MAX_SLOT_LENGTH - 3)}_${suffix++}"
        return candidate
    }

    private fun slug(name: String): String =
        Normalizer.normalize(name.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "").replace('đ', 'd').replace(Regex("[^a-z0-9]+"), "_").trim('_').ifEmpty { "player" }

    private fun markerName(folder: File): String {
        val fingerprint = "${folder.canonicalPath}|${File(folder, "info.json").lastModified()}"
        return MessageDigest.getInstance("SHA-1").digest(fingerprint.toByteArray()).joinToString("") { "%02x".format(it) } + ".imported"
    }

    companion object {
        const val MAX_SLOT_LENGTH: Int = 32
    }
}
