package com.pxworld.content.balance

import com.pxworld.content.BattleContentAssembler
import com.pxworld.content.ContentBundle
import com.pxworld.content.LineupSlot
import com.pxworld.domain.PERMILLE
import com.pxworld.domain.battle.BattleEngine
import com.pxworld.domain.battle.BattleEvent
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.BattleRecord
import com.pxworld.domain.battle.BattleSide
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.battle.UnitId

data class SeedRange(val first: Long, val count: Int) {
    init {
        require(count > 0) { "seed count must be positive, was $count" }
        require(first <= Long.MAX_VALUE - count) { "seed range starting at $first overflows" }
    }

    val seeds: LongRange get() = first until first + count

    val last: Long get() = first + count - 1

    companion object {
        const val DEFAULT_FIRST: Long = 1
        const val DEFAULT_COUNT: Int = 200
        val DEFAULT: SeedRange = SeedRange(DEFAULT_FIRST, DEFAULT_COUNT)
    }
}

data class SimulatedUnit(val id: UnitId, val contentId: String, val classId: String, val level: Int, val star: Int, val cell: GridCell)

data class UnitTotals(val unit: SimulatedUnit, val damageDealt: Long, val damageTaken: Long, val healing: Long, val deaths: Int)

data class SimulationResult(
    val encounterId: String,
    val lineup: List<LineupSlot>,
    val seeds: SeedRange,
    val victories: Int,
    val draws: Int,
    val defeats: Int,
    val sortedRounds: List<Int>,
    val units: List<UnitTotals>,
) {
    val battles: Int get() = seeds.count
    val winPermille: Int get() = permilleOf(victories)
    val drawPermille: Int get() = permilleOf(draws)
    val lossPermille: Int get() = permilleOf(defeats)
    val totalRounds: Long get() = sortedRounds.fold(0L) { total, round -> total + round }

    fun roundsAtPercentile(percent: Int): Int {
        require(percent in 1..100) { "percentile must be in 1..100, was $percent" }
        val rank = (percent.toLong() * sortedRounds.size + 99) / 100
        return sortedRounds[(rank - 1).toInt()]
    }

    fun permilleOf(count: Int): Int = (count.toLong() * PERMILLE / battles).toInt()
}

class EncounterSimulator(bundle: ContentBundle) {

    private val assembler = BattleContentAssembler(bundle)
    private val encounters = bundle.encounters.associateBy { it.id }

    fun simulate(encounterId: String, lineup: List<LineupSlot>, seeds: SeedRange = SeedRange.DEFAULT): SimulationResult {
        val encounter = encounters[encounterId] ?: throw IllegalArgumentException("unknown encounter $encounterId")
        val setup = assembler.battle(seeds.first, lineup, encounterId)
        val units = setup.allies.mapIndexed { slot, ally ->
            SimulatedUnit(UnitId(BattleSide.ALLY, slot), ally.name, ally.classId, ally.level, lineup[slot].star, ally.cell)
        } + setup.enemies.mapIndexed { slot, enemy ->
            SimulatedUnit(UnitId(BattleSide.ENEMY, slot), enemy.name, enemy.classId, enemy.level, encounter.enemies[slot].star, enemy.cell)
        }
        val tally = SimulationTally(units)
        seeds.seeds.forEach { seed -> tally.record(BattleEngine.runAuto(setup.copy(seed = seed))) }
        return tally.result(encounterId, lineup, seeds)
    }
}

private class SimulationTally(private val units: List<SimulatedUnit>) {

    private val positions = units.withIndex().associate { it.value.id to it.index }
    private val damageDealt = LongArray(units.size)
    private val damageTaken = LongArray(units.size)
    private val healing = LongArray(units.size)
    private val deaths = IntArray(units.size)
    private val rounds = mutableListOf<Int>()
    private var victories = 0
    private var draws = 0
    private var defeats = 0

    fun record(record: BattleRecord) {
        record.events.forEach { event ->
            when (event) {
                is BattleEvent.DamageDealt -> {
                    val total = event.amount.toLong() + event.absorbedByShield
                    damageDealt[position(event.source)] += total
                    damageTaken[position(event.target)] += total
                }
                is BattleEvent.Healed -> healing[position(event.source)] += event.amount.toLong()
                is BattleEvent.UnitDefeated -> deaths[position(event.unit)] += 1
                is BattleEvent.BattleEnded -> rounds += event.round
                else -> Unit
            }
        }
        when (record.finalState.outcome) {
            BattleOutcome.VICTORY -> victories += 1
            BattleOutcome.DRAW -> draws += 1
            BattleOutcome.DEFEAT -> defeats += 1
            null -> throw IllegalStateException("automatic battle ended without an outcome")
        }
    }

    fun result(encounterId: String, lineup: List<LineupSlot>, seeds: SeedRange): SimulationResult = SimulationResult(
        encounterId = encounterId,
        lineup = lineup,
        seeds = seeds,
        victories = victories,
        draws = draws,
        defeats = defeats,
        sortedRounds = rounds.sorted(),
        units = units.mapIndexed { index, unit -> UnitTotals(unit, damageDealt[index], damageTaken[index], healing[index], deaths[index]) },
    )

    private fun position(id: UnitId): Int = positions[id] ?: throw IllegalStateException("event for unknown unit $id")
}
