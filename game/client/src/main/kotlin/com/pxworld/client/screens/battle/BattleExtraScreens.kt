package com.pxworld.client.screens.battle

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.GameEvent
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.ConfirmScreen
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.battle.BattleEvent
import com.pxworld.domain.battle.BattleSide
import com.pxworld.domain.battle.Combatant
import com.pxworld.domain.battle.UnitId
import com.pxworld.domain.stats.StatKind
import com.pxworld.screens.GameScreenId

internal fun ScreenContext.activeBattle(): BattleMainScreen? = navigator.visibleScreens().firstOrNull() as? BattleMainScreen

internal fun ModalScreen.noBattle(content: Table) {
    content.add(screenContext.widgets.label(screenContext.text("ui.battle.no_battle"), "muted")).pad(Tokens.SPACE_M).row()
    content.add(screenContext.widgets.button("${id.id}/close", screenContext.text("ui.common.close"), "secondary") { screenContext.navigator.back() })
}

internal fun Lookup.unitName(combatant: Combatant): String =
    if (combatant.id.side == BattleSide.ALLY) heroName(combatant.setup.name) else enemyName(combatant.setup.name)

class BattlePauseScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_BATTLE_PAUSE, context, args) {

    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.battle.paused"), "title")).padBottom(Tokens.SPACE_M).row()
        val entries = listOf(
            "timeline" to GameScreenId.BATTLE_TURN_TIMELINE,
            "weakness" to GameScreenId.BATTLE_WEAKNESS_HINT,
            "inspect" to GameScreenId.BATTLE_UNIT_INSPECT,
            "skills" to GameScreenId.BATTLE_SKILL_SELECT,
        )
        entries.forEach { (key, target) ->
            content.add(ui.button(testId(key), text("ui.battle.pause_$key"), "secondary") { context.navigator.replace(target) }).width(320f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.button(testId("flee"), text("ui.battle.flee"), "danger", enabled = context.activeBattle()?.isReplay == false) {
            context.navigator.replace(GameScreenId.BATTLE_FLEE_CONFIRM)
        }).width(320f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_S).row()
        content.add(ui.button(testId("resume"), text("ui.battle.resume")) { context.navigator.back() }).width(320f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_S)
    }
}

class SkillSelectScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_SKILL_SELECT, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val battle = context.activeBattle() ?: return noBattle(content)
        val state = battle.battleState
        val actorId = state.activeUnit ?: return
        val actor = state.combatant(actorId)
        val legal = battle.legalCommands().map { it.skillId }.toSet()
        content.add(ui.label(text("ui.battle.skills_of", lookup.unitName(actor)), "title")).colspan(2).padBottom(Tokens.SPACE_M).row()
        actor.setup.skills.forEach { skill ->
            val record = context.services.content.skills.firstOrNull { it.id == skill.id }
            val info = Table()
            info.add(ui.label(lookup.skillName(skill.id), "heading")).left().row()
            record?.let { info.add(ui.label(text(it.description), "small", wrap = true)).width(460f).left().row() }
            val cost = if (skill.energyCost > 0) text("ui.battle.energy_cost", skill.energyCost) else text("ui.battle.cooldown", skill.cooldownTurns)
            info.add(ui.label(cost, "muted")).left()
            content.add(info).left().padBottom(Tokens.SPACE_S)
            content.add(ui.button(testId("use/${skill.id}"), text("ui.battle.use"), enabled = skill.id in legal) {
                context.navigator.back()
                battle.selectSkill(skill.id)
            }).padLeft(Tokens.SPACE_M).row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).colspan(2).padTop(Tokens.SPACE_S)
    }
}

class TargetSelectScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_TARGET_SELECT, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val battle = context.activeBattle() ?: return noBattle(content)
        val skillId = args.optional("skill")
        val commands = battle.legalCommands().filter { skillId == null || it.skillId == skillId }.filter { it.target != null }
        content.add(ui.label(text("ui.battle.pick_target"), "title")).colspan(2).padBottom(Tokens.SPACE_M).row()
        if (commands.isEmpty()) content.add(ui.label(text("ui.battle.no_targets"), "muted")).colspan(2).row()
        commands.forEach { command ->
            val target = battle.battleState.combatant(requireNotNull(command.target))
            content.add(ui.label("${lookup.unitName(target)}  ${target.hp}/${target.maxHp}", "body")).left().padRight(Tokens.SPACE_M)
            content.add(ui.button(testId("target/${target.id}"), lookup.skillName(command.skillId)) {
                context.navigator.back()
                battle.issue(command)
            }).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).colspan(2).padTop(Tokens.SPACE_S)
    }
}

class UnitInspectScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_UNIT_INSPECT, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val battle = context.activeBattle() ?: return noBattle(content)
        val state = battle.battleState
        content.add(ui.label(text("ui.battle.units"), "title")).colspan(5).padBottom(Tokens.SPACE_M).row()
        listOf("ui.battle.unit", "ui.stat.hp", "ui.stat.attack", "ui.stat.speed", "ui.battle.statuses").forEach { content.add(ui.label(text(it), "muted")).left().padRight(Tokens.SPACE_M) }
        content.row()
        state.combatants.forEach { combatant ->
            val statuses = combatant.statuses.joinToString(", ") { active ->
                val name = context.services.content.statuses.firstOrNull { it.id == active.statusId }?.let { text(it.name) } ?: active.statusId
                "$name(${active.remainingTurns})"
            }.ifEmpty { "-" }
            content.add(ui.label(lookup.unitName(combatant), if (combatant.id.side == BattleSide.ALLY) "positive" else "negative", testId("unit/${combatant.id}"))).left().padRight(Tokens.SPACE_M)
            content.add(ui.label("${combatant.hp}/${combatant.maxHp}", "small")).left().padRight(Tokens.SPACE_M)
            content.add(ui.label(combatant.setup.stats[StatKind.ATTACK].toString(), "small")).left().padRight(Tokens.SPACE_M)
            content.add(ui.label(combatant.setup.stats[StatKind.SPEED].toString(), "small")).left().padRight(Tokens.SPACE_M)
            content.add(ui.label(statuses, "small")).left().row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).colspan(5).padTop(Tokens.SPACE_M)
    }
}

class TurnTimelineScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_TURN_TIMELINE, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val battle = context.activeBattle() ?: return noBattle(content)
        val state = battle.battleState
        content.add(ui.label(text("ui.battle.timeline"), "title")).padBottom(Tokens.SPACE_M).row()
        val rules = state.rules
        val projected = state.combatants.filter { it.isAlive }.flatMap { combatant ->
            val interval = rules.timeUnitsPerRound.toLong() * rules.referenceSpeed / combatant.setup.stats[StatKind.SPEED].coerceAtLeast(1)
            (0 until UPCOMING).map { step -> Triple(combatant, combatant.turnDelay + step * interval, step) }
        }.sortedWith(compareBy({ it.second }, { state.combatants.indexOf(it.first) })).take(UPCOMING)
        state.activeUnit?.let { active -> content.add(ui.label(text("ui.battle.now", lookup.unitName(state.combatant(active))), "heading")).left().row() }
        projected.forEachIndexed { index, (combatant, _, _) ->
            content.add(ui.label("${index + 1}. ${lookup.unitName(combatant)}", if (combatant.id.side == BattleSide.ALLY) "positive" else "negative", testId("slot/$index"))).left().row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).padTop(Tokens.SPACE_M)
    }

    companion object {
        const val UPCOMING: Int = 8
    }
}

class WeaknessHintScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.BATTLE_WEAKNESS_HINT, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val battle = context.activeBattle() ?: return noBattle(content)
        val classes = context.services.content.heroClasses.associateBy { it.id }
        content.add(ui.label(text("ui.battle.weakness"), "title")).padBottom(Tokens.SPACE_M).row()
        battle.battleState.livingOn(BattleSide.ENEMY).forEach { enemy ->
            val enemyClass = enemy.setup.classId
            val countered = classes.values.filter { enemyClass in it.counters }.map { lookup.className(it.id) }
            val threatens = classes[enemyClass]?.counters.orEmpty().map { lookup.className(it) }
            content.add(ui.label(lookup.unitName(enemy), "heading")).left().row()
            content.add(ui.label(text("ui.battle.weak_to", countered.joinToString(", ").ifEmpty { "-" }), "positive")).left().row()
            content.add(ui.label(text("ui.battle.strong_against", threatens.joinToString(", ").ifEmpty { "-" }), "negative")).left().padBottom(Tokens.SPACE_S).row()
        }
        content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).padTop(Tokens.SPACE_S)
    }
}

class FleeConfirmScreen(context: ScreenContext, args: ScreenArgs) : ConfirmScreen(GameScreenId.BATTLE_FLEE_CONFIRM, context, args) {
    override val titleText get() = text("ui.battle.flee_title")
    override val messageText get() = text("ui.battle.flee_warning")
    override val confirmText get() = text("ui.battle.flee")
    override val confirmStyle = "danger"

    override fun confirm() {
        val battle = context.activeBattle()
        context.navigator.back()
        battle?.flee()
    }
}

class RetryConfirmScreen(context: ScreenContext, args: ScreenArgs) : ConfirmScreen(GameScreenId.BATTLE_RETRY_CONFIRM, context, args) {
    override val titleText get() = text("ui.result.retry")
    override val messageText get() = text("ui.result.retry_warning")

    override fun confirm() {
        context.navigator.back()
        context.navigator.replace(GameScreenId.BATTLE_BATTLE_MAIN, ScreenArgs.of("encounter" to args["encounter"]))
    }
}

class BattleRewardsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BATTLE_BATTLE_REWARDS, context, args) {
    override val titleKey = "ui.result.rewards_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val events = context.session.lastBattle?.gameEvents.orEmpty()
        val lines = events.mapNotNull { event ->
            when (event) {
                is GameEvent.CurrencyChanged -> "${if (event.entry.delta > 0) "+" else ""}${event.entry.delta} ${lookup.currencyName(event.entry.currency)} (${event.entry.balanceAfter})"
                is GameEvent.ItemsGained -> "+${event.quantity} ${lookup.itemName(event.itemId)}"
                is GameEvent.EquipmentGained -> "+ ${lookup.equipmentName(event.equipmentId)}"
                is GameEvent.HeroRecruited -> "+ ${lookup.heroName(event.heroId)}"
                else -> null
            }
        }
        if (lines.isEmpty()) content.add(ui.label(text("ui.result.nothing"), "muted")).row()
        lines.forEachIndexed { index, line -> content.add(ui.label(line, "body", testId("line/$index"))).left().row() }
    }
}

class DamageBreakdownScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BATTLE_DAMAGE_BREAKDOWN, context, args) {
    override val titleKey = "ui.result.breakdown_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val events = context.session.lastBattle?.battleEvents.orEmpty()
        val dealt = mutableMapOf<UnitId, Int>()
        val taken = mutableMapOf<UnitId, Int>()
        val healed = mutableMapOf<UnitId, Int>()
        val names = mutableMapOf<UnitId, String>()
        events.forEach { event ->
            when (event) {
                is BattleEvent.DamageDealt -> {
                    dealt.merge(event.source, event.amount, Int::plus)
                    taken.merge(event.target, event.amount, Int::plus)
                }
                is BattleEvent.Healed -> healed.merge(event.source, event.amount, Int::plus)
                else -> Unit
            }
        }
        val units = (dealt.keys + taken.keys + healed.keys).sortedWith(compareBy({ it.side }, { it.slot }))
        context.session.lastBattle?.let { summary ->
            context.services.content.encounters.firstOrNull { it.id == summary.encounterId }?.enemies?.forEachIndexed { index, placed -> names[UnitId(BattleSide.ENEMY, index)] = lookup.enemyName(placed.enemy) }
        }
        listOf("ui.battle.unit", "ui.result.dealt", "ui.result.taken", "ui.result.healed").forEach { content.add(ui.label(text(it), "muted")).left().padRight(Tokens.SPACE_L) }
        content.row()
        units.forEach { unit ->
            content.add(ui.label(names[unit] ?: unit.toString(), if (unit.side == BattleSide.ALLY) "positive" else "negative", testId("unit/$unit"))).left().padRight(Tokens.SPACE_L)
            content.add(ui.label((dealt[unit] ?: 0).toString(), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label((taken[unit] ?: 0).toString(), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label((healed[unit] ?: 0).toString(), "body")).left().row()
        }
        if (units.isEmpty()) content.add(ui.label(text("ui.result.nothing"), "muted"))
    }
}

class LevelUpScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BATTLE_LEVEL_UP, context, args) {
    override val titleKey = "ui.result.level_up_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val events = context.session.lastBattle?.gameEvents.orEmpty()
        val levels = events.filterIsInstance<GameEvent.HeroLeveledUp>()
        if (levels.isEmpty()) content.add(ui.label(text("ui.result.no_level_up"), "muted")).row()
        levels.forEach { event ->
            val hero = context.state.heroes.firstOrNull { it.instanceId == event.instanceId } ?: return@forEach
            val stats = context.services.rules.heroStats(context.state, hero.instanceId)
            content.add(ui.label(text("ui.result.level_up", lookup.heroName(hero.heroId), event.level), "heading", testId("hero/${hero.instanceId}"))).left().row()
            content.add(ui.label(lookup.statLines(stats).take(4).joinToString("   ") { (label, value) -> "$label $value" }, "small")).left().padBottom(Tokens.SPACE_S).row()
        }
        events.filterIsInstance<GameEvent.ProfileLeveledUp>().forEach { content.add(ui.label(text("ui.result.profile_level_up", it.level), "positive")).left().row() }
    }
}

class BattleQuestProgressScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BATTLE_BATTLE_QUEST_PROGRESS, context, args) {
    override val titleKey = "ui.result.quests_title"

    override fun body(content: Table) {
        val progressed = context.session.lastBattle?.gameEvents.orEmpty().filterIsInstance<GameEvent.QuestProgressed>()
        if (progressed.isEmpty()) content.add(ui.label(text("ui.result.no_quest_progress"), "muted")).row()
        progressed.forEach { event ->
            val quest = context.services.content.quests.firstOrNull { it.id == event.questId }
            content.add(ui.label("${quest?.let { text(it.name) } ?: event.questId}: ${event.progress}/${event.target}", "body", testId("quest/${event.questId}"))).left().row()
        }
    }
}

class ReplayListScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.BATTLE_REPLAY_LIST, context, args) {
    override val titleKey = "ui.replay.title"

    override fun body(content: Table) {
        val list = Table().top()
        val replays = context.services.replays.list()
        if (replays.isEmpty()) list.add(ui.label(text("ui.replay.empty"), "muted")).row()
        replays.forEach { record ->
            val row = ui.panel(Tokens.SPACE_S)
            val encounterName = context.services.content.encounters.firstOrNull { it.id == record.encounterId }?.let { text(it.name) } ?: record.encounterId
            row.add(ui.label(encounterName, "heading")).expandX().left()
            row.add(ui.label(text("ui.replay.summary", text("ui.outcome.${record.outcome.name.lowercase()}"), record.rounds, record.commands.size), "muted")).padRight(Tokens.SPACE_M)
            row.add(ui.button(testId("watch/${record.id}"), text("ui.replay.watch")) {
                context.navigator.open(GameScreenId.BATTLE_REPLAY_VIEWER, ScreenArgs.of("encounter" to record.encounterId, "replay" to record.id))
            })
            list.add(row).width(1000f).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}
