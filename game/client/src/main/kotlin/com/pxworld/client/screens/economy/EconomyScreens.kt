package com.pxworld.client.screens.economy

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.Currencies
import com.pxworld.application.GameEvent
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.SpriteActor
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId

class ShopHomeScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_SHOP_HOME, context, args) {

    override val titleKey = "ui.shop.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val state = context.state
        val tab = args.optional("tab") ?: TAB_ITEMS
        val bar = Table()
        bar.add(ui.label(text("ui.shop.balance", state.wallet.balance(Currencies.GOLD), state.wallet.balance(Currencies.GEM)), "heading", testId("balance"))).expandX().left()
        listOf(TAB_ITEMS to "ui.shop.items", TAB_EQUIPMENT to "ui.shop.equipment").forEach { (key, label) ->
            bar.add(ui.button(testId("tab/$key"), text(label), if (key == tab) "tab-active" else "tab") {
                if (key != tab) context.navigator.replace(id, ScreenArgs.of("tab" to key))
            }).padLeft(Tokens.SPACE_XS)
        }
        content.add(bar).growX().padBottom(Tokens.SPACE_S).row()
        val grid = Table().top().left()
        val offers = if (tab == TAB_ITEMS) {
            context.services.content.items.mapNotNull { item -> item.shop?.takeIf { it.listed }?.let { shop -> Offer(KIND_ITEM, item.id, lookup.itemName(item.id), lookup.itemIcon(item.id), shop.currency, shop.price) } }
        } else {
            context.services.content.equipment.filter { it.shop.listed }.map { Offer(KIND_EQUIPMENT, it.id, lookup.equipmentName(it.id), lookup.equipmentIcon(it.id), it.shop.currency, it.shop.price) }
        }
        offers.forEachIndexed { index, offer ->
            val card = ui.panel(Tokens.SPACE_S)
            card.add(ui.image(offer.icon, 48f)).size(48f).row()
            card.add(ui.label(offer.name, "small")).row()
            card.add(ui.label("${offer.price} ${lookup.currencyName(offer.currency)}", "muted")).row()
            val affordable = state.wallet.balance(offer.currency) >= offer.price
            card.add(ui.button(testId("buy/${offer.id}"), text("ui.shop.buy"), enabled = affordable) {
                context.navigator.open(GameScreenId.ECONOMY_PURCHASE_CONFIRM, ScreenArgs.of("kind" to offer.kind, "id" to offer.id))
            }).growX()
            grid.add(card).width(160f).pad(Tokens.SPACE_XS)
            if (index % 6 == 5) grid.row()
        }
        content.add(ui.scroll(grid, testId("offers"))).grow()
    }

    private data class Offer(val kind: String, val id: String, val name: String, val icon: com.badlogic.gdx.graphics.g2d.TextureRegion, val currency: String, val price: Int)

    companion object {
        const val TAB_ITEMS = "items"
        const val TAB_EQUIPMENT = "equipment"
        const val KIND_ITEM = "item"
        const val KIND_EQUIPMENT = "equipment"
    }
}

class PurchaseConfirmScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.ECONOMY_PURCHASE_CONFIRM, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val kind = args["kind"]
        val offerId = args["id"]
        val isItem = kind == ShopHomeScreen.KIND_ITEM
        val name = if (isItem) lookup.itemName(offerId) else lookup.equipmentName(offerId)
        val price = if (isItem) context.services.catalog.itemPrice(offerId) else context.services.catalog.equipmentPrice(offerId)
        content.add(ui.label(text("ui.shop.confirm_title"), "title")).colspan(2).row()
        content.add(ui.label(name, "heading")).colspan(2).padTop(Tokens.SPACE_S).row()
        if (price != null) content.add(ui.label("${price.amount} ${lookup.currencyName(price.currency)}", "body")).colspan(2).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("cancel"), text("ui.common.cancel"), "secondary") { context.navigator.back() }).growX().padRight(Tokens.SPACE_S)
        content.add(ui.button(testId("confirm"), text("ui.shop.buy")) {
            val result = context.act(text("ui.shop.bought", name)) {
                if (isItem) context.services.rules.buyItem(it, offerId, 1) else context.services.rules.buyEquipment(it, offerId)
            }
            if (result != null) context.navigator.back()
        }).growX()
    }
}

class RecruitHomeScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_RECRUIT_HOME, context, args) {

    override val titleKey = "ui.recruit.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val price = context.services.catalog.recruitPrice()
        val balance = context.state.wallet.balance(price.currency)
        content.add(ui.label(text("ui.recruit.pool"), "heading")).padBottom(Tokens.SPACE_S).row()
        val pool = Table()
        context.services.catalog.recruitableHeroes().forEach { heroId ->
            val card = ui.panel(Tokens.SPACE_S)
            card.add(SpriteActor(lookup.heroSprite(heroId)).apply { setSize(72f, 72f) }).size(72f).row()
            card.add(ui.label(lookup.heroName(heroId), "small")).row()
            card.add(ui.label(lookup.className(lookup.heroClass(heroId)), "muted"))
            pool.add(card).width(130f).pad(Tokens.SPACE_XS)
        }
        content.add(pool).padBottom(Tokens.SPACE_L).row()
        content.add(ui.label(text("ui.recruit.rates", context.services.catalog.recruitableHeroes().size), "muted")).padBottom(Tokens.SPACE_S).row()
        content.add(ui.label(text("ui.recruit.balance", balance, lookup.currencyName(price.currency)), "body", testId("balance"))).padBottom(Tokens.SPACE_S).row()
        content.add(ui.button(testId("recruit"), text("ui.recruit.recruit", price.amount, lookup.currencyName(price.currency)), enabled = balance >= price.amount) {
            val transition = context.act { context.services.rules.recruit(it, context.services.clock.nowMillis()) }
            val recruited = transition?.events?.filterIsInstance<GameEvent.HeroRecruited>()?.firstOrNull()
            if (recruited != null) context.navigator.open(GameScreenId.ECONOMY_RECRUIT_RESULT_SINGLE, ScreenArgs.of("hero" to recruited.instanceId))
        }).width(320f).height(Tokens.BUTTON_HEIGHT)
    }
}

class RecruitResultScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_RECRUIT_RESULT_SINGLE, context, args) {

    override val titleKey = "ui.recruit.result_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val hero = context.state.heroes.firstOrNull { it.instanceId == args["hero"] } ?: return
        content.add(SpriteActor(lookup.heroSprite(hero.heroId)).apply { setSize(192f, 192f) }).size(192f).row()
        content.add(ui.label(lookup.heroName(hero.heroId), "title", testId("hero_name"))).row()
        content.add(ui.label("${lookup.heroTitle(hero.heroId)} | ${lookup.className(lookup.heroClass(hero.heroId))}", "muted")).padBottom(Tokens.SPACE_L).row()
        content.add(ui.button(testId("again"), text("ui.recruit.again"), "secondary") { context.navigator.back() }).width(260f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
        content.add(ui.button(testId("view"), text("ui.common.details")) {
            context.navigator.replace(GameScreenId.HEROES_HERO_OVERVIEW, ScreenArgs.of("hero" to hero.instanceId))
        }).width(260f).height(Tokens.BUTTON_HEIGHT)
    }
}

class DailyCheckinScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_DAILY_CHECKIN, context, args) {

    override val titleKey = "ui.checkin.title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val progress = context.state.checkin
        val table = context.services.content.checkinTables.first { it.id == progress.tableId }
        val today = context.services.clock.epochDay()
        val claimable = progress.lastClaimEpochDay == null || progress.lastClaimEpochDay!! < today
        val nextDay = progress.claimedDays % table.days.size + 1
        val grid = Table()
        table.days.forEachIndexed { index, day ->
            val claimed = day.day <= progress.claimedDays % table.days.size || (progress.claimedDays > 0 && progress.claimedDays % table.days.size == 0)
            val cell = ui.panel(Tokens.SPACE_S, if (day.day == nextDay && claimable) Tokens.surfaceRaised else Tokens.surface)
            cell.add(ui.label(text("ui.checkin.day", day.day), if (claimed) "positive" else "small")).row()
            day.rewards.forEach { reward -> cell.add(ui.label(lookup.grantLabel(com.pxworld.application.Grant(com.pxworld.application.GrantKind.CURRENCY, reward.id, reward.quantity.toLong())), "muted")).row() }
            grid.add(cell).size(150f, 72f).pad(Tokens.SPACE_XS)
            if (index % 6 == 5) grid.row()
        }
        content.add(grid).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("claim"), text(if (claimable) "ui.checkin.claim" else "ui.checkin.claimed_today", nextDay), enabled = claimable) {
            context.act(text("ui.checkin.claimed")) { context.services.rules.claimCheckin(it, context.services.clock.epochDay()) }
        }).width(320f).height(Tokens.BUTTON_HEIGHT)
    }
}
