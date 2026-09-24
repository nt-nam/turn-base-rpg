package com.pxworld.screens

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameScreenIdTest {

    @Test
    fun `every game screen in the catalog has exactly one enum constant`() {
        val catalog = File(System.getProperty("catalogJson")).readText()
        val idPattern = Regex(""""id": "(game\.[a-z0-9_.]+)"""")
        val catalogIds = idPattern.findAll(catalog).map { it.groupValues[1] }.toList()
        assertEquals(catalogIds, GameScreenId.values().map { it.id })
    }

    @Test
    fun `ids resolve back to their constant`() {
        GameScreenId.values().forEach { assertEquals(it, GameScreenId.fromId(it.id)) }
    }

    @Test
    fun `launch scope covers the core playable loop`() {
        val launch = GameScreenId.values().filter { it.season == ReleaseSeason.LAUNCH }.map { it.id }.toSet()
        listOf("game.boot.splash", "game.world.world_explore", "game.battle.battle_main", "game.heroes.lineup_editor", "game.economy.daily_checkin")
            .forEach { assertTrue(it in launch, "$it must ship at launch") }
    }
}
