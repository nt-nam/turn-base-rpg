package com.pxworld.client.screens.debug

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.world.MinimapActor
import com.pxworld.client.ui.Tokens
import com.pxworld.client.world.MapLayout
import com.pxworld.screens.GameScreenId

class DebugBattleSandboxScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_BATTLE_SANDBOX, context, args) {
    override val titleKey = "ui.debug.battle_sandbox"

    override fun body(content: Table) {
        if (context.session.store == null) {
            content.add(ui.label(text("ui.debug.needs_game"), "muted"))
            return
        }
        val list = Table().top()
        context.services.content.encounters.forEach { encounter ->
            list.add(ui.label("${text(encounter.name)} (${encounter.id})", "body")).left().padRight(Tokens.SPACE_M)
            list.add(ui.button(testId("fight/${encounter.id}"), text("ui.encounter.fight"), "secondary") {
                context.navigator.open(GameScreenId.BATTLE_BATTLE_MAIN, ScreenArgs.of("encounter" to encounter.id))
            }).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}

class DebugMapInspectorScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_MAP_INSPECTOR, context, args) {
    override val titleKey = "ui.debug.map_inspector"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val maps = context.services.content.maps
        val selected = maps.firstOrNull { it.id == args.optional("map") } ?: maps.first()
        val layout = MapLayout(context.assets.map(selected.asset))
        val side = Table().top()
        maps.forEach { map ->
            side.add(ui.button(testId("map/${map.id}"), lookup.mapName(map.id), if (map == selected) "tab-active" else "tab") {
                context.navigator.replace(id, ScreenArgs.of("map" to map.id))
            }).growX().padBottom(Tokens.SPACE_XS).row()
        }
        val detail = Table().top()
        detail.add(ui.label("${selected.id} -> ${selected.legacyName}.tmx", "heading")).left().row()
        detail.add(ui.label(text("ui.debug.map_stats", layout.widthPixels.toInt(), layout.heightPixels.toInt(), layout.colliders.size, layout.teleports.size, layout.encounters.size, layout.spawns.size), "body", testId("stats"))).left().row()
        detail.add(MinimapActor(layout, context.ui.whiteRegion) { null }).size(600f, 380f).padTop(Tokens.SPACE_S).row()
        layout.teleports.forEach { detail.add(ui.label("-> ${it.label} (${it.targetLegacyMap}#${it.targetSpawn})", "small")).left().row() }
        content.add(ui.scroll(side, testId("maps"))).width(300f).top().growY().padRight(Tokens.SPACE_M)
        content.add(detail).top().grow()
    }
}

class DebugSaveEditorScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_SAVE_EDITOR, context, args) {
    override val titleKey = "ui.debug.save_editor"

    override fun body(content: Table) {
        val slot = context.session.store?.slot
        if (slot == null) {
            content.add(ui.label(text("ui.debug.needs_game"), "muted"))
            return
        }
        val json = context.services.saves.export(slot)
        content.add(ui.label(text("ui.debug.save_size", slot, json.length), "muted", testId("size"))).left().row()
        val body = Table().top().left()
        json.lines().take(MAX_LINES).forEach { body.add(Label(it, context.ui.skin, "small")).left().row() }
        content.add(ui.scroll(body, testId("json"))).grow()
    }

    companion object {
        const val MAX_LINES: Int = 400
    }
}

class DebugFlagsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_FLAGS, context, args) {
    override val titleKey = "ui.debug.flags"

    override fun body(content: Table) {
        val flags = context.debugFlags
        listOf(
            Triple("performance", flags.showPerformance) { value: Boolean -> flags.showPerformance = value },
            Triple("colliders", flags.showColliders) { value: Boolean -> flags.showColliders = value },
            Triple("fast_battles", flags.fastBattles) { value: Boolean -> flags.fastBattles = value },
        ).forEach { (key, value, set) ->
            content.add(ui.label(text("ui.debug.flag.$key"), "body")).width(320f).left()
            content.add(ui.button(testId(key), text(if (value) "ui.settings.on" else "ui.settings.off"), if (value) "primary" else "secondary") { set(!value); rebuild() })
                .width(140f).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class DebugPerformanceScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_PERF_OVERLAY, context, args) {
    override val titleKey = "ui.debug.performance"
    private val readout = Label("", context.ui.skin, "body")

    override fun body(content: Table) {
        readout.name = testId("readout")
        content.add(readout).left()
        refresh()
    }

    override fun update(delta: Float) = refresh()

    private fun refresh() {
        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        readout.setText(text("ui.debug.perf_line", Gdx.graphics.framesPerSecond, usedMb, runtime.maxMemory() / (1024 * 1024), context.batch.renderCalls, context.navigator.stackIds.size))
    }
}

class DebugLogsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_LOGS, context, args) {
    override val titleKey = "ui.debug.logs"

    override fun body(content: Table) {
        val list = Table().top().left()
        val entries = context.logs.entries
        if (entries.isEmpty()) list.add(ui.label(text("ui.debug.no_logs"), "muted")).left().row()
        entries.takeLast(200).forEach { list.add(Label(it, context.ui.skin, if (it.startsWith("E")) "negative" else "small")).left().row() }
        content.add(ui.button(testId("write_sample"), text("ui.debug.write_log"), "secondary") {
            Gdx.app.log("debug", "sample log line at ${context.services.clock.nowMillis()}")
            rebuild()
        }).left().padBottom(Tokens.SPACE_S).row()
        content.add(ui.scroll(list, testId("entries"))).grow()
    }
}

class DebugLocalePreviewScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_LOCALE_PREVIEW, context, args) {
    override val titleKey = "ui.debug.locale_preview"

    override fun body(content: Table) {
        val tables = context.services.content.localization
        val vi = tables["vi"].orEmpty()
        val en = tables["en"].orEmpty()
        val risky = vi.keys.filter { key -> en[key] == null || (en[key]?.length ?: 0) > vi.getValue(key).length * 3 / 2 + 6 }
        content.add(ui.label(text("ui.debug.locale_summary", vi.size, en.size, risky.size), "heading", testId("summary"))).left().row()
        val list = Table().top().left()
        risky.take(150).forEach { key ->
            list.add(Label(key, context.ui.skin, "small")).left().padRight(Tokens.SPACE_M)
            list.add(Label(vi.getValue(key).take(40), context.ui.skin, "small")).left().padRight(Tokens.SPACE_M)
            list.add(Label(en[key]?.take(40) ?: text("ui.debug.missing"), context.ui.skin, if (en[key] == null) "negative" else "small")).left().row()
        }
        content.add(ui.scroll(list, testId("keys"))).grow()
    }
}

class DebugAutomationScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.DEBUG_DEBUG_AUTOMATION, context, args) {
    override val titleKey = "ui.debug.automation"

    override fun body(content: Table) {
        val flavor = context.services.flavor
        content.add(ui.label(text("ui.debug.automation_state", flavor.name, if (flavor.automation) text("ui.settings.on") else text("ui.settings.off")), "heading", testId("state"))).left().row()
        content.add(ui.label(text("ui.debug.automation_help"), "body", wrap = true)).width(800f).left().padTop(Tokens.SPACE_S).row()
        content.add(ui.label(text("ui.debug.registered", context.navigator.registered.size), "muted")).left().padTop(Tokens.SPACE_S)
    }
}
