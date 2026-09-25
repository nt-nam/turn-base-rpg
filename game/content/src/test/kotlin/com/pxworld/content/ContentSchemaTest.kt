package com.pxworld.content

import com.pxworld.content.compiler.SchemaExport
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContentSchemaTest {

    private val contentDir = File(System.getProperty("contentDir"))

    @Test
    fun `every record directory has a kind and a committed schema that is up to date`() {
        val directories = contentDir.listFiles { file -> file.isDirectory }.orEmpty().map { it.name }.toSet() - NON_RECORD_DIRECTORIES
        assertEquals(directories, ContentKinds.all.map { it.directory }.toSet())
        assertEquals(emptyList(), SchemaExport.stale(File(contentDir, "schemas")))
    }

    @Test
    fun `schemas mark defaulted fields optional, enums closed and nullable fields nullable`() {
        val audio = ContentSchema.record(ContentKinds.AUDIO_CUES)
        val required = audio.getValue("required").jsonArray.map { it.jsonPrimitive.content }
        assertTrue("id" in required && "volumePercent" !in required, required.toString())

        val skill = ContentSchema.record(ContentKinds.SKILLS).getValue("properties").jsonObject
        val slot = skill.getValue("slot").jsonObject
        assertEquals(listOf("basic", "skill", "ultimate"), slot.getValue("enum").jsonArray.map { it.jsonPrimitive.content })

        val targetCount = ContentSchema.record(ContentKinds.SKILLS).getValue("properties").jsonObject.getValue("targeting").jsonObject
            .getValue("properties").jsonObject.getValue("count").jsonObject
        assertTrue(targetCount["anyOf"] is JsonArray, targetCount.toString())
        assertEquals(false, (audio["additionalProperties"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.toBoolean())
        assertTrue(ContentSchema.file(ContentKinds.ITEMS)["items"] is JsonObject)
    }

    companion object {
        val NON_RECORD_DIRECTORIES = setOf("localization", "assets", "schemas")
    }
}
