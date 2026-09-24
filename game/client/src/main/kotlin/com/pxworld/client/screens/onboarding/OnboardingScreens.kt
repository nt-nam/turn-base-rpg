package com.pxworld.client.screens.onboarding

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.GameRuleViolation
import com.pxworld.application.NewGame
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.boot.SlotLoading
import com.pxworld.client.ui.Tokens
import com.pxworld.content.BattleContentAssembler
import com.pxworld.domain.stats.StatFormula
import com.pxworld.screens.GameScreenId

class HeroCreateClassScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ONBOARDING_HERO_CREATE_CLASS, context, args) {

    override val titleKey = "ui.create.class_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val grid = Table().top()
        context.services.catalog.starterHeroes().forEachIndexed { index, heroId ->
            val hero = context.services.content.heroes.first { it.id == heroId }
            val card = ui.panel()
            card.add(SpriteActor(lookup.heroSprite(heroId)).apply { setSize(96f, 96f) }).size(96f).row()
            card.add(ui.label(lookup.heroName(heroId), "heading")).row()
            card.add(ui.label(lookup.heroTitle(heroId), "muted")).row()
            card.add(ui.label(lookup.className(hero.classId), "small")).padBottom(Tokens.SPACE_S).row()
            val stats = StatFormula.grow(BattleContentAssembler.statBlock(hero.baseStats), 1, 0)
            card.add(ui.label(text("ui.create.power", Lookup.power(stats)), "small")).padBottom(Tokens.SPACE_S).row()
            card.add(ui.button(testId("pick/${heroId.removePrefix("hero.")}"), text("ui.create.choose")) {
                context.navigator.open(GameScreenId.ONBOARDING_HERO_CREATE_NAME, ScreenArgs.of("hero" to heroId))
            }).growX()
            grid.add(card).width(190f).pad(Tokens.SPACE_S)
            if (index % 3 == 2) grid.row()
        }
        content.add(grid)
    }
}

class HeroCreateNameScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ONBOARDING_HERO_CREATE_NAME, context, args) {

    override val titleKey = "ui.create.name_title"
    private var draft = ""

    override fun body(content: Table) {
        val field = ui.textField(testId("name"), draft, text("ui.create.name_hint"))
        field.setTextFieldListener { textField, _ -> draft = textField.text }
        field.maxLength = NewGame.NAME_LENGTH.last
        content.add(ui.label(text("ui.create.name_rule", NewGame.NAME_LENGTH.first, NewGame.NAME_LENGTH.last), "muted")).padBottom(Tokens.SPACE_S).row()
        content.add(field).width(420f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("next"), text("ui.common.next")) {
            val name = field.text.trim()
            if (name.length !in NewGame.NAME_LENGTH) {
                context.navigator.toast(text("ui.create.name_rule", NewGame.NAME_LENGTH.first, NewGame.NAME_LENGTH.last), positive = false)
            } else {
                context.navigator.open(GameScreenId.ONBOARDING_HERO_CREATE_CONFIRM, ScreenArgs.of("hero" to args["hero"], "name" to name))
            }
        }).width(240f).height(Tokens.BUTTON_HEIGHT)
        content.stage?.keyboardFocus = field
    }
}

class HeroCreateConfirmScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ONBOARDING_HERO_CREATE_CONFIRM, context, args) {

    override val titleKey = "ui.create.confirm_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val heroId = args["hero"]
        val name = args["name"]
        content.add(SpriteActor(lookup.heroSprite(heroId)).apply { setSize(128f, 128f) }).size(128f).row()
        content.add(ui.label(text("ui.create.confirm_summary", name, lookup.heroName(heroId), lookup.className(lookup.heroClass(heroId))), "body")).padBottom(Tokens.SPACE_L).row()
        content.add(ui.button(testId("start"), text("ui.create.start")) {
            try {
                val state = context.services.newGame.create(name, heroId)
                val slot = SlotLoading.slotNameFor(name, context.services.saves.slots())
                SlotLoading.start(context, slot, state)
                context.navigator.toast(text("ui.create.welcome", name))
            } catch (violation: GameRuleViolation) {
                context.navigator.toast(violation.message ?: "", positive = false)
            }
        }).width(280f).height(Tokens.BUTTON_HEIGHT)
    }
}
