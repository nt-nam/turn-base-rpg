package com.pxworld.architecture

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArchitectureTest {

    private val root = File(System.getProperty("repositoryRoot"))

    private data class Source(val module: String, val file: File, val packageName: String, val imports: List<String>, val text: String)

    private val sources: List<Source> by lazy {
        MODULES.flatMap { module ->
            File(root, module).resolve("src/main").walkTopDown()
                .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                .map { file ->
                    val text = file.readText()
                    val lines = text.lineSequence().map { it.trim() }
                    Source(
                        module = module,
                        file = file,
                        packageName = lines.firstOrNull { it.startsWith("package ") }?.removePrefix("package ")?.removeSuffix(";")?.trim().orEmpty(),
                        imports = lines.filter { it.startsWith("import ") }.map { it.removePrefix("import ").removePrefix("static ").removeSuffix(";").trim() }.toList(),
                        text = text,
                    )
                }
                .toList()
        }
    }

    private fun violations(module: String, allowed: (String) -> Boolean): List<String> =
        sources.filter { it.module == module }.flatMap { source ->
            source.imports.filterNot(allowed).map { "${source.file.relativeTo(root).invariantSeparatorsPath}: $it" }
        }

    private fun String.within(vararg prefixes: String) = prefixes.any { startsWith(it) }

    private val standardLibrary = arrayOf("kotlin.", "kotlinx.coroutines.")

    @Test
    fun `every module is scanned`() {
        MODULES.forEach { module -> assertTrue(sources.any { it.module == module }, "no sources found for $module") }
    }

    @Test
    fun `domain is pure kotlin with no platform or io`() {
        assertEquals(emptyList(), violations("game/domain") { it.within(*standardLibrary, "com.pxworld.domain.") })
    }

    @Test
    fun `application depends only on domain`() {
        assertEquals(emptyList(), violations("game/application") { it.within(*standardLibrary, "com.pxworld.domain.", "com.pxworld.application.") })
    }

    @Test
    fun `content knows rules and data formats but no engine, storage or network`() {
        val allowed = arrayOf(*standardLibrary, "kotlinx.serialization.", "com.pxworld.domain.", "com.pxworld.application.", "com.pxworld.content.")
        val found = sources.filter { it.module == "game/content" }.flatMap { source ->
            val tooling = source.packageName.startsWith("com.pxworld.content.compiler")
            source.imports.filterNot { it.within(*allowed) || (tooling && it.within("java.io.", "java.security.")) }
                .map { "${source.file.name}: $it" }
        }
        assertEquals(emptyList(), found)
    }

    @Test
    fun `infrastructure never reaches up into client, server or the engine`() {
        assertEquals(emptyList(), violations("game/infrastructure") { !it.within("com.pxworld.client.", "com.pxworld.server.", "com.badlogic.", "io.ktor.") })
    }

    @Test
    fun `client talks to storage only through application ports`() {
        assertEquals(emptyList(), violations("game/client") { !it.within("com.pxworld.infrastructure.", "com.pxworld.server.", "io.ktor.", "java.sql.", "java.net.http.") })
    }

    @Test
    fun `client does not touch files or threads directly`() {
        assertEquals(emptyList(), violations("game/client") { !it.within("java.io.File", "java.nio.file.", "java.lang.Thread", "java.util.concurrent.Executors") })
    }

    @Test
    fun `server shares rules but never the game client or engine`() {
        assertEquals(emptyList(), violations("server/app") { !it.within("com.pxworld.client.", "com.badlogic.", "com.github.quillraven.") })
    }

    @Test
    fun `balance simulator reaches the game only through content rules`() {
        val allowed = arrayOf(*standardLibrary, "kotlinx.serialization.", "com.pxworld.domain.", "com.pxworld.content.", "com.pxworld.simulation.", "java.io.File")
        assertEquals(emptyList(), violations("tools/sim-cli") { it.within(*allowed) })
    }

    @Test
    fun `platform launchers only wire modules together`() {
        listOf("game/platform-desktop", "game/platform-android").forEach { module ->
            val bodies = sources.filter { it.module == module }
            assertTrue(bodies.sumOf { it.text.lines().size } < LAUNCHER_LINE_BUDGET, "$module grew past $LAUNCHER_LINE_BUDGET lines; move logic into a shared module")
        }
    }

    @Test
    fun `packages match their module`() {
        val expected = mapOf(
            "game/domain" to "com.pxworld.domain", "game/application" to "com.pxworld.application", "game/content" to "com.pxworld.content",
            "game/infrastructure" to "com.pxworld.infrastructure", "game/client" to "com.pxworld.client", "game/screens" to "com.pxworld.screens",
            "game/automation" to "com.pxworld.automation", "game/platform-desktop" to "com.pxworld.desktop", "game/platform-android" to "com.pxworld.android",
            "server/app" to "com.pxworld.server", "tools/asset-pipeline" to "com.pxworld.assets", "tools/sim-cli" to "com.pxworld.simulation",
        )
        val misplaced = sources.filterNot { it.packageName.startsWith(expected.getValue(it.module)) }.map { "${it.file.relativeTo(root).invariantSeparatorsPath}: ${it.packageName}" }
        assertEquals(emptyList(), misplaced)
    }

    @Test
    fun `domain randomness and time come from explicit inputs`() {
        val found = sources.filter { it.module in DETERMINISTIC_MODULES || it.packageName.startsWith("com.pxworld.content.balance") }.flatMap { source ->
            FORBIDDEN_NONDETERMINISM.filter { it in source.text }.map { "${source.file.name}: $it" }
        }
        assertEquals(emptyList(), found)
    }

    companion object {
        const val LAUNCHER_LINE_BUDGET: Int = 250
        val MODULES = listOf(
            "game/domain", "game/application", "game/content", "game/infrastructure", "game/screens", "game/client",
            "game/automation", "game/platform-desktop", "game/platform-android", "server/app", "tools/asset-pipeline", "tools/sim-cli",
        )
        val DETERMINISTIC_MODULES = setOf("game/domain", "game/application", "tools/sim-cli")
        val FORBIDDEN_NONDETERMINISM = listOf("System.currentTimeMillis", "System.nanoTime", "Random()", "kotlin.random.Random.Default", "Math.random", "LocalDate.now", "Instant.now", "UUID.randomUUID")
    }
}
