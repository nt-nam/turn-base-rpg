package com.pxworld.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.pxworld.application.CloudResult
import com.pxworld.application.CloudSync
import com.pxworld.application.TelemetryBuffer
import com.pxworld.application.TelemetryEvent
import com.pxworld.application.TelemetryMapping
import com.pxworld.client.automation.StageAutomationDriver
import com.pxworld.client.core.HttpCloudGateway
import com.pxworld.client.core.PreferencesCredentialStore
import com.pxworld.client.core.AppPreferences
import com.pxworld.client.core.AssetService
import com.pxworld.client.core.AudioDirector
import com.pxworld.client.core.LogBuffer
import com.pxworld.client.core.GameApi
import com.pxworld.client.core.GameServices
import com.pxworld.client.core.GameSession
import com.pxworld.client.core.Localization
import com.pxworld.client.navigation.Navigator
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.DefaultScreens
import com.pxworld.client.ui.Tokens
import com.pxworld.client.ui.UiKit
import com.pxworld.screens.GameScreenId

class GameApp(private val services: GameServices) : ApplicationAdapter(), GameApi {

    private lateinit var batch: SpriteBatch
    private lateinit var stage: Stage
    private lateinit var assets: AssetService
    private lateinit var ui: UiKit
    private lateinit var navigator: Navigator
    lateinit var context: ScreenContext
        private set
    lateinit var automation: StageAutomationDriver
        private set
    private var unsubscribe: (() -> Unit)? = null
    private lateinit var logs: LogBuffer
    private lateinit var audio: AudioDirector
    private var playTimeAccumulator = 0f
    private var cloud: CloudSync? = null
    private val telemetry = TelemetryBuffer()
    private var telemetryAccumulator = 0f
    private var telemetryInFlight = false

    override fun create() {
        batch = SpriteBatch()
        stage = Stage(ExtendViewport(Tokens.VIRTUAL_WIDTH, Tokens.VIRTUAL_HEIGHT), batch)
        assets = AssetService(services.content.assetMap)
        ui = UiKit(font("font:title"), font("font:body"), font("font:small"))
        navigator = Navigator(stage, DefaultScreens.registry())
        val session = GameSession(services)
        logs = LogBuffer(Gdx.app.applicationLogger).also { Gdx.app.applicationLogger = it }
        val preferences = AppPreferences.open(PREFERENCES)
        val localization = Localization(services.content.localization, preferences.locale?.takeIf { it in services.content.localization } ?: Localization.FALLBACK)
        audio = AudioDirector(services.content.audioCues, services.content.assetMap)
        cloud = services.cloudUrl?.let { url ->
            CloudSync(HttpCloudGateway(url, { Gdx.app.postRunnable(it) }), services.saves, PreferencesCredentialStore(Gdx.app.getPreferences(PreferencesCredentialStore.preferencesFor(url))), { it() })
        }
        context = ScreenContext(services, assets, localization, ui, navigator, session, batch, preferences, logs, audio, cloud = cloud)
        navigator.context = context
        automation = StageAutomationDriver(this, stage, context)
        Gdx.input.inputProcessor = InputMultiplexer(stage, object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
                    navigator.back()
                    return true
                }
                if (keycode == Input.Keys.F1 && services.flavor.debugTools) {
                    navigator.open(GameScreenId.DEBUG_DEBUG_MENU)
                    return true
                }
                return false
            }
        })
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        navigator.reset(GameScreenId.BOOT_SPLASH)
        services.onReady(this)
    }

    private fun font(key: String) = Gdx.files.internal(assets.resolve(key))

    fun watchStore() {
        unsubscribe?.invoke()
        unsubscribe = context.session.store?.subscribe { state, events ->
            events.mapNotNull(TelemetryMapping::of).forEach(::track)
            navigator.broadcast(state, events)
        }
    }

    private var watchedStore: Any? = null

    override fun render() {
        val store = context.session.store
        if (store !== watchedStore) {
            watchedStore = store
            watchStore()
            store?.let {
                ui.applyTextScale(it.state.settings.textScalePercent)
                track(TelemetryEvent("session.start", mapOf("flavor" to services.flavor.name.lowercase())))
            }
        }
        val delta = Gdx.graphics.deltaTime.coerceAtMost(MAX_FRAME_SECONDS)
        if (store != null) {
            playTimeAccumulator += delta
            if (playTimeAccumulator >= PLAY_TIME_FLUSH_SECONDS) {
                val seconds = playTimeAccumulator.toLong()
                playTimeAccumulator -= seconds
                store.update { services.collection.addPlayTime(it, seconds) }
            }
        }
        val settings = store?.state?.settings
        audio.configure(settings?.musicEnabled ?: true, settings?.soundEnabled ?: true)
        audio.playMusic(musicFor(navigator.visibleScreens().firstOrNull()?.id))
        ScreenUtils.clear(Tokens.background)
        navigator.update(delta)
        navigator.renderWorld(delta)
        stage.viewport.apply()
        stage.act(delta)
        stage.draw()
        automation.drainRenderThreadWork()
        flushTelemetry(delta)
    }

    private fun track(event: TelemetryEvent) {
        if (cloud != null && context.preferences.analyticsConsent) telemetry.record(event)
    }

    private fun flushTelemetry(delta: Float) {
        val sync = cloud ?: return
        telemetryAccumulator += delta
        if (telemetryAccumulator < TELEMETRY_FLUSH_SECONDS || telemetryInFlight || telemetry.size == 0) return
        telemetryAccumulator = 0f
        val batch = telemetry.drain(TELEMETRY_BATCH)
        telemetryInFlight = true
        sync.report(services.clientVersion, batch) { result ->
            telemetryInFlight = false
            if (result !is CloudResult.Ok) telemetry.restore(batch)
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        navigator.resize(width, height)
    }

    private fun musicFor(screen: GameScreenId?): String? = when {
        screen == null -> null
        screen.module == "battle" -> AudioDirector.BATTLE_MUSIC
        else -> AudioDirector.WORLD_MUSIC
    }

    override fun dispose() {
        audio.dispose()
        unsubscribe?.invoke()
        stage.dispose()
        batch.dispose()
        ui.dispose()
        assets.dispose()
    }

    override fun onRenderThread(block: () -> Unit) {
        Gdx.app.postRunnable(block)
    }

    companion object {
        const val MAX_FRAME_SECONDS: Float = 1f / 20f
        const val PLAY_TIME_FLUSH_SECONDS: Float = 60f
        const val PREFERENCES: String = "pxworld"
        const val TELEMETRY_FLUSH_SECONDS: Float = 30f
        const val TELEMETRY_BATCH: Int = 50
    }
}
