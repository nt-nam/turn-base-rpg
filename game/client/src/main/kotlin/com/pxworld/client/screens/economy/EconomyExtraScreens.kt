package com.pxworld.client.screens.economy

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pxworld.application.Currencies
import com.pxworld.application.EconomyTuning
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.ModalScreen
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.inventory.statKey
import com.pxworld.client.ui.Tokens
import com.pxworld.screens.GameScreenId
import java.text.SimpleDateFormat
import java.util.Date

class OfferDetailScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_OFFER_DETAIL, context, args) {
    override val titleKey = "ui.shop.offer"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val kind = args["kind"]
        val offerId = args["id"]
        if (kind == ShopHomeScreen.KIND_ITEM) {
            content.add(ui.image(lookup.itemIcon(offerId), 96f)).size(96f).row()
            content.add(ui.label(lookup.itemName(offerId), "title")).row()
            context.services.catalog.itemExperience(offerId)?.let { content.add(ui.label(text("ui.bag.gives_experience", it), "body")).row() }
        } else {
            content.add(ui.image(lookup.equipmentIcon(offerId), 96f)).size(96f).row()
            content.add(ui.label(lookup.equipmentName(offerId), "title")).row()
            context.services.content.equipment.first { it.id == offerId }.stats.forEach { (stat, value) -> content.add(ui.label("${text("ui.stat.${statKey(stat)}")} +$value", "body")).row() }
        }
        val price = if (kind == ShopHomeScreen.KIND_ITEM) context.services.catalog.itemPrice(offerId) else context.services.catalog.equipmentPrice(offerId)
        price?.let { content.add(ui.label("${it.amount} ${lookup.currencyName(it.currency)}", "heading", testId("price"))).padTop(Tokens.SPACE_M).row() }
        content.add(ui.button(testId("buy"), text("ui.shop.buy"), enabled = price != null && context.state.wallet.balance(price.currency) >= price.amount) {
            context.navigator.open(GameScreenId.ECONOMY_PURCHASE_CONFIRM, ScreenArgs.of("kind" to kind, "id" to offerId))
        }).width(240f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_S)
    }
}

class PurchaseResultScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.ECONOMY_PURCHASE_RESULT, context, args) {
    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val name = if (args["kind"] == ShopHomeScreen.KIND_ITEM) lookup.itemName(args["id"]) else lookup.equipmentName(args["id"])
        content.add(ui.label(text("ui.shop.purchase_done"), "title")).row()
        content.add(ui.label(text("ui.shop.bought", name), "positive", testId("bought"))).pad(Tokens.SPACE_M).row()
        content.add(ui.label(text("ui.shop.balance", context.state.wallet.balance(Currencies.GOLD), context.state.wallet.balance(Currencies.GEM)), "muted")).row()
        content.add(ui.button(testId("close"), text("ui.common.continue")) { context.navigator.back() }).padTop(Tokens.SPACE_M)
    }
}

class RecruitRatesScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_RECRUIT_RATES, context, args) {
    override val titleKey = "ui.recruit.rates_title"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val pool = context.services.catalog.recruitableHeroes()
        val rate = 1000 / pool.size.coerceAtLeast(1)
        content.add(ui.label(text("ui.recruit.rates_rule"), "muted", wrap = true)).width(800f).colspan(3).padBottom(Tokens.SPACE_M).row()
        pool.forEach { heroId ->
            content.add(ui.label(lookup.heroName(heroId), "body")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(lookup.className(lookup.heroClass(heroId)), "muted")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label("${rate / 10}.${rate % 10}%", "heading", testId("rate/$heroId"))).right().row()
        }
    }
}

class RecruitHistoryScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_RECRUIT_HISTORY, context, args) {
    override val titleKey = "ui.recruit.history"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val history = context.state.journal.recruitHistory.reversed()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        if (history.isEmpty()) content.add(ui.label(text("ui.recruit.no_history"), "muted")).row()
        history.forEachIndexed { index, record ->
            content.add(ui.label(format.format(Date(record.epochMillis)), "muted")).left().padRight(Tokens.SPACE_L)
            content.add(ui.label(lookup.heroName(record.heroId), "body", testId("entry/$index"))).left().row()
        }
    }
}

class PityTrackerScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_PITY_TRACKER, context, args) {
    override val titleKey = "ui.recruit.pity"

    override fun body(content: Table) {
        val lookup = Lookup(context)
        val history = context.state.journal.recruitHistory
        val owned = context.state.heroes.map { it.heroId }.toSet()
        val missing = context.services.catalog.recruitableHeroes().filterNot { it in owned }
        content.add(ui.label(text("ui.recruit.total_pulls", history.size), "title", testId("pulls"))).row()
        content.add(ui.label(text("ui.recruit.pity_rule"), "muted", wrap = true)).width(760f).padTop(Tokens.SPACE_S).row()
        content.add(ui.label(text("ui.recruit.missing"), "heading")).padTop(Tokens.SPACE_M).row()
        if (missing.isEmpty()) content.add(ui.label(text("ui.recruit.all_collected"), "positive")).row()
        missing.forEach { content.add(ui.label(lookup.heroName(it), "body")).row() }
    }
}

class CheckinClaimScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.ECONOMY_CHECKIN_CLAIM, context, args) {
    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val day = args.optional("day")?.toIntOrNull() ?: context.state.checkin.claimedDays
        val rewards = context.services.catalog.checkinRewards(context.state.checkin.tableId, day).orEmpty()
        content.add(ui.label(text("ui.checkin.day", day), "title")).row()
        rewards.forEach { content.add(ui.label("+ ${lookup.grantLabel(it)}", "positive", testId("reward"))).pad(Tokens.SPACE_XS).row() }
        content.add(ui.button(testId("close"), text("ui.common.continue")) { context.navigator.back() }).padTop(Tokens.SPACE_M)
    }
}

class CurrencyExchangeScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_CURRENCY_EXCHANGE, context, args) {
    override val titleKey = "ui.exchange.title"

    override fun body(content: Table) {
        val gems = context.state.wallet.balance(Currencies.GEM)
        content.add(ui.label(text("ui.exchange.rate", EconomyTuning.GOLD_PER_GEM), "heading")).colspan(3).row()
        content.add(ui.label(text("ui.shop.balance", context.state.wallet.balance(Currencies.GOLD), gems), "muted", testId("balance"))).colspan(3).padBottom(Tokens.SPACE_M).row()
        listOf(1L, 10L, 50L).forEach { amount ->
            content.add(ui.button(testId("exchange/$amount"), text("ui.exchange.button", amount, amount * EconomyTuning.GOLD_PER_GEM), enabled = gems >= amount) {
                context.act(text("ui.exchange.done")) { context.services.collection.exchangeGems(it, amount) }
            }).height(Tokens.BUTTON_HEIGHT).padRight(Tokens.SPACE_S)
        }
    }
}

class IdleRewardsScreen(context: ScreenContext, args: ScreenArgs) : StandardScreen(GameScreenId.ECONOMY_IDLE_REWARDS, context, args) {
    override val titleKey = "ui.idle.title"

    override fun body(content: Table) {
        val now = context.services.clock.nowMillis()
        val started = context.state.journal.lastIdleClaimMillis != null
        val preview = context.services.collection.idleRewardPreview(context.state, now)
        content.add(ui.label(text("ui.idle.rule", EconomyTuning.IDLE_CAP_HOURS), "muted", wrap = true)).width(700f).row()
        content.add(ui.label(if (started) text("ui.idle.ready", preview) else text("ui.idle.not_started"), "title", testId("preview"))).padTop(Tokens.SPACE_M).row()
        content.add(ui.button(testId("claim"), text(if (started) "ui.quests.claim" else "ui.idle.start"), enabled = !started || preview > 0) {
            context.act { context.services.collection.claimIdleRewards(it, now) }
        }).width(260f).height(Tokens.BUTTON_HEIGHT).padTop(Tokens.SPACE_M)
    }
}

