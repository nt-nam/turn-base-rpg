package com.pxworld.client.screens.settings

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.Transition
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId

class SettingsHomeScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.SETTINGS_SETTINGS_HOME, context, args) {

    override val titleKey = "ui.settings.title"

    override fun body(content: Table) {
        val inGame = context.session.store != null
        val hub = Table()
        listOf(
            Triple("audio", GameScreenId.SETTINGS_SETTINGS_AUDIO, true),
            Triple("graphics", GameScreenId.SETTINGS_SETTINGS_GRAPHICS, false),
            Triple("controls", GameScreenId.SETTINGS_SETTINGS_CONTROLS, false),
            Triple("language_page", GameScreenId.SETTINGS_SETTINGS_LANGUAGE, false),
            Triple("accessibility", GameScreenId.SETTINGS_SETTINGS_ACCESSIBILITY, true),
            Triple("privacy", GameScreenId.SETTINGS_SETTINGS_PRIVACY, false),
            Triple("playtime_report", GameScreenId.SETTINGS_PLAYTIME_REPORT, true),
            Triple("help_center", GameScreenId.SETTINGS_HELP_CENTER, false),
            Triple("faq", GameScreenId.SETTINGS_FAQ, false),
            Triple("bug_report", GameScreenId.SETTINGS_BUG_REPORT, false),
            Triple("credits", GameScreenId.SETTINGS_CREDITS, false),
        ).forEachIndexed { index, (key, target, needsGame) ->
            hub.add(ui.button(testId("open/$key"), text("ui.settings.$key"), "secondary", enabled = inGame || !needsGame) { context.navigator.open(target) })
                .width(260f).height(Tokens.BUTTON_HEIGHT).pad(Tokens.SPACE_XS)
            if (index % 3 == 2) hub.row()
        }
        content.add(hub).colspan(2).padBottom(Tokens.SPACE_L).row()
        if (!inGame) return
        val settings = context.state.settings
        fun toggleRow(key: String, value: Boolean, change: (Boolean) -> Unit) {
            content.add(ui.label(text("ui.settings.$key"), "body")).width(260f).left()
            content.add(ui.button(testId(key), text(if (value) "ui.settings.on" else "ui.settings.off"), if (value) "primary" else "secondary") { change(!value) })
                .width(140f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        }
        toggleRow("music", settings.musicEnabled) { enabled -> context.act { Transition(it.copy(settings = it.settings.copy(musicEnabled = enabled)), emptyList()) } }
        toggleRow("sound", settings.soundEnabled) { enabled -> context.act { Transition(it.copy(settings = it.settings.copy(soundEnabled = enabled)), emptyList()) } }
        content.add(ui.label(text("ui.settings.language"), "body")).width(260f).left()
        val languages = Table()
        context.text.availableLocales.forEach { locale ->
            languages.add(ui.button(testId("language/$locale"), text("ui.language.$locale"), if (locale == context.text.locale) "tab-active" else "tab") {
                context.act { Transition(it.copy(settings = it.settings.copy(locale = locale)), emptyList()) }
                context.text.switchTo(locale)
                context.navigator.rebuildAll()
            }).padRight(Tokens.SPACE_XS)
        }
        content.add(languages).left().padBottom(Tokens.SPACE_S).row()
        content.add(ui.button(testId("main_menu"), text("ui.pause.main_menu"), "danger") {
            context.session.end()
            context.navigator.reset(GameScreenId.BOOT_MAIN_MENU)
        }).colspan(2).padTop(Tokens.SPACE_L).width(300f).height(Tokens.BUTTON_HEIGHT)
    }
}
