package com.pxworld.domain.battle

import com.pxworld.domain.PERMILLE
import com.pxworld.domain.random.Pcg32
import com.pxworld.domain.random.RandomCursor
import com.pxworld.domain.stats.StatKind
import kotlin.math.abs

data class BattleRecord(
    val setup: BattleSetup,
    val commands: List<BattleCommand>,
    val events: List<BattleEvent>,
    val finalState: BattleState,
)

object BattleEngine {

    fun start(setup: BattleSetup): BattleStep {
        val rules = setup.rules
        val combatants = setup.allies.mapIndexed { slot, combatant -> enter(UnitId(BattleSide.ALLY, slot), combatant, rules) } +
            setup.enemies.mapIndexed { slot, combatant -> enter(UnitId(BattleSide.ENEMY, slot), combatant, rules) }
        val initial = BattleState(
            rules = rules,
            combatants = combatants,
            random = Pcg32.seeded(setup.seed),
            elapsedTime = 0L,
            actionCount = 0,
            activeUnit = null,
            outcome = null,
        )
        val workspace = BattleWorkspace(initial)
        workspace.emit(
            BattleEvent.BattleStarted(
                seed = setup.seed,
                allies = combatants.filter { it.id.side == BattleSide.ALLY }.map { it.id },
                enemies = combatants.filter { it.id.side == BattleSide.ENEMY }.map { it.id },
            ),
        )
        workspace.advanceToNextActor()
        return workspace.toStep()
    }

    fun legalCommands(state: BattleState): List<BattleCommand> {
        val actorId = state.activeUnit ?: return emptyList()
        if (state.isOver) return emptyList()
        val actor = state.combatant(actorId)
        val silenced = actor.statuses.any { state.rules.status(it.statusId).kind == StatusKind.Silence }
        return actor.setup.skills
            .filter { actor.energy >= it.energyCost && actor.cooldownOf(it.id) == 0 }
            .filter { !silenced || it.slot == SkillSlot.BASIC }
            .flatMap { skill -> commandsFor(state, actor, skill) }
    }

    fun apply(state: BattleState, command: BattleCommand): BattleStep {
        if (state.isOver) throw IllegalBattleCommand("battle is already over")
        if (command !in legalCommands(state)) throw IllegalBattleCommand("illegal command $command")
        val workspace = BattleWorkspace(state)
        workspace.perform(command)
        workspace.advanceToNextActor()
        return workspace.toStep()
    }

    fun autoCommand(state: BattleState): BattleCommand = AutoPolicy.choose(state, legalCommands(state))

    fun replay(setup: BattleSetup, commands: List<BattleCommand>): BattleRecord {
        var step = start(setup)
        val events = step.events.toMutableList()
        for (command in commands) {
            step = apply(step.state, command)
            events += step.events
        }
        return BattleRecord(setup, commands, events, step.state)
    }

    fun runAuto(setup: BattleSetup): BattleRecord {
        var step = start(setup)
        val events = step.events.toMutableList()
        val commands = mutableListOf<BattleCommand>()
        while (!step.state.isOver) {
            val command = autoCommand(step.state)
            commands += command
            step = apply(step.state, command)
            events += step.events
        }
        return BattleRecord(setup, commands, events, step.state)
    }

    internal fun effectiveStat(rules: BattleRules, combatant: Combatant, kind: StatKind): Int {
        var modifierPermille = 0
        var flatBonus = 0
        for (active in combatant.statuses) {
            when (val statusKind = rules.status(active.statusId).kind) {
                is StatusKind.StatModifier -> if (statusKind.stat == kind) modifierPermille += statusKind.permille * active.stacks
                is StatusKind.StatBonus -> if (statusKind.stat == kind) flatBonus += statusKind.amount * active.stacks
                else -> Unit
            }
        }
        val value = ((combatant.setup.stats[kind] + flatBonus).toLong() * (PERMILLE + modifierPermille) / PERMILLE).toInt()
        return if (kind == StatKind.SPEED) value.coerceAtLeast(1) else value.coerceAtLeast(0)
    }

    internal fun targetableEnemies(state: BattleState, actor: Combatant): List<Combatant> {
        val living = state.livingOn(actor.id.side.opponent)
        val taunting = living.filter { enemy -> enemy.statuses.any { state.rules.status(it.statusId).kind == StatusKind.Taunt } }
        val candidates = taunting.ifEmpty { living }
        return candidates.sortedWith(
            compareBy<Combatant>({ it.setup.cell.depth }, { abs(it.setup.cell.lane - actor.setup.cell.lane) }, { it.id.slot }),
        )
    }

    private fun enter(id: UnitId, setup: CombatantSetup, rules: BattleRules): Combatant {
        val speed = setup.stats[StatKind.SPEED].coerceAtLeast(1)
        return Combatant(
            id = id,
            setup = setup,
            hp = setup.stats[StatKind.HP],
            energy = rules.startingEnergy.coerceAtMost(rules.maxEnergy),
            turnDelay = rules.timeUnitsPerRound.toLong() * rules.referenceSpeed / speed,
            statuses = emptyList(),
        )
    }

    private fun commandsFor(state: BattleState, actor: Combatant, skill: SkillDefinition): List<BattleCommand> {
        val command = { target: UnitId? -> BattleCommand(actor.id, skill.id, target) }
        return when (val targeting = skill.targeting) {
            Targeting.SingleEnemy -> targetableEnemies(state, actor).map { command(it.id) }
            Targeting.EnemyRow -> targetableEnemies(state, actor).distinctBy { it.setup.cell.depth }.map { command(it.id) }
            Targeting.EnemyLane -> targetableEnemies(state, actor).distinctBy { it.setup.cell.lane }.map { command(it.id) }
            Targeting.SingleAlly -> state.livingOn(actor.id.side).map { command(it.id) }
            Targeting.AllEnemies, Targeting.AllAllies, Targeting.Self,
            is Targeting.LowestHpAllies, is Targeting.RandomEnemies -> {
                check(!targeting.needsChosenTarget)
                listOf(command(null))
            }
        }
    }
}

private class BattleWorkspace(origin: BattleState) {

    private val rules = origin.rules
    private val combatants = origin.combatants.toMutableList()
    private val random = RandomCursor(origin.random)
    private val events = mutableListOf<BattleEvent>()
    private var elapsedTime = origin.elapsedTime
    private var actionCount = origin.actionCount
    private var activeUnit = origin.activeUnit
    private var outcome = origin.outcome

    fun emit(event: BattleEvent) {
        events += event
    }

    fun toStep(): BattleStep = BattleStep(snapshot(), events.toList())

    fun perform(command: BattleCommand) {
        val actor = get(command.actor)
        val skill = actor.setup.skill(command.skillId)
        changeEnergy(actor.id, -skill.energyCost)
        if (skill.cooldownTurns > 0) update(actor.id) { it.copy(cooldowns = it.cooldowns + (skill.id to skill.cooldownTurns)) }
        val targets = resolveTargets(actor, skill, command.target)
        emit(BattleEvent.SkillUsed(actor.id, skill.id, targets))
        for (effect in skill.effects) {
            applyEffect(command.actor, effect, targets)
        }
        if (get(command.actor).isAlive) changeEnergy(command.actor, rules.energyPerAction)
        endTurn(command.actor)
        actionCount += 1
        activeUnit = null
    }

    fun advanceToNextActor() {
        while (true) {
            if (settleOutcome()) return
            val next = combatants.withIndex()
                .filter { it.value.isAlive }
                .minWith(compareBy({ it.value.turnDelay }, { it.index }))
                .value
            val elapsedStep = next.turnDelay
            elapsedTime += elapsedStep
            combatants.replaceAll { if (it.isAlive) it.copy(turnDelay = it.turnDelay - elapsedStep) else it }
            if (currentRound() > rules.maxRounds) {
                finish(BattleOutcome.DRAW)
                return
            }
            update(next.id) { it.copy(turnDelay = delayFor(it)) }
            emit(BattleEvent.TurnStarted(next.id, currentRound()))
            tickCooldowns(next.id)
            applyTurnStartStatuses(next.id)
            if (!get(next.id).isAlive) continue
            if (hasStatus(next.id) { it == StatusKind.Stun }) {
                emit(BattleEvent.TurnSkipped(next.id, "stun"))
                endTurn(next.id)
                continue
            }
            activeUnit = next.id
            return
        }
    }

    private fun snapshot(): BattleState = BattleState(
        rules = rules,
        combatants = combatants.toList(),
        random = random.current,
        elapsedTime = elapsedTime,
        actionCount = actionCount,
        activeUnit = activeUnit,
        outcome = outcome,
    )

    private fun currentRound(): Int = (elapsedTime / rules.timeUnitsPerRound).toInt() + 1

    private fun get(id: UnitId): Combatant = combatants.first { it.id == id }

    private fun update(id: UnitId, transform: (Combatant) -> Combatant) {
        val index = combatants.indexOfFirst { it.id == id }
        combatants[index] = transform(combatants[index])
    }

    private fun stat(id: UnitId, kind: StatKind): Int = BattleEngine.effectiveStat(rules, get(id), kind)

    private fun delayFor(combatant: Combatant): Long =
        rules.timeUnitsPerRound.toLong() * rules.referenceSpeed / BattleEngine.effectiveStat(rules, combatant, StatKind.SPEED)

    private fun hasStatus(id: UnitId, predicate: (StatusKind) -> Boolean): Boolean =
        get(id).statuses.any { predicate(rules.status(it.statusId).kind) }

    private fun settleOutcome(): Boolean {
        if (outcome != null) return true
        val alliesAlive = combatants.any { it.id.side == BattleSide.ALLY && it.isAlive }
        val enemiesAlive = combatants.any { it.id.side == BattleSide.ENEMY && it.isAlive }
        return when {
            !alliesAlive -> finish(BattleOutcome.DEFEAT).let { true }
            !enemiesAlive -> finish(BattleOutcome.VICTORY).let { true }
            else -> false
        }
    }

    private fun finish(result: BattleOutcome) {
        outcome = result
        activeUnit = null
        emit(BattleEvent.BattleEnded(result, currentRound().coerceAtMost(rules.maxRounds)))
    }

    private fun changeEnergy(id: UnitId, delta: Int) {
        val before = get(id).energy
        val after = (before + delta).coerceIn(0, rules.maxEnergy)
        if (after == before) return
        update(id) { it.copy(energy = after) }
        emit(BattleEvent.EnergyChanged(id, after))
    }

    private fun resolveTargets(actor: Combatant, skill: SkillDefinition, chosen: UnitId?): List<UnitId> {
        val opponents = combatants.filter { it.id.side == actor.id.side.opponent && it.isAlive }
        val friends = combatants.filter { it.id.side == actor.id.side && it.isAlive }
        return when (val targeting = skill.targeting) {
            Targeting.SingleEnemy, Targeting.SingleAlly -> listOf(requireNotNull(chosen))
            Targeting.EnemyRow -> {
                val depth = get(requireNotNull(chosen)).setup.cell.depth
                opponents.filter { it.setup.cell.depth == depth }.map { it.id }
            }
            Targeting.EnemyLane -> {
                val lane = get(requireNotNull(chosen)).setup.cell.lane
                opponents.filter { it.setup.cell.lane == lane }.map { it.id }
            }
            Targeting.AllEnemies -> opponents.map { it.id }
            Targeting.AllAllies -> friends.map { it.id }
            Targeting.Self -> listOf(actor.id)
            is Targeting.LowestHpAllies -> friends.sortedWith(compareBy({ it.hpPermille }, { it.id.slot })).take(targeting.count).map { it.id }
            is Targeting.RandomEnemies -> List(targeting.count) { opponents[random.below(opponents.size)].id }
        }
    }

    private fun applyEffect(actorId: UnitId, effect: SkillEffect, targets: List<UnitId>) {
        when (effect) {
            is SkillEffect.Damage -> targets.forEach { target ->
                repeat(effect.hits) { if (get(actorId).isAlive && get(target).isAlive) strike(actorId, target, effect.powerPermille) }
            }
            is SkillEffect.Heal -> targets.forEach { target -> if (get(target).isAlive) heal(actorId, target, effect.powerPermille) }
            is SkillEffect.ApplyStatus -> {
                val recipients = if (effect.onSelf) listOf(actorId) else targets
                recipients.forEach { recipient -> if (get(recipient).isAlive) applyStatus(actorId, recipient, effect) }
            }
            is SkillEffect.GainEnergy -> targets.forEach { target -> if (get(target).isAlive) changeEnergy(target, effect.amount) }
        }
    }

    private fun strike(actorId: UnitId, targetId: UnitId, powerPermille: Int) {
        val attacker = get(actorId)
        val defender = get(targetId)
        val hitPermille = (rules.baseHitPermille + (stat(actorId, StatKind.ACCURACY) - stat(targetId, StatKind.EVASION)) / 2)
            .coerceIn(rules.minimumHitPermille, PERMILLE)
        if (random.permilleRoll() >= hitPermille) {
            emit(BattleEvent.AttackMissed(actorId, targetId))
            return
        }
        val critical = random.permilleRoll() < stat(actorId, StatKind.CRIT_RATE).coerceAtMost(rules.criticalRateCapPermille)
        val raw = stat(actorId, StatKind.ATTACK).toLong() * powerPermille / PERMILLE
        val defenseConstant = rules.defenseConstantBase + rules.defenseConstantPerLevel * attacker.setup.level
        val mitigated = raw * defenseConstant / (defenseConstant + stat(targetId, StatKind.DEFENSE))
        val matchup = rules.matchup.multiplierPermille(attacker.setup.classId, defender.setup.classId, rules.matchupMultipliers)
        val criticalFactor = if (critical) stat(actorId, StatKind.CRIT_DAMAGE).coerceAtLeast(PERMILLE) else PERMILLE
        val depthFactor = rules.damageTakenByDepthPermille[defender.setup.cell.depth]
        val variance = PERMILLE - rules.damageVariancePermille + random.below(2 * rules.damageVariancePermille + 1)
        val amount = (mitigated * matchup / PERMILLE * criticalFactor / PERMILLE * depthFactor / PERMILLE * variance / PERMILLE)
            .toInt().coerceAtLeast(1)
        dealDamage(actorId, targetId, amount, critical, matchup, absorbable = true)
    }

    private fun dealDamage(sourceId: UnitId, targetId: UnitId, amount: Int, critical: Boolean, matchup: Int, absorbable: Boolean) {
        var remaining = amount
        var absorbed = 0
        if (absorbable) {
            val statuses = get(targetId).statuses.map { active ->
                if (remaining > 0 && active.shieldPoints > 0) {
                    val taken = minOf(active.shieldPoints, remaining)
                    remaining -= taken
                    absorbed += taken
                    active.copy(shieldPoints = active.shieldPoints - taken)
                } else {
                    active
                }
            }
            val broken = statuses.filter { rules.status(it.statusId).kind is StatusKind.Shield && it.shieldPoints == 0 }
            update(targetId) { it.copy(statuses = statuses - broken.toSet()) }
            broken.forEach { emit(BattleEvent.StatusExpired(targetId, it.statusId)) }
        }
        val hpAfter = (get(targetId).hp - remaining).coerceAtLeast(0)
        update(targetId) { it.copy(hp = hpAfter) }
        emit(BattleEvent.DamageDealt(sourceId, targetId, remaining, absorbed, critical, matchup, hpAfter))
        if (hpAfter == 0) {
            update(targetId) { it.copy(statuses = emptyList(), energy = 0) }
            emit(BattleEvent.UnitDefeated(targetId))
        } else if (remaining > 0) {
            changeEnergy(targetId, rules.energyWhenHit)
        }
    }

    private fun heal(sourceId: UnitId, targetId: UnitId, powerPermille: Int) {
        val amount = (stat(sourceId, StatKind.ATTACK).toLong() * powerPermille / PERMILLE).toInt()
        restoreHp(sourceId, targetId, amount)
    }

    private fun restoreHp(sourceId: UnitId, targetId: UnitId, amount: Int) {
        val target = get(targetId)
        val applied = amount.coerceAtMost(target.maxHp - target.hp).coerceAtLeast(0)
        update(targetId) { it.copy(hp = it.hp + applied) }
        emit(BattleEvent.Healed(sourceId, targetId, applied, target.hp + applied))
    }

    private fun applyStatus(sourceId: UnitId, recipientId: UnitId, effect: SkillEffect.ApplyStatus) {
        val definition = rules.status(effect.statusId)
        val hostile = sourceId.side != recipientId.side
        val chance = if (hostile) {
            (effect.chancePermille + stat(sourceId, StatKind.EFFECT_HIT) - stat(recipientId, StatKind.EFFECT_RESISTANCE)).coerceIn(0, PERMILLE)
        } else {
            effect.chancePermille.coerceIn(0, PERMILLE)
        }
        if (chance < PERMILLE && random.permilleRoll() >= chance) {
            emit(BattleEvent.StatusResisted(sourceId, recipientId, definition.id))
            return
        }
        val shieldPoints = when (val kind = definition.kind) {
            is StatusKind.Shield -> (stat(sourceId, StatKind.ATTACK).toLong() * kind.powerPermilleOfCasterAttack / PERMILLE).toInt()
            else -> 0
        }
        val existing = get(recipientId).statuses.firstOrNull { it.statusId == definition.id }
        val applied = if (existing == null) {
            ActiveStatus(definition.id, sourceId, definition.durationTurns, 1, shieldPoints)
        } else {
            existing.copy(
                source = sourceId,
                remainingTurns = definition.durationTurns,
                stacks = (existing.stacks + 1).coerceAtMost(definition.maxStacks),
                shieldPoints = existing.shieldPoints + shieldPoints,
            )
        }
        update(recipientId) { combatant ->
            combatant.copy(statuses = combatant.statuses.filterNot { it.statusId == definition.id } + applied)
        }
        emit(BattleEvent.StatusApplied(sourceId, recipientId, definition.id, applied.stacks, applied.remainingTurns))
    }

    private fun applyTurnStartStatuses(id: UnitId) {
        for (active in get(id).statuses) {
            if (!get(id).isAlive) return
            when (val kind = rules.status(active.statusId).kind) {
                is StatusKind.DamageOverTime -> {
                    val amount = (get(id).maxHp.toLong() * kind.permilleOfMaxHp / PERMILLE * active.stacks).toInt().coerceAtLeast(1)
                    dealDamage(active.source, id, amount, critical = false, matchup = PERMILLE, absorbable = false)
                }
                is StatusKind.HealOverTime -> {
                    val amount = (get(id).maxHp.toLong() * kind.permilleOfMaxHp / PERMILLE * active.stacks).toInt()
                    restoreHp(active.source, id, amount)
                }
                else -> Unit
            }
        }
    }

    private fun tickCooldowns(id: UnitId) {
        update(id) { combatant ->
            combatant.copy(cooldowns = combatant.cooldowns.mapValues { (it.value - 1).coerceAtLeast(0) }.filterValues { it > 0 })
        }
    }

    private fun endTurn(id: UnitId) {
        val combatant = get(id)
        if (!combatant.isAlive) return
        val ticked = combatant.statuses.map { it.copy(remainingTurns = it.remainingTurns - 1) }
        val expired = ticked.filter { it.remainingTurns <= 0 }
        update(id) { it.copy(statuses = ticked - expired.toSet()) }
        expired.forEach { emit(BattleEvent.StatusExpired(id, it.statusId)) }
    }
}
