package com.pxworld.domain.battle

import com.pxworld.domain.random.Pcg32
import com.pxworld.domain.stats.StatKind

data class ActiveStatus(
    val statusId: String,
    val source: UnitId,
    val remainingTurns: Int,
    val stacks: Int,
    val shieldPoints: Int = 0,
)

data class Combatant(
    val id: UnitId,
    val setup: CombatantSetup,
    val hp: Int,
    val energy: Int,
    val turnDelay: Long,
    val statuses: List<ActiveStatus>,
    val cooldowns: Map<String, Int> = emptyMap(),
) {
    val isAlive: Boolean get() = hp > 0
    val maxHp: Int get() = setup.stats[StatKind.HP]
    val hpPermille: Int get() = (hp.toLong() * 1000 / maxHp).toInt()

    fun cooldownOf(skillId: String): Int = cooldowns[skillId] ?: 0
}

enum class BattleOutcome { VICTORY, DEFEAT, DRAW }

data class BattleState(
    val rules: BattleRules,
    val combatants: List<Combatant>,
    val random: Pcg32,
    val elapsedTime: Long,
    val actionCount: Int,
    val activeUnit: UnitId?,
    val outcome: BattleOutcome?,
) {
    val round: Int get() = (elapsedTime / rules.timeUnitsPerRound).toInt() + 1
    val isOver: Boolean get() = outcome != null

    fun combatant(id: UnitId): Combatant =
        combatants.firstOrNull { it.id == id } ?: throw IllegalArgumentException("no combatant $id")

    fun side(side: BattleSide): List<Combatant> = combatants.filter { it.id.side == side }

    fun livingOn(side: BattleSide): List<Combatant> = side(side).filter { it.isAlive }
}

data class BattleCommand(val actor: UnitId, val skillId: String, val target: UnitId?)

data class BattleStep(val state: BattleState, val events: List<BattleEvent>)

class IllegalBattleCommand(message: String) : IllegalArgumentException(message)
