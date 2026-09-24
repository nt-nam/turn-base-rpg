package com.pxworld.client.screens.settings

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.Transition
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.ConfirmScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.TextPageScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.PlayerSettings
import com.pxworld.screens.GameScreenId
import java.io.File

private fun ScreenContext.updateSettings(change: (PlayerSettings) -> PlayerSettings) {
    act { state: GameState -> Transition(state.copy(settings = change(state.settings)), emptyList()) }
}

private fun StandardScreen.toggleRow(context: ScreenContext, content: Table, key: String, value: Boolean, testId: (String) -> String, change: (Boolean) -> Unit) {
    content.add(context.widgets.label(context.text("ui.settings.$key"), "body")).width(320f).left()
    content.add(context.widgets.button(testId(key), context.text(if (value) "ui.settings.on" else "ui.settings.off"), if (value) "primary" else "secondary") { change(!value) })
        .width(140f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
}

class SettingsAudioScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_SETTINGS_AUDIO, context, args) {
    override val titleKey = "ui.settings.audio"

    override fun body(content: Table) {
        val settings = context.state.settings
        toggleRow(context, content, "music", settings.musicEnabled, ::testId) { enabled -> context.updateSettings { it.copy(musicEnabled = enabled) } }
        toggleRow(context, content, "sound", settings.soundEnabled, ::testId) { enabled -> context.updateSettings { it.copy(soundEnabled = enabled) } }
    }
}

class SettingsGraphicsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_SETTINGS_GRAPHICS, context, args) {
    override val titleKey = "ui.settings.graphics"

    override fun body(content: Table) {
        val fullscreen = Gdx.graphics.isFullscreen
        content.add(ui.label(text("ui.settings.resolution", Gdx.graphics.width, Gdx.graphics.height), "muted")).colspan(2).padBottom(Tokens.SPACE_M).row()
        toggleRow(context, content, "fullscreen", fullscreen, ::testId) { enabled ->
            if (enabled) Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode) else Gdx.graphics.setWindowedMode(WINDOWED_WIDTH, WINDOWED_HEIGHT)
            rebuild()
        }
        content.add(ui.label(text("ui.settings.fps", Gdx.graphics.framesPerSecond), "muted", testId("fps"))).colspan(2).left()
    }

    companion object {
        const val WINDOWED_WIDTH: Int = 1280
        const val WINDOWED_HEIGHT: Int = 720
    }
}

class SettingsControlsScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.SETTINGS_SETTINGS_CONTROLS, context, args) {
    override val titleKey = "ui.settings.controls"
    override val sections = listOf("move", "back", "debug", "touch").map { "ui.controls.$it.title" to "ui.controls.$it.body" }
}

class SettingsLanguageScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_SETTINGS_LANGUAGE, context, args) {
    override val titleKey = "ui.settings.language"

    override fun body(content: Table) {
        context.text.availableLocales.forEach { locale ->
            content.add(ui.button(testId("language/$locale"), text("ui.language.$locale"), if (locale == context.text.locale) "tab-active" else "tab") {
                if (context.session.store != null) context.updateSettings { it.copy(locale = locale) }
                context.preferences.locale = locale
                context.text.switchTo(locale)
                context.navigator.rebuildAll()
            }).width(320f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_XS).row()
        }
    }
}

class SettingsAccessibilityScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_SETTINGS_ACCESSIBILITY, context, args) {
    override val titleKey = "ui.settings.accessibility"

    override fun body(content: Table) {
        val settings = context.state.settings
        content.add(ui.label(text("ui.settings.text_size"), "body")).width(320f).left()
        val sizes = Table()
        listOf(90, 100, 115, 130).forEach { percent ->
            sizes.add(ui.button(testId("text/$percent"), "$percent%", if (settings.textScalePercent == percent) "tab-active" else "tab") {
                context.updateSettings { it.copy(textScalePercent = percent) }
                context.ui.applyTextScale(percent)
                context.navigator.rebuildAll()
            }).padRight(Tokens.SPACE_XS)
        }
        content.add(sizes).left().padBottom(Tokens.SPACE_S).row()
        toggleRow(context, content, "reduced_motion", settings.reducedMotion, ::testId) { enabled -> context.updateSettings { it.copy(reducedMotion = enabled) } }
        content.add(ui.label(text("ui.settings.battle_speed"), "body")).width(320f).left()
        val speeds = Table()
        listOf(1, 2, 4).forEach { speed ->
            speeds.add(ui.button(testId("speed/$speed"), "x$speed", if (settings.battleSpeed == speed) "tab-active" else "tab") {
                context.updateSettings { it.copy(battleSpeed = speed) }
            }).padRight(Tokens.SPACE_XS)
        }
        content.add(speeds).left().row()
    }
}

class SettingsPrivacyScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_SETTINGS_PRIVACY, context, args) {
    override val titleKey = "ui.settings.privacy"

    override fun body(content: Table) {
        content.add(ui.label(text("ui.privacy.explain"), "body", wrap = true)).width(760f).colspan(2).padBottom(Tokens.SPACE_M).row()
        toggleRow(context, content, "analytics", context.preferences.analyticsConsent, ::testId) { enabled ->
            context.preferences.analyticsConsent = enabled
            if (context.session.store != null) context.updateSettings { it.copy(analyticsConsent = enabled) }
            rebuild()
        }
        content.add(ui.button(testId("download"), text("ui.settings.data_download"), "secondary") { context.navigator.open(GameScreenId.SETTINGS_DATA_DOWNLOAD) }).colspan(2).left().padTop(Tokens.SPACE_M).row()
        content.add(ui.button(testId("delete"), text("ui.settings.delete_account"), "danger") { context.navigator.open(GameScreenId.SETTINGS_DELETE_ACCOUNT) }).colspan(2).left().padTop(Tokens.SPACE_S)
    }
}

class DataDownloadScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_DATA_DOWNLOAD, context, args) {
    override val titleKey = "ui.settings.data_download"
    private var exported: String? = null

    override fun body(content: Table) {
        content.add(ui.label(text("ui.privacy.download_explain"), "body", wrap = true)).width(760f).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("export"), text("ui.privacy.export")) {
            val slot = context.store.slot
            val target = File(System.getProperty("user.home"), "pxworld-export-$slot.json")
            target.writeText(context.services.saves.export(slot))
            exported = target.absolutePath
            rebuild()
        }).width(280f).height(Tokens.BUTTON_HEIGHT).row()
        exported?.let { content.add(ui.label(text("ui.privacy.exported", it), "positive", testId("path"), wrap = true)).width(760f).padTop(Tokens.SPACE_M) }
    }
}

class DeleteAccountScreen(context: ScreenContext, args: ScreenArgs) : ConfirmScreen(GameScreenId.SETTINGS_DELETE_ACCOUNT, context, args) {
    override val titleText get() = text("ui.settings.delete_account")
    override val messageText get() = text("ui.slots.delete_warning", context.store.slot)
    override val confirmText get() = text("ui.slots.delete")
    override val confirmStyle = "danger"

    override fun confirm() {
        val slot = context.store.slot
        context.session.end()
        context.services.saves.delete(slot)
        context.navigator.reset(GameScreenId.BOOT_MAIN_MENU)
        context.navigator.toast(text("ui.slots.deleted", slot))
    }
}

class CreditsScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.SETTINGS_CREDITS, context, args) {
    override val titleKey = "ui.settings.credits"
    override val sections = listOf("team", "engine", "art", "fonts").map { "ui.credits.$it.title" to "ui.credits.$it.body" }
}

class HelpCenterScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.SETTINGS_HELP_CENTER, context, args) {
    override val titleKey = "ui.settings.help_center"
    override val sections = listOf("start", "battle", "heroes", "economy", "saves").map { "ui.help.$it.title" to "ui.help.$it.body" }
}

class FaqScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.SETTINGS_FAQ, context, args) {
    override val titleKey = "ui.settings.faq"
    override val sections = (1..5).map { "ui.faq.$it.q" to "ui.faq.$it.a" }
}

class BugReportScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_BUG_REPORT, context, args) {
    override val titleKey = "ui.settings.bug_report"
    private var draft = ""
    private var saved: String? = null

    override fun body(content: Table) {
        val field = ui.textField(testId("description"), draft, text("ui.bug.hint"))
        field.setTextFieldListener { textField, _ -> draft = textField.text }
        content.add(ui.label(text("ui.bug.explain"), "muted", wrap = true)).width(760f).padBottom(Tokens.SPACE_S).row()
        content.add(field).width(760f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        content.add(ui.button(testId("submit"), text("ui.bug.submit"), enabled = true) {
            val directory = File(System.getProperty("user.home"), ".pxworld/bug-reports").apply { mkdirs() }
            val file = File(directory, "report-${context.services.clock.nowMillis()}.txt")
            val body = buildString {
                appendLine("description: ${field.text}")
                appendLine("screen: ${context.navigator.stackIds}")
                appendLine("flavor: ${context.services.flavor}")
                context.session.store?.let { appendLine("slot: ${it.slot}") }
                appendLine("--- log ---")
                context.logs.entries.takeLast(LOG_LINES).forEach(::appendLine)
            }
            file.writeText(body)
            saved = file.absolutePath
            draft = ""
            rebuild()
        }).width(240f).height(Tokens.BUTTON_HEIGHT).row()
        saved?.let { content.add(ui.label(text("ui.bug.saved", it), "positive", testId("saved"), wrap = true)).width(760f).padTop(Tokens.SPACE_M) }
    }

    companion object {
        const val LOG_LINES: Int = 100
    }
}

class PlaytimeReportScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_PLAYTIME_REPORT, context, args) {
    override val titleKey = "ui.settings.playtime_report"

    override fun body(content: Table) {
        val seconds = context.state.journal.playSeconds
        content.add(ui.label(text("ui.playtime.total", seconds / 3600, seconds % 3600 / 60), "title", testId("total"))).colspan(2).padBottom(Tokens.SPACE_M).row()
        context.state.stats.counters.toSortedMap().forEach { (counter, value) ->
            content.add(ui.label(text("ui.counter.$counter"), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(value.toString(), "heading")).right().row()
        }
    }
}
