package com.pxworld.client.screens.boot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.GameScreen
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.GameState
import com.pxworld.screens.GameScreenId

class SplashScreen(context: ScreenContext, args: ScreenArgs) : GameScreen(GameScreenId.BOOT_SPLASH, context, args) {

    private var elapsed = 0f

    override fun build(content: Table) {
        content.background = context.ui.tinted(Tokens.background)
        content.add(ui.label(text("ui.game.title"), "title", testId("title"))).row()
        content.add(ui.label(text("ui.game.subtitle"), "muted")).padTop(Tokens.SPACE_S)
    }

    override fun update(delta: Float) {
        elapsed += delta
        if (elapsed >= DURATION) context.navigator.reset(FirstRun.next(context))
    }

    companion object {
        const val DURATION: Float = 0.8f
    }
}

class MainMenuScreen(context: ScreenContext, args: ScreenArgs) : GameScreen(GameScreenId.BOOT_MAIN_MENU, context, args) {

    override fun build(content: Table) {
        content.background = context.ui.tinted(Tokens.background)
        val slots = context.services.saves.slots()
        val menu = Table().defaults().width(360f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).table
        menu.add(ui.label(text("ui.game.title"), "title")).padBottom(Tokens.SPACE_L).row()
        if (slots.isNotEmpty()) {
            menu.add(ui.button(testId("continue"), text("ui.menu.continue")) { SlotLoading.load(context, slots.first()) }).row()
        }
        menu.add(ui.button(testId("new_game"), text("ui.menu.new_game"), if (slots.isEmpty()) "primary" else "secondary") {
            context.navigator.open(GameScreenId.ONBOARDING_HERO_CREATE_CLASS)
        }).row()
        menu.add(ui.button(testId("load"), text("ui.menu.load"), "secondary", enabled = slots.isNotEmpty()) {
            context.navigator.open(GameScreenId.BOOT_SLOT_LIST)
        }).row()
        val extras = Table()
        extras.add(ui.button(testId("settings"), text("ui.pause.settings"), "ghost") { context.navigator.open(GameScreenId.SETTINGS_SETTINGS_HOME) }).padRight(Tokens.SPACE_XS)
        extras.add(ui.button(testId("patch_notes"), text("ui.patch.title"), "ghost") { context.navigator.open(GameScreenId.BOOT_PATCH_NOTES) }).padRight(Tokens.SPACE_XS)
        extras.add(ui.button(testId("offline"), text("ui.offline.title"), "ghost") { context.navigator.open(GameScreenId.BOOT_OFFLINE_MODE) })
        menu.add(extras).width(560f).row()
        if (context.services.flavor.debugTools) {
            menu.add(ui.button(testId("debug"), text("ui.menu.debug"), "ghost") { context.navigator.open(GameScreenId.DEBUG_DEBUG_MENU) }).row()
        }
        menu.add(ui.button(testId("exit"), text("ui.menu.exit"), "ghost") { Gdx.app.exit() }).row()
        content.add(menu)
    }
}

object SlotLoading {

    fun load(context: ScreenContext, slot: String) {
        val state = try {
            context.services.saves.load(slot)
        } catch (failure: IllegalStateException) {
            context.navigator.toast(context.text("ui.slots.corrupt", slot), positive = false)
            return
        }
        start(context, slot, state)
    }

    fun start(context: ScreenContext, slot: String, state: GameState) {
        context.session.start(slot, state)
        context.text.switchTo(state.settings.locale.takeIf { it in context.text.availableLocales } ?: context.text.locale)
        context.navigator.reset(GameScreenId.WORLD_WORLD_EXPLORE)
    }

    fun slotNameFor(playerName: String, existing: List<String>): String {
        val base = java.text.Normalizer.normalize(playerName.lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "")
            .replace('đ', 'd')
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifEmpty { "player" }
            .take(24)
        var candidate = base
        var suffix = 2
        while (candidate in existing) candidate = "${base}_${suffix++}"
        return candidate
    }
}

class SlotListScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BOOT_SLOT_LIST, context, args) {

    override val titleKey = "ui.slots.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val list = Table().top()
        context.services.saves.slots().forEach { slot ->
            val row = ui.panel()
            val summary = runCatching { context.services.saves.load(slot) }.getOrNull()
            if (summary == null) {
                row.add(ui.label(text("ui.slots.corrupt", slot), "negative")).expandX().left()
            } else {
                val info = Table()
                info.add(ui.label(summary.profile.name, "heading")).left().row()
                info.add(ui.label(text("ui.slots.summary", summary.profile.level, lookup.mapName(summary.position.mapId), summary.heroes.size), "muted")).left()
                row.add(info).expandX().left()
                row.add(ui.button(testId("play/$slot"), text("ui.slots.play")) { SlotLoading.start(context, slot, summary) }).padLeft(Tokens.SPACE_S)
            }
            row.add(ui.button(testId("delete/$slot"), text("ui.slots.delete"), "danger") {
                context.navigator.open(GameScreenId.BOOT_SLOT_DELETE_CONFIRM, ScreenArgs.of("slot" to slot))
            }).padLeft(Tokens.SPACE_S)
            list.add(row).growX().padBottom(Tokens.SPACE_S).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}

class SlotDeleteConfirmScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BOOT_SLOT_DELETE_CONFIRM, context, args) {

    override fun dialog(content: Table) {
        val slot = args["slot"]
        content.add(ui.label(text("ui.slots.delete_title"), "title")).colspan(2).row()
        content.add(ui.label(text("ui.slots.delete_warning", slot), "body", wrap = true)).width(420f).colspan(2).pad(Tokens.SPACE_M, 0f, Tokens.SPACE_M, 0f).row()
        content.add(ui.button(testId("cancel"), text("ui.common.cancel"), "secondary") { context.navigator.back() }).growX().padRight(Tokens.SPACE_S)
        content.add(ui.button(testId("confirm"), text("ui.slots.delete"), "danger") {
            context.services.saves.delete(slot)
            context.navigator.toast(text("ui.slots.deleted", slot))
            context.navigator.reset(GameScreenId.BOOT_MAIN_MENU)
        }).growX()
    }
}
