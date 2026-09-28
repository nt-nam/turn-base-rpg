package com.pxworld.content.balance

import com.pxworld.domain.battle.GridCell
import kotlin.math.abs

object Formation {

    const val CAPACITY: Int = GridCell.GRID_SIZE * GridCell.GRID_SIZE

    private val PREFERRED_CELLS: Map<String, GridCell> = mapOf(
        "class.tank" to GridCell(lane = 1, depth = 0),
        "class.warrior" to GridCell(lane = 0, depth = 0),
        "class.assassin" to GridCell(lane = 2, depth = 0),
        "class.ranger" to GridCell(lane = 0, depth = 1),
        "class.mage" to GridCell(lane = 1, depth = 2),
        "class.support" to GridCell(lane = 2, depth = 2),
    )
    private val UNKNOWN_CLASS_CELL = GridCell(lane = 1, depth = 1)
    private val ALL_CELLS: List<GridCell> = (0 until GridCell.GRID_SIZE).flatMap { depth -> (0 until GridCell.GRID_SIZE).map { lane -> GridCell(lane, depth) } }

    fun preferredCell(classId: String): GridCell = PREFERRED_CELLS[classId] ?: UNKNOWN_CLASS_CELL

    fun place(classIds: List<String>, fixed: Map<Int, GridCell> = emptyMap()): List<GridCell> {
        require(classIds.size <= CAPACITY) { "a lineup holds at most $CAPACITY heroes, got ${classIds.size}" }
        require(fixed.keys.all { it in classIds.indices }) { "fixed cells refer to heroes outside the lineup" }
        require(fixed.values.toSet().size == fixed.size) { "two heroes are fixed to the same cell" }
        val taken = fixed.values.toMutableSet()
        return classIds.mapIndexed { index, classId ->
            fixed[index] ?: nearestFree(preferredCell(classId), taken).also { taken += it }
        }
    }

    private fun nearestFree(preferred: GridCell, taken: Set<GridCell>): GridCell =
        ALL_CELLS.filterNot { it in taken }
            .minWith(compareBy({ abs(it.depth - preferred.depth) }, { abs(it.lane - preferred.lane) }, { it.depth }, { it.lane }))
}
