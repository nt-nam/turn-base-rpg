package com.pxworld.content

import com.pxworld.domain.battle.BattleRules
import com.pxworld.domain.battle.BattleSetup
import com.pxworld.domain.battle.ClassMatchup
import com.pxworld.domain.battle.CombatantSetup
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.battle.MatchupMultipliers
import com.pxworld.domain.battle.SkillDefinition
import com.pxworld.domain.battle.SkillEffect
import com.pxworld.domain.battle.SkillSlot
import com.pxworld.domain.battle.StatusDefinition
import com.pxworld.domain.battle.StatusKind
import com.pxworld.domain.battle.Targeting
import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatFormula
import com.pxworld.domain.stats.StatKind

data class LineupSlot(val heroId: String, val level: Int, val star: Int, val cell: GridCell, val flatBonus: StatBlock = StatBlock.EMPTY)

class BattleContentAssembler(private val bundle: ContentBundle) {

    private val skills = bundle.skills.associateBy { it.id }
    private val heroes = bundle.heroes.associateBy { it.id }
    private val enemies = bundle.enemies.associateBy { it.id }
    private val encounters = bundle.encounters.associateBy { it.id }

    val rules: BattleRules = run {
        val record = bundle.battleRules.single()
        BattleRules(
            maxRounds = record.maxRounds,
            timeUnitsPerRound = record.timeUnitsPerRound,
            referenceSpeed = record.referenceSpeed,
            startingEnergy = record.startingEnergy,
            maxEnergy = record.maxEnergy,
            energyPerAction = record.energyPerAction,
            energyWhenHit = record.energyWhenHit,
            baseHitPermille = record.baseHitPermille,
            minimumHitPermille = record.minimumHitPermille,
            criticalRateCapPermille = record.criticalRateCapPermille,
            damageVariancePermille = record.damageVariancePermille,
            defenseConstantBase = record.defenseConstantBase,
            defenseConstantPerLevel = record.defenseConstantPerLevel,
            damageTakenByDepthPermille = record.damageTakenByDepthPermille,
            matchupMultipliers = MatchupMultipliers(
                advantagePermille = record.matchupAdvantagePermille,
                disadvantagePermille = record.matchupDisadvantagePermille,
            ),
            matchup = ClassMatchup(bundle.heroClasses.associate { it.id to it.counters.toSet() }),
            statuses = bundle.statuses.associate { it.id to statusDefinition(it) },
        )
    }

    fun battle(seed: Long, lineup: List<LineupSlot>, encounterId: String): BattleSetup {
        val encounter = encounters[encounterId] ?: throw IllegalArgumentException("unknown encounter $encounterId")
        val allies = lineup.map { slot ->
            val hero = heroes[slot.heroId] ?: throw IllegalArgumentException("unknown hero ${slot.heroId}")
            combatant(hero.id, hero.classId, hero.baseStats, hero.skills, slot.level, slot.star, slot.cell, slot.flatBonus)
        }
        val foes = encounter.enemies.map { placed ->
            val enemy = enemies[placed.enemy] ?: throw IllegalArgumentException("unknown enemy ${placed.enemy}")
            combatant(enemy.id, enemy.classId, enemy.baseStats, enemy.skills, placed.level, placed.star, GridCell(placed.cell.lane, placed.cell.depth))
        }
        return BattleSetup(seed, allies, foes, rules)
    }

    private fun combatant(
        name: String,
        classId: String,
        base: StatsRecord,
        skillIds: List<String>,
        level: Int,
        star: Int,
        cell: GridCell,
        flatBonus: StatBlock = StatBlock.EMPTY,
    ): CombatantSetup = CombatantSetup(
        name = name,
        classId = classId,
        level = level,
        cell = cell,
        stats = StatFormula.finalStats(statBlock(base), level, star, flatBonus, StatBlock.EMPTY),
        skills = skillIds.map { id -> skillDefinition(skills[id] ?: throw IllegalArgumentException("unknown skill $id")) },
    )

    private fun statusDefinition(record: StatusRecord): StatusDefinition = StatusDefinition(
        id = record.id,
        kind = when (record.kind) {
            StatusKindName.STUN -> StatusKind.Stun
            StatusKindName.SILENCE -> StatusKind.Silence
            StatusKindName.TAUNT -> StatusKind.Taunt
            StatusKindName.SHIELD -> StatusKind.Shield(record.magnitude)
            StatusKindName.DAMAGE_OVER_TIME -> StatusKind.DamageOverTime(record.magnitude)
            StatusKindName.HEAL_OVER_TIME -> StatusKind.HealOverTime(record.magnitude)
            StatusKindName.STAT_MODIFIER -> StatusKind.StatModifier(statKind(requireNotNull(record.stat)), record.magnitude)
            StatusKindName.STAT_BONUS -> StatusKind.StatBonus(statKind(requireNotNull(record.stat)), record.magnitude)
        },
        durationTurns = record.durationTurns,
        maxStacks = record.maxStacks,
        dispellable = record.dispellable,
    )

    private fun skillDefinition(record: SkillRecord): SkillDefinition = SkillDefinition(
        id = record.id,
        slot = when (record.slot) {
            SkillSlotName.BASIC -> SkillSlot.BASIC
            SkillSlotName.SKILL -> SkillSlot.SKILL
            SkillSlotName.ULTIMATE -> SkillSlot.ULTIMATE
        },
        energyCost = record.energyCost,
        cooldownTurns = record.cooldownTurns,
        targeting = when (record.targeting.kind) {
            TargetingKindName.SINGLE_ENEMY -> Targeting.SingleEnemy
            TargetingKindName.ENEMY_ROW -> Targeting.EnemyRow
            TargetingKindName.ENEMY_LANE -> Targeting.EnemyLane
            TargetingKindName.ALL_ENEMIES -> Targeting.AllEnemies
            TargetingKindName.SINGLE_ALLY -> Targeting.SingleAlly
            TargetingKindName.ALL_ALLIES -> Targeting.AllAllies
            TargetingKindName.SELF -> Targeting.Self
            TargetingKindName.LOWEST_HP_ALLIES -> Targeting.LowestHpAllies(requireNotNull(record.targeting.count))
            TargetingKindName.RANDOM_ENEMIES -> Targeting.RandomEnemies(requireNotNull(record.targeting.count))
        },
        effects = record.effects.map { effect ->
            when (effect.kind) {
                EffectKindName.DAMAGE -> SkillEffect.Damage(requireNotNull(effect.power), effect.hits ?: 1)
                EffectKindName.HEAL -> SkillEffect.Heal(requireNotNull(effect.power))
                EffectKindName.APPLY_STATUS -> SkillEffect.ApplyStatus(requireNotNull(effect.status), effect.chance ?: 1000, effect.onSelf ?: false)
                EffectKindName.GAIN_ENERGY -> SkillEffect.GainEnergy(requireNotNull(effect.amount))
            }
        },
    )

    companion object {
        fun statKind(name: String): StatKind = when (name) {
            "hp" -> StatKind.HP
            "attack" -> StatKind.ATTACK
            "defense" -> StatKind.DEFENSE
            "speed" -> StatKind.SPEED
            "critRate" -> StatKind.CRIT_RATE
            "critDamage" -> StatKind.CRIT_DAMAGE
            "accuracy" -> StatKind.ACCURACY
            "evasion" -> StatKind.EVASION
            "effectHit" -> StatKind.EFFECT_HIT
            "effectResistance" -> StatKind.EFFECT_RESISTANCE
            else -> throw IllegalArgumentException("unknown stat $name")
        }

        fun statBlock(record: StatsRecord): StatBlock = StatBlock.of(
            StatKind.HP to record.hp,
            StatKind.ATTACK to record.attack,
            StatKind.DEFENSE to record.defense,
            StatKind.SPEED to record.speed,
            StatKind.CRIT_RATE to record.critRate,
            StatKind.CRIT_DAMAGE to record.critDamage,
            StatKind.ACCURACY to record.accuracy,
            StatKind.EVASION to record.evasion,
            StatKind.EFFECT_HIT to record.effectHit,
            StatKind.EFFECT_RESISTANCE to record.effectResistance,
        )
    }
}
