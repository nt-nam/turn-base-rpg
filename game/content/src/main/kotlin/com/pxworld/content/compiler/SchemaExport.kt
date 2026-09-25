package com.pxworld.content.compiler

import com.pxworld.content.ContentKinds
import com.pxworld.content.ContentSchema
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import kotlin.system.exitProcess

object SchemaExport {

    private val pretty = Json { prettyPrint = true; prettyPrintIndent = "  " }

    fun render(): Map<String, String> =
        ContentKinds.all.associate { kind -> "${kind.directory}.schema.json" to pretty.encodeToString(JsonElement.serializer(), ContentSchema.file(kind)) + "\n" }

    fun stale(directory: File): List<String> {
        val expected = render()
        val present = directory.listFiles { file -> file.name.endsWith(".schema.json") }.orEmpty().associate { it.name to it.readText() }
        return (expected.keys + present.keys).sorted().filter { expected[it] != present[it] }
    }
}

fun main(arguments: Array<String>) {
    require(arguments.isNotEmpty()) { "usage: SchemaExport <schemaDir> [--check]" }
    val directory = File(arguments[0])
    if ("--check" in arguments) {
        val stale = SchemaExport.stale(directory)
        if (stale.isNotEmpty()) {
            println("content schemas are out of date: $stale — run ./gradlew :game:content:exportSchemas")
            exitProcess(1)
        }
        println("content schemas up to date")
        return
    }
    directory.mkdirs()
    directory.listFiles { file -> file.name.endsWith(".schema.json") }.orEmpty().forEach { it.delete() }
    SchemaExport.render().forEach { (name, text) -> File(directory, name).writeText(text) }
    println("wrote ${ContentKinds.all.size} schemas to $directory")
}
