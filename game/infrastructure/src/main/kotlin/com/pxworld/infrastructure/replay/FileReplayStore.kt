package com.pxworld.infrastructure.replay

import com.pxworld.application.ReplayRecord
import com.pxworld.application.ReplayRepository
import com.pxworld.application.ReplaySlot
import com.pxworld.domain.battle.BattleCommand
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.BattleSide
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.battle.UnitId
import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatKind
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class ReplaySlotDocument(val heroId: String, val level: Int, val star: Int, val lane: Int, val depth: Int, val bonus: Map<String, Int>)

@Serializable
data class ReplayCommandDocument(val actor: String, val skill: String, val target: String? = null)

@Serializable
data class ReplayDocument(
    val id: String,
    val encounterId: String,
    val seed: Long,
    val lineup: List<ReplaySlotDocument>,
    val commands: List<ReplayCommandDocument>,
    val outcome: String,
    val rounds: Int,
    val recordedAtMillis: Long,
)

class FileReplayStore(private val directory: File, private val capacity: Int = 30) : ReplayRepository {

    override fun list(): List<ReplayRecord> =
        directory.listFiles { file -> file.extension == "json" }.orEmpty()
            .mapNotNull { runCatching { ReplayCodec.decode(it.readText()) }.getOrNull() }
            .sortedByDescending { it.recordedAtMillis }

    override fun save(record: ReplayRecord) {
        directory.mkdirs()
        File(directory, "${record.id}.json").writeText(ReplayCodec.encode(record))
        list().drop(capacity).forEach { File(directory, "${it.id}.json").delete() }
    }

    override fun load(id: String): ReplayRecord? =
        File(directory, "$id.json").takeIf { it.isFile }?.let { ReplayCodec.decode(it.readText()) }
}

object ReplayCodec {

    val json = Json { prettyPrint = false; ignoreUnknownKeys = true }

    fun encode(record: ReplayRecord): String = json.encodeToString(ReplayDocument.serializer(), document(record))

    fun decode(text: String): ReplayRecord = record(json.decodeFromString(ReplayDocument.serializer(), text))

    fun document(record: ReplayRecord) = ReplayDocument(
        id = record.id,
        encounterId = record.encounterId,
        seed = record.seed,
        lineup = record.lineup.map { slot ->
            ReplaySlotDocument(slot.heroId, slot.level, slot.star, slot.cell.lane, slot.cell.depth, slot.bonus.toMap().filterValues { it != 0 }.mapKeys { it.key.name })
        },
        commands = record.commands.map { ReplayCommandDocument(it.actor.toString(), it.skillId, it.target?.toString()) },
        outcome = record.outcome.name,
        rounds = record.rounds,
        recordedAtMillis = record.recordedAtMillis,
    )

    fun record(document: ReplayDocument): ReplayRecord {
        return ReplayRecord(
            id = document.id,
            encounterId = document.encounterId,
            seed = document.seed,
            lineup = document.lineup.map { slot ->
                ReplaySlot(
                    slot.heroId, slot.level, slot.star, GridCell(slot.lane, slot.depth),
                    slot.bonus.entries.fold(StatBlock.EMPTY) { block, (stat, value) -> block.with(StatKind.valueOf(stat), value) },
                )
            },
            commands = document.commands.map { BattleCommand(unit(it.actor), it.skill, it.target?.let(::unit)) },
            outcome = BattleOutcome.valueOf(document.outcome),
            rounds = document.rounds,
            recordedAtMillis = document.recordedAtMillis,
        )
    }

    private fun unit(text: String): UnitId {
        val (side, slot) = text.split("#")
        return UnitId(BattleSide.valueOf(side.uppercase()), slot.toInt())
    }
}
