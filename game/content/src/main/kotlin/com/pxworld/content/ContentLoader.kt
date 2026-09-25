package com.pxworld.content

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class ContentFormatException(val file: String, cause: Throwable) :
    IllegalArgumentException("invalid content file $file: ${cause.message}", cause)

object ContentLoader {

    val json: Json = Json {
        ignoreUnknownKeys = false
        isLenient = false
        prettyPrint = false
    }

    fun decodePack(text: String): ContentBundle = json.decodeFromString(ContentBundle.serializer(), text)

    fun load(files: Map<String, String>): ContentBundle {
        fun <T> kind(kind: ContentKind<T>): List<T> =
            files.filterKeys { it.startsWith("${kind.directory}/") && it.endsWith(".json") }
                .toSortedMap()
                .flatMap { (path, text) -> parse(path, text, ListSerializer(kind.serializer)) }

        val stringMap = MapSerializer(String.serializer(), String.serializer())
        val localization = files.filterKeys { it.startsWith("localization/") && it.endsWith(".json") }
            .toSortedMap()
            .map { (path, text) -> path.removePrefix("localization/").substringBefore(".") to parse(path, text, stringMap) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, tables) -> tables.fold(emptyMap<String, String>()) { merged, table -> merged + table } }
        val assetMap = files["assets/legacy_asset_map.json"]?.let { parse("assets/legacy_asset_map.json", it, stringMap) }.orEmpty()

        return ContentBundle(
            currencies = kind(ContentKinds.CURRENCIES),
            heroClasses = kind(ContentKinds.HERO_CLASSES),
            statuses = kind(ContentKinds.STATUSES),
            skills = kind(ContentKinds.SKILLS),
            heroes = kind(ContentKinds.HEROES),
            enemies = kind(ContentKinds.ENEMIES),
            encounters = kind(ContentKinds.ENCOUNTERS),
            items = kind(ContentKinds.ITEMS),
            equipment = kind(ContentKinds.EQUIPMENT),
            quests = kind(ContentKinds.QUESTS),
            achievements = kind(ContentKinds.ACHIEVEMENTS),
            checkinTables = kind(ContentKinds.CHECKIN_TABLES),
            battleRules = kind(ContentKinds.BALANCE),
            maps = kind(ContentKinds.MAPS),
            npcs = kind(ContentKinds.NPCS),
            dialogues = kind(ContentKinds.DIALOGUES),
            audioCues = kind(ContentKinds.AUDIO_CUES),
            localization = localization,
            assetMap = assetMap,
        )
    }

    private fun <T> parse(path: String, text: String, serializer: KSerializer<T>): T =
        try {
            json.decodeFromString(serializer, text)
        } catch (failure: IllegalArgumentException) {
            throw ContentFormatException(path, failure)
        }
}
