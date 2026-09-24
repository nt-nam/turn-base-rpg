package com.pxworld.client.screens.inventory

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.screens.GameScreenId

private fun StandardScreen.tabs(context: ScreenContext, content: Table, active: GameScreenId, testId: (String) -> String, carry: ScreenArgs) {
    val bar = Table()
    listOf(
        GameScreenId.INVENTORY_BAG_EQUIPMENT to "ui.bag.equipment",
        GameScreenId.INVENTORY_BAG_CONSUMABLES to "ui.bag.items",
        GameScreenId.INVENTORY_BAG_MATERIALS to "ui.bag.materials",
        GameScreenId.INVENTORY_BAG_KEY_ITEMS to "ui.bag.key_items",
    ).forEach { (target, key) ->
        bar.add(context.widgets.button(testId("tab/${target.id.substringAfterLast('.')}"), context.text(key), if (target == active) "tab-active" else "tab") {
            if (target != active) context.navigator.replace(target, carry)
        }).padRight(Tokens.SPACE_XS)
    }
    bar.add(context.widgets.button(testId("filter"), context.text("ui.bag.filter"), "ghost") { context.navigator.open(GameScreenId.INVENTORY_BAG_FILTER) }).padLeft(Tokens.SPACE_M)
    bar.add(context.widgets.button(testId("bulk_sell"), context.text("ui.bag.bulk_sell"), "ghost") { context.navigator.open(GameScreenId.INVENTORY_BULK_SELL) }).padLeft(Tokens.SPACE_XS)
    content.add(bar).left().padBottom(Tokens.SPACE_S).row()
}

class BagEquipmentScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_BAG_EQUIPMENT, context, args) {

    override val titleKey = "ui.bag.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val forHero = args.optional("hero")
        val slotFilter = args.optional("slot")?.let(EquipmentSlot::valueOf)
        tabs(context, content, id, ::testId, args)
        if (forHero == null && slotFilter != null) content.add(ui.label(text("ui.bag.filtered", text("ui.slot.${slotFilter.name.lowercase()}")), "muted")).left().row()
        if (forHero != null) {
            content.add(ui.label(text("ui.bag.choosing_for", lookup.heroName(state.hero(forHero).heroId)), "heading")).left().padBottom(Tokens.SPACE_S).row()
        }
        val list = Table().top()
        val equipment = state.inventory.equipment.filter { slotFilter == null || it.slot == slotFilter }
        if (equipment.isEmpty()) list.add(ui.label(text("ui.bag.empty"), "muted")).row()
        equipment.forEach { equip ->
            val row = ui.panel(Tokens.SPACE_S)
            row.add(ui.image(lookup.equipmentIcon(equip.equipmentId), 40f)).size(40f).padRight(Tokens.SPACE_S)
            val info = Table()
            info.add(ui.label(lookup.equipmentName(equip.equipmentId), "body")).left().row()
            val stats = context.services.content.equipment.first { it.id == equip.equipmentId }.stats.entries.joinToString("  ") { (stat, value) -> "${text("ui.stat.${statKey(stat)}")} +$value" }
            info.add(ui.label(stats, "small")).left().row()
            equip.equippedBy?.let { owner -> info.add(ui.label(text("ui.bag.worn_by", lookup.heroName(state.hero(owner).heroId)), "muted")).left() }
            row.add(info).expandX().left()
            if (forHero != null) {
                row.add(ui.button(testId("equip/${equip.instanceId}"), text("ui.bag.equip"), enabled = equip.equippedBy != forHero) {
                    context.act(text("ui.bag.equipped")) { context.services.rules.equip(it, forHero, equip.instanceId) }?.let { context.navigator.back() }
                })
            } else {
                row.add(ui.button(testId("open/${equip.instanceId}"), text("ui.common.details"), "secondary") {
                    context.navigator.open(GameScreenId.INVENTORY_EQUIPMENT_DETAIL, ScreenArgs.of("equipment" to equip.instanceId))
                })
            }
            list.add(row).width(900f).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}

class BagConsumablesScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_BAG_CONSUMABLES, context, args) {

    override val titleKey = "ui.bag.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        tabs(context, content, id, ::testId, args)
        val list = Table().top()
        val items = context.state.inventory.items
        if (items.isEmpty()) list.add(ui.label(text("ui.bag.empty"), "muted")).row()
        items.toSortedMap().forEach { (itemId, quantity) ->
            val row = ui.panel(Tokens.SPACE_S)
            row.add(ui.image(lookup.itemIcon(itemId), 40f)).size(40f).padRight(Tokens.SPACE_S)
            row.add(ui.label("${lookup.itemName(itemId)} x $quantity", "body")).expandX().left()
            row.add(ui.button(testId("open/${itemId.removePrefix("item.")}"), text("ui.common.details"), "secondary") {
                context.navigator.open(GameScreenId.INVENTORY_ITEM_DETAIL, ScreenArgs.of("item" to itemId))
            })
            list.add(row).width(900f).padBottom(Tokens.SPACE_XS).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }
}

class EquipmentDetailScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_EQUIPMENT_DETAIL, context, args) {

    override val titleKey = "ui.bag.equipment_detail"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val equip = state.inventory.equipment.firstOrNull { it.instanceId == args["equipment"] } ?: return
        val record = context.services.content.equipment.first { it.id == equip.equipmentId }
        content.add(ui.image(lookup.equipmentIcon(equip.equipmentId), 96f)).size(96f).row()
        content.add(ui.label(lookup.equipmentName(equip.equipmentId), "title")).row()
        content.add(ui.label(text("ui.slot.${record.slot}"), "muted")).padBottom(Tokens.SPACE_S).row()
        record.stats.forEach { (stat, value) -> content.add(ui.label("${text("ui.stat.${statKey(stat)}")} +$value", "body")).row() }
        equip.equippedBy?.let { owner -> content.add(ui.label(text("ui.bag.worn_by", lookup.heroName(state.hero(owner).heroId)), "muted")).padTop(Tokens.SPACE_S).row() }
        content.add(ui.label(text("ui.bag.level", equip.level - 1), "muted")).row()
        val actions = Table()
        listOf(
            "assign" to GameScreenId.INVENTORY_EQUIPMENT_ASSIGN,
            "compare" to GameScreenId.INVENTORY_EQUIPMENT_COMPARE,
            "upgrade" to GameScreenId.INVENTORY_EQUIPMENT_UPGRADE,
        ).forEach { (key, target) ->
            actions.add(ui.button(testId(key), text("ui.bag.action.$key"), "secondary") { context.navigator.open(target, ScreenArgs.of("equipment" to equip.instanceId)) }).padRight(Tokens.SPACE_XS)
        }
        actions.add(ui.button(testId("salvage"), text("ui.bag.salvage"), "danger", enabled = equip.equippedBy == null) {
            context.navigator.open(GameScreenId.INVENTORY_EQUIPMENT_SALVAGE, ScreenArgs.of("equipment" to equip.instanceId))
        }).padRight(Tokens.SPACE_XS)
        if (equip.equippedBy != null) {
            actions.add(ui.button(testId("unequip"), text("ui.heroes.unequip"), "ghost") {
                context.act { context.services.rules.unequip(it, equip.instanceId) }
            })
        }
        content.add(actions).padTop(Tokens.SPACE_M)
    }
}

class ItemDetailScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_ITEM_DETAIL, context, args) {

    override val titleKey = "ui.bag.item_detail"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val itemId = args["item"]
        val quantity = state.inventory.quantity(itemId)
        content.add(ui.image(lookup.itemIcon(itemId), 96f)).size(96f).row()
        content.add(ui.label(lookup.itemName(itemId), "title")).row()
        content.add(ui.label(text("ui.bag.owned", quantity), "muted", testId("owned"))).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("sell"), text("ui.bag.sell"), "secondary", enabled = quantity > 0 && context.services.collection.sellValue(itemId) != null) {
            context.navigator.open(GameScreenId.INVENTORY_SELL_CONFIRM, ScreenArgs.of("item" to itemId))
        }).padBottom(Tokens.SPACE_S).row()
        val experience = context.services.catalog.itemExperience(itemId)
        if (experience == null) {
            content.add(ui.label(text("ui.bag.material"), "muted")).row()
            return
        }
        content.add(ui.button(testId("choose_hero"), text("ui.bag.use_on"), enabled = quantity > 0) {
            context.navigator.open(GameScreenId.INVENTORY_ITEM_USE_TARGET, ScreenArgs.of("item" to itemId))
        }).padBottom(Tokens.SPACE_S).row()
        content.add(ui.label(text("ui.bag.gives_experience", experience), "body")).padBottom(Tokens.SPACE_S).row()
        state.heroes.forEach { hero ->
            val row = Table()
            row.add(ui.label("${lookup.heroName(hero.heroId)} Lv${hero.level}", "body")).width(260f).left()
            row.add(ui.button(testId("use_on/${hero.instanceId}"), text("ui.bag.use"), enabled = quantity > 0) {
                context.act(text("ui.bag.used")) { context.services.rules.useExperienceItem(it, itemId, hero.instanceId) }
            })
            content.add(row).padTop(Tokens.SPACE_XS).row()
        }
    }
}

fun statKey(stat: String): String = when (stat) {
    "critRate" -> "crit_rate"
    "critDamage" -> "crit_damage"
    "effectHit" -> "effect_hit"
    "effectResistance" -> "effect_resistance"
    else -> stat
}
