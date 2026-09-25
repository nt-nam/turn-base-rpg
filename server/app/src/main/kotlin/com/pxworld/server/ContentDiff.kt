package com.pxworld.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Serializable
data class ChangedRecord(val id: String, val fields: List<String>)

@Serializable
data class KindDiff(val kind: String, val added: List<String>, val removed: List<String>, val changed: List<ChangedRecord>)

@Serializable
data class TableDiff(val table: String, val added: Int, val removed: Int, val changed: Int, val sample: List<String>)

@Serializable
data class ContentDiffView(val from: String, val to: String, val kinds: List<KindDiff>, val tables: List<TableDiff>)

object ContentDiff {

    const val LOCALIZATION: String = "localization"
    const val SAMPLE: Int = 20

    fun between(from: String, to: String, before: String, after: String): ContentDiffView {
        val old = ApiJson.parseToJsonElement(before) as JsonObject
        val new = ApiJson.parseToJsonElement(after) as JsonObject
        val kinds = mutableListOf<KindDiff>()
        val tables = mutableListOf<TableDiff>()
        for (key in (old.keys + new.keys).sorted()) {
            val left = old[key]
            val right = new[key]
            when {
                left is JsonArray || right is JsonArray -> records(key, left as? JsonArray, right as? JsonArray)?.let(kinds::add)
                key == LOCALIZATION -> {
                    val locales = ((left as? JsonObject)?.keys.orEmpty() + (right as? JsonObject)?.keys.orEmpty()).sorted()
                    locales.forEach { locale ->
                        table("$key.$locale", (left as? JsonObject)?.get(locale) as? JsonObject, (right as? JsonObject)?.get(locale) as? JsonObject)?.let(tables::add)
                    }
                }
                else -> table(key, left as? JsonObject, right as? JsonObject)?.let(tables::add)
            }
        }
        return ContentDiffView(from, to, kinds, tables)
    }

    private fun records(kind: String, before: JsonArray?, after: JsonArray?): KindDiff? {
        val left = byId(before)
        val right = byId(after)
        val changed = (left.keys intersect right.keys).sorted().mapNotNull { id ->
            val a = left.getValue(id)
            val b = right.getValue(id)
            if (a == b) null else ChangedRecord(id, (a.keys + b.keys).filter { a[it] != b[it] }.sorted())
        }
        val diff = KindDiff(kind, (right.keys - left.keys).sorted(), (left.keys - right.keys).sorted(), changed)
        return diff.takeIf { it.added.isNotEmpty() || it.removed.isNotEmpty() || it.changed.isNotEmpty() }
    }

    private fun table(name: String, before: JsonObject?, after: JsonObject?): TableDiff? {
        val left: Map<String, JsonElement> = before ?: emptyMap()
        val right: Map<String, JsonElement> = after ?: emptyMap()
        val added = right.keys - left.keys
        val removed = left.keys - right.keys
        val changed = (left.keys intersect right.keys).filter { left[it] != right[it] }
        if (added.isEmpty() && removed.isEmpty() && changed.isEmpty()) return null
        return TableDiff(name, added.size, removed.size, changed.size, (added + removed + changed).sorted().take(SAMPLE))
    }

    private fun byId(records: JsonArray?): Map<String, JsonObject> =
        records.orEmpty().mapNotNull { element ->
            val record = element as? JsonObject ?: return@mapNotNull null
            (record["id"] as? JsonPrimitive)?.contentOrNull?.let { it to record }
        }.toMap()
}
