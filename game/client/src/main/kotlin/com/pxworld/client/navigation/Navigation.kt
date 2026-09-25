package com.pxworld.client.navigation

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.pxworld.application.GameEvent
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.GameState
import com.pxworld.screens.GameScreenId

enum class Presentation { FULL, MODAL }

data class ScreenArgs(val values: Map<String, String> = emptyMap()) {
    operator fun get(key: String): String = values[key] ?: throw IllegalArgumentException("missing screen argument $key")
    fun optional(key: String): String? = values[key]

    companion object {
        val EMPTY = ScreenArgs()
        fun of(vararg pairs: Pair<String, String>) = ScreenArgs(pairs.toMap())
    }
}

abstract class GameScreen(val id: GameScreenId, protected val context: ScreenContext, protected val args: ScreenArgs) {

    open val presentation: Presentation = Presentation.FULL

    val root: Table = Table().apply {
        setFillParent(true)
        name = id.id
    }

    protected val text get() = context.text
    protected val ui get() = context.widgets

    abstract fun build(content: Table)

    fun rebuild() {
        root.clearChildren()
        root.background = if (presentation == Presentation.MODAL) context.ui.tinted(Tokens.scrim) else null
        root.touchable = Touchable.enabled
        build(root)
    }

    open fun onShow() {}
    open fun onHide() {}
    open fun update(delta: Float) {}
    open fun renderWorld(delta: Float) {}
    open fun resize(width: Int, height: Int) {}
    open fun onStateChanged(state: GameState, events: List<GameEvent>) = rebuild()
    open fun dispose() {}

    protected fun testId(element: String): String = "${id.id}/$element"
}

typealias ScreenFactory = (ScreenContext, ScreenArgs) -> GameScreen

class ScreenRegistry(private val factories: Map<GameScreenId, ScreenFactory>) {
    val registered: Set<GameScreenId> get() = factories.keys

    fun create(id: GameScreenId, context: ScreenContext, args: ScreenArgs): GameScreen {
        val factory = factories[id] ?: throw IllegalArgumentException("screen ${id.id} is not implemented yet")
        return factory(context, args)
    }
}

class Navigator(private val stage: Stage, private val registry: ScreenRegistry) {

    lateinit var context: ScreenContext
    private val stack = mutableListOf<GameScreen>()
    private val toastLayer = Table().apply { setFillParent(true); touchable = Touchable.disabled; bottom().padBottom(150f) }

    val current: GameScreen? get() = stack.lastOrNull()
    val stackIds: List<String> get() = stack.map { it.id.id }
    val registered: Set<GameScreenId> get() = registry.registered

    fun reset(id: GameScreenId, args: ScreenArgs = ScreenArgs.EMPTY) {
        stack.toList().forEach { it.onHide(); it.dispose() }
        stack.clear()
        push(id, args)
    }

    fun open(id: GameScreenId, args: ScreenArgs = ScreenArgs.EMPTY) = push(id, args)

    fun replace(id: GameScreenId, args: ScreenArgs = ScreenArgs.EMPTY) {
        stack.removeLastOrNull()?.let { it.onHide(); it.dispose() }
        push(id, args)
    }

    fun replaceFrom(screen: GameScreen, id: GameScreenId, args: ScreenArgs = ScreenArgs.EMPTY): Boolean {
        val index = stack.indexOf(screen)
        if (index < 0) return false
        while (stack.size > index) stack.removeLast().let { it.onHide(); it.dispose() }
        push(id, args)
        return true
    }

    fun back() {
        if (stack.size <= 1) return
        stack.removeLast().let { it.onHide(); it.dispose() }
        stack.lastOrNull()?.rebuild()
        attachVisible()
    }

    fun backTo(id: GameScreenId) {
        while (stack.size > 1 && stack.last().id != id) stack.removeLast().let { it.onHide(); it.dispose() }
        stack.lastOrNull()?.rebuild()
        attachVisible()
    }

    fun visibleScreens(): List<GameScreen> {
        val baseIndex = stack.indexOfLast { it.presentation == Presentation.FULL }.coerceAtLeast(0)
        return stack.drop(baseIndex)
    }

    fun broadcast(state: GameState, events: List<GameEvent>) {
        visibleScreens().forEach { it.onStateChanged(state, events) }
    }

    fun rebuildAll() {
        stack.forEach { it.rebuild() }
    }

    fun toast(message: String, positive: Boolean = true) {
        val label = Label(message, context.ui.skin, if (positive) "body" else "negative")
        val bubble = Container(label).apply {
            background = context.ui.tinted(Tokens.surfaceRaised, rounded = true)
            pad(Tokens.SPACE_S, Tokens.SPACE_M, Tokens.SPACE_S, Tokens.SPACE_M)
            name = "toast"
        }
        label.setAlignment(Align.center)
        toastLayer.clearChildren()
        toastLayer.add(bubble)
        bubble.addAction(Actions.sequence(Actions.delay(TOAST_SECONDS), Actions.fadeOut(0.3f), Actions.removeActor()))
        toastLayer.toFront()
    }

    fun update(delta: Float) {
        visibleScreens().forEach { it.update(delta) }
    }

    fun renderWorld(delta: Float) {
        visibleScreens().firstOrNull()?.renderWorld(delta)
    }

    fun resize(width: Int, height: Int) {
        stack.forEach { it.resize(width, height) }
    }

    private fun push(id: GameScreenId, args: ScreenArgs) {
        val screen = registry.create(id, context, args)
        stack += screen
        screen.rebuild()
        screen.onShow()
        attachVisible()
    }

    private fun attachVisible() {
        stage.root.clearChildren()
        visibleScreens().forEach { stage.addActor(it.root) }
        stage.addActor(toastLayer)
        stage.keyboardFocus = null
    }

    companion object {
        const val TOAST_SECONDS: Float = 1.8f
    }
}
