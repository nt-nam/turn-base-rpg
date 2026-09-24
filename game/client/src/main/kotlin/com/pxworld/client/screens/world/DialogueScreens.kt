package com.pxworld.client.screens.world

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.QuestSummary
import com.pxworld.application.isQuestActive
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.ui.Tokens
import com.pxworld.content.DialogueChoiceRecord
import com.pxworld.content.DialogueRecord
import com.pxworld.content.NpcRecord
import com.pxworld.screens.GameScreenId

object Dialogues {

    fun npc(context: ScreenContext, npcId: String): NpcRecord = context.services.content.npcs.first { it.id == npcId }

    fun dialogueFor(context: ScreenContext, npc: NpcRecord): DialogueRecord {
        val rule = npc.dialogues.firstOrNull { rule ->
            rule.whenQuestActive?.let { context.state.isQuestActive(it, context.services.catalog) } ?: true
        } ?: npc.dialogues.last()
        return context.services.content.dialogues.first { it.id == rule.dialogue }
    }

    fun speakerName(context: ScreenContext, speaker: String): String =
        if (speaker == "player") context.state.profile.name else context.text(npc(context, speaker).name)

    fun perform(context: ScreenContext, choice: DialogueChoiceRecord) {
        when (choice.action) {
            "open_shop" -> context.navigator.open(GameScreenId.ECONOMY_SHOP_HOME)
            "open_recruit" -> context.navigator.open(GameScreenId.ECONOMY_RECRUIT_HOME)
            "open_bag" -> context.navigator.open(GameScreenId.INVENTORY_BAG_EQUIPMENT)
        }
    }

    fun activeMainQuest(context: ScreenContext): QuestSummary? =
        context.services.catalog.quests().firstOrNull { it.category == "main" && context.state.isQuestActive(it.id, context.services.catalog) }

    fun objectiveText(context: ScreenContext, quest: QuestSummary): String {
        val lookup = Lookup(context)
        val target = quest.target?.let { id ->
            when (quest.objectiveKind) {
                "talk_to_npc" -> context.text(npc(context, id).name)
                "win_encounter" -> context.services.content.encounters.firstOrNull { it.id == id }?.let { context.text(it.name) } ?: id
                "reach_map" -> lookup.mapName(id)
                "collect_item" -> lookup.itemName(id)
                else -> id
            }
        } ?: ""
        return context.text("ui.quests.objective.${quest.objectiveKind}", quest.count, target)
    }
}

class NpcDialogueScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_NPC_DIALOGUE, context, args) {

    private val npc = Dialogues.npc(context, args["npc"])
    private val dialogue = context.services.content.dialogues.firstOrNull { it.id == args.optional("dialogue") } ?: Dialogues.dialogueFor(context, npc)
    private var nodeId = args.optional("node") ?: dialogue.start

    override fun onShow() {
        if (args.optional("node") == null) context.act { context.services.rules.talkTo(it, npc.id) }
    }

    override fun dialog(content: Table) {
        val node = dialogue.nodes.first { it.id == nodeId }
        val speaker = if (node.speaker == "player") null else Dialogues.npc(context, node.speaker)
        val header = Table()
        speaker?.let { header.add(SpriteActor(context.assets.sprite(it.sprite)).apply { setSize(64f, 64f) }).size(64f).padRight(Tokens.SPACE_M) }
        header.add(ui.label(Dialogues.speakerName(context, node.speaker), "heading", testId("speaker"))).left()
        content.add(header).left().row()
        content.add(ui.label(text(node.text), "body", testId("line"), wrap = true)).width(640f).pad(Tokens.SPACE_M, 0f, Tokens.SPACE_M, 0f).row()
        when {
            node.choices.isNotEmpty() -> content.add(ui.button(testId("answer"), text("ui.dialogue.answer")) {
                context.navigator.replace(GameScreenId.WORLD_DIALOGUE_CHOICE, ScreenArgs.of("npc" to npc.id, "dialogue" to dialogue.id, "node" to node.id))
            }).width(240f).height(Tokens.BUTTON_HEIGHT)
            node.next != null -> content.add(ui.button(testId("next"), text("ui.common.next")) {
                nodeId = requireNotNull(node.next)
                rebuild()
            }).width(240f).height(Tokens.BUTTON_HEIGHT)
            else -> content.add(ui.button(testId("close"), text("ui.common.close")) { context.navigator.back() }).width(240f).height(Tokens.BUTTON_HEIGHT)
        }
    }
}

class DialogueChoiceScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.WORLD_DIALOGUE_CHOICE, context, args) {

    override fun dialog(content: Table) {
        val dialogue = context.services.content.dialogues.first { it.id == args["dialogue"] }
        val node = dialogue.nodes.first { it.id == args["node"] }
        content.add(ui.label(text(node.text), "muted", wrap = true)).width(640f).padBottom(Tokens.SPACE_M).row()
        node.choices.forEachIndexed { index, choice ->
            content.add(ui.button(testId("choice/$index"), text(choice.text), "secondary") {
                val next = choice.next
                if (next != null) {
                    context.navigator.replace(GameScreenId.WORLD_NPC_DIALOGUE, ScreenArgs.of("npc" to args["npc"], "dialogue" to dialogue.id, "node" to next))
                } else {
                    context.navigator.back()
                    Dialogues.perform(context, choice)
                }
            }).width(520f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_XS).row()
        }
    }
}
