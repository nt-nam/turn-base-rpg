package com.pxworld.client.screens

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.Grant
import com.pxworld.application.GrantKind
import com.pxworld.client.core.SpriteSet
import com.pxworld.client.navigation.GameScreen
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.stats.StatBlock
import com.pxworld.domain.stats.StatKind
import com.pxworld.screens.GameScreenId

abstract class StandardScreen(id: GameScreenId, context: ScreenContext, args: ScreenArgs) : GameScreen(id, context, args) {

    abstract val titleKey: String
    open val showBack: Boolean = true

    abstract fun body(content: Table)

    override fun build(content: Table) {
        content.top()
        content.background = context.ui.tinted(Tokens.background)
        content.add(ui.header(id.id, text(titleKey), if (showBack) ({ context.navigator.back() }) else null)).growX().row()
        val body = Table().top().pad(Tokens.SPACE_M)
        body(body)
        content.add(body).grow()
    }
}

abstract class ModalScreen(id: GameScreenId, context: ScreenContext, args: ScreenArgs) : GameScreen(id, context, args) {

    override val presentation = com.pxworld.client.navigation.Presentation.MODAL

    abstract fun dialog(content: Table)

    override fun build(content: Table) {
        val panel = ui.panel(Tokens.SPACE_L, Tokens.surface)
        dialog(panel)
        content.add(panel).minWidth(MIN_WIDTH).maxWidth(MAX_WIDTH)
    }

    companion object {
        const val MIN_WIDTH: Float = 420f
        const val MAX_WIDTH: Float = 900f
    }
}

class SpriteActor(private val sprite: SpriteSet, var animation: String = SpriteSet.IDLE, private val flip: Boolean = false) : Actor() {

    private var time = 0f

    fun play(name: String) {
        if (name != animation) time = 0f
        animation = name
    }

    override fun act(delta: Float) {
        super.act(delta)
        time += delta
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val frame: TextureRegion = sprite.frame(animation, time)
        val previous = batch.color.cpy()
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        if (flip) batch.draw(frame, x + width, y, -width, height) else batch.draw(frame, x, y, width, height)
        batch.color = previous
    }
}

class Lookup(private val context: ScreenContext) {

    private val content get() = context.services.content
    private val text get() = context.text

    fun heroName(heroId: String): String = content.heroes.firstOrNull { it.id == heroId }?.let { text(it.name) } ?: heroId
    fun heroTitle(heroId: String): String = content.heroes.firstOrNull { it.id == heroId }?.let { text(it.title) } ?: ""
    fun heroClass(heroId: String): String = content.heroes.firstOrNull { it.id == heroId }?.classId ?: ""
    fun className(classId: String): String = content.heroClasses.firstOrNull { it.id == classId }?.let { text(it.name) } ?: classId
    fun heroSprite(heroId: String): SpriteSet = context.assets.sprite(content.heroes.first { it.id == heroId }.sprite)
    fun enemySprite(enemyId: String): SpriteSet = context.assets.sprite(content.enemies.first { it.id == enemyId }.sprite)
    fun enemyName(enemyId: String): String = content.enemies.firstOrNull { it.id == enemyId }?.let { text(it.name) } ?: enemyId
    fun enemyClass(enemyId: String): String = content.enemies.firstOrNull { it.id == enemyId }?.classId ?: ""
    fun itemName(itemId: String): String = content.items.firstOrNull { it.id == itemId }?.let { text(it.name) } ?: itemId
    fun itemIcon(itemId: String): TextureRegion = context.assets.region(content.items.first { it.id == itemId }.icon)
    fun equipmentName(equipmentId: String): String = content.equipment.firstOrNull { it.id == equipmentId }?.let { text(it.name) } ?: equipmentId
    fun equipmentIcon(equipmentId: String): TextureRegion = context.assets.region(content.equipment.first { it.id == equipmentId }.icon)
    fun mapName(mapId: String): String = content.maps.firstOrNull { it.id == mapId }?.let { text(it.name) } ?: mapId
    fun currencyName(currencyId: String): String = content.currencies.firstOrNull { it.id == currencyId }?.let { text(it.name) } ?: currencyId
    fun currencyIcon(currencyId: String): TextureRegion = context.assets.region(content.currencies.first { it.id == currencyId }.icon)
    fun skillName(skillId: String): String = content.skills.firstOrNull { it.id == skillId }?.let { text(it.name) } ?: skillId

    fun grantLabel(grant: Grant): String = when (grant.kind) {
        GrantKind.CURRENCY -> "${grant.quantity} ${currencyName(grant.id)}"
        GrantKind.ITEM -> "${grant.quantity} x ${itemName(grant.id)}"
        GrantKind.EQUIPMENT -> "${grant.quantity} x ${equipmentName(grant.id)}"
        GrantKind.HERO -> heroName(grant.id)
    }

    fun statLines(stats: StatBlock): List<Pair<String, String>> = listOf(
        text("ui.stat.hp") to stats[StatKind.HP].toString(),
        text("ui.stat.attack") to stats[StatKind.ATTACK].toString(),
        text("ui.stat.defense") to stats[StatKind.DEFENSE].toString(),
        text("ui.stat.speed") to stats[StatKind.SPEED].toString(),
        text("ui.stat.crit_rate") to "${stats[StatKind.CRIT_RATE] / 10.0}%",
        text("ui.stat.crit_damage") to "${stats[StatKind.CRIT_DAMAGE] / 10}%",
    )

    companion object {
        fun power(stats: StatBlock): Int =
            stats[StatKind.HP] / 10 + stats[StatKind.ATTACK] + stats[StatKind.DEFENSE] + stats[StatKind.SPEED] + stats[StatKind.CRIT_RATE] / 5
    }
}
