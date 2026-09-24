package com.pxworld.server

import com.pxworld.content.BattleContentAssembler
import com.pxworld.content.ContentBundle
import com.pxworld.content.ContentIssue
import com.pxworld.content.ContentLoader
import com.pxworld.content.ContentValidator
import com.pxworld.content.IssueSeverity
import com.pxworld.content.LineupSlot
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.content.compiler.LegacyAssetExistence
import com.pxworld.domain.battle.BattleEngine
import com.pxworld.domain.battle.GridCell
import com.pxworld.infrastructure.replay.ReplayDocument
import com.pxworld.infrastructure.replay.ReplayCodec
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.File

@Serializable
data class IssueView(val severity: String, val recordId: String, val message: String)

@Serializable
data class ValidationView(val errors: Int, val warnings: Int, val records: Int, val issues: List<IssueView>)

class ContentRejected(val validation: ValidationView) : RuntimeException("content has ${validation.errors} errors")

class ContentService(private val contentDir: File, legacyAssetsDir: File, private val writable: Boolean) {

    private val assets = LegacyAssetExistence(legacyAssetsDir)
    private val pretty = Json { prettyPrint = true; prettyPrintIndent = "  " }

    @Volatile
    var bundle: ContentBundle = load(ContentCompilation.readTree(contentDir))
        private set

    val pack: String get() = ContentCompilation.packJson(bundle)

    fun kinds(): List<String> = arrayFiles(ContentCompilation.readTree(contentDir)).keys.map { it.substringBefore('/') }.distinct().sorted()

    fun records(kind: String): List<JsonObject> =
        arrayFiles(ContentCompilation.readTree(contentDir)).filterKeys { it.substringBefore('/') == kind }.values.flatMap { it.map { element -> element as JsonObject } }

    fun validate(tree: Map<String, String>): ValidationView {
        val issues = runCatching { ContentValidator.validate(ContentLoader.load(tree), assets) }
            .getOrElse { listOf(ContentIssue(IssueSeverity.ERROR, "format", it.message ?: it.toString())) }
        val errors = issues.count { it.severity == IssueSeverity.ERROR }
        val records = runCatching { ContentLoader.load(tree).allIds().size }.getOrDefault(0)
        return ValidationView(errors, issues.size - errors, records, issues.map { IssueView(it.severity.name, it.recordId, it.message) })
    }

    @Synchronized
    fun upsert(kind: String, record: JsonObject, dryRun: Boolean): ValidationView {
        val id = (record["id"] as? JsonPrimitive)?.contentOrNull ?: throw IllegalArgumentException("record needs an id")
        val tree = ContentCompilation.readTree(contentDir)
        val files = arrayFiles(tree)
        val owner = files.entries.firstOrNull { (path, items) -> path.substringBefore('/') == kind && items.any { idOf(it) == id } }?.key
            ?: files.keys.firstOrNull { it.substringBefore('/') == kind }
            ?: throw IllegalArgumentException("unknown kind $kind")
        val existing = files.getValue(owner)
        val updated = if (existing.any { idOf(it) == id }) existing.map { if (idOf(it) == id) record else it } else existing + record
        val text = pretty.encodeToString(JsonArray.serializer(), JsonArray(updated)) + "\n"
        val candidate = tree + (owner to text)
        val validation = validate(candidate)
        if (validation.errors > 0) throw ContentRejected(validation)
        if (!dryRun) {
            check(writable) { "content is read-only in this environment" }
            File(contentDir, owner).writeText(text)
            bundle = load(candidate)
        }
        return validation
    }

    fun validateReplay(document: ReplayDocument): ReplayVerdict {
        val record = ReplayCodec.record(document)
        val violations = buildList {
            if (record.lineup.isEmpty() || record.lineup.size > MAX_LINEUP) add("lineup size ${record.lineup.size}")
            if (record.lineup.map { it.cell }.distinct().size != record.lineup.size) add("duplicate grid cells")
            if (record.lineup.map { it.heroId }.distinct().size != record.lineup.size) add("duplicate heroes")
            record.lineup.forEach { slot ->
                if (slot.level !in 1..MAX_LEVEL) add("${slot.heroId} level ${slot.level}")
                if (slot.star !in 0..MAX_STAR) add("${slot.heroId} star ${slot.star}")
                if (slot.cell.lane !in 0 until GRID || slot.cell.depth !in 0 until GRID) add("${slot.heroId} cell ${slot.cell}")
                if (slot.bonus.toMap().values.any { it < 0 || it > MAX_FLAT_BONUS }) add("${slot.heroId} bonus out of range")
            }
        }
        if (violations.isNotEmpty()) return ReplayVerdict(false, "REJECTED", 0, violations)
        return runCatching {
            val lineup = record.lineup.map { LineupSlot(it.heroId, it.level, it.star, GridCell(it.cell.lane, it.cell.depth), it.bonus) }
            val final = BattleEngine.replay(BattleContentAssembler(bundle).battle(record.seed, lineup, record.encounterId), record.commands).finalState
            val replayed = final.outcome?.name ?: "UNFINISHED"
            val mismatches = buildList {
                if (replayed != record.outcome.name) add("outcome claimed ${record.outcome} replayed $replayed")
                if (final.round != record.rounds) add("rounds claimed ${record.rounds} replayed ${final.round}")
            }
            ReplayVerdict(mismatches.isEmpty(), replayed, final.round, mismatches)
        }.getOrElse { ReplayVerdict(false, "ILLEGAL", 0, listOf(it.message ?: it.toString())) }
    }

    private fun load(tree: Map<String, String>): ContentBundle = ContentLoader.load(tree)

    private fun arrayFiles(tree: Map<String, String>): Map<String, List<JsonElement>> =
        tree.filterKeys { '/' in it && !it.startsWith(NON_RECORD_PREFIX) }
            .mapNotNull { (path, text) -> (Json.parseToJsonElement(text) as? JsonArray)?.let { path to it.toList() } }
            .toMap()

    private fun idOf(element: JsonElement): String? = ((element as? JsonObject)?.get("id") as? JsonPrimitive)?.contentOrNull

    companion object {
        const val NON_RECORD_PREFIX: String = "localization/"
        const val MAX_LINEUP: Int = 6
        const val MAX_LEVEL: Int = 100
        const val MAX_STAR: Int = 6
        val GRID: Int = GridCell.GRID_SIZE
        const val MAX_FLAT_BONUS: Int = 100_000
    }
}

@Serializable
data class ReplayVerdict(val valid: Boolean, val replayedOutcome: String, val replayedRounds: Int, val reasons: List<String>)
