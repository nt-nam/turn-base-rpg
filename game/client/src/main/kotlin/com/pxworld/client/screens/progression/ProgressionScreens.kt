package com.pxworld.client.screens.progression

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId

class QuestListScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_QUEST_SIDE, context, args) {

    override val titleKey = "ui.quests.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val list = Table().top()
        context.services.catalog.quests().forEach { quest ->
            val record = context.services.content.quests.first { it.id == quest.id }
            val progress = state.quests.firstOrNull { it.questId == quest.id }
            val row = ui.panel()
            val info = Table()
            info.add(ui.label(text(record.name), "heading")).left().row()
            info.add(ui.label(text(record.description), "small", wrap = true)).width(620f).left().row()
            info.add(ui.label(quest.rewards.joinToString("  |  ") { lookup.grantLabel(it) }, "muted")).left()
            row.add(info).expandX().left()
            row.add(ui.label("${progress?.progress ?: 0}/${quest.count}", "body", testId("progress/${quest.id}"))).padRight(Tokens.SPACE_M)
            when {
                progress?.claimed == true -> row.add(ui.label(text("ui.quests.claimed"), "positive"))
                progress?.completed == true -> row.add(ui.button(testId("claim/${quest.id}"), text("ui.quests.claim")) {
                    context.act(text("ui.quests.claimed")) { context.services.rules.claimQuest(it, quest.id) }
                })
                else -> row.add(ui.label(text("ui.quests.in_progress"), "muted"))
            }
            list.add(row).width(1000f).padBottom(Tokens.SPACE_S).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}

class AchievementListScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.PROGRESSION_ACHIEVEMENT_LIST, context, args) {

    override val titleKey = "ui.achievements.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val list = Table().top()
        context.services.catalog.achievements().forEach { achievement ->
            val record = context.services.content.achievements.first { it.id == achievement.id }
            val claimed = state.claimedAchievementTiers[achievement.id] ?: 0
            val next = achievement.tiers.getOrNull(claimed)
            val value = state.stats.value(achievement.counter)
            val row = ui.panel()
            val info = Table()
            info.add(ui.label(text(record.name), "heading")).left().row()
            info.add(ui.label(text(record.description), "small")).left().row()
            if (next != null) info.add(ui.label(next.rewards.joinToString("  |  ") { lookup.grantLabel(it) }, "muted")).left()
            row.add(info).expandX().left()
            row.add(ui.label(if (next == null) text("ui.achievements.complete") else "$value/${next.target}", "body", testId("progress/${achievement.id}"))).padRight(Tokens.SPACE_M)
            row.add(ui.label(text("ui.achievements.tier", claimed, achievement.tiers.size), "muted")).padRight(Tokens.SPACE_M)
            row.add(ui.button(testId("claim/${achievement.id}"), text("ui.quests.claim"), enabled = next != null && value >= next.target) {
                context.act(text("ui.quests.claimed")) { context.services.rules.claimAchievementTier(it, achievement.id) }
            })
            list.add(row).width(1000f).padBottom(Tokens.SPACE_S).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}
