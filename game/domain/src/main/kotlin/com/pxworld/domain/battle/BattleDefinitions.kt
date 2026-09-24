package com.pxworld.domain.battle

import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatKind

enum class BattleSide {
    ALLY,
    ENEMY;

    val opponent: BattleSide get() = if (this == ALLY) ENEMY else ALLY
}

data class UnitId(val side: BattleSide, val slot: Int) {
    override fun toString(): String = "${side.name.lowercase()}#$slot"
}

data class GridCell(val lane: Int, val depth: Int) {
    init {
        require(lane in 0 until GRID_SIZE) { "lane must be in 0 until $GRID_SIZE, was $lane" }
        require(depth in 0 until GRID_SIZE) { "depth must be in 0 until $GRID_SIZE, was $depth" }
    }

    companion object {
        const val GRID_SIZE: Int = 3
    }
}

enum class SkillSlot { BASIC, SKILL, ULTIMATE }

sealed interface Targeting {
    val needsChosenTarget: Boolean get() = false

    object SingleEnemy : Targeting {
        override val needsChosenTarget: Boolean get() = true
        override fun toString(): String = "SingleEnemy"
    }

    object EnemyRow : Targeting {
        override val needsChosenTarget: Boolean get() = true
        override fun toString(): String = "EnemyRow"
    }

    object EnemyLane : Targeting {
        override val needsChosenTarget: Boolean get() = true
        override fun toString(): String = "EnemyLane"
    }

    object AllEnemies : Targeting {
        override fun toString(): String = "AllEnemies"
    }

    object SingleAlly : Targeting {
        override val needsChosenTarget: Boolean get() = true
        override fun toString(): String = "SingleAlly"
    }

    object AllAllies : Targeting {
        override fun toString(): String = "AllAllies"
    }

    object Self : Targeting {
        override fun toString(): String = "Self"
    }

    data class LowestHpAllies(val count: Int) : Targeting

    data class RandomEnemies(val count: Int) : Targeting
}

sealed interface SkillEffect {
    data class Damage(val powerPermille: Int, val hits: Int = 1) : SkillEffect
    data class Heal(val powerPermille: Int) : SkillEffect
    data class ApplyStatus(val statusId: String, val chancePermille: Int = 1000, val onSelf: Boolean = false) : SkillEffect
    data class GainEnergy(val amount: Int) : SkillEffect
}

data class SkillDefinition(
    val id: String,
    val slot: SkillSlot,
    val energyCost: Int,
    val targeting: Targeting,
    val effects: List<SkillEffect>,
    val cooldownTurns: Int = 0,
) {
    init {
        require(energyCost >= 0) { "energyCost must not be negative for $id" }
        require(cooldownTurns >= 0) { "cooldownTurns must not be negative for $id" }
        require(effects.isNotEmpty()) { "skill $id must have at least one effect" }
    }
}

sealed interface StatusKind {
    data class StatModifier(val stat: StatKind, val permille: Int) : StatusKind
    object Stun : StatusKind {
        override fun toString(): String = "Stun"
    }
    object Silence : StatusKind {
        override fun toString(): String = "Silence"
    }
    object Taunt : StatusKind {
        override fun toString(): String = "Taunt"
    }
    data class Shield(val powerPermilleOfCasterAttack: Int) : StatusKind
    data class DamageOverTime(val permilleOfMaxHp: Int) : StatusKind
    data class HealOverTime(val permilleOfMaxHp: Int) : StatusKind
}

data class StatusDefinition(
    val id: String,
    val kind: StatusKind,
    val durationTurns: Int,
    val maxStacks: Int = 1,
    val dispellable: Boolean = true,
) {
    init {
        require(durationTurns > 0) { "status $id must last at least one turn" }
        require(maxStacks > 0) { "status $id must allow at least one stack" }
    }
}

class ClassMatchup(private val advantages: Map<String, Set<String>>) {

    fun multiplierPermille(attackerClass: String, defenderClass: String, rules: MatchupMultipliers): Int = when {
        advantages[attackerClass].orEmpty().contains(defenderClass) -> rules.advantagePermille
        advantages[defenderClass].orEmpty().contains(attackerClass) -> rules.disadvantagePermille
        else -> rules.neutralPermille
    }

    companion object {
        val NONE = ClassMatchup(emptyMap())
    }
}

data class MatchupMultipliers(
    val advantagePermille: Int = 1250,
    val neutralPermille: Int = 1000,
    val disadvantagePermille: Int = 850,
)

data class BattleRules(
    val maxRounds: Int = 30,
    val timeUnitsPerRound: Int = 10_000,
    val referenceSpeed: Int = 100,
    val startingEnergy: Int = 25,
    val maxEnergy: Int = 100,
    val energyPerAction: Int = 20,
    val energyWhenHit: Int = 10,
    val baseHitPermille: Int = 950,
    val minimumHitPermille: Int = 600,
    val criticalRateCapPermille: Int = 750,
    val damageVariancePermille: Int = 50,
    val defenseConstantBase: Int = 100,
    val defenseConstantPerLevel: Int = 10,
    val damageTakenByDepthPermille: List<Int> = listOf(1000, 900, 800),
    val matchupMultipliers: MatchupMultipliers = MatchupMultipliers(),
    val matchup: ClassMatchup = ClassMatchup.NONE,
    val statuses: Map<String, StatusDefinition> = emptyMap(),
) {
    init {
        require(damageTakenByDepthPermille.size == GridCell.GRID_SIZE) { "damageTakenByDepthPermille needs one value per depth" }
    }

    fun status(id: String): StatusDefinition =
        statuses[id] ?: throw IllegalArgumentException("unknown status $id")
}

data class CombatantSetup(
    val name: String,
    val classId: String,
    val level: Int,
    val cell: GridCell,
    val stats: StatBlock,
    val skills: List<SkillDefinition>,
) {
    init {
        require(stats[StatKind.HP] > 0) { "$name must have positive HP" }
        require(stats[StatKind.SPEED] > 0) { "$name must have positive SPEED" }
        require(skills.count { it.slot == SkillSlot.BASIC } == 1) { "$name must have exactly one BASIC skill" }
        require(skills.map { it.id }.toSet().size == skills.size) { "$name has duplicate skill ids" }
    }

    fun skill(id: String): SkillDefinition =
        skills.firstOrNull { it.id == id } ?: throw IllegalArgumentException("$name has no skill $id")
}

data class BattleSetup(
    val seed: Long,
    val allies: List<CombatantSetup>,
    val enemies: List<CombatantSetup>,
    val rules: BattleRules = BattleRules(),
) {
    init {
        require(allies.isNotEmpty()) { "a battle needs at least one ally" }
        require(enemies.isNotEmpty()) { "a battle needs at least one enemy" }
        require(allies.map { it.cell }.toSet().size == allies.size) { "two allies share a grid cell" }
        require(enemies.map { it.cell }.toSet().size == enemies.size) { "two enemies share a grid cell" }
    }
}
