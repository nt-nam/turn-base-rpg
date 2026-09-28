package com.pxworld.simulation

import com.pxworld.content.balance.SimulationResult
import com.pxworld.content.balance.UnitTotals
import com.pxworld.domain.PERMILLE
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object Figures {

    fun percent(permille: Int): String = "${permille / 10}.${permille % 10}%"

    fun tenths(total: Long, count: Int): Long = (total * 10 + count / 2) / count

    fun decimal(tenths: Long): String = "${tenths / 10}.${tenths % 10}"

    fun average(total: Long, count: Int): String = decimal(tenths(total, count))

    fun ratio(permille: Int): Double = permille / PERMILLE.toDouble()

    fun averageNumber(total: Long, count: Int): Double = tenths(total, count) / 10.0
}

object TableReport {

    const val NINETIETH: Int = 90
    private val NUMERIC_COLUMNS = setOf("level", "stars", "dealt/battle", "taken/battle", "healed/battle", "deaths")
    private val UNIT_HEADER = listOf("unit" to 9, "id" to 28, "cell" to 5, "level" to 6, "stars" to 6, "dealt/battle" to 13, "taken/battle" to 13, "healed/battle" to 14, "deaths" to 7)

    fun render(report: SimulationReport): String = report.runs.joinToString(separator = "\n") { run(it, report) }.trimEnd()

    private fun run(run: EncounterRun, report: SimulationReport): String = buildString {
        val result = run.result
        appendLine("${result.encounterId}  lineup ${run.job.lineupName}  seeds ${report.seeds.first}..${report.seeds.last}")
        appendLine(
            "  win ${Figures.percent(result.winPermille)}  draw ${Figures.percent(result.drawPermille)}  loss ${Figures.percent(result.lossPermille)}" +
                "  rounds mean ${Figures.average(result.totalRounds, result.battles)} p90 ${result.roundsAtPercentile(NINETIETH)}",
        )
        appendLine("  " + UNIT_HEADER.joinToString(" ") { (title, width) -> title.padEnd(width) }.trimEnd())
        result.units.forEach { appendLine("  " + unitRow(it, result)) }
    }

    private fun unitRow(totals: UnitTotals, result: SimulationResult): String {
        val unit = totals.unit
        val cells = listOf(
            unit.id.toString(),
            unit.contentId,
            "${unit.cell.lane}-${unit.cell.depth}",
            unit.level.toString(),
            unit.star.toString(),
            Figures.average(totals.damageDealt, result.battles),
            Figures.average(totals.damageTaken, result.battles),
            Figures.average(totals.healing, result.battles),
            Figures.percent(result.permilleOf(totals.deaths)),
        )
        return cells.zip(UNIT_HEADER).joinToString(" ") { (text, column) -> if (column.first in NUMERIC_COLUMNS) text.padStart(column.second) else text.padEnd(column.second) }.trimEnd()
    }
}

@Serializable
data class LineupMemberDocument(val hero: String, val level: Int, val star: Int, val lane: Int, val depth: Int, val equipment: List<String>)

@Serializable
data class UnitDocument(
    val unit: String,
    val id: String,
    val classId: String,
    val level: Int,
    val star: Int,
    val lane: Int,
    val depth: Int,
    val damageDealtPerBattle: Double,
    val damageTakenPerBattle: Double,
    val healingPerBattle: Double,
    val deathRate: Double,
)

@Serializable
data class RunDocument(
    val encounter: String,
    val lineup: String,
    val heroes: List<LineupMemberDocument>,
    val battles: Int,
    val victories: Int,
    val draws: Int,
    val defeats: Int,
    val winRate: Double,
    val drawRate: Double,
    val lossRate: Double,
    val meanRounds: Double,
    val p90Rounds: Int,
    val units: List<UnitDocument>,
)

@Serializable
data class ReportDocument(val seedStart: Long, val seeds: Int, val runs: List<RunDocument>)

object JsonReport {

    private val json = Json { prettyPrint = true; prettyPrintIndent = "  " }

    fun document(report: SimulationReport): ReportDocument = ReportDocument(report.seeds.first, report.seeds.count, report.runs.map(::run))

    fun render(report: SimulationReport): String = json.encodeToString(ReportDocument.serializer(), document(report))

    private fun run(run: EncounterRun): RunDocument {
        val result = run.result
        return RunDocument(
            encounter = result.encounterId,
            lineup = run.job.lineupName,
            heroes = run.job.members.map { LineupMemberDocument(it.heroId, it.level, it.star, it.cell.lane, it.cell.depth, it.equipment) },
            battles = result.battles,
            victories = result.victories,
            draws = result.draws,
            defeats = result.defeats,
            winRate = Figures.ratio(result.winPermille),
            drawRate = Figures.ratio(result.drawPermille),
            lossRate = Figures.ratio(result.lossPermille),
            meanRounds = Figures.averageNumber(result.totalRounds, result.battles),
            p90Rounds = result.roundsAtPercentile(TableReport.NINETIETH),
            units = result.units.map { totals ->
                val unit = totals.unit
                UnitDocument(
                    unit = unit.id.toString(),
                    id = unit.contentId,
                    classId = unit.classId,
                    level = unit.level,
                    star = unit.star,
                    lane = unit.cell.lane,
                    depth = unit.cell.depth,
                    damageDealtPerBattle = Figures.averageNumber(totals.damageDealt, result.battles),
                    damageTakenPerBattle = Figures.averageNumber(totals.damageTaken, result.battles),
                    healingPerBattle = Figures.averageNumber(totals.healing, result.battles),
                    deathRate = Figures.ratio(result.permilleOf(totals.deaths)),
                )
            },
        )
    }
}
