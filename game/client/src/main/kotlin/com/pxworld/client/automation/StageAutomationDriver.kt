package com.pxworld.client.automation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Base64Coder
import com.pxworld.client.GameApp
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.battle.BattleMainScreen
import com.pxworld.client.screens.world.WorldExploreScreen
import com.pxworld.domain.battle.BattleCommand
import com.pxworld.domain.battle.BattleSide
import com.pxworld.domain.battle.UnitId
import com.pxworld.screens.GameScreenId
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

data class UiNode(
    val testId: String,
    val type: String,
    val text: String?,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val enabled: Boolean,
)

class AutomationFailure(message: String) : IllegalStateException(message)

class StageAutomationDriver(private val app: GameApp, private val stage: Stage, private val context: ScreenContext) {

    private val work = ConcurrentLinkedQueue<() -> Unit>()

    fun drainRenderThreadWork() {
        while (true) work.poll()?.invoke() ?: break
    }

    fun <T> call(block: () -> T): T {
        val future = CompletableFuture<T>()
        work += {
            try {
                future.complete(block())
            } catch (failure: Throwable) {
                future.completeExceptionally(failure)
            }
        }
        return try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } catch (wrapped: java.util.concurrent.ExecutionException) {
            throw wrapped.cause ?: wrapped
        }
    }

    fun session(): Map<String, Any?> = call {
        val state = context.session.store?.state
        mapOf(
            "flavor" to context.services.flavor.name,
            "locale" to context.text.locale,
            "screen" to context.navigator.current?.id?.id,
            "stack" to context.navigator.stackIds,
            "slot" to context.session.store?.slot,
            "player" to state?.profile?.name,
            "music" to context.audio.playingCue,
            "musicEnabled" to context.audio.musicEnabled,
            "width" to Gdx.graphics.width,
            "height" to Gdx.graphics.height,
        )
    }

    fun exit() = call {
        Gdx.app.exit()
        true
    }

    fun resetFirstRun() = call {
        context.preferences.legalAccepted = false
        context.preferences.privacyAnswered = false
        context.preferences.locale = null
        context.session.end()
        context.navigator.reset(GameScreenId.BOOT_SPLASH)
        true
    }

    fun replays(): List<Map<String, Any?>> = call {
        context.services.replays.list().map { mapOf("id" to it.id, "encounter" to it.encounterId, "outcome" to it.outcome.name, "commands" to it.commands.size) }
    }

    fun registeredScreens(): List<String> = call { context.navigator.registered.map { it.id }.sorted() }

    fun tree(): List<UiNode> = call { treeNow() }

    private fun treeNow(): List<UiNode> {
        val nodes = mutableListOf<UiNode>()
        fun visit(actor: Actor) {
            if (!actor.isVisible) return
            val name = actor.name
            if (name != null && name.contains('/')) {
                val position = actor.localToStageCoordinates(Vector2(0f, 0f))
                nodes += UiNode(
                    testId = name,
                    type = actor.javaClass.simpleName,
                    text = when (actor) {
                        is TextButton -> actor.text.toString()
                        is Label -> actor.text.toString()
                        is TextField -> actor.text
                        else -> null
                    },
                    x = position.x,
                    y = position.y,
                    width = actor.width,
                    height = actor.height,
                    enabled = actor.touchable != Touchable.disabled && (actor !is Button || !actor.isDisabled),
                )
            }
            if (actor is Group) actor.children.forEach(::visit)
        }
        stage.root.children.forEach(::visit)
        return nodes
    }

    fun tap(testId: String): Boolean {
        call {
            val actor = find(testId) ?: throw AutomationFailure("no visible actor $testId")
            if (actor is Button && actor.isDisabled) throw AutomationFailure("$testId is disabled")
            scrollIntoView(actor)
        }
        return call { touch(testId) }
    }

    private fun touch(testId: String): Boolean {
        val actor = find(testId) ?: throw AutomationFailure("no visible actor $testId")
        val center = actor.localToStageCoordinates(Vector2(actor.width / 2, actor.height / 2))
        val hit = stage.hit(center.x, center.y, true)
        if (hit == null || !(hit == actor || hit.isDescendantOf(actor))) throw AutomationFailure("$testId is covered by ${hit?.name ?: hit?.javaClass?.simpleName}")
        val screen = stage.stageToScreenCoordinates(Vector2(center))
        stage.touchDown(screen.x.toInt(), screen.y.toInt(), 0, Input.Buttons.LEFT)
        stage.touchUp(screen.x.toInt(), screen.y.toInt(), 0, Input.Buttons.LEFT)
        return true
    }

    fun type(testId: String, value: String) = call {
        val field = find(testId) as? TextField ?: throw AutomationFailure("$testId is not a text field")
        field.text = value
        field.setCursorPosition(value.length)
        true
    }

    fun open(screenId: String, arguments: Map<String, String>) = call {
        context.navigator.open(GameScreenId.fromId(screenId), ScreenArgs(arguments))
        context.navigator.current?.id?.id
    }

    fun reset(screenId: String) = call {
        context.navigator.reset(GameScreenId.fromId(screenId))
        true
    }

    fun back() = call {
        context.navigator.back()
        context.navigator.current?.id?.id
    }

    fun worldInfo(): Map<String, Any?> = call {
        val world = context.navigator.visibleScreens().firstOrNull() as? WorldExploreScreen ?: throw AutomationFailure("not exploring the world")
        val position = world.playerPosition
        mapOf("map" to world.activeMapId, "x" to position?.x, "y" to position?.y) + world.triggers()
    }

    fun moveTo(x: Float, y: Float) = call {
        val world = context.navigator.visibleScreens().firstOrNull() as? WorldExploreScreen ?: throw AutomationFailure("not exploring the world")
        world.steerTo(x, y)
        true
    }

    fun battleInfo(): Map<String, Any?> = call {
        val battle = context.navigator.current as? BattleMainScreen ?: throw AutomationFailure("no battle on screen")
        val state = battle.battleState
        mapOf(
            "round" to state.round,
            "active" to state.activeUnit?.toString(),
            "awaitingPlayer" to battle.awaitingPlayer,
            "outcome" to state.outcome?.name,
            "units" to state.combatants.map { mapOf("id" to it.id.toString(), "name" to it.setup.name, "hp" to it.hp, "maxHp" to it.maxHp, "energy" to it.energy) },
            "commands" to battle.legalCommands().map { mapOf("skill" to it.skillId, "target" to it.target?.toString()) },
        )
    }

    fun battleCommand(skillId: String, target: String?) = call {
        val battle = context.navigator.current as? BattleMainScreen ?: throw AutomationFailure("no battle on screen")
        val actor = battle.battleState.activeUnit ?: throw AutomationFailure("no active unit")
        battle.issue(BattleCommand(actor, skillId, target?.let(::parseUnit)))
        true
    }

    fun battleAuto(enabled: Boolean) = call {
        val battle = context.navigator.current as? BattleMainScreen ?: throw AutomationFailure("no battle on screen")
        battle.setAuto(enabled)
        true
    }

    fun gameState(): Map<String, Any?> = call {
        val state = context.session.store?.state ?: return@call mapOf("loaded" to false)
        mapOf(
            "loaded" to true,
            "name" to state.profile.name,
            "level" to state.profile.level,
            "map" to state.position.mapId,
            "balances" to state.wallet.balances,
            "heroes" to state.heroes.map { mapOf("instance" to it.instanceId, "hero" to it.heroId, "level" to it.level, "star" to it.star) },
            "lineup" to state.lineup.cells.map { (cell, hero) -> mapOf("lane" to cell.lane, "depth" to cell.depth, "hero" to hero) },
            "items" to state.inventory.items,
            "equipment" to state.inventory.equipment.map { mapOf("instance" to it.instanceId, "equipment" to it.equipmentId, "equippedBy" to it.equippedBy) },
            "quests" to state.quests.map { mapOf("quest" to it.questId, "progress" to it.progress, "completed" to it.completed, "claimed" to it.claimed) },
            "checkinDays" to state.checkin.claimedDays,
            "counters" to state.stats.counters,
            "ledgerSize" to state.ledgerTail.size,
        )
    }

    fun invariants(): List<String> = call {
        val state = context.session.store?.state ?: return@call emptyList()
        val problems = mutableListOf<String>()
        state.wallet.balances.forEach { (currency, balance) -> if (balance < 0) problems += "negative $currency" }
        state.ledgerTail.groupBy { it.currency }.forEach { (currency, entries) ->
            if (entries.last().balanceAfter != state.wallet.balance(currency)) problems += "ledger tail for $currency disagrees with wallet"
        }
        val missing = treeNow().filter { it.text != null && it.text.startsWith("ui.") }
        missing.forEach { problems += "untranslated key ${it.text} at ${it.testId}" }
        problems
    }

    fun screenshot(target: String?): Any = call {
        val pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
        try {
            if (target == null) inlinePng(pixmap) else writePng(pixmap, target)
        } finally {
            pixmap.dispose()
        }
    }

    private fun writePng(pixmap: Pixmap, target: String): String {
        val handle = Gdx.files.absolute(java.nio.file.Paths.get(target).toAbsolutePath().toString())
        PixmapIO.writePNG(handle, pixmap, PNG_COMPRESSION, true)
        return handle.path()
    }

    private fun inlinePng(pixmap: Pixmap): Map<String, Any> {
        val bytes = ByteArrayOutputStream()
        val encoder = PixmapIO.PNG((pixmap.width * pixmap.height * 1.5f).toInt())
        try {
            encoder.setFlipY(true)
            encoder.setCompression(PNG_COMPRESSION)
            encoder.write(bytes, pixmap)
        } finally {
            encoder.dispose()
        }
        return mapOf("format" to "png", "width" to pixmap.width, "height" to pixmap.height, "base64" to String(Base64Coder.encode(bytes.toByteArray())))
    }

    private fun scrollIntoView(actor: Actor) {
        var child: Actor = actor
        var parent = actor.parent
        while (parent != null) {
            if (parent is ScrollPane) {
                val widget = parent.actor ?: break
                val corner = actor.localToActorCoordinates(widget, Vector2(0f, 0f))
                parent.scrollTo(corner.x, corner.y, actor.width, actor.height, true, true)
                parent.updateVisualScroll()
                parent.validate()
            }
            child = parent
            parent = parent.parent
        }
        stage.act(0f)
    }

    private fun find(testId: String): Actor? {
        var found: Actor? = null
        fun visit(actor: Actor) {
            if (found != null || !actor.isVisible) return
            if (actor.name == testId) found = actor
            if (actor is Group) actor.children.forEach(::visit)
        }
        stage.root.children.forEach(::visit)
        return found
    }

    private fun parseUnit(text: String): UnitId {
        val (side, slot) = text.split("#")
        return UnitId(BattleSide.valueOf(side.uppercase()), slot.toInt())
    }

    companion object {
        const val TIMEOUT_SECONDS: Long = 10
        const val PNG_COMPRESSION: Int = 6
    }
}
