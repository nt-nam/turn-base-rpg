package com.pxworld.domain.battle

import com.pxworld.domain.battle.BattleFixtures.custom
import com.pxworld.domain.battle.BattleFixtures.rules
import com.pxworld.domain.battle.BattleFixtures.stats
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BattleEngineTest {

    @Test
    fun `same seed and commands always produce identical events`() {
        val first = BattleEngine.runAuto(BattleFixtures.balancedBattle(seed = 42))
        val second = BattleEngine.runAuto(BattleFixtures.balancedBattle(seed = 42))
        assertEquals(first.events, second.events)
        assertEquals(first.finalState, second.finalState)
    }

    @Test
    fun `different seeds diverge`() {
        val first = BattleEngine.runAuto(BattleFixtures.balancedBattle(seed = 1))
        val second = BattleEngine.runAuto(BattleFixtures.balancedBattle(seed = 2))
        assertNotEquals(first.events, second.events)
    }

    @Test
    fun `replaying recorded commands reproduces the battle exactly`() {
        val record = BattleEngine.runAuto(BattleFixtures.balancedBattle(seed = 7))
        val replayed = BattleEngine.replay(record.setup, record.commands)
        assertEquals(record.events, replayed.events)
        assertEquals(record.finalState, replayed.finalState)
    }

    @Test
    fun `rewinding to an earlier state and choosing differently branches without corrupting the original timeline`() {
        val setup = BattleFixtures.balancedBattle(seed = 11)
        val record = BattleEngine.runAuto(setup)
        var state = BattleEngine.start(setup).state
        repeat(3) { state = BattleEngine.apply(state, record.commands[it]).state }
        val checkpoint = state
        val alternative = BattleEngine.legalCommands(checkpoint).last { it != record.commands[3] }
        val branch = BattleEngine.apply(checkpoint, alternative)
        val original = BattleEngine.apply(checkpoint, record.commands[3])
        assertNotEquals(branch.events, original.events)
        assertEquals(BattleEngine.apply(checkpoint, record.commands[3]), original)
    }

    @Test
    fun `battle ends with a decisive outcome under normal conditions`() {
        val outcomes = (1L..30L).map { BattleEngine.runAuto(BattleFixtures.balancedBattle(it)).finalState.outcome }
        assertTrue(outcomes.all { it != null })
        assertTrue(outcomes.any { it == BattleOutcome.VICTORY })
    }

    @Test
    fun `critical hits happen when crit rate is positive`() {
        val criticals = (1L..20L).flatMap { BattleEngine.runAuto(BattleFixtures.balancedBattle(it)).events }
            .filterIsInstance<BattleEvent.DamageDealt>()
            .count { it.critical }
        assertTrue(criticals > 0)
    }

    @Test
    fun `skills and ultimates are actually cast`() {
        val used = (1L..20L).flatMap { BattleEngine.runAuto(BattleFixtures.balancedBattle(it)).events }
            .filterIsInstance<BattleEvent.SkillUsed>()
            .map { it.skillId }
            .toSet()
        assertTrue("warrior.cleave" in used)
        assertTrue(used.any { it.endsWith("earthsplitter") || it.endsWith("meteor") || it.endsWith("dawn_hymn") })
    }

    @Test
    fun `energy starts at configured value and ultimate is not legal on first turn`() {
        val step = BattleEngine.start(BattleFixtures.balancedBattle(seed = 3))
        assertTrue(step.state.combatants.all { it.energy == rules.startingEnergy })
        val legalSkills = BattleEngine.legalCommands(step.state).map { it.skillId }
        assertTrue(legalSkills.none { it.endsWith("earthsplitter") || it.endsWith("meteor") })
    }

    @Test
    fun `a skill on cooldown is not legal until its cooldown has ticked down on the owner's turns`() {
        val duelist = BattleFixtures.hero("warrior", lane = 0, depth = 0).copy(stats = stats(hp = 100_000, attack = 1, defense = 0, speed = 300))
        val dummy = custom("dummy", "neutral", GridCell(0, 0), stats(hp = 100_000, attack = 1, defense = 0, speed = 100))
        var state = BattleEngine.start(BattleSetup(12, listOf(duelist), listOf(dummy), rules)).state
        val cleave = BattleEngine.legalCommands(state).first { it.skillId == "warrior.cleave" }
        state = BattleEngine.apply(state, cleave).state
        val availability = mutableListOf<Boolean>()
        repeat(6) {
            if (state.activeUnit?.side == BattleSide.ALLY) {
                availability += BattleEngine.legalCommands(state).any { it.skillId == "warrior.cleave" }
                state = BattleEngine.apply(state, BattleEngine.legalCommands(state).first { it.skillId == "warrior.slash" }).state
            } else {
                state = BattleEngine.apply(state, BattleEngine.autoCommand(state)).state
            }
        }
        assertEquals(listOf(false, true), availability.take(2))
    }

    @Test
    fun `commands from a unit that is not active are rejected`() {
        val step = BattleEngine.start(BattleFixtures.balancedBattle(seed = 3))
        val active = requireNotNull(step.state.activeUnit)
        val intruder = step.state.combatants.first { it.id != active }
        assertThrows<IllegalBattleCommand> {
            BattleEngine.apply(step.state, BattleCommand(intruder.id, intruder.setup.skills.first().id, null))
        }
    }

    @Test
    fun `faster unit acts first`() {
        val fast = custom("fast", "warrior", GridCell(0, 0), stats(hp = 100, attack = 10, defense = 0, speed = 200))
        val slow = custom("slow", "warrior", GridCell(0, 0), stats(hp = 100, attack = 10, defense = 0, speed = 50))
        val step = BattleEngine.start(BattleSetup(1, listOf(slow), listOf(fast), rules))
        assertEquals(UnitId(BattleSide.ENEMY, 0), step.state.activeUnit)
    }

    @Test
    fun `class advantage multiplies damage`() {
        val warrior = custom("w", "warrior", GridCell(0, 0), stats(hp = 5000, attack = 100, defense = 0, speed = 200, accuracy = 1000))
        val assassin = custom("a", "assassin", GridCell(0, 0), stats(hp = 5000, attack = 100, defense = 0, speed = 50))
        val record = BattleEngine.runAuto(BattleSetup(5, listOf(warrior), listOf(assassin), rules.copy(maxRounds = 2)))
        val damages = record.events.filterIsInstance<BattleEvent.DamageDealt>()
        assertTrue(damages.filter { it.source.side == BattleSide.ALLY }.all { it.matchupPermille == 1250 })
        assertTrue(damages.filter { it.source.side == BattleSide.ENEMY }.all { it.matchupPermille == 850 })
    }

    @Test
    fun `back row takes less damage than front row`() {
        fun damageTo(depth: Int): Int {
            val attacker = custom("a", "neutral", GridCell(0, 0), stats(hp = 5000, attack = 200, defense = 0, speed = 300, accuracy = 1000))
            val target = custom("t", "neutral", GridCell(0, depth), stats(hp = 50_000, attack = 1, defense = 0, speed = 1))
            val step = BattleEngine.start(BattleSetup(9, listOf(attacker), listOf(target), rules.copy(damageVariancePermille = 0)))
            val hit = BattleEngine.apply(step.state, BattleEngine.legalCommands(step.state).first())
            return hit.events.filterIsInstance<BattleEvent.DamageDealt>().single().amount
        }
        assertEquals(200, damageTo(0))
        assertEquals(160, damageTo(2))
    }

    @Test
    fun `taunt forces single target attacks onto the taunting unit`() {
        val setup = BattleSetup(
            seed = 4,
            allies = listOf(custom("hero", "neutral", GridCell(1, 0), stats(hp = 1000, attack = 50, defense = 0, speed = 100))),
            enemies = listOf(
                custom("front", "neutral", GridCell(1, 0), stats(hp = 1000, attack = 10, defense = 0, speed = 300)),
                custom(
                    "guardian", "neutral", GridCell(1, 2), stats(hp = 1000, attack = 10, defense = 0, speed = 250),
                    skills = listOf(
                        SkillDefinition("guardian.hit", SkillSlot.BASIC, 0, Targeting.SingleEnemy, listOf(SkillEffect.Damage(100))),
                        SkillDefinition("guardian.provoke", SkillSlot.SKILL, 0, Targeting.Self, listOf(SkillEffect.ApplyStatus("taunt"))),
                    ),
                ),
            ),
            rules = rules,
        )
        var state = BattleEngine.start(setup).state
        while (state.activeUnit != UnitId(BattleSide.ALLY, 0)) state = BattleEngine.apply(state, BattleEngine.autoCommand(state)).state
        val targets = BattleEngine.legalCommands(state).map { it.target }.toSet()
        assertEquals(setOf(UnitId(BattleSide.ENEMY, 1)), targets)
    }

    @Test
    fun `stunned unit skips its turn`() {
        val stunner = custom(
            "stunner", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 10, defense = 0, speed = 300),
            skills = listOf(SkillDefinition("stunner.jolt", SkillSlot.BASIC, 0, Targeting.SingleEnemy, listOf(SkillEffect.ApplyStatus("stun")))),
        )
        val victim = custom("victim", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 10, defense = 0, speed = 200))
        val step = BattleEngine.start(BattleSetup(8, listOf(stunner), listOf(victim), rules))
        val after = BattleEngine.apply(step.state, BattleEngine.legalCommands(step.state).single())
        assertTrue(after.events.any { it == BattleEvent.TurnSkipped(UnitId(BattleSide.ENEMY, 0), "stun") })
        assertEquals(UnitId(BattleSide.ALLY, 0), after.state.activeUnit)
    }

    @Test
    fun `silence limits the unit to its basic skill`() {
        val silencer = custom(
            "silencer", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 10, defense = 0, speed = 300),
            skills = listOf(SkillDefinition("silencer.hush", SkillSlot.BASIC, 0, Targeting.SingleEnemy, listOf(SkillEffect.ApplyStatus("silence")))),
        )
        val caster = BattleFixtures.hero("mage", lane = 0, depth = 0).copy(stats = stats(hp = 1000, attack = 10, defense = 0, speed = 200))
        val boosted = rules.copy(startingEnergy = 100)
        val step = BattleEngine.start(BattleSetup(8, listOf(silencer), listOf(caster), boosted))
        val after = BattleEngine.apply(step.state, BattleEngine.legalCommands(step.state).single())
        assertEquals(setOf("mage.spark"), BattleEngine.legalCommands(after.state).map { it.skillId }.toSet())
    }

    @Test
    fun `shield absorbs damage before hp`() {
        val tank = custom(
            "tank", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 100, defense = 0, speed = 300),
            skills = listOf(SkillDefinition("tank.guard", SkillSlot.BASIC, 0, Targeting.Self, listOf(SkillEffect.ApplyStatus("shield")))),
        )
        val hitter = custom("hitter", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 100, defense = 0, speed = 200, accuracy = 1000))
        var state = BattleEngine.start(BattleSetup(2, listOf(tank), listOf(hitter), rules.copy(damageVariancePermille = 0))).state
        state = BattleEngine.apply(state, BattleEngine.legalCommands(state).single()).state
        val hit = BattleEngine.apply(state, BattleEngine.legalCommands(state).single())
        val damage = hit.events.filterIsInstance<BattleEvent.DamageDealt>().single()
        assertEquals(100, damage.absorbedByShield)
        assertEquals(0, damage.amount)
        assertEquals(1000, damage.remainingHp)
    }

    @Test
    fun `damage over time ticks at the start of the victim's turn`() {
        val burner = custom(
            "burner", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 10, defense = 0, speed = 300),
            skills = listOf(SkillDefinition("burner.ignite", SkillSlot.BASIC, 0, Targeting.SingleEnemy, listOf(SkillEffect.ApplyStatus("burn")))),
        )
        val victim = custom("victim", "neutral", GridCell(0, 0), stats(hp = 1000, attack = 10, defense = 0, speed = 200))
        val step = BattleEngine.start(BattleSetup(6, listOf(burner), listOf(victim), rules))
        val after = BattleEngine.apply(step.state, BattleEngine.legalCommands(step.state).single())
        val tick = after.events.filterIsInstance<BattleEvent.DamageDealt>().single()
        assertEquals(60, tick.amount)
        assertEquals(940, after.state.combatant(UnitId(BattleSide.ENEMY, 0)).hp)
    }

    @Test
    fun `stalemate ends in a draw after max rounds instead of a defeat`() {
        val wall = stats(hp = 1_000_000, attack = 1, defense = 10_000, speed = 100)
        val setup = BattleSetup(3, listOf(custom("a", "neutral", GridCell(0, 0), wall)), listOf(custom("b", "neutral", GridCell(0, 0), wall)), rules.copy(maxRounds = 5))
        val record = BattleEngine.runAuto(setup)
        assertEquals(BattleOutcome.DRAW, record.finalState.outcome)
        assertEquals(BattleEvent.BattleEnded(BattleOutcome.DRAW, 5), record.events.last())
    }

    @Test
    fun `hp and energy invariants hold across many battles`() {
        (1L..50L).forEach { seed ->
            val record = BattleEngine.runAuto(BattleFixtures.balancedBattle(seed))
            record.events.filterIsInstance<BattleEvent.DamageDealt>().forEach { assertTrue(it.remainingHp >= 0 && it.amount >= 0) }
            record.events.filterIsInstance<BattleEvent.EnergyChanged>().forEach { assertTrue(it.energy in 0..rules.maxEnergy) }
            record.finalState.combatants.forEach { assertTrue(it.hp in 0..it.maxHp) }
            assertTrue(record.events.filterIsInstance<BattleEvent.BattleEnded>().size == 1)
        }
    }
}
