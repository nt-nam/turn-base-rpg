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
        fun <T> kind(directory: String, serializer: KSerializer<T>): List<T> =
            files.filterKeys { it.startsWith("$directory/") && it.endsWith(".json") }
                .toSortedMap()
                .flatMap { (path, text) -> parse(path, text, ListSerializer(serializer)) }

        val stringMap = MapSerializer(String.serializer(), String.serializer())
        val localization = files.filterKeys { it.startsWith("localization/") && it.endsWith(".json") }
            .toSortedMap()
            .map { (path, text) -> path.removePrefix("localization/").substringBefore(".") to parse(path, text, stringMap) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, tables) -> tables.fold(emptyMap<String, String>()) { merged, table -> merged + table } }
        val assetMap = files["assets/legacy_asset_map.json"]?.let { parse("assets/legacy_asset_map.json", it, stringMap) }.orEmpty()

        return ContentBundle(
            currencies = kind("currencies", CurrencyRecord.serializer()),
            heroClasses = kind("hero_classes", HeroClassRecord.serializer()),
            statuses = kind("statuses", StatusRecord.serializer()),
            skills = kind("skills", SkillRecord.serializer()),
            heroes = kind("heroes", HeroRecord.serializer()),
            enemies = kind("enemies", EnemyRecord.serializer()),
            encounters = kind("encounters", EncounterRecord.serializer()),
            items = kind("items", ItemRecord.serializer()),
            equipment = kind("equipment", EquipmentRecord.serializer()),
            quests = kind("quests", QuestRecord.serializer()),
            achievements = kind("achievements", AchievementRecord.serializer()),
            checkinTables = kind("checkin_tables", CheckinTableRecord.serializer()),
            battleRules = kind("balance", BattleRulesRecord.serializer()),
            maps = kind("maps", MapRecord.serializer()),
            npcs = kind("npcs", NpcRecord.serializer()),
            dialogues = kind("dialogues", DialogueRecord.serializer()),
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
