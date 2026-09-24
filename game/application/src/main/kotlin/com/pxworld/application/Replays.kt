package com.pxworld.application

import com.pxworld.domain.battle.BattleCommand
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.stats.StatBlock

data class ReplaySlot(val heroId: String, val level: Int, val star: Int, val cell: GridCell, val bonus: StatBlock)

data class ReplayRecord(
    val id: String,
    val encounterId: String,
    val seed: Long,
    val lineup: List<ReplaySlot>,
    val commands: List<BattleCommand>,
    val outcome: BattleOutcome,
    val rounds: Int,
    val recordedAtMillis: Long,
)

interface ReplayRepository {
    fun list(): List<ReplayRecord>
    fun save(record: ReplayRecord)
    fun load(id: String): ReplayRecord?
}

class InMemoryReplays(private val capacity: Int = 20) : ReplayRepository {
    private val records = ArrayDeque<ReplayRecord>()

    override fun list(): List<ReplayRecord> = records.toList().sortedByDescending { it.recordedAtMillis }

    override fun save(record: ReplayRecord) {
        records.removeAll { it.id == record.id }
        records.addLast(record)
        while (records.size > capacity) records.removeFirst()
    }

    override fun load(id: String): ReplayRecord? = records.firstOrNull { it.id == id }
}
