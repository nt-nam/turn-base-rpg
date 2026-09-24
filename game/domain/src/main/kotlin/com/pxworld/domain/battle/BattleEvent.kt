package com.pxworld.domain.battle

sealed interface BattleEvent {

    fun describe(): String

    data class BattleStarted(val seed: Long, val allies: List<UnitId>, val enemies: List<UnitId>) : BattleEvent {
        override fun describe() = "battle_started seed=$seed allies=$allies enemies=$enemies"
    }

    data class TurnStarted(val unit: UnitId, val round: Int) : BattleEvent {
        override fun describe() = "turn_started $unit round=$round"
    }

    data class TurnSkipped(val unit: UnitId, val reason: String) : BattleEvent {
        override fun describe() = "turn_skipped $unit reason=$reason"
    }

    data class SkillUsed(val actor: UnitId, val skillId: String, val targets: List<UnitId>) : BattleEvent {
        override fun describe() = "skill_used $actor skill=$skillId targets=$targets"
    }

    data class AttackMissed(val actor: UnitId, val target: UnitId) : BattleEvent {
        override fun describe() = "attack_missed $actor -> $target"
    }

    data class DamageDealt(
        val source: UnitId,
        val target: UnitId,
        val amount: Int,
        val absorbedByShield: Int,
        val critical: Boolean,
        val matchupPermille: Int,
        val remainingHp: Int,
    ) : BattleEvent {
        override fun describe() =
            "damage $source -> $target amount=$amount shield=$absorbedByShield critical=$critical matchup=$matchupPermille hp=$remainingHp"
    }

    data class Healed(val source: UnitId, val target: UnitId, val amount: Int, val remainingHp: Int) : BattleEvent {
        override fun describe() = "healed $source -> $target amount=$amount hp=$remainingHp"
    }

    data class StatusApplied(val source: UnitId, val target: UnitId, val statusId: String, val stacks: Int, val turns: Int) : BattleEvent {
        override fun describe() = "status_applied $source -> $target status=$statusId stacks=$stacks turns=$turns"
    }

    data class StatusResisted(val source: UnitId, val target: UnitId, val statusId: String) : BattleEvent {
        override fun describe() = "status_resisted $source -> $target status=$statusId"
    }

    data class StatusExpired(val target: UnitId, val statusId: String) : BattleEvent {
        override fun describe() = "status_expired $target status=$statusId"
    }

    data class EnergyChanged(val unit: UnitId, val energy: Int) : BattleEvent {
        override fun describe() = "energy $unit value=$energy"
    }

    data class UnitDefeated(val unit: UnitId) : BattleEvent {
        override fun describe() = "defeated $unit"
    }

    data class BattleEnded(val outcome: BattleOutcome, val round: Int) : BattleEvent {
        override fun describe() = "battle_ended outcome=$outcome round=$round"
    }
}

object BattleEventLog {
    fun render(events: List<BattleEvent>): String = events.joinToString(separator = "\n", postfix = "\n") { it.describe() }
}
