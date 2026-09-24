package com.pxworld.client.screens.inventory

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.EconomyTuning
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.ConfirmScreen
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.stats.StatKind
import com.pxworld.screens.GameScreenId

private fun ScreenContext.itemRows(content: Table, testId: (String) -> String, filter: (String) -> Boolean, emptyKey: String, action: ((String) -> Unit)? = null) {
    val lookup = Lookup(this)
    val list = Table().top()
    val items = state.inventory.items.filterKeys(filter).toSortedMap()
    if (items.isEmpty()) list.add(widgets.label(text(emptyKey), "muted")).row()
    items.forEach { (itemId, quantity) ->
        val row = widgets.panel(Tokens.SPACE_S)
        row.add(widgets.image(lookup.itemIcon(itemId), 40f)).size(40f).padRight(Tokens.SPACE_S)
        row.add(widgets.label("${lookup.itemName(itemId)} x $quantity", "body")).expandX().left()
        row.add(widgets.button(testId("open/${itemId.removePrefix("item.")}"), text("ui.common.details"), "secondary") {
            action?.invoke(itemId) ?: navigator.open(GameScreenId.INVENTORY_ITEM_DETAIL, ScreenArgs.of("item" to itemId))
        })
        list.add(row).width(900f).padBottom(Tokens.SPACE_XS).row()
    }
    content.add(widgets.scroll(list, testId("list"))).grow()
}

class BagMaterialsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_BAG_MATERIALS, context, args) {
    override val titleKey = "ui.bag.materials"
    override fun body(content: Table) = context.itemRows(content, ::testId, { context.services.catalog.itemExperience(it) == null && context.services.catalog.itemCategory(it) != "key" }, "ui.bag.empty")
}

class BagKeyItemsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_BAG_KEY_ITEMS, context, args) {
    override val titleKey = "ui.bag.key_items"
    override fun body(content: Table) = context.itemRows(content, ::testId, { context.services.catalog.itemCategory(it) == "key" }, "ui.bag.no_key_items")
}

class EquipmentCompareScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_EQUIPMENT_COMPARE, context, args) {
    override val titleKey = "ui.bag.compare"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val candidate = state.inventory.equipment.firstOrNull { it.instanceId == args.optional("equipment") } ?: return
        val hero = state.heroes.firstOrNull { it.instanceId == args.optional("hero") } ?: state.heroes.first()
        val current = context.services.rules.heroStats(state, hero.instanceId)
        val preview = context.services.rules.heroStats(context.services.rules.equip(state, hero.instanceId, candidate.instanceId).state, hero.instanceId)
        content.add(ui.label(text("ui.bag.compare_for", lookup.equipmentName(candidate.equipmentId), lookup.heroName(hero.heroId)), "heading")).colspan(3).padBottom(Tokens.SPACE_M).row()
        listOf(StatKind.HP, StatKind.ATTACK, StatKind.DEFENSE, StatKind.SPEED, StatKind.CRIT_RATE, StatKind.EFFECT_HIT).forEach { kind ->
            val delta = preview[kind] - current[kind]
            content.add(ui.label(kind.name.lowercase(), "muted")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label("${current[kind]} -> ${preview[kind]}", "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(if (delta > 0) "+$delta" else delta.toString(), if (delta > 0) "positive" else if (delta < 0) "negative" else "muted", testId("delta/${kind.name.lowercase()}"))).left().row()
        }
        content.add(ui.button(testId("equip"), text("ui.bag.equip")) {
            context.act(text("ui.bag.equipped")) { context.services.rules.equip(it, hero.instanceId, candidate.instanceId) }
        }).colspan(3).padTop(Tokens.SPACE_M)
    }
}

class EquipmentAssignScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_EQUIPMENT_ASSIGN, context, args) {
    override val titleKey = "ui.bag.equip_to"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val equip = state.inventory.equipment.firstOrNull { it.instanceId == args.optional("equipment") } ?: return
        content.add(ui.label(lookup.equipmentName(equip.equipmentId), "heading")).colspan(3).padBottom(Tokens.SPACE_M).row()
        state.heroes.forEach { hero ->
            content.add(ui.label("${lookup.heroName(hero.heroId)} Lv${hero.level}", "body")).width(260f).left()
            content.add(ui.button(testId("compare/${hero.instanceId}"), text("ui.bag.compare"), "secondary") {
                context.navigator.open(GameScreenId.INVENTORY_EQUIPMENT_COMPARE, ScreenArgs.of("equipment" to equip.instanceId, "hero" to hero.instanceId))
            }).padRight(Tokens.SPACE_XS)
            content.add(ui.button(testId("equip_to/${hero.instanceId}"), text("ui.bag.equip"), enabled = equip.equippedBy != hero.instanceId) {
                context.act(text("ui.bag.equipped")) { context.services.rules.equip(it, hero.instanceId, equip.instanceId) }
            }).padBottom(Tokens.SPACE_XS).row()
        }
    }
}

class EquipmentUpgradeScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_EQUIPMENT_UPGRADE, context, args) {
    override val titleKey = "ui.bag.upgrade"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val equip = context.state.inventory.equipment.firstOrNull { it.instanceId == args.optional("equipment") } ?: return
        val record = context.services.content.equipment.first { it.id == equip.equipmentId }
        val cost = context.services.collection.upgradeCost(equip.level)
        content.add(ui.image(lookup.equipmentIcon(equip.equipmentId), 80f)).size(80f).row()
        content.add(ui.label("${lookup.equipmentName(equip.equipmentId)} +${equip.level - 1}", "title", testId("level"))).row()
        record.stats.forEach { (stat, value) ->
            val now = value * (1000 + EconomyTuning.EQUIPMENT_GROWTH_PERMILLE * (equip.level - 1)) / 1000
            val next = value * (1000 + EconomyTuning.EQUIPMENT_GROWTH_PERMILLE * equip.level) / 1000
            content.add(ui.label("${text("ui.stat.${statKey(stat)}")}: $now -> $next", "body")).row()
        }
        val maxed = equip.level >= EconomyTuning.MAX_EQUIPMENT_LEVEL
        content.add(ui.label(if (maxed) text("ui.bag.max_level") else text("ui.bag.upgrade_cost", cost.amount), "muted")).padTop(Tokens.SPACE_M).row()
        content.add(ui.button(testId("upgrade"), text("ui.bag.upgrade"), enabled = !maxed) {
            context.act(text("ui.bag.upgraded")) { context.services.collection.upgradeEquipment(it, equip.instanceId) }
        }).width(260f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_S)
    }
}

class EquipmentSalvageScreen(context: ScreenContext, args: ScreenArgs) : ConfirmScreen(GameScreenId.INVENTORY_EQUIPMENT_SALVAGE, context, args) {
    private val equip get() = context.state.inventory.equipment.first { it.instanceId == args["equipment"] }
    override val titleText get() = text("ui.bag.salvage")
    override val messageText get() = text("ui.bag.salvage_warning", Lookup(context).equipmentName(equip.equipmentId), context.services.collection.salvageValue(equip.equipmentId).amount * equip.level)
    override val confirmText get() = text("ui.bag.salvage")
    override val confirmStyle = "danger"

    override fun confirm() {
        val result = context.act(text("ui.bag.salvaged")) { context.services.collection.salvageEquipment(it, args["equipment"]) }
        context.navigator.back()
        if (result != null) context.navigator.backTo(GameScreenId.INVENTORY_BAG_EQUIPMENT)
    }
}

class BagFilterScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.INVENTORY_BAG_FILTER, context, args) {
    override fun dialog(content: Table) {
        content.add(ui.label(text("ui.bag.filter"), "title")).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("all"), text("ui.bag.filter_all"), "secondary") {
            context.navigator.back()
            context.navigator.replace(GameScreenId.INVENTORY_BAG_EQUIPMENT)
        }).width(280f).padBottom(Tokens.SPACE_XS).row()
        EquipmentSlot.values().forEach { slot ->
            content.add(ui.button(testId("slot/${slot.name.lowercase()}"), text("ui.slot.${slot.name.lowercase()}"), "secondary") {
                context.navigator.back()
                context.navigator.replace(GameScreenId.INVENTORY_BAG_EQUIPMENT, ScreenArgs.of("slot" to slot.name, "browse" to "true"))
            }).width(280f).padBottom(Tokens.SPACE_XS).row()
        }
    }
}

class SellConfirmScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.INVENTORY_SELL_CONFIRM, context, args) {
    private var quantity = 1L

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val itemId = args["item"]
        val owned = context.state.inventory.quantity(itemId)
        val value = context.services.collection.sellValue(itemId)
        content.add(ui.label(text("ui.bag.sell"), "title")).colspan(3).row()
        content.add(ui.label(lookup.itemName(itemId), "heading")).colspan(3).padBottom(Tokens.SPACE_S).row()
        if (value == null || owned == 0L) {
            content.add(ui.label(text("ui.bag.not_sellable"), "muted")).colspan(3).row()
            content.add(ui.button(testId("close"), text("ui.common.close"), "secondary") { context.navigator.back() }).colspan(3)
            return
        }
        quantity = quantity.coerceIn(1, owned)
        content.add(ui.button(testId("less"), "-", "secondary", enabled = quantity > 1) { quantity -= 1; rebuild() })
        content.add(ui.label("$quantity / $owned", "heading", testId("quantity"))).pad(0f, Tokens.SPACE_M, 0f, Tokens.SPACE_M)
        content.add(ui.button(testId("more"), "+", "secondary", enabled = quantity < owned) { quantity += 1; rebuild() }).row()
        content.add(ui.label("${value.amount * quantity} ${lookup.currencyName(value.currency)}", "positive")).colspan(3).pad(Tokens.SPACE_S).row()
        content.add(ui.button(testId("cancel"), text("ui.common.cancel"), "secondary") { context.navigator.back() }).padRight(Tokens.SPACE_S)
        content.add()
        content.add(ui.button(testId("confirm"), text("ui.bag.sell")) {
            context.act(text("ui.bag.sold")) { context.services.collection.sellItem(it, itemId, quantity) }
            context.navigator.back()
        })
    }
}

class BulkSellScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_BULK_SELL, context, args) {
    override val titleKey = "ui.bag.bulk_sell"
    private val selected = mutableSetOf<String>()

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val sellable = context.state.inventory.items.filterKeys { context.services.collection.sellValue(it) != null }
        var total = 0L
        val list = Table().top()
        sellable.toSortedMap().forEach { (itemId, quantity) ->
            val value = context.services.collection.sellValue(itemId)!!.amount * quantity
            if (itemId in selected) total += value
            list.add(ui.button(testId("toggle/${itemId.removePrefix("item.")}"), "${lookup.itemName(itemId)} x $quantity  (+$value)", if (itemId in selected) "tab-active" else "tab") {
                if (!selected.add(itemId)) selected.remove(itemId)
                rebuild()
            }).width(700f).padBottom(Tokens.SPACE_XS).row()
        }
        if (sellable.isEmpty()) list.add(ui.label(text("ui.bag.nothing_to_sell"), "muted")).row()
        content.add(ui.scroll(list, testId("list"))).grow().row()
        content.add(ui.button(testId("sell_selected"), text("ui.bag.sell_selected", total), enabled = selected.isNotEmpty()) {
            val chosen = selected.toList()
            context.act(text("ui.bag.sold")) { state ->
                chosen.fold(com.pxworld.application.Transition(state, emptyList())) { acc, itemId ->
                    val next = context.services.collection.sellItem(acc.state, itemId, acc.state.inventory.quantity(itemId))
                    com.pxworld.application.Transition(next.state, acc.events + next.events)
                }
            }
            selected.clear()
        }).padTop(Tokens.SPACE_S)
    }
}

class ItemUseTargetScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_ITEM_USE_TARGET, context, args) {
    override val titleKey = "ui.bag.use_on"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val itemId = args.optional("item") ?: return
        content.add(ui.label(lookup.itemName(itemId), "heading")).colspan(2).padBottom(Tokens.SPACE_M).row()
        context.state.heroes.forEach { hero ->
            content.add(ui.label("${lookup.heroName(hero.heroId)} Lv${hero.level}", "body")).width(300f).left()
            content.add(ui.button(testId("use_on/${hero.instanceId}"), text("ui.bag.use"), enabled = context.state.inventory.quantity(itemId) > 0) {
                val before = hero.level
                val result = context.act { context.services.rules.useExperienceItem(it, itemId, hero.instanceId) }
                if (result != null) context.navigator.replace(GameScreenId.INVENTORY_ITEM_USE_RESULT, ScreenArgs.of("hero" to hero.instanceId, "before" to before.toString(), "item" to itemId))
            }).padBottom(Tokens.SPACE_XS).row()
        }
    }
}

class ItemUseResultScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.INVENTORY_ITEM_USE_RESULT, context, args) {
    override val titleKey = "ui.bag.used"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.state.heroes.firstOrNull { it.instanceId == args.optional("hero") } ?: return
        val before = args.optional("before")?.toIntOrNull() ?: hero.level
        content.add(ui.label(lookup.heroName(hero.heroId), "title")).row()
        content.add(ui.label(if (hero.level > before) text("ui.result.level_up", lookup.heroName(hero.heroId), hero.level) else text("ui.bag.exp_now", hero.experience), "positive", testId("result"))).padTop(Tokens.SPACE_S).row()
        args.optional("item")?.let { itemId ->
            content.add(ui.button(testId("again"), text("ui.bag.use_again"), "secondary", enabled = context.state.inventory.quantity(itemId) > 0) {
                context.navigator.replace(GameScreenId.INVENTORY_ITEM_USE_TARGET, ScreenArgs.of("item" to itemId))
            }).padTop(Tokens.SPACE_M)
        }
    }
}
