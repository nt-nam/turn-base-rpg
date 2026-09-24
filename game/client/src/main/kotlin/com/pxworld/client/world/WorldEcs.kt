package com.pxworld.client.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.pxworld.client.core.SpriteSet

class Transform(val position: Vector2) : Component<Transform> {
    override fun type() = Transform
    companion object : ComponentType<Transform>()
}

class Motion(val direction: Vector2 = Vector2(), var speed: Float = 90f) : Component<Motion> {
    override fun type() = Motion
    companion object : ComponentType<Motion>()
}

class Body(val width: Float, val height: Float) : Component<Body> {
    fun footprint(position: Vector2): Rectangle = Rectangle(position.x - width / 2, position.y, width, height)
    override fun type() = Body
    companion object : ComponentType<Body>()
}

class Appearance(val sprite: SpriteSet, val size: Float, var animation: String = SpriteSet.IDLE, var time: Float = 0f, var facingLeft: Boolean = false) :
    Component<Appearance> {
    override fun type() = Appearance
    companion object : ComponentType<Appearance>()
}

class PlayerControlled(val steering: Steering) : Component<PlayerControlled> {
    override fun type() = PlayerControlled
    companion object : ComponentType<PlayerControlled>()
}

class Steering {
    val analog = Vector2()
    var target: Vector2? = null

    fun direction(from: Vector2): Vector2 {
        val keyboard = Vector2(
            (if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) 1f else 0f) -
                (if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) 1f else 0f),
            (if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) 1f else 0f) -
                (if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) 1f else 0f),
        )
        if (!keyboard.isZero) {
            target = null
            return keyboard.nor()
        }
        if (!analog.isZero) {
            target = null
            return Vector2(analog).limit(1f)
        }
        val goal = target ?: return Vector2.Zero.cpy()
        val delta = Vector2(goal).sub(from)
        if (delta.len() < ARRIVAL_DISTANCE) {
            target = null
            return Vector2.Zero.cpy()
        }
        return delta.nor()
    }

    companion object {
        const val ARRIVAL_DISTANCE: Float = 3f
    }
}

class ControlSystem : IteratingSystem(family { all(PlayerControlled, Motion, Transform) }) {
    override fun onTickEntity(entity: Entity) {
        entity[Motion].direction.set(entity[PlayerControlled].steering.direction(entity[Transform].position))
    }
}

class MovementSystem(private val layout: MapLayout = inject()) : IteratingSystem(family { all(Transform, Motion, Body) }) {
    override fun onTickEntity(entity: Entity) {
        val motion = entity[Motion]
        if (motion.direction.isZero) return
        val position = entity[Transform].position
        val body = entity[Body]
        val step = motion.speed * deltaTime
        val nextX = Vector2(position.x + motion.direction.x * step, position.y)
        if (!layout.blocked(body.footprint(nextX))) position.x = nextX.x
        val nextY = Vector2(position.x, position.y + motion.direction.y * step)
        if (!layout.blocked(body.footprint(nextY))) position.y = nextY.y
    }
}

class AnimationSystem : IteratingSystem(family { all(Appearance, Motion) }) {
    override fun onTickEntity(entity: Entity) {
        val appearance = entity[Appearance]
        val direction = entity[Motion].direction
        val wanted = if (direction.isZero) SpriteSet.IDLE else SpriteSet.RUN
        if (wanted != appearance.animation) {
            appearance.animation = wanted
            appearance.time = 0f
        }
        if (direction.x < -0.01f) appearance.facingLeft = true
        if (direction.x > 0.01f) appearance.facingLeft = false
        appearance.time += deltaTime
    }
}

class RenderSystem(
    private val camera: OrthographicCamera = inject(),
    private val batch: SpriteBatch = inject(),
    private val mapRenderer: OrthogonalTiledMapRenderer = inject(),
    private val layout: MapLayout = inject(),
) : IntervalSystem() {

    private val drawables = world.family { all(Transform, Appearance) }
    private val followed = world.family { all(PlayerControlled, Transform) }

    override fun onTick() {
        followed.firstOrNull()?.let { player ->
            val position = player[Transform].position
            val halfWidth = camera.viewportWidth * camera.zoom / 2
            val halfHeight = camera.viewportHeight * camera.zoom / 2
            camera.position.set(
                if (layout.widthPixels <= halfWidth * 2) layout.widthPixels / 2 else MathUtils.clamp(position.x, halfWidth, layout.widthPixels - halfWidth),
                if (layout.heightPixels <= halfHeight * 2) layout.heightPixels / 2 else MathUtils.clamp(position.y, halfHeight, layout.heightPixels - halfHeight),
                0f,
            )
        }
        camera.update()
        mapRenderer.setView(camera)
        mapRenderer.render()
        batch.projectionMatrix = camera.combined
        batch.begin()
        val ordered = mutableListOf<Entity>()
        drawables.forEach { ordered += it }
        ordered.sortedByDescending { it[Transform].position.y }.forEach { entity ->
            val appearance = entity[Appearance]
            val position = entity[Transform].position
            val frame = appearance.sprite.frame(appearance.animation, appearance.time)
            val size = appearance.size
            val left = position.x - size / 2
            if (appearance.facingLeft) batch.draw(frame, left + size, position.y - size * 0.15f, -size, size)
            else batch.draw(frame, left, position.y - size * 0.15f, size, size)
        }
        batch.end()
    }
}
