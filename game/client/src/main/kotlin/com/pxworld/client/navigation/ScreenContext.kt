package com.pxworld.client.navigation

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.pxworld.application.GameRuleViolation
import com.pxworld.application.GameStore
import com.pxworld.application.Transition
import com.pxworld.client.core.AppPreferences
import com.pxworld.client.core.AssetService
import com.pxworld.client.core.DebugFlags
import com.pxworld.client.core.LogBuffer
import com.pxworld.client.core.GameServices
import com.pxworld.client.core.GameSession
import com.pxworld.client.core.Localization
import com.pxworld.client.ui.UiKit
import com.pxworld.client.ui.Widgets
import com.pxworld.domain.progression.GameState

class ScreenContext(
    val services: GameServices,
    val assets: AssetService,
    val text: Localization,
    val ui: UiKit,
    val navigator: Navigator,
    val session: GameSession,
    val batch: SpriteBatch,
    val preferences: AppPreferences,
    val logs: LogBuffer,
    val debugFlags: DebugFlags = DebugFlags(),
) {
    val widgets: Widgets = Widgets(ui, text)
    val store: GameStore get() = session.requireStore
    val state: GameState get() = session.state

    fun act(successMessage: String? = null, action: (GameState) -> Transition): Transition? =
        try {
            store.dispatch(action).also { if (successMessage != null) navigator.toast(successMessage) }
        } catch (violation: GameRuleViolation) {
            navigator.toast(text("ui.error.rule", violation.message ?: ""), positive = false)
            null
        }
}
