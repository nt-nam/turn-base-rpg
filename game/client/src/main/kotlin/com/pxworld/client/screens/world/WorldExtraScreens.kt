package com.pxworld.client.screens.world

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.GameScreen
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.client.world.MapLayout
import com.pxworld.screens.GameScreenId

class RegionMapScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.WORLD_REGION_MAP, context, args) {
    override val titleKey = "ui.world.region_map"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val visited = context.state.journal.visitedMaps
        context.services.content.maps.groupBy { it.region }.forEach { (region, maps) ->
            val panel = ui.panel(Tokens.SPACE_S)
            panel.add(ui.label(text("ui.region.${region.removePrefix("region.")}"), "heading")).colspan(3).left().row()
            maps.forEach { map ->
                val here = map.id == context.state.position.mapId
                panel.add(ui.label(lookup.mapName(map.id) + if (here) "  <" else "", if (map.id in visited) "body" else "muted")).left().padRight(Tokens.SPACE_M)
                panel.add(ui.label(text("ui.encounter.recommended", map.recommendedLevel), "small")).padRight(Tokens.SPACE_M)
                panel.add(ui.button(testId("travel/${map.id}"), text("ui.world.fast_travel"), "secondary", enabled = map.id in visited && !here) {
                    context.act(text("ui.world.arrived", lookup.mapName(map.id))) { context.services.collection.fastTravel(it, map.id) }
                        ?.let { context.navigator.backTo(GameScreenId.WORLD_WORLD_EXPLORE) }
                }).padBottom(Tokens.SPACE_XS).row()
            }
            content.add(panel).width(900f).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class FastTravelScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_FAST_TRAVEL, context, args) {
    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        content.add(ui.label(text("ui.world.fast_travel"), "title")).colspan(2).padBottom(Tokens.SPACE_M).row()
        val visited = context.services.content.maps.filter { it.id in context.state.journal.visitedMaps && it.id != context.state.position.mapId }
        if (visited.isEmpty()) content.add(ui.label(text("ui.world.no_destinations"), "muted")).colspan(2).row()
        visited.forEach { map ->
            content.add(ui.label(lookup.mapName(map.id), "body")).left().padRight(Tokens.SPACE_M)
            content.add(ui.button(testId("go/${map.id}"), text("ui.world.go")) {
                context.act(text("ui.world.arrived", lookup.mapName(map.id))) { context.services.collection.fastTravel(it, map.id) }
                context.navigator.back()
            }).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).colspan(2).padTop(Tokens.SPACE_S)
    }
}

class MinimapActor(private val layout: MapLayout, private val white: TextureRegion, private val player: () -> com.badlogic.gdx.math.Vector2?) : Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        val scale = minOf(width / layout.widthPixels, height / layout.heightPixels)
        val previous = batch.color.cpy()
        fun rect(x: Float, y: Float, w: Float, h: Float, color: com.badlogic.gdx.graphics.Color) {
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
            batch.draw(white, this.x + x * scale, this.y + y * scale, maxOf(2f, w * scale), maxOf(2f, h * scale))
        }
        rect(0f, 0f, layout.widthPixels, layout.heightPixels, Tokens.surfaceRaised)
        layout.teleports.forEach { rect(it.bounds.x, it.bounds.y, it.bounds.width, it.bounds.height, Tokens.energy) }
        layout.encounters.forEach { rect(it.bounds.x, it.bounds.y, it.bounds.width, it.bounds.height, Tokens.danger) }
        player()?.let { rect(it.x - 4f, it.y - 4f, 8f, 8f, Tokens.accent) }
        batch.color = previous
    }
}

class MinimapScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_MINIMAP, context, args) {
    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val world = context.navigator.visibleScreens().firstOrNull() as? WorldExploreScreen
        val mapId = context.state.position.mapId
        val record = context.services.content.maps.first { it.id == mapId }
        val layout = MapLayout(context.assets.map(record.asset))
        content.add(ui.label(lookup.mapName(mapId), "title")).row()
        content.add(MinimapActor(layout, context.ui.whiteRegion) { world?.playerPosition }.apply { name = testId("map") }).size(640f, 400f).pad(Tokens.SPACE_M).row()
        content.add(ui.label(text("ui.world.legend"), "muted")).row()
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).padTop(Tokens.SPACE_S)
    }
}

class QuestTrackerScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_QUEST_TRACKER, context, args) {
    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.world.quest_tracker"), "title")).colspan(2).padBottom(Tokens.SPACE_M).row()
        val active = context.state.quests.filter { !it.claimed }
        if (active.isEmpty()) content.add(ui.label(text("ui.world.no_active_quests"), "muted")).colspan(2).row()
        active.forEach { progress ->
            val quest = context.services.catalog.quests().firstOrNull { it.id == progress.questId } ?: return@forEach
            val record = context.services.content.quests.first { it.id == quest.id }
            content.add(ui.label(text(record.name), if (progress.completed) "positive" else "body")).left().padRight(Tokens.SPACE_M)
            content.add(ui.button(testId("open/${quest.id}"), "${progress.progress}/${quest.count}", "ghost") {
                context.navigator.replace(GameScreenId.PROGRESSION_QUEST_DETAIL, ScreenArgs.of("quest" to quest.id))
            }).row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).colspan(2).padTop(Tokens.SPACE_S)
    }
}

class MapTransitionScreen(context: ScreenContext, args: ScreenArgs) : GameScreen(GameScreenId.WORLD_MAP_TRANSITION, context, args) {
    private var elapsed = 0f
    override val presentation = com.pxworld.client.navigation.Presentation.MODAL

    override fun build(content: Table) {
        content.background = context.ui.tinted(Tokens.background)
        content.add(ui.label(Lookup(context).mapName(args.optional("map") ?: context.state.position.mapId), "title", testId("map_name"))).row()
        content.add(ui.label(text("ui.world.arriving"), "muted"))
    }

    override fun update(delta: Float) {
        elapsed += delta
        if (elapsed >= DURATION && context.navigator.current === this) context.navigator.back()
    }

    companion object {
        const val DURATION: Float = 0.7f
    }
}
