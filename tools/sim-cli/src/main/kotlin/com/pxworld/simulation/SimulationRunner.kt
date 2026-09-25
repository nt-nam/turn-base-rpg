package com.pxworld.simulation

import com.pxworld.content.ContentBundle
import com.pxworld.content.EncounterRecord
import com.pxworld.content.balance.EncounterSimulator
import com.pxworld.content.balance.Formation
import com.pxworld.content.balance.LineupAssembler
import com.pxworld.content.balance.LineupMember
import com.pxworld.content.balance.SeedRange
import com.pxworld.content.balance.SimulationResult
import com.pxworld.content.compiler.ContentCompilation

data class SimulationJob(val encounterId: String, val lineupName: String, val members: List<LineupMember>)

data class EncounterRun(val job: SimulationJob, val result: SimulationResult)

data class SimulationReport(val seeds: SeedRange, val runs: List<EncounterRun>)

class SimulationRunner(private val bundle: ContentBundle) {

    private val simulator = EncounterSimulator(bundle)
    private val assembler = LineupAssembler(bundle)
    private val heroes = bundle.heroes.associateBy { it.id }
    private val equipment = bundle.equipment.associateBy { it.id }

    fun plan(request: SimulationRequest): List<SimulationJob> = encounters(request.encounter).map { encounter ->
        when (val selection = request.lineup) {
            LineupSelection.Reference -> SimulationJob(encounter.id, REFERENCE_LINEUP, referenceLineup(request.defaultLevel ?: encounter.recommendedLevel))
            is LineupSelection.Heroes -> SimulationJob(encounter.id, CUSTOM_LINEUP, explicitLineup(selection.heroes, encounter, request))
        }
    }

    fun run(request: SimulationRequest): SimulationReport =
        SimulationReport(request.seeds, plan(request).map { job -> EncounterRun(job, simulator.simulate(job.encounterId, assembler.slots(job.members), request.seeds)) })

    private fun encounters(selection: EncounterSelection): List<EncounterRecord> = when (selection) {
        EncounterSelection.All -> bundle.encounters
        is EncounterSelection.Only -> listOf(
            bundle.encounters.firstOrNull { it.id == selection.encounterId }
                ?: throw UsageException("unknown encounter ${selection.encounterId}; known: ${bundle.encounters.joinToString { it.id }}"),
        )
    }

    private fun referenceLineup(level: Int): List<LineupMember> =
        ContentCompilation.referenceLineup(bundle, level).map { LineupMember(it.heroId, it.level, it.star, it.cell) }

    private fun explicitLineup(requested: List<HeroRequest>, encounter: EncounterRecord, request: SimulationRequest): List<LineupMember> {
        if (requested.size > Formation.CAPACITY) throw UsageException("a lineup holds at most ${Formation.CAPACITY} heroes, got ${requested.size}")
        requested.groupingBy { it.heroId }.eachCount().filterValues { it > 1 }.keys.firstOrNull()?.let { throw UsageException("$it is listed more than once") }
        val records = requested.map { hero -> heroes[hero.heroId] ?: throw UsageException("unknown hero ${hero.heroId}; known: ${bundle.heroes.joinToString { it.id }}") }
        requested.forEach(::checkEquipment)
        val fixed = requested.withIndex().mapNotNull { (index, hero) -> hero.cell?.let { index to it } }
        fixed.groupBy({ it.second }, { requested[it.first].heroId }).filterValues { it.size > 1 }.entries.firstOrNull()?.let { (cell, owners) ->
            throw UsageException("${owners.joinToString(" and ")} are both placed at ${cell.lane}-${cell.depth}")
        }
        val cells = Formation.place(records.map { it.classId }, fixed.toMap())
        return requested.mapIndexed { index, hero ->
            LineupMember(hero.heroId, hero.level ?: request.defaultLevel ?: encounter.recommendedLevel, hero.star ?: request.defaultStar, cells[index], hero.equipment)
        }
    }

    private fun checkEquipment(hero: HeroRequest) {
        val records = hero.equipment.map { equipmentId -> equipment[equipmentId] ?: throw UsageException("unknown equipment $equipmentId for ${hero.heroId}") }
        records.groupBy { it.slot }.filterValues { it.size > 1 }.keys.firstOrNull()?.let { slot -> throw UsageException("${hero.heroId} wears more than one $slot item") }
    }

    companion object {
        const val REFERENCE_LINEUP: String = "reference"
        const val CUSTOM_LINEUP: String = "custom"
    }
}
