package com.pxworld.client.screens.progression

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.TextPageScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId

class QuestDetailScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_QUEST_DETAIL, context, args) {
    override val titleKey = "ui.quests.detail"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val quest = context.services.catalog.quests().firstOrNull { it.id == args.optional("quest") } ?: return
        val record = context.services.content.quests.first { it.id == quest.id }
        val progress = context.state.quests.firstOrNull { it.questId == quest.id }
        content.add(ui.label(text(record.name), "title")).row()
        content.add(ui.label(text(record.description), "body", wrap = true)).width(760f).padTop(Tokens.SPACE_S).row()
        content.add(ui.label(text("ui.quests.objective.${quest.objectiveKind}", quest.count, quest.target?.let { target -> objectiveTarget(lookup, quest.objectiveKind, target) } ?: ""), "heading")).padTop(Tokens.SPACE_M).row()
        content.add(ui.label("${progress?.progress ?: 0}/${quest.count}", "title", testId("progress"))).row()
        content.add(ui.label(text("ui.encounter.rewards"), "heading")).padTop(Tokens.SPACE_M).row()
        quest.rewards.forEach { content.add(ui.label(lookup.grantLabel(it), "body")).row() }
        if (progress?.completed == true && !progress.claimed) {
            content.add(ui.button(testId("claim"), text("ui.quests.claim")) {
                val result = context.act { context.services.rules.claimQuest(it, quest.id) }
                if (result != null) context.navigator.open(GameScreenId.PROGRESSION_QUEST_CLAIM, ScreenArgs.of("quest" to quest.id))
            }).padTop(Tokens.SPACE_M)
        }
    }

    private fun objectiveTarget(lookup: Lookup, kind: String, target: String): String = when (kind) {
        "win_encounter" -> context.services.content.encounters.firstOrNull { it.id == target }?.let { text(it.name) } ?: target
        "collect_item" -> lookup.itemName(target)
        "reach_map" -> lookup.mapName(target)
        "defeat_enemies" -> lookup.enemyName(target)
        else -> target
    }
}

class QuestClaimScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.PROGRESSION_QUEST_CLAIM, context, args) {
    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val quest = context.services.catalog.quests().firstOrNull { it.id == args.optional("quest") }
        content.add(ui.label(text("ui.quests.completed_title"), "title")).row()
        quest?.rewards?.forEach { content.add(ui.label("+ ${lookup.grantLabel(it)}", "positive", testId("reward"))).pad(Tokens.SPACE_XS).row() }
        content.add(ui.button(testId("close"), text("ui.common.continue")) { context.navigator.back() }).padTop(Tokens.SPACE_M)
    }
}

class AchievementDetailScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_ACHIEVEMENT_DETAIL, context, args) {
    override val titleKey = "ui.achievements.detail"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val achievement = context.services.catalog.achievements().firstOrNull { it.id == args.optional("achievement") } ?: return
        val record = context.services.content.achievements.first { it.id == achievement.id }
        val claimed = context.state.claimedAchievementTiers[achievement.id] ?: 0
        val value = context.state.stats.value(achievement.counter)
        content.add(ui.label(text(record.name), "title")).colspan(3).row()
        content.add(ui.label(text(record.description), "muted")).colspan(3).padBottom(Tokens.SPACE_M).row()
        achievement.tiers.forEachIndexed { index, tier ->
            val state = when {
                index < claimed -> "ui.achievements.tier_claimed"
                value >= tier.target -> "ui.achievements.tier_ready"
                else -> "ui.achievements.tier_locked"
            }
            content.add(ui.label(text("ui.achievements.tier", index + 1, achievement.tiers.size), "heading")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label("${minOf(value, tier.target)}/${tier.target}  ${tier.rewards.joinToString(", ") { lookup.grantLabel(it) }}", "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(text(state), if (state.endsWith("ready")) "positive" else "muted", testId("tier/$index"))).left().row()
        }
    }
}

class CodexHeroesScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_CODEX_HEROES, context, args) {
    override val titleKey = "ui.codex.heroes"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val known = context.state.heroes.map { it.heroId }.toSet() + context.state.journal.recruitHistory.map { it.heroId }
        val grid = Table()
        context.services.content.heroes.forEachIndexed { index, hero ->
            val card = ui.panel(Tokens.SPACE_S)
            val discovered = hero.id in known
            if (discovered) card.add(SpriteActor(lookup.heroSprite(hero.id)).apply { setSize(72f, 72f) }).size(72f).row() else card.add(ui.label("?", "title")).size(72f).row()
            card.add(ui.label(if (discovered) lookup.heroName(hero.id) else "???", "heading", testId("hero/${hero.id}"))).row()
            card.add(ui.label(lookup.className(hero.classId), "muted"))
            grid.add(card).width(180f).pad(Tokens.SPACE_XS)
            if (index % 5 == 4) grid.row()
        }
        content.add(ui.label(text("ui.codex.progress", known.size, context.services.content.heroes.size), "muted")).padBottom(Tokens.SPACE_S).row()
        content.add(grid)
    }
}

class CodexEnemiesScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_CODEX_ENEMIES, context, args) {
    override val titleKey = "ui.codex.enemies"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val seen = context.state.journal.seenEnemies
        content.add(ui.label(text("ui.codex.progress", seen.size, context.services.content.enemies.size), "muted")).padBottom(Tokens.SPACE_S).row()
        val grid = Table()
        context.services.content.enemies.forEachIndexed { index, enemy ->
            val card = ui.panel(Tokens.SPACE_S)
            if (enemy.id in seen) card.add(SpriteActor(lookup.enemySprite(enemy.id)).apply { setSize(72f, 72f) }).size(72f).row() else card.add(ui.label("?", "title")).size(72f).row()
            card.add(ui.label(if (enemy.id in seen) lookup.enemyName(enemy.id) else "???", "body", testId("enemy/${enemy.id}"))).row()
            card.add(ui.label(lookup.className(enemy.classId), "muted"))
            grid.add(card).width(200f).pad(Tokens.SPACE_XS)
            if (index % 5 == 4) grid.row()
        }
        content.add(grid)
    }
}

class CodexItemsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_CODEX_ITEMS, context, args) {
    override val titleKey = "ui.codex.items"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val seen = context.state.journal.seenItems + context.state.inventory.items.keys
        content.add(ui.label(text("ui.codex.progress", seen.size, context.services.content.items.size), "muted")).padBottom(Tokens.SPACE_S).row()
        val grid = Table().top().left()
        context.services.content.items.forEachIndexed { index, item ->
            val cell = Table()
            if (item.id in seen) cell.add(ui.image(lookup.itemIcon(item.id), 40f)).size(40f).row() else cell.add(ui.label("?", "heading")).size(40f).row()
            cell.add(ui.label(if (item.id in seen) lookup.itemName(item.id) else "???", "small"))
            grid.add(cell).width(120f).pad(Tokens.SPACE_XS)
            if (index % 8 == 7) grid.row()
        }
        content.add(ui.scroll(grid, testId("grid"))).grow()
    }
}

class CodexMapsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_CODEX_MAPS, context, args) {
    override val titleKey = "ui.codex.maps"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val visited = context.state.journal.visitedMaps
        content.add(ui.label(text("ui.codex.progress", visited.size, context.services.content.maps.size), "muted")).colspan(2).padBottom(Tokens.SPACE_S).row()
        context.services.content.maps.groupBy { it.region }.forEach { (region, maps) ->
            content.add(ui.label(text("ui.region.${region.removePrefix("region.")}"), "heading")).colspan(2).left().padTop(Tokens.SPACE_S).row()
            maps.forEach { map ->
                content.add(ui.label(if (map.id in visited) lookup.mapName(map.id) else "???", if (map.id in visited) "body" else "muted", testId("map/${map.id}"))).left().padRight(Tokens.SPACE_L)
                content.add(ui.label(text("ui.encounter.recommended", map.recommendedLevel), "small")).left().row()
            }
        }
    }
}

class TipsLibraryScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.PROGRESSION_TIPS_LIBRARY, context, args) {
    override val titleKey = "ui.tips.title"
    override val sections = (1..6).map { "ui.tips.$it.title" to "ui.tips.$it.body" }
}

class GlossaryScreen(context: ScreenContext, args: ScreenArgs) : TextPageScreen(GameScreenId.PROGRESSION_GLOSSARY, context, args) {
    override val titleKey = "ui.glossary.title"
    override val sections = listOf("energy", "cooldown", "counter", "depth", "star", "shield", "status", "power").map { "ui.glossary.$it.title" to "ui.glossary.$it.body" }
}
