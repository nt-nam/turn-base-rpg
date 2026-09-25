package com.pxworld.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.pxworld.automation.AutomationProtocol
import com.pxworld.automation.AutomationServer
import com.pxworld.client.GameApp
import com.pxworld.client.automation.StageAutomationDriver
import com.pxworld.client.core.BuildFlavor
import com.pxworld.client.core.GameClock
import com.pxworld.client.core.GameServices
import com.pxworld.content.ContentBundle
import com.pxworld.content.ContentLoader
import com.pxworld.infrastructure.legacy.LegacySaveMigration
import com.pxworld.infrastructure.legacy.LegacyV1Importer
import com.pxworld.infrastructure.replay.FileReplayStore
import com.pxworld.infrastructure.save.FileSaveStore
import java.io.File
import java.time.LocalDate

object SystemClock : GameClock {
    override fun epochDay(): Long = LocalDate.now().toEpochDay()
    override fun nowMillis(): Long = System.currentTimeMillis()
}

data class DesktopOptions(
    val flavor: BuildFlavor,
    val environment: String,
    val saveDirectory: File,
    val automationPort: Int?,
    val width: Int,
    val height: Int,
    val cloudUrl: String?,
) {
    companion object {
        fun fromEnvironment(read: (String) -> String? = System::getenv): DesktopOptions {
            val flavor = BuildFlavor.valueOf((read("PXWORLD_FLAVOR") ?: "DEV").uppercase())
            val environment = read("PXWORLD_ENV") ?: "local"
            val saves = read("PXWORLD_SAVE_DIR")?.let(::File) ?: File(System.getProperty("user.home"), ".pxworld/$environment/saves")
            val port = read("PXWORLD_AUTOMATION_PORT")?.toIntOrNull() ?: DEFAULT_AUTOMATION_PORT
            return DesktopOptions(
                flavor = flavor,
                environment = environment,
                saveDirectory = saves,
                automationPort = port.takeIf { flavor.automation && it > 0 },
                width = read("PXWORLD_WIDTH")?.toIntOrNull() ?: 1280,
                height = read("PXWORLD_HEIGHT")?.toIntOrNull() ?: 720,
                cloudUrl = read("PXWORLD_API_URL")?.takeIf { it.isNotBlank() && it != "off" } ?: DEFAULT_CLOUD_URL.takeIf { flavor == BuildFlavor.DEV && read("PXWORLD_API_URL") != "off" },
            )
        }

        const val DEFAULT_AUTOMATION_PORT: Int = 47017
        const val DEFAULT_CLOUD_URL: String = "http://localhost:8080"
    }
}

fun loadContentPack(): ContentBundle {
    val stream = DesktopOptions::class.java.getResourceAsStream("/content-pack.json") ?: throw IllegalStateException("content-pack.json missing from resources")
    return ContentLoader.decodePack(stream.bufferedReader(Charsets.UTF_8).use { it.readText() })
}

fun main() {
    val options = DesktopOptions.fromEnvironment()
    var driver: StageAutomationDriver? = null
    val server = options.automationPort?.let { port -> AutomationServer(port, AutomationProtocol { driver }).also { it.start() } }
    val content = loadContentPack()
    val saves = FileSaveStore(options.saveDirectory)
    val migration = LegacySaveMigration(LegacyV1Importer(content), saves)
        .run(listOf(File("data"), File("lwjgl3/data"), File("../data")), File(options.saveDirectory, ".legacy-imports"))
    if (migration.imported.isNotEmpty()) println("imported legacy saves: ${migration.imported}")
    val services = GameServices(
        content = content,
        saves = saves,
        clock = SystemClock,
        flavor = options.flavor,
        replays = FileReplayStore(File(options.saveDirectory, "replays")),
        onReady = { api -> driver = (api as GameApp).automation },
        cloudUrl = options.cloudUrl,
    )
    val configuration = Lwjgl3ApplicationConfiguration().apply {
        setTitle("PXWORLD · ${options.flavor.name.lowercase()} · ${options.environment}")
        setWindowedMode(options.width, options.height)
        useVsync(true)
        setForegroundFPS(60)
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    }
    try {
        Lwjgl3Application(GameApp(services), configuration)
    } finally {
        server?.stop(500)
    }
}
