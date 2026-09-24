package com.pxworld.client.screens.world

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import com.pxworld.application.Currencies
import com.pxworld.application.GameEvent
import com.pxworld.client.navigation.GameScreen
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.client.world.AnimationSystem
import com.pxworld.client.world.Appearance
import com.pxworld.client.world.Body
import com.pxworld.client.world.ControlSystem
import com.pxworld.client.world.EncounterTrigger
import com.pxworld.client.world.MapLayout
import com.pxworld.client.world.Motion
import com.pxworld.client.world.MovementSystem
import com.pxworld.client.world.PlayerControlled
import com.pxworld.client.world.RenderSystem
import com.pxworld.client.world.Steering
import com.pxworld.client.world.TeleportTrigger
import com.pxworld.client.world.Transform
import com.pxworld.domain.progression.GameState
import com.pxworld.screens.GameScreenId

class WorldExploreScreen(context: ScreenContext, args: ScreenArgs) : GameScreen(GameScreenId.WORLD_WORLD_EXPLORE, context, args) {

    private val lookup = Lookup(context)
    private val camera = OrthographicCamera().apply { setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT) }
    private val steering = Steering()
    private lateinit var mapId: String
    private lateinit var layout: MapLayout
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private var world: World? = null
    private var player: Entity? = null
    private var nearbyTeleport: TeleportTrigger? = null
    private var nearbyEncounter: EncounterTrigger? = null
    private var nearbyNpc: String? = null
    private val placedNpcs = mutableListOf<Pair<String, com.pxworld.client.world.NpcSpot>>()
    private val objective = com.badlogic.gdx.scenes.scene2d.ui.Label("", context.ui.skin, "small")
    private var positionSaveTimer = 0f
    private val topBar = Table()
    private val actions = Table()

    val playerPosition: Vector2? get() = player?.let { entity -> world?.let { with(it) { entity[Transform].position } } }
    val activeMapId: String get() = mapId

    override fun onShow() {
        loadMap(context.state.position.mapId, context.state.position.spawnIndex, restoreSavedPosition = true)
        com.pxworld.client.screens.onboarding.Tutorials.showIfPending(context, "tutorial_move")
    }

    private fun loadMap(targetMap: String, spawnIndex: Int, restoreSavedPosition: Boolean) {
        world?.dispose()
        mapId = if (context.services.content.maps.any { it.id == targetMap }) targetMap else context.services.catalog.startingMap()
        val record = context.services.content.maps.first { it.id == mapId }
        val tiledMap = context.assets.map(record.asset)
        layout = MapLayout(tiledMap)
        renderer = OrthogonalTiledMapRenderer(tiledMap, context.batch)
        val saved = context.state.position
        val start = if (restoreSavedPosition && saved.mapId == mapId && saved.x >= 0 && saved.y >= 0) Vector2(saved.x.toFloat(), saved.y.toFloat()) else layout.spawn(spawnIndex)
        val heroSprite = lookup.heroSprite(context.state.profile.starterHeroId)
        val runtime = configureWorld {
            injectables {
                add(layout)
                add(camera)
                add(context.batch)
                add(renderer)
            }
            systems {
                add(ControlSystem())
                add(MovementSystem())
                add(AnimationSystem())
                add(RenderSystem())
            }
        }
        placedNpcs.clear()
        context.services.content.npcs.forEach { npc ->
            npc.placements.filter { it.map == mapId }.forEach { placement ->
                layout.npcSpots.firstOrNull { it.objectName == placement.objectName }?.let { spot ->
                    placedNpcs += npc.id to spot
                    runtime.entity {
                        it += Transform(com.badlogic.gdx.math.Vector2(spot.x, spot.y))
                        it += Motion(speed = 0f)
                        it += Appearance(context.assets.sprite(npc.sprite), 34f)
                    }
                }
            }
        }
        player = runtime.entity {
            it += Transform(start)
            it += Motion()
            it += Body(12f, 6f)
            it += Appearance(heroSprite, 40f)
            it += PlayerControlled(steering)
        }
        world = runtime
        context.act { context.services.rules.enterMap(it, mapId, start.x.toInt(), start.y.toInt(), spawnIndex) }
    }

    override fun build(content: Table) {
        topBar.pad(Tokens.SPACE_S)
        topBar.background = context.ui.tinted(Tokens.scrim)
        refreshTopBar()
        content.top()
        content.add(topBar).growX().colspan(2).row()
        objective.name = testId("objective")
        val objectiveBox = com.badlogic.gdx.scenes.scene2d.ui.Container(objective).apply {
            background = context.ui.tinted(Tokens.scrim, rounded = true)
            pad(Tokens.SPACE_XS, Tokens.SPACE_S, Tokens.SPACE_XS, Tokens.SPACE_S)
        }
        content.add(objectiveBox).left().pad(Tokens.SPACE_XS, Tokens.SPACE_M, 0f, 0f).colspan(2).row()
        content.add().expand().colspan(2).row()
        val touchpad = Touchpad(6f, Touchpad.TouchpadStyle(context.ui.tinted(Tokens.scrim, rounded = true), context.ui.tinted(Tokens.accent, rounded = true).also {
            it.minWidth = 48f
            it.minHeight = 48f
        })).apply {
            name = testId("joystick")
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent, actor: Actor) {
                    steering.analog.set(knobPercentX, knobPercentY)
                }
            })
        }
        content.add(touchpad).size(160f).left().bottom().pad(Tokens.SPACE_L)
        refreshActions()
        content.add(actions).right().bottom().pad(Tokens.SPACE_L)
    }

    private fun refreshTopBar() {
        val state = context.state
        objective.setText(Dialogues.activeMainQuest(context)?.let { text("ui.world.objective", Dialogues.objectiveText(context, it)) } ?: "")
        topBar.clearChildren()
        topBar.add(ui.label(state.profile.name, "heading", testId("player_name"))).padRight(Tokens.SPACE_M)
        topBar.add(ui.label(text("ui.world.level", state.profile.level), "small")).padRight(Tokens.SPACE_M)
        topBar.add(ui.label(lookup.mapName(state.position.mapId), "small", testId("map_name"))).expandX().left()
        topBar.add(ui.image(lookup.currencyIcon(Currencies.GOLD), 24f)).size(24f)
        topBar.add(ui.label(state.wallet.balance(Currencies.GOLD).toString(), "body", testId("gold"))).padRight(Tokens.SPACE_M)
        topBar.add(ui.image(lookup.currencyIcon(Currencies.GEM), 24f)).size(24f)
        topBar.add(ui.label(state.wallet.balance(Currencies.GEM).toString(), "body", testId("gem"))).padRight(Tokens.SPACE_M)
        topBar.add(ui.button(testId("menu"), text("ui.world.menu"), "secondary") { context.navigator.open(GameScreenId.WORLD_PAUSE_MENU) })
    }

    private fun refreshActions() {
        actions.clearChildren()
        nearbyNpc?.let { npcId ->
            val npc = Dialogues.npc(context, npcId)
            actions.add(ui.button(testId("talk"), text("ui.world.talk", text(npc.name))) {
                context.navigator.open(GameScreenId.WORLD_NPC_DIALOGUE, ScreenArgs.of("npc" to npcId))
            }).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        }
        nearbyEncounter?.let { encounter ->
            val encounterId = encounterIdFor(encounter)
            actions.add(ui.button(testId("inspect_enemy"), text("ui.world.inspect_enemy"), enabled = encounterId != null) {
                encounterId?.let { context.navigator.open(GameScreenId.WORLD_ENCOUNTER_PREVIEW, ScreenArgs.of("encounter" to it)) }
            }).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        }
        nearbyTeleport?.let { teleport ->
            actions.add(ui.button(testId("travel"), text("ui.world.travel", teleport.label)) { travel(teleport) }).height(Tokens.BUTTON_HEIGHT).row()
        }
    }

    override fun update(delta: Float) {
        val entity = player ?: return
        val runtime = world ?: return
        val footprint = with(runtime) { entity[Body].footprint(entity[Transform].position) }
        val teleport = layout.teleports.firstOrNull { it.bounds.overlaps(footprint) }
        val encounter = layout.encounters.firstOrNull { it.bounds.overlaps(footprint) }
        val npc = placedNpcs.firstOrNull { (_, spot) -> spot.bounds.overlaps(footprint) }?.first
        if (teleport != nearbyTeleport || encounter != nearbyEncounter || npc != nearbyNpc) {
            nearbyTeleport = teleport
            nearbyEncounter = encounter
            nearbyNpc = npc
            refreshActions()
        }
        positionSaveTimer += delta
        if (positionSaveTimer >= POSITION_SAVE_SECONDS) {
            positionSaveTimer = 0f
            persistPosition()
        }
    }

    override fun renderWorld(delta: Float) {
        world?.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        val aspect = width.toFloat() / height.coerceAtLeast(1)
        camera.setToOrtho(false, VIEW_HEIGHT * aspect, VIEW_HEIGHT)
    }

    override fun onStateChanged(state: GameState, events: List<GameEvent>) {
        events.filterIsInstance<GameEvent.QuestCompleted>().forEach { completed ->
            val quest = context.services.content.quests.firstOrNull { it.id == completed.questId }
            if (quest != null) context.navigator.toast(text(if (quest.id == CHAPTER_FINALE) "ui.chapter.complete" else "ui.result.quest_completed", text(quest.name)))
        }
        if (state.position.mapId != mapId && state.position.x < 0) {
            loadMap(state.position.mapId, state.position.spawnIndex, restoreSavedPosition = false)
            context.navigator.open(GameScreenId.WORLD_MAP_TRANSITION, ScreenArgs.of("map" to state.position.mapId))
        }
        refreshTopBar()
        refreshActions()
    }

    override fun onHide() {
        persistPosition()
        steering.analog.setZero()
    }

    override fun dispose() {
        world?.dispose()
        world = null
    }

    fun steerTo(x: Float, y: Float) {
        val from = playerPosition ?: return
        val route = com.pxworld.client.world.Pathfinder(layout, 12f, 6f).route(from, Vector2(x, y))
        if (route.isEmpty()) steering.target = Vector2(x, y) else steering.follow(route + Vector2(x, y))
    }

    fun triggers(): Map<String, Any?> = mapOf(
        "teleports" to layout.teleports.map { mapOf("label" to it.label, "target" to it.targetLegacyMap, "x" to it.bounds.x + it.bounds.width / 2, "y" to it.bounds.y + it.bounds.height / 2) },
        "nearbyNpc" to nearbyNpc,
        "nearbyEncounter" to nearbyEncounter?.let { encounterIdFor(it) },
        "nearbyTeleport" to nearbyTeleport?.targetLegacyMap,
        "npcs" to placedNpcs.map { (npcId, spot) -> mapOf("npc" to npcId, "x" to spot.x, "y" to spot.y) },
        "encounters" to layout.encounters.map { mapOf("encounter" to (encounterIdFor(it) ?: ""), "x" to it.bounds.x + it.bounds.width / 2, "y" to it.bounds.y + it.bounds.height / 2) },
    )

    private fun encounterIdFor(trigger: EncounterTrigger): String? {
        val id = "encounter.${mapId.removePrefix("map.")}.e${trigger.objectId}"
        return id.takeIf { candidate -> context.services.content.encounters.any { it.id == candidate } }
    }

    private fun travel(teleport: TeleportTrigger) {
        val target = context.services.content.maps.firstOrNull { it.legacyName == teleport.targetLegacyMap }?.id
        if (target == null) {
            context.navigator.toast(text("ui.world.travel_blocked"), positive = false)
            return
        }
        nearbyTeleport = null
        loadMap(target, teleport.targetSpawn, restoreSavedPosition = false)
        refreshTopBar()
        refreshActions()
        context.navigator.open(GameScreenId.WORLD_MAP_TRANSITION, ScreenArgs.of("map" to target))
    }

    private fun persistPosition() {
        val position = playerPosition ?: return
        if (context.session.store == null) return
        context.act { context.services.rules.enterMap(it, mapId, position.x.toInt(), position.y.toInt(), it.position.spawnIndex) }
    }

    companion object {
        const val VIEW_WIDTH: Float = 480f
        const val VIEW_HEIGHT: Float = 270f
        const val POSITION_SAVE_SECONDS: Float = 5f
        const val CHAPTER_FINALE: String = "quest.main.ch1_05"
    }
}

class EncounterPreviewScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_ENCOUNTER_PREVIEW, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val encounterId = args["encounter"]
        val record = context.services.content.encounters.first { it.id == encounterId }
        val summary = context.services.catalog.encounter(encounterId)
        content.add(ui.label(text(record.name), "title", testId("title"))).colspan(2).row()
        content.add(ui.label(text("ui.encounter.recommended", record.recommendedLevel), "muted")).colspan(2).padBottom(Tokens.SPACE_M).row()
        val enemies = Table()
        record.enemies.forEach { placed ->
            enemies.add(ui.label(lookup.enemyName(placed.enemy), "body")).left().padRight(Tokens.SPACE_M)
            enemies.add(ui.label(lookup.className(lookup.enemyClass(placed.enemy)), "muted")).left().padRight(Tokens.SPACE_M)
            enemies.add(ui.label(text("ui.encounter.enemy_level", placed.level, placed.star), "small")).left().row()
        }
        content.add(enemies).colspan(2).padBottom(Tokens.SPACE_M).row()
        content.add(ui.label(text("ui.encounter.rewards"), "heading")).colspan(2).left().row()
        summary.rewards.forEach { content.add(ui.label(lookup.grantLabel(it), "body")).colspan(2).left().row() }
        val lineupSize = context.state.lineup.cells.size
        content.add(ui.label(text("ui.encounter.team", lineupSize, record.enemies.size), "muted")).colspan(2).padTop(Tokens.SPACE_M).row()
        content.add(ui.button(testId("cancel"), text("ui.common.cancel"), "secondary") { context.navigator.back() }).growX().padTop(Tokens.SPACE_M).padRight(Tokens.SPACE_S)
        content.add(ui.button(testId("fight"), text("ui.encounter.fight")) {
            context.navigator.replace(GameScreenId.BATTLE_BATTLE_MAIN, ScreenArgs.of("encounter" to encounterId))
        }).growX().padTop(Tokens.SPACE_M)
    }
}

class PauseMenuScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_PAUSE_MENU, context, args) {

    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.pause.title"), "title")).colspan(3).padBottom(Tokens.SPACE_M).row()
        val entries = listOf(
            "map" to GameScreenId.WORLD_REGION_MAP,
            "minimap" to GameScreenId.WORLD_MINIMAP,
            "tracker" to GameScreenId.WORLD_QUEST_TRACKER,
            "codex" to GameScreenId.PROGRESSION_CODEX_HEROES,
            "exchange" to GameScreenId.ECONOMY_CURRENCY_EXCHANGE,
            "idle" to GameScreenId.ECONOMY_IDLE_REWARDS,
            "replays" to GameScreenId.BATTLE_REPLAY_LIST,
            "tips" to GameScreenId.PROGRESSION_TIPS_LIBRARY,
            "heroes" to GameScreenId.HEROES_HERO_ROSTER,
            "lineup" to GameScreenId.HEROES_LINEUP_EDITOR,
            "bag" to GameScreenId.INVENTORY_BAG_EQUIPMENT,
            "shop" to GameScreenId.ECONOMY_SHOP_HOME,
            "recruit" to GameScreenId.ECONOMY_RECRUIT_HOME,
            "checkin" to GameScreenId.ECONOMY_DAILY_CHECKIN,
            "quests" to GameScreenId.PROGRESSION_QUEST_SIDE,
            "achievements" to GameScreenId.PROGRESSION_ACHIEVEMENT_LIST,
            "settings" to GameScreenId.SETTINGS_SETTINGS_HOME,
        )
        entries.forEachIndexed { index, (key, target) ->
            content.add(ui.button(testId(key), text("ui.pause.$key"), "secondary") { context.navigator.replace(target) })
                .width(200f).height(Tokens.BUTTON_HEIGHT).pad(Tokens.SPACE_XS)
            if (index % 3 == 2) content.row()
        }
        content.row()
        content.add(ui.button(testId("main_menu"), text("ui.pause.main_menu"), "ghost") {
            context.session.end()
            context.navigator.reset(GameScreenId.BOOT_MAIN_MENU)
        }).colspan(1).padTop(Tokens.SPACE_M)
        content.add()
        content.add(ui.button(testId("resume"), text("ui.pause.resume")) { context.navigator.back() }).width(200f).padTop(Tokens.SPACE_M)
    }
}
