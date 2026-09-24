package com.pxworld.client.screens.debug

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.Currencies
import com.pxworld.application.Grant
import com.pxworld.application.GrantKind
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.screens.GameScreenId
import com.pxworld.screens.ReleaseSeason

class DebugMenuScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_MENU, context, args) {

    override val titleKey = "ui.debug.title"

    override fun body(content: Table) {
        content.add(ui.label(text("ui.debug.flavor", context.services.flavor.name), "muted")).padBottom(Tokens.SPACE_M).row()
        listOf(
            "screen_jump" to GameScreenId.DEBUG_DEBUG_SCREEN_JUMP,
            "cheats" to GameScreenId.DEBUG_DEBUG_CHEATS,
            "atlas_browser" to GameScreenId.DEBUG_DEBUG_ATLAS_BROWSER,
        ).forEach { (key, target) ->
            content.add(ui.button(testId(key), text("ui.debug.$key"), "secondary") { context.navigator.open(target) }).width(320f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class DebugScreenJumpScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_SCREEN_JUMP, context, args) {

    override val titleKey = "ui.debug.screen_jump"

    override fun body(content: Table) {
        val registered = context.navigator.registered
        val launch = GameScreenId.values().filter { it.season == ReleaseSeason.LAUNCH }
        content.add(ui.label(text("ui.debug.coverage", registered.size, launch.size, GameScreenId.values().size), "heading", testId("coverage"))).padBottom(Tokens.SPACE_S).row()
        val list = Table().top().left()
        val needsGame = context.session.store != null
        GameScreenId.values().filter { it in registered }.forEach { screen ->
            list.add(ui.label(screen.id, "small")).left().padRight(Tokens.SPACE_M)
            list.add(ui.button(testId("open/${screen.id}"), text("ui.debug.open"), "secondary", enabled = needsGame || screen.module in NO_GAME_MODULES) {
                context.navigator.open(screen)
            }).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }

    companion object {
        val NO_GAME_MODULES = setOf("boot", "onboarding", "debug")
    }
}

class DebugCheatsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_CHEATS, context, args) {

    override val titleKey = "ui.debug.cheats"

    override fun body(content: Table) {
        if (context.session.store == null) {
            content.add(ui.label(text("ui.debug.needs_game"), "muted"))
            return
        }
        val grants = listOf(
            "gold" to Grant(GrantKind.CURRENCY, Currencies.GOLD, 10_000),
            "gem" to Grant(GrantKind.CURRENCY, Currencies.GEM, 1_000),
            "food" to Grant(GrantKind.ITEM, "item.food_t3", 10),
        )
        grants.forEach { (key, grant) ->
            content.add(ui.button(testId(key), text("ui.debug.grant_$key"), "secondary") {
                context.act(text("ui.debug.granted")) { context.services.rules.grant(it, listOf(grant), LedgerReason("debug_cheat", key)) }
            }).width(320f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class DebugAtlasBrowserScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_ATLAS_BROWSER, context, args) {

    override val titleKey = "ui.debug.atlas_browser"

    override fun body(content: Table) {
        val paths = context.assets.atlasPaths()
        val selected = args.optional("atlas") ?: paths.first()
        val side = Table().top()
        paths.forEach { path ->
            side.add(ui.button(testId("atlas/${path.substringAfterLast('/')}"), path.substringAfterLast('/'), if (path == selected) "tab-active" else "tab") {
                context.navigator.replace(id, ScreenArgs.of("atlas" to path))
            }).growX().padBottom(Tokens.SPACE_XS).row()
        }
        val grid = Table().top().left()
        context.assets.atlas(selected).regions.distinctBy { it.name }.take(REGION_LIMIT).forEachIndexed { index, region ->
            val cell = Table()
            cell.add(ui.image(region, 56f)).size(56f).row()
            cell.add(ui.label(region.name, "small"))
            grid.add(cell).width(110f).pad(Tokens.SPACE_XS)
            if (index % 8 == 7) grid.row()
        }
        content.add(ui.scroll(side, testId("atlases"))).width(240f).top().growY().padRight(Tokens.SPACE_M)
        content.add(ui.scroll(grid, testId("regions"))).grow()
    }

    companion object {
        const val REGION_LIMIT: Int = 240
    }
}
