package com.pxworld.client.world

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

sealed interface Collider {
    fun blocks(area: Rectangle): Boolean

    data class Box(val bounds: Rectangle) : Collider {
        override fun blocks(area: Rectangle): Boolean = bounds.overlaps(area)
    }

    class Shape(private val polygon: Polygon) : Collider {
        private val vertices = polygon.transformedVertices
        override fun blocks(area: Rectangle): Boolean {
            if (!polygon.boundingRectangle.overlaps(area)) return false
            val corners = listOf(area.x to area.y, area.x + area.width to area.y, area.x to area.y + area.height, area.x + area.width to area.y + area.height)
            if (corners.any { (x, y) -> polygon.contains(x, y) }) return true
            return segments(vertices, closed = true).any { (a, b) -> Intersector.intersectSegmentRectangle(a, b, area) }
        }
    }

    class Line(private val points: FloatArray) : Collider {
        override fun blocks(area: Rectangle): Boolean =
            segments(points, closed = false).any { (a, b) -> Intersector.intersectSegmentRectangle(a, b, area) }
    }
}

private fun segments(vertices: FloatArray, closed: Boolean): List<Pair<Vector2, Vector2>> {
    val count = vertices.size / 2
    val edges = (0 until count - 1).map { Vector2(vertices[it * 2], vertices[it * 2 + 1]) to Vector2(vertices[it * 2 + 2], vertices[it * 2 + 3]) }
    return if (closed && count > 2) edges + (Vector2(vertices[count * 2 - 2], vertices[count * 2 - 1]) to Vector2(vertices[0], vertices[1])) else edges
}

data class SpawnPoint(val index: Int, val x: Float, val y: Float)

data class TeleportTrigger(val bounds: Rectangle, val targetLegacyMap: String, val targetSpawn: Int, val label: String)

data class EncounterTrigger(val bounds: Rectangle, val objectId: Int)

class MapLayout(map: TiledMap) {

    val widthPixels: Float
    val heightPixels: Float
    val colliders: List<Collider>
    val spawns: List<SpawnPoint>
    val teleports: List<TeleportTrigger>
    val encounters: List<EncounterTrigger>

    init {
        val properties = map.properties
        widthPixels = properties.get("width", Int::class.java).toFloat() * properties.get("tilewidth", Int::class.java)
        heightPixels = properties.get("height", Int::class.java).toFloat() * properties.get("tileheight", Int::class.java)
        fun layer(name: String): List<MapObject> = map.layers.get(name)?.objects?.toList().orEmpty()

        colliders = (layer("wall") + layer("line")).mapNotNull(::collider)
        spawns = layer("player").map { spawn ->
            val point = anchor(spawn)
            SpawnPoint(spawn.properties.get("index", 0, Int::class.java), point.x, point.y)
        }
        teleports = layer("teleport").mapNotNull { teleport ->
            val target = teleport.properties.get("map", String::class.java) ?: return@mapNotNull null
            TeleportTrigger(
                bounds = triggerBounds(teleport),
                targetLegacyMap = target,
                targetSpawn = teleport.properties.get("spawn", 0, Int::class.java),
                label = teleport.properties.get("name", target, String::class.java),
            )
        }
        encounters = layer("enemies").map { enemy ->
            EncounterTrigger(triggerBounds(enemy), enemy.properties.get("id", 0, Int::class.java))
        }
    }

    fun spawn(index: Int): Vector2 {
        val point = spawns.firstOrNull { it.index == index } ?: spawns.firstOrNull()
        return if (point == null) Vector2(widthPixels / 2, heightPixels / 2) else Vector2(point.x, point.y)
    }

    fun blocked(area: Rectangle): Boolean =
        area.x < 0 || area.y < 0 || area.x + area.width > widthPixels || area.y + area.height > heightPixels ||
            colliders.any { it.blocks(area) }

    private fun collider(mapObject: MapObject): Collider? = when (mapObject) {
        is RectangleMapObject -> Collider.Box(Rectangle(mapObject.rectangle))
        is PolygonMapObject -> Collider.Shape(mapObject.polygon)
        is PolylineMapObject -> Collider.Line(mapObject.polyline.transformedVertices)
        is EllipseMapObject -> mapObject.ellipse.let { Collider.Box(Rectangle(it.x, it.y, it.width, it.height)) }
        else -> null
    }

    private fun anchor(mapObject: MapObject): Vector2 = when (mapObject) {
        is RectangleMapObject -> mapObject.rectangle.let { Vector2(it.x + it.width / 2, it.y + it.height / 2) }
        else -> Vector2(mapObject.properties.get("x", 0f, Float::class.java), mapObject.properties.get("y", 0f, Float::class.java))
    }

    private fun triggerBounds(mapObject: MapObject): Rectangle = when (mapObject) {
        is RectangleMapObject -> Rectangle(mapObject.rectangle).let { if (it.width < 8f || it.height < 8f) Rectangle(it.x - 12f, it.y - 12f, 24f, 24f) else it }
        is PolygonMapObject -> Rectangle(mapObject.polygon.boundingRectangle)
        else -> anchor(mapObject).let { Rectangle(it.x - 12f, it.y - 12f, 24f, 24f) }
    }
}
