package com.pxworld.client.screens.heroes

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.ConfirmScreen
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.stats.StatFormula
import com.pxworld.domain.stats.StatKind
import com.pxworld.screens.GameScreenId

private fun ScreenContext.heroArg(args: ScreenArgs, key: String = "hero"): OwnedHero? = state.heroes.firstOrNull { it.instanceId == args.optional(key) }

private val STAT_NAMES = mapOf(
    StatKind.HP to "hp", StatKind.ATTACK to "attack", StatKind.DEFENSE to "defense", StatKind.SPEED to "speed",
    StatKind.CRIT_RATE to "crit_rate", StatKind.CRIT_DAMAGE to "crit_damage", StatKind.ACCURACY to "accuracy",
    StatKind.EVASION to "evasion", StatKind.EFFECT_HIT to "effect_hit", StatKind.EFFECT_RESISTANCE to "effect_resistance",
)

class HeroStatsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_STATS, context, args) {
    override val titleKey = "ui.heroes.stats"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.heroArg(args) ?: return
        val base = StatFormula.grow(context.services.catalog.heroBaseStats(hero.heroId), hero.level, hero.star)
        val gear = context.services.collection.equipmentBonus(context.state, hero.instanceId)
        val total = context.services.rules.heroStats(context.state, hero.instanceId)
        content.add(ui.label(lookup.heroName(hero.heroId), "title")).colspan(4).padBottom(Tokens.SPACE_M).row()
        listOf("ui.heroes.stat", "ui.heroes.base", "ui.heroes.gear", "ui.heroes.total").forEach { content.add(ui.label(text(it), "muted")).left().padRight(Tokens.SPACE_L) }
        content.row()
        StatKind.values().forEach { kind ->
            content.add(ui.label(text("ui.stat.${STAT_NAMES.getValue(kind)}"), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(base[kind].toString(), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(if (gear[kind] > 0) "+${gear[kind]}" else "-", "positive")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(total[kind].toString(), "heading", testId("total/${STAT_NAMES.getValue(kind)}"))).left().row()
        }
    }
}

class HeroSkillsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_SKILLS, context, args) {
    override val titleKey = "ui.heroes.skills"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.heroArg(args) ?: return
        val record = context.services.content.heroes.first { it.id == hero.heroId }
        record.skills.forEach { skillId ->
            val skill = context.services.content.skills.first { it.id == skillId }
            val card = ui.panel()
            card.add(ui.label(lookup.skillName(skillId), "heading", testId("skill/$skillId"))).left().row()
            card.add(ui.label(text("ui.skill.slot.${skill.slot.name.lowercase()}"), "muted")).left().row()
            card.add(ui.label(text(skill.description), "body", wrap = true)).width(900f).left().row()
            val cost = if (skill.energyCost > 0) text("ui.battle.energy_cost", skill.energyCost) else if (skill.cooldownTurns > 0) text("ui.battle.cooldown", skill.cooldownTurns) else text("ui.skill.free")
            card.add(ui.label("$cost | ${text("ui.skill.target.${skill.targeting.kind.name.lowercase()}", skill.targeting.count ?: 0)}", "small")).left()
            content.add(card).width(940f).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class HeroEquipmentScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_EQUIPMENT, context, args) {
    override val titleKey = "ui.heroes.equipment"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.heroArg(args) ?: return
        EquipmentSlot.values().forEach { slot ->
            val equipped = context.state.inventory.equippedOn(hero.instanceId).firstOrNull { it.slot == slot }
            val row = ui.panel(Tokens.SPACE_S)
            row.add(ui.label(text("ui.slot.${slot.name.lowercase()}"), "muted")).width(140f).left()
            if (equipped != null) {
                row.add(ui.image(lookup.equipmentIcon(equipped.equipmentId), 40f)).size(40f).padRight(Tokens.SPACE_S)
                row.add(ui.label("${lookup.equipmentName(equipped.equipmentId)} +${equipped.level - 1}", "body")).expandX().left()
                row.add(ui.button(testId("upgrade/${slot.name.lowercase()}"), text("ui.bag.upgrade"), "secondary") {
                    context.navigator.open(GameScreenId.INVENTORY_EQUIPMENT_UPGRADE, ScreenArgs.of("equipment" to equipped.instanceId))
                }).padRight(Tokens.SPACE_XS)
            } else {
                row.add(ui.label(text("ui.heroes.empty_slot"), "small")).expandX().left()
            }
            row.add(ui.button(testId("change/${slot.name.lowercase()}"), text("ui.heroes.change")) {
                context.navigator.open(GameScreenId.INVENTORY_BAG_EQUIPMENT, ScreenArgs.of("hero" to hero.instanceId, "slot" to slot.name))
            })
            content.add(row).width(900f).padBottom(Tokens.SPACE_XS).row()
        }
    }
}

class HeroLoreScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_LORE, context, args) {
    override val titleKey = "ui.heroes.lore"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.heroArg(args) ?: return
        val record = context.services.content.heroes.first { it.id == hero.heroId }
        content.add(SpriteActor(lookup.heroSprite(hero.heroId)).apply { setSize(160f, 160f) }).size(160f).row()
        content.add(ui.label("${lookup.heroName(hero.heroId)}, ${lookup.heroTitle(hero.heroId)}", "title")).row()
        content.add(ui.label(lookup.className(record.classId), "muted")).padBottom(Tokens.SPACE_M).row()
        content.add(ui.label(text(record.description), "body", testId("description"), wrap = true)).width(700f).row()
        val counters = context.services.content.heroClasses.first { it.id == record.classId }.counters.map { lookup.className(it) }
        content.add(ui.label(text("ui.battle.strong_against", counters.joinToString(", ").ifEmpty { "-" }), "positive")).padTop(Tokens.SPACE_M)
    }
}

class HeroLevelUpScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_LEVEL_UP, context, args) {
    override val titleKey = "ui.heroes.level_up"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.heroArg(args) ?: return
        content.add(ui.label(text("ui.heroes.level_star", hero.level, hero.star), "heading", testId("level"))).colspan(3).row()
        content.add(ui.label(text("ui.heroes.experience", hero.experience, com.pxworld.domain.progression.ExperienceCurve.requiredForNextLevel(hero.level)), "muted")).colspan(3).padBottom(Tokens.SPACE_M).row()
        val usable = context.state.inventory.items.filterKeys { context.services.catalog.itemExperience(it) != null }
        if (usable.isEmpty()) content.add(ui.label(text("ui.heroes.no_exp_items"), "muted")).colspan(3).row()
        usable.forEach { (itemId, quantity) ->
            content.add(ui.label("${lookup.itemName(itemId)} x $quantity", "body")).left().padRight(Tokens.SPACE_M)
            content.add(ui.label(text("ui.bag.gives_experience", context.services.catalog.itemExperience(itemId) ?: 0), "muted")).left().padRight(Tokens.SPACE_M)
            content.add(ui.button(testId("feed/${itemId.removePrefix("item.")}"), text("ui.bag.use")) {
                context.act { context.services.rules.useExperienceItem(it, itemId, hero.instanceId) }
            }).padBottom(Tokens.SPACE_XS).row()
        }
    }
}

class HeroMergeConfirmScreen(context: ScreenContext, args: ScreenArgs) : ConfirmScreen(GameScreenId.HEROES_HERO_MERGE_CONFIRM, context, args) {
    private val lookup = Lookup(context)
    override val titleText get() = text("ui.heroes.star_up")
    override val messageText get() = text("ui.heroes.merge_warning", lookup.heroName(context.state.hero(args["target"]).heroId))
    override val confirmText get() = text("ui.heroes.consume")
    override val confirmStyle = "danger"

    override fun confirm() {
        val result = context.act(text("ui.heroes.star_up_done")) { context.services.rules.raiseStar(it, args["target"], args["fodder"]) }
        context.navigator.back()
        if (result != null) context.navigator.back()
    }
}

class HeroCompareScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_COMPARE, context, args) {
    override val titleKey = "ui.heroes.compare"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val first = context.heroArg(args, "first") ?: state.heroes.first()
        val second = context.heroArg(args, "second") ?: state.heroes.firstOrNull { it.instanceId != first.instanceId }
        if (second == null) {
            content.add(ui.label(text("ui.heroes.compare_need_two"), "muted"))
            return
        }
        val left = context.services.rules.heroStats(state, first.instanceId)
        val right = context.services.rules.heroStats(state, second.instanceId)
        content.add(ui.label(text("ui.heroes.stat"), "muted")).left().padRight(Tokens.SPACE_L)
        content.add(ui.label(lookup.heroName(first.heroId), "heading")).padRight(Tokens.SPACE_L)
        content.add(ui.label(lookup.heroName(second.heroId), "heading")).row()
        StatKind.values().take(6).forEach { kind ->
            content.add(ui.label(text("ui.stat.${STAT_NAMES.getValue(kind)}"), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(left[kind].toString(), if (left[kind] >= right[kind]) "positive" else "body")).padRight(Tokens.SPACE_L)
            content.add(ui.label(right[kind].toString(), if (right[kind] >= left[kind]) "positive" else "body")).row()
        }
        val others = Table()
        state.heroes.filter { it.instanceId != first.instanceId }.forEach { other ->
            others.add(ui.button(testId("with/${other.instanceId}"), lookup.heroName(other.heroId), if (other == second) "tab-active" else "tab") {
                context.navigator.replace(id, ScreenArgs.of("first" to first.instanceId, "second" to other.instanceId))
            }).padRight(Tokens.SPACE_XS)
        }
        content.add(others).colspan(3).padTop(Tokens.SPACE_M)
    }
}

class HeroDismissScreen(context: ScreenContext, args: ScreenArgs) : ConfirmScreen(GameScreenId.HEROES_HERO_DISMISS, context, args) {
    private val lookup = Lookup(context)
    private val hero get() = context.state.hero(args["hero"])
    override val titleText get() = text("ui.heroes.dismiss")
    override val messageText get() = text("ui.heroes.dismiss_warning", lookup.heroName(hero.heroId), context.services.collection.dismissValue(hero.star).amount)
    override val confirmText get() = text("ui.heroes.dismiss")
    override val confirmStyle = "danger"

    override fun confirm() {
        val result = context.act(text("ui.heroes.dismissed")) { context.services.collection.dismissHero(it, args["hero"]) }
        context.navigator.back()
        if (result != null) context.navigator.backTo(GameScreenId.HEROES_HERO_ROSTER)
    }
}

class LineupPresetsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_LINEUP_PRESETS, context, args) {
    override val titleKey = "ui.lineup.presets"
    private var draft = ""

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val field = ui.textField(testId("name"), draft, text("ui.lineup.preset_name"))
        field.setTextFieldListener { textField, _ -> draft = textField.text }
        content.add(field).width(320f).height(Tokens.BUTTON_HEIGHT).padRight(Tokens.SPACE_S)
        content.add(ui.button(testId("save"), text("ui.lineup.save_preset")) {
            context.act(text("ui.lineup.preset_saved")) { context.services.collection.savePreset(it, field.text) }
        }).height(Tokens.BUTTON_HEIGHT).row()
        val presets = context.state.journal.lineupPresets
        if (presets.isEmpty()) content.add(ui.label(text("ui.lineup.no_presets"), "muted")).colspan(2).padTop(Tokens.SPACE_M).row()
        presets.forEach { (name, cells) ->
            val row = ui.panel(Tokens.SPACE_S)
            row.add(ui.label(name, "heading")).width(200f).left()
            row.add(ui.label(cells.values.mapNotNull { instance -> context.state.heroes.firstOrNull { it.instanceId == instance }?.let { lookup.heroName(it.heroId) } }.joinToString(", "), "small")).expandX().left()
            row.add(ui.button(testId("apply/$name"), text("ui.lineup.apply")) { context.act(text("ui.lineup.preset_applied")) { context.services.collection.applyPreset(it, name) } }).padRight(Tokens.SPACE_XS)
            row.add(ui.button(testId("delete/$name"), text("ui.slots.delete"), "danger") { context.act { context.services.collection.deletePreset(it, name) } })
            content.add(row).colspan(2).width(900f).padTop(Tokens.SPACE_XS).row()
        }
    }
}

class LineupAnalysisScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_LINEUP_ANALYSIS, context, args) {
    override val titleKey = "ui.lineup.analysis"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val members = state.lineup.cells.map { (cell, instance) -> cell to state.hero(instance) }
        val power = members.sumOf { (_, hero) -> Lookup.power(context.services.rules.heroStats(state, hero.instanceId)) }
        content.add(ui.label(text("ui.lineup.total_power", power), "title", testId("power"))).left().row()
        val classes = members.map { (_, hero) -> lookup.heroClass(hero.heroId) }
        content.add(ui.label(text("ui.lineup.classes", classes.groupingBy { lookup.className(it) }.eachCount().entries.joinToString(", ") { "${it.key} x${it.value}" }), "body")).left().padTop(Tokens.SPACE_S).row()
        val warnings = mutableListOf<String>()
        if (members.none { (cell, _) -> cell.depth == 0 }) warnings += text("ui.lineup.warn_no_front")
        if ("class.support" !in classes) warnings += text("ui.lineup.warn_no_healer")
        if ("class.tank" !in classes && members.size >= 3) warnings += text("ui.lineup.warn_no_tank")
        if (members.size < state.lineup.capacity) warnings += text("ui.lineup.warn_free_slots", state.lineup.capacity - members.size)
        content.add(ui.label(text("ui.lineup.advice"), "heading")).left().padTop(Tokens.SPACE_M).row()
        if (warnings.isEmpty()) content.add(ui.label(text("ui.lineup.balanced"), "positive")).left().row()
        warnings.forEachIndexed { index, warning -> content.add(ui.label("- $warning", "negative", testId("warning/$index"))).left().row() }
    }
}

class SynergyViewScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_SYNERGY_VIEW, context, args) {
    override val titleKey = "ui.lineup.synergy"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val classes = context.services.content.heroClasses.associateBy { it.id }
        val teamClasses = state.lineup.cells.values.map { lookup.heroClass(state.hero(it).heroId) }.toSet()
        val covered = teamClasses.flatMap { classes[it]?.counters.orEmpty() }.toSet()
        val threats = classes.values.filter { other -> other.counters.any { it in teamClasses } }.map { it.id }.toSet()
        content.add(ui.label(text("ui.lineup.covers"), "heading")).left().row()
        classes.keys.forEach { classId ->
            val style = if (classId in covered) "positive" else "muted"
            content.add(ui.label("${lookup.className(classId)}: ${text(if (classId in covered) "ui.lineup.countered" else "ui.lineup.not_countered")}", style, testId("cover/$classId"))).left().row()
        }
        content.add(ui.label(text("ui.lineup.threatened_by", threats.joinToString(", ") { lookup.className(it) }.ifEmpty { "-" }), "negative")).left().padTop(Tokens.SPACE_M)
    }
}

class ClassCounterChartScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_CLASS_COUNTER_CHART, context, args) {
    override val titleKey = "ui.lineup.counter_chart"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val classes = context.services.content.heroClasses
        content.add(ui.label(text("ui.lineup.attacker_defender"), "muted")).left().padRight(Tokens.SPACE_M)
        classes.forEach { content.add(ui.label(lookup.className(it.id), "small")).padRight(Tokens.SPACE_S) }
        content.row()
        classes.forEach { attacker ->
            content.add(ui.label(lookup.className(attacker.id), "body")).left().padRight(Tokens.SPACE_M)
            classes.forEach { defender ->
                val mark = when {
                    defender.id in attacker.counters -> "+25%"
                    attacker.id in defender.counters -> "-15%"
                    else -> "."
                }
                content.add(ui.label(mark, if (mark.startsWith("+")) "positive" else if (mark.startsWith("-")) "negative" else "muted", testId("cell/${attacker.id}/${defender.id}"))).padRight(Tokens.SPACE_S)
            }
            content.row()
        }
    }
}

internal fun heroOverviewLinks(context: ScreenContext, hero: OwnedHero, testId: (String) -> String): Table {
    val links = Table()
    listOf(
        "stats" to GameScreenId.HEROES_HERO_STATS,
        "skills" to GameScreenId.HEROES_HERO_SKILLS,
        "gear" to GameScreenId.HEROES_HERO_EQUIPMENT,
        "lore" to GameScreenId.HEROES_HERO_LORE,
        "level_up" to GameScreenId.HEROES_HERO_LEVEL_UP,
        "compare" to GameScreenId.HEROES_HERO_COMPARE,
    ).forEach { (key, target) ->
        val arguments = if (target == GameScreenId.HEROES_HERO_COMPARE) ScreenArgs.of("first" to hero.instanceId) else ScreenArgs.of("hero" to hero.instanceId)
        links.add(context.widgets.button(testId("link/$key"), context.text("ui.heroes.link.$key"), "secondary") { context.navigator.open(target, arguments) }).padRight(Tokens.SPACE_XS)
    }
    links.add(context.widgets.button(testId("lock"), context.text(if (hero.locked) "ui.heroes.unlock" else "ui.heroes.lock"), "ghost") {
        context.act { context.services.collection.toggleLock(it, hero.instanceId) }
    }).padRight(Tokens.SPACE_XS)
    links.add(context.widgets.button(testId("dismiss"), context.text("ui.heroes.dismiss"), "danger", enabled = !hero.locked && !context.state.lineup.contains(hero.instanceId)) {
        context.navigator.open(GameScreenId.HEROES_HERO_DISMISS, ScreenArgs.of("hero" to hero.instanceId))
    })
    return links
}
