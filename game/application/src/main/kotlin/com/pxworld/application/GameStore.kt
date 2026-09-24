package com.pxworld.application

import com.pxworld.domain.progression.GameState

interface SaveRepository {
    fun slots(): List<String>
    fun load(slot: String): GameState
    fun save(slot: String, state: GameState)
    fun delete(slot: String)
    fun export(slot: String): String
}

fun interface GameStoreListener {
    fun onTransition(state: GameState, events: List<GameEvent>)
}

class GameStore(
    val slot: String,
    initial: GameState,
    private val saves: SaveRepository,
    private val followUp: (Transition) -> Transition = { it },
) {
    var state: GameState = initial
        private set

    private val listeners = mutableListOf<GameStoreListener>()

    fun subscribe(listener: GameStoreListener): () -> Unit {
        listeners += listener
        return { listeners -= listener }
    }

    fun dispatch(action: (GameState) -> Transition): Transition {
        val transition = followUp(action(state))
        state = transition.state
        saves.save(slot, state)
        listeners.toList().forEach { it.onTransition(state, transition.events) }
        return transition
    }

    fun update(change: (GameState) -> GameState) {
        dispatch { Transition(change(it), emptyList()) }
    }
}
