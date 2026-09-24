package com.pxworld.client.screens.onboarding

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.CollectionRules
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId

object Tutorials {
    val order: List<Pair<String, GameScreenId>> = listOf(
        "tutorial_move" to GameScreenId.ONBOARDING_TUTORIAL_MOVE,
        "tutorial_battle" to GameScreenId.ONBOARDING_TUTORIAL_BATTLE,
        "tutorial_lineup" to GameScreenId.ONBOARDING_TUTORIAL_LINEUP,
        CollectionRules.TUTORIAL_COMPLETE to GameScreenId.ONBOARDING_TUTORIAL_REWARD,
    )

    fun pending(context: ScreenContext, id: String): Boolean = context.session.store?.state?.journal?.tutorialsSeen?.contains(id) == false

    fun showIfPending(context: ScreenContext, id: String) {
        val screen = order.firstOrNull { it.first == id }?.second ?: return
        if (pending(context, id)) context.navigator.open(screen)
    }
}

abstract class TutorialScreen(id: GameScreenId, private val tutorialId: String, context: ScreenContext, args: ScreenArgs) : ModalScreen(id, context, args) {

    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.$tutorialId.title"), "title")).row()
        content.add(ui.label(text("ui.$tutorialId.body"), "body", testId("body"), wrap = true)).width(560f).pad(Tokens.SPACE_M).row()
        extra(content)
        content.add(ui.button(testId("got_it"), text("ui.tutorial.got_it")) {
            context.act { context.services.collection.markTutorial(it, tutorialId) }
            context.navigator.back()
        }).width(240f).height(Tokens.BUTTON_HEIGHT)
    }

    open fun extra(content: Table) {}
}

class TutorialMoveScreen(context: ScreenContext, args: ScreenArgs) : TutorialScreen(GameScreenId.ONBOARDING_TUTORIAL_MOVE, "tutorial_move", context, args)

class TutorialBattleScreen(context: ScreenContext, args: ScreenArgs) : TutorialScreen(GameScreenId.ONBOARDING_TUTORIAL_BATTLE, "tutorial_battle", context, args)

class TutorialLineupScreen(context: ScreenContext, args: ScreenArgs) : TutorialScreen(GameScreenId.ONBOARDING_TUTORIAL_LINEUP, "tutorial_lineup", context, args)

class TutorialRewardScreen(context: ScreenContext, args: ScreenArgs) : TutorialScreen(GameScreenId.ONBOARDING_TUTORIAL_REWARD, CollectionRules.TUTORIAL_COMPLETE, context, args) {
    override fun extra(content: Table) {
        val lookup = Lookup(context)
        CollectionRules.TUTORIAL_REWARD.forEach { content.add(ui.label("+ ${lookup.grantLabel(it)}", "positive")).row() }
    }
}
