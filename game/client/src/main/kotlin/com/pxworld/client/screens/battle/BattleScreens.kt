package com.pxworld.client.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pxworld.application.GameEvent
import com.pxworld.client.core.SpriteSet
import com.pxworld.client.navigation.GameScreen
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.client.ui.ValueBar
import com.pxworld.content.LineupSlot
import com.pxworld.domain.battle.BattleCommand
import com.pxworld.domain.battle.BattleEngine
import com.pxworld.domain.battle.BattleEvent
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.BattleSide
import com.pxworld.domain.battle.BattleState
import com.pxworld.domain.battle.Targeting
import com.pxworld.domain.battle.UnitId
import com.pxworld.domain.stats.StatBlock
import com.pxworld.screens.GameScreenId

data class BattleSummary(
    val encounterId: String,
    val outcome: BattleOutcome,
    val rounds: Int,
    val battleEvents: List<BattleEvent>,
    val gameEvents: List<GameEvent>,
)

class CombatantActor(
    val unit: UnitId,
    sprite: SpriteSet,
    name: String,
    maxHp: Int,
    private val white: TextureRegion,
    skin: com.badlogic.gdx.scenes.scene2d.ui.Skin,
    private val onTap: (UnitId) -> Unit,
) : Group() {

    var hp: Int = maxHp
    var energy: Int = 0
    private val maxHp = maxHp
    val figure = SpriteActor(sprite, flip = unit.side == BattleSide.ENEMY).apply { setSize(SIZE, SIZE) }
    private val nameLabel = Label(name, skin, "small")
    private val hpBar = ValueBar(white, { hp.toFloat() / this.maxHp }, if (unit.side == BattleSide.ALLY) Tokens.ally else Tokens.enemy).apply { setSize(SIZE, 8f) }
    private val energyBar = ValueBar(white, { energy / 100f }, Tokens.energy).apply { setSize(SIZE, 5f) }
    var highlighted: Boolean = false
    var targetable: Boolean = false

    init {
        setSize(SIZE, SIZE + 36f)
        this.name = "${GameScreenId.BATTLE_BATTLE_MAIN.id}/unit/$unit"
        energyBar.setPosition(0f, 0f)
        hpBar.setPosition(0f, 7f)
        figure.setPosition(0f, 16f)
        nameLabel.setPosition(0f, SIZE + 14f)
        nameLabel.setFontScale(0.9f)
        addActor(energyBar)
        addActor(hpBar)
        addActor(figure)
        addActor(nameLabel)
        touchable = Touchable.enabled
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                onTap(unit)
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (highlighted || targetable) {
            val previous = batch.color.cpy()
            val ring = if (targetable) Tokens.accent else Tokens.energy
            batch.setColor(ring.r, ring.g, ring.b, 0.35f * parentAlpha)
            batch.draw(white, x - 4f, y + 12f, width + 8f, SIZE + 8f)
            batch.color = previous
        }
        super.draw(batch, parentAlpha)
    }

    companion object {
        const val SIZE: Float = 96f
    }
}

class EffectActor(private val animation: Animation<TextureRegion>) : Actor() {
    private var time = 0f

    override fun act(delta: Float) {
        super.act(delta)
        time += delta
        if (animation.isAnimationFinished(time)) remove()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(animation.getKeyFrame(time), x, y, width, height)
    }
}

class BattleMainScreen(context: ScreenContext, args: ScreenArgs) : GameScreen(GameScreenId.BATTLE_BATTLE_MAIN, context, args) {

    private val lookup = Lookup(context)
    private val encounterId = args["encounter"]
    private val arena = Group()
    private val controls = Table()
    private val status = Label("", context.ui.skin, "heading")
    private val units = mutableMapOf<UnitId, CombatantActor>()
    private val queue = ArrayDeque<BattleEvent>()
    private val log = mutableListOf<BattleEvent>()
    private var state: BattleState
    private var wait = 0f
    private var auto = false
    private var speed = 1
    private var pendingSkill: String? = null
    private var finished = false

    init {
        val game = context.state
        val lineup = game.lineup.cells.map { (cell, instanceId) ->
            val hero = game.hero(instanceId)
            val bonus = game.inventory.equippedOn(instanceId).fold(StatBlock.EMPTY) { total, equip -> total + context.services.catalog.equipmentStats(equip.equipmentId) }
            LineupSlot(hero.heroId, hero.level, hero.star, cell, bonus)
        }
        val setup = context.services.battles.battle(context.services.clock.nowMillis(), lineup, encounterId)
        val start = BattleEngine.start(setup)
        state = start.state
        enqueue(start.events)
    }

    val battleState: BattleState get() = state
    val awaitingPlayer: Boolean get() = !finished && queue.isEmpty() && wait <= 0f && !auto && state.activeUnit?.side == BattleSide.ALLY

    fun legalCommands(): List<BattleCommand> = if (awaitingPlayer) BattleEngine.legalCommands(state) else emptyList()

    fun issue(command: BattleCommand) {
        if (!awaitingPlayer) throw IllegalStateException("battle is not waiting for the player")
        apply(command)
    }

    fun setAuto(enabled: Boolean) {
        auto = enabled
        refreshControls()
    }

    override fun build(content: Table) {
        content.background = context.ui.tinted(Tokens.background)
        val top = Table().pad(Tokens.SPACE_S)
        top.background = context.ui.tinted(Tokens.surface)
        top.add(ui.label(text(context.services.content.encounters.first { it.id == encounterId }.name), "heading", testId("title"))).expandX().left()
        top.add(status).padRight(Tokens.SPACE_M)
        top.add(ui.button(testId("auto"), text(if (auto) "ui.battle.auto_on" else "ui.battle.auto_off"), "secondary") { setAuto(!auto) }).padRight(Tokens.SPACE_S)
        top.add(ui.button(testId("speed"), "x$speed", "secondary") {
            speed = if (speed >= 4) 1 else speed * 2
            rebuild()
        }).padRight(Tokens.SPACE_S)
        top.add(ui.button(testId("log"), text("ui.battle.log"), "secondary") {
            context.session.pendingBattleLog = log.toList()
            context.navigator.open(GameScreenId.BATTLE_BATTLE_LOG)
        }).padRight(Tokens.SPACE_S)
        top.add(ui.button(testId("flee"), text("ui.battle.flee"), "danger") { finish(BattleOutcome.DEFEAT) })
        content.top()
        content.add(top).growX().row()
        arena.setSize(Tokens.VIRTUAL_WIDTH, ARENA_HEIGHT)
        if (units.isEmpty()) placeUnits()
        content.add(arena).size(Tokens.VIRTUAL_WIDTH, ARENA_HEIGHT).expand().row()
        controls.background = context.ui.tinted(Tokens.surface)
        refreshControls()
        content.add(controls).growX().height(CONTROLS_HEIGHT)
    }

    private fun placeUnits() {
        state.combatants.forEach { combatant ->
            val sprite = if (combatant.id.side == BattleSide.ALLY) lookup.heroSprite(combatant.setup.name) else lookup.enemySprite(combatant.setup.name)
            val displayName = if (combatant.id.side == BattleSide.ALLY) lookup.heroName(combatant.setup.name) else lookup.enemyName(combatant.setup.name)
            val actor = CombatantActor(combatant.id, sprite, "$displayName Lv${combatant.setup.level}", combatant.maxHp, context.ui.whiteRegion, context.ui.skin, ::onUnitTapped)
            actor.energy = combatant.energy
            val depthOffset = combatant.setup.cell.depth * COLUMN_SPACING
            val x = if (combatant.id.side == BattleSide.ALLY) ALLY_FRONT_X - depthOffset else ENEMY_FRONT_X + depthOffset
            val y = LANE_BASE_Y + (2 - combatant.setup.cell.lane) * LANE_SPACING
            actor.setPosition(x, y)
            units[combatant.id] = actor
            arena.addActor(actor)
        }
    }

    private fun refreshControls() {
        controls.clearChildren()
        val active = state.activeUnit
        units.values.forEach { it.highlighted = it.unit == active; it.targetable = false }
        if (finished) return
        if (!awaitingPlayer) {
            controls.add(ui.label(text(if (auto) "ui.battle.auto_running" else "ui.battle.enemy_turn"), "muted"))
            return
        }
        val actor = state.combatant(requireNotNull(active))
        val legal = BattleEngine.legalCommands(state)
        controls.add(ui.label(lookup.heroName(actor.setup.name), "heading")).padRight(Tokens.SPACE_L)
        actor.setup.skills.forEach { skill ->
            val available = legal.any { it.skillId == skill.id }
            val suffix = when {
                skill.energyCost > 0 -> " (${actor.energy}/${skill.energyCost})"
                actor.cooldownOf(skill.id) > 0 -> " (${actor.cooldownOf(skill.id)})"
                else -> ""
            }
            val style = if (pendingSkill == skill.id) "primary" else "secondary"
            controls.add(ui.button(testId("skill/${skill.id}"), lookup.skillName(skill.id) + suffix, style, enabled = available) { chooseSkill(skill.id) })
                .height(Tokens.BUTTON_HEIGHT).padRight(Tokens.SPACE_S)
        }
        pendingSkill?.let { skillId ->
            legal.filter { it.skillId == skillId }.mapNotNull { it.target }.forEach { units[it]?.targetable = true }
            controls.add(ui.label(text("ui.battle.pick_target"), "muted")).padLeft(Tokens.SPACE_M)
        }
    }

    private fun chooseSkill(skillId: String) {
        val commands = BattleEngine.legalCommands(state).filter { it.skillId == skillId }
        val skill = state.combatant(requireNotNull(state.activeUnit)).setup.skill(skillId)
        if (!skill.targeting.needsChosenTarget || skill.targeting == Targeting.Self) {
            apply(commands.first())
        } else {
            pendingSkill = skillId
            refreshControls()
        }
    }

    private fun onUnitTapped(unit: UnitId) {
        val skillId = pendingSkill ?: return
        val command = BattleEngine.legalCommands(state).firstOrNull { it.skillId == skillId && it.target == unit } ?: return
        apply(command)
    }

    private fun apply(command: BattleCommand) {
        pendingSkill = null
        val step = BattleEngine.apply(state, command)
        state = step.state
        enqueue(step.events)
        refreshControls()
    }

    private fun enqueue(events: List<BattleEvent>) {
        queue.addAll(events)
        log.addAll(events)
    }

    override fun update(delta: Float) {
        if (finished) return
        wait -= delta * speed
        while (wait <= 0f && queue.isNotEmpty()) {
            wait += play(queue.removeFirst())
            if (finished) return
        }
        if (wait > 0f || queue.isNotEmpty()) return
        state.outcome?.let { finish(it); return }
        if (auto || state.activeUnit?.side == BattleSide.ENEMY) {
            apply(BattleEngine.autoCommand(state))
            wait = AI_THINK_SECONDS
        } else {
            refreshControlsOnce()
        }
    }

    private var lastPromptedActor: UnitId? = null
    private var lastPromptedAction = -1

    private fun refreshControlsOnce() {
        if (lastPromptedActor != state.activeUnit || lastPromptedAction != state.actionCount) {
            lastPromptedActor = state.activeUnit
            lastPromptedAction = state.actionCount
            refreshControls()
        }
    }

    private fun play(event: BattleEvent): Float {
        status.setText(text("ui.battle.round", state.round))
        return when (event) {
            is BattleEvent.TurnStarted -> {
                units.values.forEach { it.highlighted = it.unit == event.unit }
                TURN_SECONDS
            }
            is BattleEvent.SkillUsed -> {
                units[event.actor]?.let { actor ->
                    actor.figure.play(SpriteSet.ATTACK)
                    val lunge = if (event.actor.side == BattleSide.ALLY) LUNGE else -LUNGE
                    actor.addAction(Actions.sequence(Actions.moveBy(lunge, 0f, 0.12f), Actions.moveBy(-lunge, 0f, 0.12f), Actions.run { actor.figure.play(SpriteSet.IDLE) }))
                }
                val vfx = context.services.content.skills.firstOrNull { it.id == event.skillId }?.vfx
                if (vfx != null && context.assets.has(vfx)) event.targets.forEach { target -> spawnEffect(vfx, target) }
                floatText(event.actor, lookup.skillName(event.skillId), Tokens.text)
                SKILL_SECONDS
            }
            is BattleEvent.DamageDealt -> {
                units[event.target]?.let { target ->
                    target.hp = event.remainingHp
                    if (event.remainingHp > 0) target.figure.play(SpriteSet.HURT)
                    target.addAction(Actions.sequence(Actions.delay(0.2f), Actions.run { if (target.hp > 0) target.figure.play(SpriteSet.IDLE) }))
                }
                val label = if (event.critical) "${event.amount}!" else event.amount.toString()
                floatText(event.target, if (event.absorbedByShield > 0) "$label (${event.absorbedByShield})" else label, if (event.critical) Tokens.accent else Tokens.danger)
                HIT_SECONDS
            }
            is BattleEvent.Healed -> {
                units[event.target]?.hp = event.remainingHp
                floatText(event.target, "+${event.amount}", Tokens.positive)
                HIT_SECONDS
            }
            is BattleEvent.AttackMissed -> {
                floatText(event.target, text("ui.battle.miss"), Tokens.muted)
                HIT_SECONDS
            }
            is BattleEvent.StatusApplied -> {
                floatText(event.target, text(context.services.content.statuses.firstOrNull { it.id == event.statusId }?.name ?: event.statusId), Tokens.energy)
                STATUS_SECONDS
            }
            is BattleEvent.StatusResisted -> {
                floatText(event.target, text("ui.battle.resisted"), Tokens.muted)
                STATUS_SECONDS
            }
            is BattleEvent.EnergyChanged -> {
                units[event.unit]?.energy = event.energy
                0f
            }
            is BattleEvent.UnitDefeated -> {
                units[event.unit]?.let { unit ->
                    unit.figure.play(SpriteSet.DIE)
                    unit.addAction(Actions.sequence(Actions.delay(0.5f), Actions.alpha(0.25f, 0.3f)))
                    unit.touchable = Touchable.disabled
                }
                DEFEAT_SECONDS
            }
            is BattleEvent.TurnSkipped -> {
                floatText(event.unit, text("ui.battle.stunned"), Tokens.muted)
                STATUS_SECONDS
            }
            is BattleEvent.BattleEnded -> {
                finish(event.outcome)
                0f
            }
            is BattleEvent.BattleStarted, is BattleEvent.StatusExpired -> 0f
        }
    }

    private fun spawnEffect(key: String, target: UnitId) {
        val unit = units[target] ?: return
        val effect = EffectActor(context.assets.effect(key)).apply {
            setSize(CombatantActor.SIZE, CombatantActor.SIZE)
            setPosition(unit.x, unit.y + 16f)
            touchable = Touchable.disabled
        }
        arena.addActor(effect)
    }

    private fun floatText(unit: UnitId, value: String, color: Color) {
        val anchor = units[unit] ?: return
        val label = Label(value, context.ui.skin, "heading").apply {
            this.color = color.cpy()
            setPosition(anchor.x + CombatantActor.SIZE / 2 - prefWidth / 2, anchor.y + CombatantActor.SIZE + 30f)
            touchable = Touchable.disabled
        }
        arena.addActor(label)
        label.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0f, 40f, 0.8f), Actions.fadeOut(0.8f)), Actions.removeActor()))
    }

    private fun finish(outcome: BattleOutcome) {
        if (finished) return
        finished = true
        refreshControls()
        val defeated = state.combatants.count { it.id.side == BattleSide.ENEMY && !it.isAlive }
        val transition = context.act { context.services.rules.finishBattle(it, encounterId, outcome, defeated) }
        context.session.lastBattle = BattleSummary(encounterId, outcome, state.round, log.toList(), transition?.events.orEmpty())
        val result = when (outcome) {
            BattleOutcome.VICTORY -> GameScreenId.BATTLE_BATTLE_VICTORY
            BattleOutcome.DEFEAT -> GameScreenId.BATTLE_BATTLE_DEFEAT
            BattleOutcome.DRAW -> GameScreenId.BATTLE_BATTLE_DRAW
        }
        arena.addAction(Actions.sequence(Actions.delay(RESULT_DELAY_SECONDS), Actions.run { context.navigator.replace(result, ScreenArgs.of("encounter" to encounterId)) }))
    }

    override fun onStateChanged(state: com.pxworld.domain.progression.GameState, events: List<GameEvent>) {}

    companion object {
        const val ARENA_HEIGHT: Float = 540f
        const val CONTROLS_HEIGHT: Float = 96f
        const val ALLY_FRONT_X: Float = 500f
        const val ENEMY_FRONT_X: Float = 684f
        const val COLUMN_SPACING: Float = 150f
        const val LANE_BASE_Y: Float = 40f
        const val LANE_SPACING: Float = 160f
        const val LUNGE: Float = 30f
        const val TURN_SECONDS: Float = 0.12f
        const val SKILL_SECONDS: Float = 0.35f
        const val HIT_SECONDS: Float = 0.22f
        const val STATUS_SECONDS: Float = 0.15f
        const val DEFEAT_SECONDS: Float = 0.4f
        const val AI_THINK_SECONDS: Float = 0.25f
        const val RESULT_DELAY_SECONDS: Float = 1.0f
    }
}

class BattleResultScreen(id: GameScreenId, context: ScreenContext, args: ScreenArgs) : StandardScreen(id, context, args) {

    override val titleKey: String = when (id) {
        GameScreenId.BATTLE_BATTLE_VICTORY -> "ui.result.victory"
        GameScreenId.BATTLE_BATTLE_DEFEAT -> "ui.result.defeat"
        else -> "ui.result.draw"
    }
    override val showBack = false

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val summary = context.session.lastBattle
        if (summary != null) {
            content.add(ui.label(text("ui.result.rounds", summary.rounds), "muted")).padBottom(Tokens.SPACE_M).row()
            val gains = summary.gameEvents.mapNotNull { event ->
                when (event) {
                    is GameEvent.CurrencyChanged -> "+${event.entry.delta} ${lookup.currencyName(event.entry.currency)}"
                    is GameEvent.ItemsGained -> "+${event.quantity} ${lookup.itemName(event.itemId)}"
                    is GameEvent.HeroLeveledUp -> text("ui.result.level_up", lookup.heroName(context.state.hero(event.instanceId).heroId), event.level)
                    is GameEvent.ProfileLeveledUp -> text("ui.result.profile_level_up", event.level)
                    is GameEvent.QuestCompleted -> text("ui.result.quest_completed", context.services.content.quests.firstOrNull { it.id == event.questId }?.let { text(it.name) } ?: event.questId)
                    else -> null
                }
            }
            if (gains.isEmpty()) content.add(ui.label(text("ui.result.nothing"), "muted")).row()
            gains.forEachIndexed { index, line -> content.add(ui.label(line, "body", testId("gain/$index"))).left().row() }
        }
        content.add(ui.button(testId("continue"), text("ui.common.continue")) { context.navigator.back() }).width(260f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_L).row()
        if (id != GameScreenId.BATTLE_BATTLE_VICTORY) {
            content.add(ui.button(testId("retry"), text("ui.result.retry"), "secondary") {
                context.navigator.replace(GameScreenId.BATTLE_BATTLE_MAIN, ScreenArgs.of("encounter" to args["encounter"]))
            }).width(260f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_S)
        }
    }
}

class BattleLogScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_BATTLE_LOG, context, args) {

    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.battle.log"), "title")).row()
        val list = Table().top().left()
        context.session.pendingBattleLog.takeLast(LOG_LINES).forEach { list.add(ui.label(it.describe(), "small")).left().row() }
        content.add(ui.scroll(list, testId("entries"))).size(760f, 420f).row()
        content.add(ui.button(testId("close"), text("ui.common.close")) { context.navigator.back() }).padTop(Tokens.SPACE_M)
    }

    companion object {
        const val LOG_LINES: Int = 200
    }
}
