package com.pxworld.client

import com.pxworld.client.screens.DefaultScreens
import com.pxworld.content.ContentLoader
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.screens.GameScreenId
import com.pxworld.screens.ReleaseSeason
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClientTextTest {

    private val sources = File(System.getProperty("clientSources"))
    private val bundle = ContentLoader.load(ContentCompilation.readTree(File(System.getProperty("contentDir"))))
    private val keyPattern = Regex(""""(ui\.[a-z0-9_.]+[a-z0-9_])"""")
    private val dynamicFamilies = mapOf(
        "ui.pause." to listOf("heroes", "lineup", "bag", "shop", "recruit", "checkin", "quests", "achievements", "settings"),
        "ui.slot." to listOf("weapon", "armor", "jewelry", "support"),
        "ui.stat." to listOf("hp", "attack", "defense", "speed", "crit_rate", "crit_damage", "accuracy", "evasion", "effect_hit", "effect_resistance"),
        "ui.settings." to listOf("music", "sound"),
        "ui.language." to listOf("vi", "en"),
        "ui.debug." to listOf("screen_jump", "cheats", "atlas_browser", "grant_gold", "grant_gem", "grant_food"),
    )

    private fun usedKeys(): Set<String> =
        sources.walkTopDown().filter { it.extension == "kt" }
            .flatMap { file -> keyPattern.findAll(file.readText()).map { it.groupValues[1] } }
            .filterNot { key -> dynamicFamilies.keys.any { key == it.dropLast(1) } }
            .toSet() + dynamicFamilies.flatMap { (prefix, suffixes) -> suffixes.map { prefix + it } }

    @Test
    fun `every ui key used by the client exists in every locale`() {
        val keys = usedKeys()
        assertTrue(keys.size > 100)
        listOf("vi", "en").forEach { locale ->
            val table = bundle.localization[locale].orEmpty()
            assertEquals(emptyList(), keys.filterNot { it in table }.sorted(), "missing $locale keys")
        }
    }

    @Test
    fun `every registered screen belongs to the launch catalog`() {
        val registered = DefaultScreens.registry().registered
        assertTrue(registered.size >= 35)
        assertTrue(registered.all { it.season == ReleaseSeason.LAUNCH })
        assertTrue(GameScreenId.BOOT_SPLASH in registered)
    }
}
