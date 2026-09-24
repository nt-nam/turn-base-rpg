package com.pxworld.domain.battle

import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatFormula
import com.pxworld.domain.stats.StatKind

object BattleFixtures {

    val statuses: Map<String, StatusDefinition> = listOf(
        StatusDefinition("stun", StatusKind.Stun, durationTurns = 1),
        StatusDefinition("silence", StatusKind.Silence, durationTurns = 2),
        StatusDefinition("taunt", StatusKind.Taunt, durationTurns = 2),
        StatusDefinition("shield", StatusKind.Shield(powerPermilleOfCasterAttack = 1500), durationTurns = 2),
        StatusDefinition("burn", StatusKind.DamageOverTime(permilleOfMaxHp = 60), durationTurns = 3, maxStacks = 3),
        StatusDefinition("regen", StatusKind.HealOverTime(permilleOfMaxHp = 50), durationTurns = 3),
        StatusDefinition("attack_up", StatusKind.StatModifier(StatKind.ATTACK, 300), durationTurns = 3),
        StatusDefinition("defense_down", StatusKind.StatModifier(StatKind.DEFENSE, -300), durationTurns = 2),
        StatusDefinition("speed_up", StatusKind.StatModifier(StatKind.SPEED, 300), durationTurns = 2),
    ).associateBy { it.id }

    val matchup = ClassMatchup(
        mapOf(
            "warrior" to setOf("assassin"),
            "assassin" to setOf("mage"),
            "mage" to setOf("tank"),
            "tank" to setOf("ranger"),
            "ranger" to setOf("warrior"),
        ),
    )

    val rules = BattleRules(matchup = matchup, statuses = statuses)

    private fun basic(id: String, power: Int = 1000) =
        SkillDefinition(id, SkillSlot.BASIC, 0, Targeting.SingleEnemy, listOf(SkillEffect.Damage(power)))

    private val skillCooldowns = mapOf("tank.provoke" to 3)

    private val kits: Map<String, List<SkillDefinition>> = mapOf(
        "warrior" to listOf(
            basic("warrior.slash"),
            SkillDefinition("warrior.cleave", SkillSlot.SKILL, 0, Targeting.EnemyRow, listOf(SkillEffect.Damage(900))),
            SkillDefinition(
                "warrior.earthsplitter", SkillSlot.ULTIMATE, 100, Targeting.SingleEnemy,
                listOf(SkillEffect.Damage(2200), SkillEffect.ApplyStatus("stun", chancePermille = 600)),
            ),
        ),
        "assassin" to listOf(
            basic("assassin.stab", 900),
            SkillDefinition("assassin.twin_fang", SkillSlot.SKILL, 0, Targeting.SingleEnemy, listOf(SkillEffect.Damage(700, hits = 2))),
            SkillDefinition(
                "assassin.shadow_rain", SkillSlot.ULTIMATE, 100, Targeting.RandomEnemies(4),
                listOf(SkillEffect.Damage(800)),
            ),
        ),
        "mage" to listOf(
            basic("mage.spark"),
            SkillDefinition(
                "mage.flame_lance", SkillSlot.SKILL, 0, Targeting.EnemyLane,
                listOf(SkillEffect.Damage(850), SkillEffect.ApplyStatus("burn", chancePermille = 700)),
            ),
            SkillDefinition(
                "mage.meteor", SkillSlot.ULTIMATE, 100, Targeting.AllEnemies,
                listOf(SkillEffect.Damage(1100), SkillEffect.ApplyStatus("silence", chancePermille = 400)),
            ),
        ),
        "ranger" to listOf(
            basic("ranger.shot"),
            SkillDefinition(
                "ranger.pinning_arrow", SkillSlot.SKILL, 0, Targeting.SingleEnemy,
                listOf(SkillEffect.Damage(1200), SkillEffect.ApplyStatus("defense_down", chancePermille = 800)),
            ),
            SkillDefinition("ranger.arrow_storm", SkillSlot.ULTIMATE, 100, Targeting.RandomEnemies(6), listOf(SkillEffect.Damage(600))),
        ),
        "support" to listOf(
            basic("support.chime", 700),
            SkillDefinition(
                "support.mend", SkillSlot.SKILL, 0, Targeting.LowestHpAllies(2),
                listOf(SkillEffect.Heal(1400), SkillEffect.ApplyStatus("regen")),
            ),
            SkillDefinition(
                "support.dawn_hymn", SkillSlot.ULTIMATE, 100, Targeting.AllAllies,
                listOf(SkillEffect.Heal(1200), SkillEffect.ApplyStatus("attack_up"), SkillEffect.GainEnergy(20)),
            ),
        ),
        "tank" to listOf(
            basic("tank.bash", 800),
            SkillDefinition(
                "tank.provoke", SkillSlot.SKILL, 0, Targeting.Self,
                listOf(SkillEffect.ApplyStatus("taunt"), SkillEffect.ApplyStatus("shield")),
            ),
            SkillDefinition(
                "tank.bulwark", SkillSlot.ULTIMATE, 100, Targeting.AllAllies,
                listOf(SkillEffect.ApplyStatus("shield")),
            ),
        ),
    )

    private val kitsWithCooldowns: Map<String, List<SkillDefinition>> = kits.mapValues { (_, skills) ->
        skills.map { if (it.slot == SkillSlot.SKILL) it.copy(cooldownTurns = skillCooldowns[it.id] ?: 2) else it }
    }

    private val baseStats: Map<String, StatBlock> = mapOf(
        "warrior" to stats(hp = 900, attack = 120, defense = 70, speed = 100, critRate = 150),
        "assassin" to stats(hp = 650, attack = 150, defense = 40, speed = 130, critRate = 300, evasion = 100),
        "mage" to stats(hp = 600, attack = 160, defense = 35, speed = 105, critRate = 100, effectHit = 100),
        "ranger" to stats(hp = 700, attack = 135, defense = 45, speed = 115, critRate = 200, accuracy = 100),
        "support" to stats(hp = 750, attack = 100, defense = 55, speed = 110, critRate = 50, effectResistance = 150),
        "tank" to stats(hp = 1300, attack = 80, defense = 120, speed = 85, critRate = 50, effectResistance = 200),
    )

    fun stats(
        hp: Int,
        attack: Int,
        defense: Int,
        speed: Int,
        critRate: Int = 0,
        critDamage: Int = 1500,
        accuracy: Int = 0,
        evasion: Int = 0,
        effectHit: Int = 0,
        effectResistance: Int = 0,
    ): StatBlock = StatBlock.of(
        StatKind.HP to hp,
        StatKind.ATTACK to attack,
        StatKind.DEFENSE to defense,
        StatKind.SPEED to speed,
        StatKind.CRIT_RATE to critRate,
        StatKind.CRIT_DAMAGE to critDamage,
        StatKind.ACCURACY to accuracy,
        StatKind.EVASION to evasion,
        StatKind.EFFECT_HIT to effectHit,
        StatKind.EFFECT_RESISTANCE to effectResistance,
    )

    fun hero(classId: String, lane: Int, depth: Int, level: Int = 10, star: Int = 0, name: String = classId): CombatantSetup =
        CombatantSetup(
            name = name,
            classId = classId,
            level = level,
            cell = GridCell(lane, depth),
            stats = StatFormula.grow(requireNotNull(baseStats[classId]) { "unknown class $classId" }, level, star),
            skills = requireNotNull(kitsWithCooldowns[classId]),
        )

    fun custom(
        name: String,
        classId: String,
        cell: GridCell,
        stats: StatBlock,
        skills: List<SkillDefinition> = listOf(basic("$name.hit")),
        level: Int = 1,
    ): CombatantSetup = CombatantSetup(name, classId, level, cell, stats, skills)

    val balancedAllies: List<CombatantSetup> = listOf(
        hero("tank", lane = 1, depth = 0),
        hero("warrior", lane = 0, depth = 0),
        hero("mage", lane = 1, depth = 2),
        hero("support", lane = 2, depth = 2),
        hero("ranger", lane = 0, depth = 1),
    )

    val balancedEnemies: List<CombatantSetup> = listOf(
        hero("warrior", lane = 1, depth = 0, name = "raider"),
        hero("assassin", lane = 0, depth = 0, name = "cutthroat"),
        hero("assassin", lane = 2, depth = 1, name = "shade"),
        hero("mage", lane = 1, depth = 2, name = "hexer"),
        hero("ranger", lane = 0, depth = 2, name = "sniper"),
    )

    fun balancedBattle(seed: Long): BattleSetup = BattleSetup(seed, balancedAllies, balancedEnemies, rules)
}
