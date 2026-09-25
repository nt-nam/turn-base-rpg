package com.pxworld.content

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class ContentKind<T>(val directory: String, val serializer: KSerializer<T>)

object ContentKinds {
    val CURRENCIES = ContentKind("currencies", CurrencyRecord.serializer())
    val HERO_CLASSES = ContentKind("hero_classes", HeroClassRecord.serializer())
    val STATUSES = ContentKind("statuses", StatusRecord.serializer())
    val SKILLS = ContentKind("skills", SkillRecord.serializer())
    val HEROES = ContentKind("heroes", HeroRecord.serializer())
    val ENEMIES = ContentKind("enemies", EnemyRecord.serializer())
    val ENCOUNTERS = ContentKind("encounters", EncounterRecord.serializer())
    val ITEMS = ContentKind("items", ItemRecord.serializer())
    val EQUIPMENT = ContentKind("equipment", EquipmentRecord.serializer())
    val QUESTS = ContentKind("quests", QuestRecord.serializer())
    val ACHIEVEMENTS = ContentKind("achievements", AchievementRecord.serializer())
    val CHECKIN_TABLES = ContentKind("checkin_tables", CheckinTableRecord.serializer())
    val BALANCE = ContentKind("balance", BattleRulesRecord.serializer())
    val MAPS = ContentKind("maps", MapRecord.serializer())
    val NPCS = ContentKind("npcs", NpcRecord.serializer())
    val DIALOGUES = ContentKind("dialogues", DialogueRecord.serializer())
    val AUDIO_CUES = ContentKind("audio_cues", AudioCueRecord.serializer())

    val all: List<ContentKind<*>> = listOf(
        CURRENCIES, HERO_CLASSES, STATUSES, SKILLS, HEROES, ENEMIES, ENCOUNTERS, ITEMS, EQUIPMENT,
        QUESTS, ACHIEVEMENTS, CHECKIN_TABLES, BALANCE, MAPS, NPCS, DIALOGUES, AUDIO_CUES,
    )

    fun byDirectory(directory: String): ContentKind<*>? = all.firstOrNull { it.directory == directory }
}

object ContentSchema {

    const val DRAFT: String = "https://json-schema.org/draft/2020-12/schema"

    fun record(kind: ContentKind<*>): JsonObject =
        JsonObject(linkedMapOf<String, JsonElement>("\$schema" to JsonPrimitive(DRAFT), "title" to JsonPrimitive(kind.directory)) + describe(kind.serializer.descriptor))

    fun file(kind: ContentKind<*>): JsonObject = JsonObject(
        linkedMapOf(
            "\$schema" to JsonPrimitive(DRAFT),
            "title" to JsonPrimitive("${kind.directory} file"),
            "type" to JsonPrimitive("array"),
            "items" to JsonObject(describe(kind.serializer.descriptor)),
        ),
    )

    private fun describe(descriptor: SerialDescriptor): Map<String, JsonElement> {
        val base = when (val kind = descriptor.kind) {
            PrimitiveKind.STRING, PrimitiveKind.CHAR -> mapOf("type" to JsonPrimitive("string"))
            PrimitiveKind.INT, PrimitiveKind.LONG, PrimitiveKind.SHORT, PrimitiveKind.BYTE -> mapOf("type" to JsonPrimitive("integer"))
            PrimitiveKind.FLOAT, PrimitiveKind.DOUBLE -> mapOf("type" to JsonPrimitive("number"))
            PrimitiveKind.BOOLEAN -> mapOf("type" to JsonPrimitive("boolean"))
            SerialKind.ENUM -> mapOf("type" to JsonPrimitive("string"), "enum" to JsonArray((0 until descriptor.elementsCount).map { JsonPrimitive(descriptor.getElementName(it)) }))
            StructureKind.LIST -> mapOf("type" to JsonPrimitive("array"), "items" to JsonObject(describe(descriptor.getElementDescriptor(0))))
            StructureKind.MAP -> mapOf("type" to JsonPrimitive("object"), "additionalProperties" to JsonObject(describe(descriptor.getElementDescriptor(1))))
            StructureKind.CLASS, StructureKind.OBJECT -> {
                val properties = (0 until descriptor.elementsCount).associate { index -> descriptor.getElementName(index) to JsonObject(describe(descriptor.getElementDescriptor(index))) }
                val required = (0 until descriptor.elementsCount).filterNot(descriptor::isElementOptional).map { JsonPrimitive(descriptor.getElementName(it)) }
                mapOf(
                    "type" to JsonPrimitive("object"),
                    "properties" to JsonObject(properties),
                    "required" to JsonArray(required),
                    "additionalProperties" to JsonPrimitive(false),
                )
            }
            else -> throw IllegalArgumentException("content records must not use ${descriptor.serialName} ($kind)")
        }
        return if (descriptor.isNullable) mapOf("anyOf" to JsonArray(listOf(JsonObject(base), JsonObject(mapOf("type" to JsonPrimitive("null")))))) else base
    }
}
