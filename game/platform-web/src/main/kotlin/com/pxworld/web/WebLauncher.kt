package com.pxworld.web

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.pxworld.application.InMemoryReplays
import com.pxworld.client.GameApp
import com.pxworld.client.core.BuildFlavor
import com.pxworld.client.core.GameClock
import com.pxworld.client.core.GameServices
import com.pxworld.content.ContentLoader

object BrowserClock : GameClock {
    override fun epochDay(): Long = Math.floorDiv(nowMillis() - timezoneOffsetMinutes() * MILLIS_PER_MINUTE, MILLIS_PER_DAY)
    override fun nowMillis(): Long = System.currentTimeMillis()

    private const val MILLIS_PER_MINUTE: Long = 60_000L
    private const val MILLIS_PER_DAY: Long = 86_400_000L
}

class WebGame(private val cloudUrl: String?) : ApplicationListener {

    private var game: GameApp? = null

    override fun create() {
        val content = ContentLoader.decodePack(Gdx.files.internal(CONTENT_PACK).readString(Charsets.UTF_8.name()))
        val services = GameServices(
            content = content,
            saves = BrowserSaveStore(Gdx.files.local(SAVE_DIRECTORY)),
            clock = BrowserClock,
            flavor = BuildFlavor.DEV,
            replays = InMemoryReplays(),
            onReady = { api -> BrowserAutomation(api as GameApp).publish() },
            cloudUrl = cloudUrl,
            clientVersion = CLIENT_VERSION,
        )
        game = GameApp(services).also { it.create() }
    }

    override fun resize(width: Int, height: Int) {
        game?.resize(width, height)
    }

    override fun render() {
        game?.render()
    }

    override fun pause() {
        game?.pause()
    }

    override fun resume() {
        game?.resume()
    }

    override fun dispose() {
        game?.dispose()
        game = null
    }

    companion object {
        const val CONTENT_PACK: String = "content-pack.json"
        const val SAVE_DIRECTORY: String = "saves"
        const val CLIENT_VERSION: String = "web-dev"
    }
}

fun main(args: Array<String>) {
    val configuration = TeaApplicationConfiguration("canvas").apply {
        width = 0
        height = 0
        storagePrefix = "pxworld"
        localStoragePrefix = "pxworld/local"
        preserveDrawingBuffer = true
    }
    TeaApplication(WebGame(queryParameter("api").ifBlank { null }), configuration)
}
