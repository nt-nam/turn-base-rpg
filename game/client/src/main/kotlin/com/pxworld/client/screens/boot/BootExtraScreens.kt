package com.pxworld.client.screens.boot

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.TextPageScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId

object FirstRun {
    fun next(context: ScreenContext): GameScreenId = when {
        !context.preferences.legalAccepted -> GameScreenId.BOOT_LEGAL_NOTICE
        !context.preferences.privacyAnswered -> GameScreenId.BOOT_PRIVACY_CONSENT
        context.preferences.locale == null -> GameScreenId.BOOT_LANGUAGE_PICK
        else -> GameScreenId.BOOT_MAIN_MENU
    }
}

class LegalNoticeScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BOOT_LEGAL_NOTICE, context, args) {
    override val titleKey = "ui.legal.title"
    override val showBack = false

    override fun body(content: Table) {
        content.add(ui.label(text("ui.legal.body"), "body", testId("body"), wrap = true)).width(900f).row()
        content.add(ui.button(testId("accept"), text("ui.legal.accept")) {
            context.preferences.legalAccepted = true
            context.navigator.reset(FirstRun.next(context))
        }).width(320f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_L)
    }
}

class PrivacyConsentScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BOOT_PRIVACY_CONSENT, context, args) {
    override val titleKey = "ui.settings.privacy"
    override val showBack = false

    override fun body(content: Table) {
        content.add(ui.label(text("ui.privacy.explain"), "body", wrap = true)).width(900f).colspan(2).row()
        listOf(true to "ui.privacy.allow", false to "ui.privacy.deny").forEach { (allow, key) ->
            content.add(ui.button(testId(if (allow) "allow" else "deny"), text(key), if (allow) "primary" else "secondary") {
                context.preferences.analyticsConsent = allow
                context.preferences.privacyAnswered = true
                context.navigator.reset(FirstRun.next(context))
            }).width(300f).height(Tokens.BUTTON_HEIGHT).pad(Tokens.SPACE_L, Tokens.SPACE_S, 0f, Tokens.SPACE_S)
        }
    }
}

class LanguagePickScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BOOT_LANGUAGE_PICK, context, args) {
    override val titleKey = "ui.settings.language"
    override val showBack = false

    override fun body(content: Table) {
        context.text.availableLocales.forEach { locale ->
            content.add(ui.button(testId("language/$locale"), text("ui.language.$locale"), "secondary") {
                context.preferences.locale = locale
                context.text.switchTo(locale)
                context.navigator.reset(FirstRun.next(context))
            }).width(320f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class PatchNotesScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.BOOT_PATCH_NOTES, context, args) {
    override val titleKey = "ui.patch.title"
    override val sections = listOf("battle", "heroes", "economy", "saves", "tools").map { "ui.patch.$it.title" to "ui.patch.$it.body" }
}

class OfflineModeScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BOOT_OFFLINE_MODE, context, args) {
    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.offline.title"), "title")).row()
        content.add(ui.label(text("ui.offline.body"), "body", testId("body"), wrap = true)).width(520f).pad(Tokens.SPACE_M).row()
        content.add(ui.button(testId("close"), text("ui.common.close")) { context.navigator.back() })
    }
}
