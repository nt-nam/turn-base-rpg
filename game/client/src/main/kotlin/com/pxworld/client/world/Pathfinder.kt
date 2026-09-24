package com.pxworld.client.world

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.hypot

class Pathfinder(private val layout: MapLayout, private val bodyWidth: Float, private val bodyHeight: Float, private val cell: Float = CELL) {

    private val columns = (layout.widthPixels / cell).toInt()
    private val rows = (layout.heightPixels / cell).toInt()
    private val walkable = BooleanArray(columns * rows) { index ->
        val x = (index % columns + 0.5f) * cell
        val y = (index / columns + 0.5f) * cell
        !layout.blocked(Rectangle(x - bodyWidth / 2, y, bodyWidth, bodyHeight))
    }

    fun route(from: Vector2, to: Vector2): List<Vector2> {
        val start = nearestWalkable(cellOf(from)) ?: return emptyList()
        val goal = nearestWalkable(cellOf(to)) ?: return emptyList()
        val cost = HashMap<Int, Float>()
        val parent = HashMap<Int, Int>()
        val open = PriorityQueue<Pair<Int, Float>>(compareBy { it.second })
        cost[start] = 0f
        open += start to heuristic(start, goal)
        while (open.isNotEmpty()) {
            val (current, _) = open.poll()
            if (current == goal) return smooth(reconstruct(parent, current).map(::centerOf))
            for ((neighbor, step) in neighbors(current)) {
                val candidate = cost.getValue(current) + step
                if (candidate < (cost[neighbor] ?: Float.MAX_VALUE)) {
                    cost[neighbor] = candidate
                    parent[neighbor] = current
                    open += neighbor to candidate + heuristic(neighbor, goal)
                }
            }
        }
        return emptyList()
    }

    private fun neighbors(index: Int): List<Pair<Int, Float>> {
        val column = index % columns
        val row = index / columns
        val result = mutableListOf<Pair<Int, Float>>()
        for (dx in -1..1) for (dy in -1..1) {
            if (dx == 0 && dy == 0) continue
            val c = column + dx
            val r = row + dy
            if (c !in 0 until columns || r !in 0 until rows) continue
            val next = r * columns + c
            if (!walkable[next]) continue
            if (dx != 0 && dy != 0 && (!walkable[row * columns + c] || !walkable[r * columns + column])) continue
            result += next to if (dx != 0 && dy != 0) DIAGONAL else 1f
        }
        return result
    }

    private fun nearestWalkable(index: Int): Int? {
        if (index in walkable.indices && walkable[index]) return index
        val column = index % columns
        val row = index / columns
        for (radius in 1..SEARCH_RADIUS) {
            val ring = (-radius..radius).flatMap { dx -> (-radius..radius).map { dy -> dx to dy } }
                .filter { (dx, dy) -> maxOf(abs(dx), abs(dy)) == radius }
                .map { (dx, dy) -> (row + dy) to (column + dx) }
                .filter { (r, c) -> r in 0 until rows && c in 0 until columns && walkable[r * columns + c] }
                .minByOrNull { (r, c) -> hypot((r - row).toFloat(), (c - column).toFloat()) }
            if (ring != null) return ring.first * columns + ring.second
        }
        return null
    }

    private fun reconstruct(parent: Map<Int, Int>, end: Int): List<Int> {
        val path = mutableListOf(end)
        var cursor = end
        while (true) {
            cursor = parent[cursor] ?: break
            path += cursor
        }
        return path.reversed()
    }

    private fun smooth(points: List<Vector2>): List<Vector2> =
        points.filterIndexed { index, _ -> index == points.lastIndex || index % SMOOTHING_STRIDE == 0 }

    private fun heuristic(a: Int, b: Int): Float = hypot((a % columns - b % columns).toFloat(), (a / columns - b / columns).toFloat())

    private fun cellOf(point: Vector2): Int =
        (point.y / cell).toInt().coerceIn(0, rows - 1) * columns + (point.x / cell).toInt().coerceIn(0, columns - 1)

    private fun centerOf(index: Int): Vector2 = Vector2((index % columns + 0.5f) * cell, (index / columns + 0.5f) * cell)

    companion object {
        const val CELL: Float = 8f
        const val DIAGONAL: Float = 1.4142f
        const val SEARCH_RADIUS: Int = 12
        const val SMOOTHING_STRIDE: Int = 3
    }
}
