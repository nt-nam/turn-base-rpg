package com.pxworld.client.core

import com.pxworld.application.CollectionRules
import com.pxworld.application.GameRules
import com.pxworld.application.GameStore
import com.pxworld.application.NewGame
import com.pxworld.application.QuestTracker
import com.pxworld.application.SaveRepository
import com.pxworld.content.BattleContentAssembler
import com.pxworld.content.ContentBundle
import com.pxworld.content.ContentBundleCatalog
import com.pxworld.domain.progression.GameState

enum class BuildFlavor(val debugTools: Boolean, val automation: Boolean) {
    DEV(debugTools = true, automation = true),
    QA(debugTools = true, automation = true),
    PILOT(debugTools = false, automation = true),
    RELEASE(debugTools = false, automation = false),
}

interface GameClock {
    fun epochDay(): Long
    fun nowMillis(): Long
}

class GameServices(
    val content: ContentBundle,
    val saves: SaveRepository,
    val clock: GameClock,
    val flavor: BuildFlavor,
    val onReady: (GameApi) -> Unit = {},
) {
    val catalog: ContentBundleCatalog = ContentBundleCatalog(content)
    val rules: GameRules = GameRules(catalog)
    val quests: QuestTracker = QuestTracker(catalog)
    val collection: CollectionRules = CollectionRules(catalog, rules)
    val newGame: NewGame = NewGame(catalog, rules, quests)
    val battles: BattleContentAssembler = BattleContentAssembler(content)
}

class GameSession(private val services: GameServices) {

    var store: GameStore? = null
        private set

    val requireStore: GameStore get() = store ?: throw IllegalStateException("no game loaded")

    val state: GameState get() = requireStore.state

    fun start(slot: String, state: GameState): GameStore {
        val prepared = services.quests.startAll(state)
        services.saves.save(slot, prepared)
        return GameStore(slot, prepared, services.saves) { services.collection.record(services.quests.react(it), services.clock.nowMillis()) }.also { store = it }
    }

    var lastBattle: com.pxworld.client.screens.battle.BattleSummary? = null
    var pendingBattleLog: List<com.pxworld.domain.battle.BattleEvent> = emptyList()

    fun end() {
        store = null
        lastBattle = null
        pendingBattleLog = emptyList()
    }
}

interface GameApi {
    fun onRenderThread(block: () -> Unit)
}
