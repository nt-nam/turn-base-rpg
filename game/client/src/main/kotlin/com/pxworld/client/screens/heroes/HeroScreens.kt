package com.pxworld.client.screens.heroes

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.progression.ExperienceCurve
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.stats.StatFormula
import com.pxworld.screens.GameScreenId

private fun ScreenContext.power(hero: OwnedHero): Int = Lookup.power(services.rules.heroStats(state, hero.instanceId))

class HeroRosterScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_ROSTER, context, args) {

    override val titleKey = "ui.heroes.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val tools = Table()
        listOf("lineup" to GameScreenId.HEROES_LINEUP_EDITOR, "presets" to GameScreenId.HEROES_LINEUP_PRESETS, "analysis" to GameScreenId.HEROES_LINEUP_ANALYSIS, "synergy" to GameScreenId.HEROES_SYNERGY_VIEW, "counters" to GameScreenId.HEROES_CLASS_COUNTER_CHART)
            .forEach { (key, target) -> tools.add(ui.button(testId(key), text("ui.heroes.tool.$key"), "secondary") { context.navigator.open(target) }).padLeft(Tokens.SPACE_XS) }
        content.add(tools).right().padBottom(Tokens.SPACE_S).row()
        val grid = Table().top().left()
        state.heroes.sortedByDescending { context.power(it) }.forEachIndexed { index, hero ->
            val card = ui.panel()
            card.add(SpriteActor(lookup.heroSprite(hero.heroId)).apply { setSize(72f, 72f) }).size(72f).row()
            card.add(ui.label(lookup.heroName(hero.heroId), "heading")).row()
            card.add(ui.label(text("ui.heroes.level_star", hero.level, hero.star), "small")).row()
            card.add(ui.label(text("ui.heroes.power", context.power(hero)), "muted")).row()
            if (state.lineup.contains(hero.instanceId)) card.add(ui.label(text("ui.heroes.in_lineup"), "positive")).row()
            card.add(ui.button(testId("open/${hero.instanceId}"), text("ui.common.details"), "secondary") {
                context.navigator.open(GameScreenId.HEROES_HERO_OVERVIEW, ScreenArgs.of("hero" to hero.instanceId))
            }).growX().padTop(Tokens.SPACE_S)
            grid.add(card).width(200f).pad(Tokens.SPACE_S)
            if (index % 5 == 4) grid.row()
        }
        content.add(ui.scroll(grid, testId("list"))).grow()
    }
}

class HeroOverviewScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_OVERVIEW, context, args) {

    override val titleKey = "ui.heroes.overview_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val hero = state.heroes.firstOrNull { it.instanceId == args["hero"] } ?: return
        val heroRecord = context.services.content.heroes.first { it.id == hero.heroId }
        val left = ui.panel()
        left.add(SpriteActor(lookup.heroSprite(hero.heroId)).apply { setSize(160f, 160f) }).size(160f).row()
        left.add(ui.label(lookup.heroName(hero.heroId), "title")).row()
        left.add(ui.label("${lookup.heroTitle(hero.heroId)} | ${lookup.className(heroRecord.classId)}", "muted")).row()
        left.add(ui.label(text("ui.heroes.level_star", hero.level, hero.star), "body")).row()
        left.add(ui.label(text("ui.heroes.experience", hero.experience, ExperienceCurve.requiredForNextLevel(hero.level)), "small")).row()
        left.add(ui.label(text("ui.heroes.power", context.power(hero)), "heading", testId("power"))).padTop(Tokens.SPACE_S).row()
        left.add(ui.button(testId("star_up"), text("ui.heroes.star_up"), "secondary", enabled = hero.star < StatFormula.MAX_STAR) {
            context.navigator.open(GameScreenId.HEROES_HERO_STAR_UP, ScreenArgs.of("hero" to hero.instanceId))
        }).growX().padTop(Tokens.SPACE_M)

        val middle = ui.panel()
        middle.add(ui.label(text("ui.heroes.stats"), "heading")).colspan(2).left().row()
        lookup.statLines(context.services.rules.heroStats(state, hero.instanceId)).forEach { (label, value) ->
            middle.add(ui.label(label, "muted")).left().padRight(Tokens.SPACE_L)
            middle.add(ui.label(value, "body")).right().row()
        }
        middle.add(ui.label(text("ui.heroes.skills"), "heading")).colspan(2).left().padTop(Tokens.SPACE_M).row()
        heroRecord.skills.forEach { skillId ->
            val skill = context.services.content.skills.first { it.id == skillId }
            middle.add(ui.label(lookup.skillName(skillId), "body")).left().colspan(2).row()
            middle.add(ui.label(text(skill.description), "small", wrap = true)).width(320f).left().colspan(2).padBottom(Tokens.SPACE_XS).row()
        }

        val right = ui.panel()
        right.add(ui.label(text("ui.heroes.equipment"), "heading")).left().row()
        EquipmentSlot.values().forEach { slot ->
            val equipped = state.inventory.equippedOn(hero.instanceId).firstOrNull { it.slot == slot }
            val row = Table()
            row.add(ui.label(text("ui.slot.${slot.name.lowercase()}"), "muted")).width(110f).left()
            if (equipped != null) {
                row.add(ui.image(lookup.equipmentIcon(equipped.equipmentId), 32f)).size(32f).padRight(Tokens.SPACE_S)
                row.add(ui.label(lookup.equipmentName(equipped.equipmentId), "body")).expandX().left()
                row.add(ui.button(testId("unequip/${slot.name.lowercase()}"), text("ui.heroes.unequip"), "ghost") {
                    context.act { context.services.rules.unequip(it, equipped.instanceId) }
                })
            } else {
                row.add(ui.label(text("ui.heroes.empty_slot"), "small")).expandX().left()
            }
            row.add(ui.button(testId("equip/${slot.name.lowercase()}"), text("ui.heroes.change"), "secondary") {
                context.navigator.open(GameScreenId.INVENTORY_BAG_EQUIPMENT, ScreenArgs.of("hero" to hero.instanceId, "slot" to slot.name))
            }).padLeft(Tokens.SPACE_S)
            right.add(row).growX().padTop(Tokens.SPACE_XS).row()
        }

        content.add(heroOverviewLinks(context, hero, ::testId)).colspan(3).left().padBottom(Tokens.SPACE_M).row()
        content.add(left).top().width(260f).padRight(Tokens.SPACE_M)
        content.add(middle).top().width(380f).padRight(Tokens.SPACE_M)
        content.add(right).top().width(460f)
    }
}

class HeroStarUpScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_HERO_STAR_UP, context, args) {

    override val titleKey = "ui.heroes.star_up"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val target = state.heroes.firstOrNull { it.instanceId == args["hero"] } ?: return
        content.add(ui.label(text("ui.heroes.star_up_rule", lookup.heroName(target.heroId), target.star), "body", wrap = true)).width(700f).padBottom(Tokens.SPACE_M).row()
        val candidates = state.heroes.filter { it.instanceId != target.instanceId && it.heroId == target.heroId && it.star == target.star }
        if (candidates.isEmpty()) content.add(ui.label(text("ui.heroes.star_up_none"), "muted")).row()
        candidates.forEach { fodder ->
            val row = ui.panel()
            row.add(ui.label(text("ui.heroes.level_star", fodder.level, fodder.star), "body")).expandX().left()
            if (state.lineup.contains(fodder.instanceId)) row.add(ui.label(text("ui.heroes.in_lineup"), "negative")).padRight(Tokens.SPACE_S)
            row.add(ui.button(testId("consume/${fodder.instanceId}"), text("ui.heroes.consume"), "danger") {
                context.navigator.open(GameScreenId.HEROES_HERO_MERGE_CONFIRM, ScreenArgs.of("target" to target.instanceId, "fodder" to fodder.instanceId))
            })
            content.add(row).width(700f).padBottom(Tokens.SPACE_S).row()
        }
    }
}

class LineupEditorScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.HEROES_LINEUP_EDITOR, context, args) {

    override val titleKey = "ui.lineup.title"
    private var selected: GridCell? = null

    override fun onShow() = com.pxworld.client.screens.onboarding.Tutorials.showIfPending(context, "tutorial_lineup")

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        content.add(ui.label(text("ui.lineup.capacity", state.lineup.cells.size, state.lineup.capacity), "muted", testId("capacity"))).colspan(2).padBottom(Tokens.SPACE_S).row()
        val grid = Table()
        grid.add(ui.label(text("ui.lineup.back"), "small"))
        grid.add(ui.label(text("ui.lineup.middle"), "small"))
        grid.add(ui.label(text("ui.lineup.front"), "small")).row()
        for (lane in 0 until GridCell.GRID_SIZE) {
            for (depth in GridCell.GRID_SIZE - 1 downTo 0) {
                val cell = GridCell(lane, depth)
                val occupant = state.lineup.cells[cell]
                val caption = occupant?.let { lookup.heroName(state.hero(it).heroId) } ?: "+"
                val style = if (cell == selected) "primary" else "secondary"
                grid.add(ui.button(testId("cell/$lane-$depth"), caption, style) {
                    selected = if (selected == cell) null else cell
                    rebuild()
                }).size(150f, 72f).pad(Tokens.SPACE_XS)
            }
            grid.row()
        }
        val bench = Table().top()
        bench.add(ui.label(text("ui.lineup.bench"), "heading")).left().row()
        state.heroes.forEach { hero ->
            val row = Table()
            row.add(ui.label("${lookup.heroName(hero.heroId)} Lv${hero.level}", "body")).expandX().left()
            row.add(ui.button(testId("assign/${hero.instanceId}"), text("ui.lineup.assign"), "secondary", enabled = selected != null) {
                selected?.let { cell -> context.act { context.services.rules.placeInLineup(it, cell, hero.instanceId) } }
            })
            bench.add(row).width(360f).padBottom(Tokens.SPACE_XS).row()
        }
        bench.add(ui.button(testId("clear_cell"), text("ui.lineup.clear"), "danger", enabled = selected?.let { state.lineup.cells.containsKey(it) } == true) {
            selected?.let { cell -> context.act { context.services.rules.placeInLineup(it, cell, null) } }
        }).padTop(Tokens.SPACE_M).left()
        content.add(grid).top().padRight(Tokens.SPACE_L)
        content.add(ui.scroll(bench, testId("bench"))).top().height(480f)
    }
}
