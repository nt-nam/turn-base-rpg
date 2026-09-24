package com.pxworld.application

import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.economy.LedgerEntry
import com.pxworld.domain.progression.GameState

sealed interface GameEvent {
    data class CurrencyChanged(val entry: LedgerEntry) : GameEvent
    data class ItemsGained(val itemId: String, val quantity: Long) : GameEvent
    data class ItemsConsumed(val itemId: String, val quantity: Long) : GameEvent
    data class EquipmentGained(val instanceId: String, val equipmentId: String) : GameEvent
    data class EquipmentEquipped(val heroInstanceId: String, val equipmentInstanceId: String) : GameEvent
    data class EquipmentUnequipped(val heroInstanceId: String, val equipmentInstanceId: String) : GameEvent
    data class HeroRecruited(val instanceId: String, val heroId: String) : GameEvent
    data class HeroLeveledUp(val instanceId: String, val level: Int) : GameEvent
    data class HeroStarRaised(val instanceId: String, val star: Int, val consumedInstanceId: String) : GameEvent
    data class LineupChanged(val heroInstanceIds: List<String>) : GameEvent
    data class ProfileLeveledUp(val level: Int) : GameEvent
    data class CheckinClaimed(val tableId: String, val day: Int) : GameEvent
    data class BattleFinished(val encounterId: String, val outcome: BattleOutcome, val enemiesDefeated: Int) : GameEvent
}

data class Transition(val state: GameState, val events: List<GameEvent>)

class GameRuleViolation(message: String) : IllegalStateException(message)
