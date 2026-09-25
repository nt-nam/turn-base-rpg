package com.pxworld.client.screens.settings

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.pxworld.application.Currencies
import com.pxworld.application.CloudAccount
import com.pxworld.application.CloudMail
import com.pxworld.application.CloudResult
import com.pxworld.application.CloudSaveMeta
import com.pxworld.application.CloudSync
import com.pxworld.client.navigation.ScreenArgs
import com.pxworld.client.navigation.ScreenContext
import com.pxworld.client.screens.Lookup
import com.pxworld.client.screens.StandardScreen
import com.pxworld.client.screens.boot.SlotLoading
import com.pxworld.client.ui.Tokens
import com.pxworld.domain.progression.GameState
import com.pxworld.screens.GameScreenId

abstract class CloudScreen(id: GameScreenId, context: ScreenContext, args: ScreenArgs) : StandardScreen(id, context, args) {

    protected var busy: Boolean = false
    protected var problem: String? = null

    protected val cloud: CloudSync? get() = context.cloud

    protected fun <T> request(call: CloudSync.((CloudResult<T>) -> Unit) -> Unit, onOk: (T) -> Unit) {
        val sync = cloud ?: return
        busy = true
        problem = null
        rebuild()
        sync.call { result ->
            busy = false
            when (result) {
                is CloudResult.Ok -> onOk(result.value)
                else -> problem = describe(result)
            }
            rebuild()
        }
    }

    protected open fun describe(result: CloudResult<*>): String? = when (result) {
        is CloudResult.Ok -> null
        is CloudResult.Conflict -> text("ui.conflict.body", result.current.revision)
        is CloudResult.Unreachable -> text("ui.cloud.unreachable", result.message)
        is CloudResult.Rejected -> if (result.status == CloudSync.UNAUTHORIZED) text("ui.cloud.signed_out") else text("ui.cloud.rejected", result.message)
    }

    protected fun statusRows(content: Table) {
        if (busy) content.add(ui.label(text("ui.account.working"), "muted", testId("status"))).colspan(2).padBottom(Tokens.SPACE_S).row()
        problem?.let { content.add(ui.label(it, "negative", testId("problem"), wrap = true)).width(760f).colspan(2).padBottom(Tokens.SPACE_S).row() }
    }

    protected fun action(content: Table, element: String, key: String, style: String = "primary", enabled: Boolean = true, onClick: () -> Unit) {
        content.add(ui.button(testId(element), text(key), style, enabled = enabled && !busy) { onClick() }).width(360f).height(Tokens.BUTTON_HEIGHT).colspan(2).padBottom(Tokens.SPACE_S).row()
    }

    protected fun summary(state: GameState): String =
        text("ui.conflict.summary", state.profile.level, state.heroes.size, state.wallet.balance(Currencies.GOLD))
}

class AccountScreen(context: ScreenContext, args: ScreenArgs) : CloudScreen(GameScreenId.SETTINGS_SETTINGS_ACCOUNT, context, args) {

    override val titleKey = "ui.account.title"

    override fun body(content: Table) {
        val sync = cloud
        if (sync == null) {
            content.add(ui.label(text("ui.account.disabled"), "muted", testId("disabled"))).row()
            return
        }
        statusRows(content)
        val account = sync.account
        if (account == null) {
            content.add(ui.label(text("ui.account.signed_out"), "body", testId("signed_out"))).colspan(2).padBottom(Tokens.SPACE_M).row()
            action(content, "guest", "ui.account.guest") {
                val name = context.session.store?.state?.profile?.name ?: DEFAULT_GUEST_NAME
                request<CloudAccount>({ signInAsGuest(name, it) }) { context.navigator.toast(text("ui.login.welcome", it.displayName)) }
            }
            action(content, "email", "ui.account.email", "secondary") { context.navigator.open(GameScreenId.BOOT_LOGIN_EMAIL) }
            return
        }
        content.add(ui.label(text("ui.account.signed_in", account.displayName, text("ui.account.kind.${account.kind}")), "heading", testId("signed_in"))).colspan(2).padBottom(Tokens.SPACE_S).row()
        context.services.saves.slots().forEach { slot ->
            val revision = sync.syncedRevision(slot)
            val line = if (revision > 0) text("ui.account.synced", slot, revision) else text("ui.account.never_synced", slot)
            content.add(ui.label(line, "muted", testId("slot/$slot"))).colspan(2).left().row()
        }
        content.add().height(Tokens.SPACE_M).colspan(2).row()
        val slot = context.session.store?.slot
        if (slot == null) content.add(ui.label(text("ui.account.need_game"), "muted")).colspan(2).padBottom(Tokens.SPACE_S).row()
        action(content, "upload", "ui.account.upload", enabled = slot != null) { upload(sync, slot ?: return@action) }
        action(content, "restore", "ui.account.restore", "secondary") { context.navigator.open(GameScreenId.BOOT_CLOUD_RESTORE) }
        action(content, "mail", "ui.account.mail", "secondary") { context.navigator.open(GameScreenId.SOCIAL_MAIL_INBOX) }
        action(content, "sign_out", "ui.account.sign_out", "danger") {
            sync.signOut()
            rebuild()
        }
    }

    private fun upload(sync: CloudSync, slot: String) {
        busy = true
        problem = null
        rebuild()
        sync.upload(slot) { result ->
            busy = false
            when (result) {
                is CloudResult.Ok -> context.navigator.toast(text("ui.account.uploaded", result.value.revision))
                is CloudResult.Conflict -> context.navigator.open(GameScreenId.BOOT_SAVE_CONFLICT, conflictArgs(result.current))
                else -> problem = describe(result)
            }
            rebuild()
        }
    }

    companion object {
        const val DEFAULT_GUEST_NAME: String = "Player"

        fun conflictArgs(meta: CloudSaveMeta) = ScreenArgs.of("slot" to meta.slot, "revision" to meta.revision.toString(), "updatedAt" to meta.updatedAtMillis.toString())
    }
}

class LoginEmailScreen(context: ScreenContext, args: ScreenArgs) : CloudScreen(GameScreenId.BOOT_LOGIN_EMAIL, context, args) {

    override val titleKey = "ui.login.title"
    private var email = ""
    private var password = ""
    private var displayName = ""
    private val fields = mutableMapOf<String, TextField>()

    override fun describe(result: CloudResult<*>): String? =
        if (result is CloudResult.Rejected && result.status == CloudSync.UNAUTHORIZED) text("ui.login.wrong") else super.describe(result)

    private fun capture() {
        fields["email"]?.let { email = it.text.trim() }
        fields["password"]?.let { password = it.text }
        fields["name"]?.let { displayName = it.text.trim() }
    }

    override fun body(content: Table) {
        statusRows(content)
        fun field(element: String, labelKey: String, value: String, secret: Boolean): TextField {
            content.add(ui.label(text(labelKey), "body")).width(300f).left()
            val field = ui.textField(testId(element), value, "")
            if (secret) {
                field.isPasswordMode = true
                field.setPasswordCharacter('*')
            }
            fields[element] = field
            content.add(field).width(420f).height(Tokens.BUTTON_HEIGHT).padBottom(Tokens.SPACE_S).row()
            return field
        }
        val first = field("email", "ui.login.email", email, secret = false)
        field("password", "ui.login.password", password, secret = true)
        field("name", "ui.login.name", displayName, secret = false)
        content.add().height(Tokens.SPACE_M).colspan(2).row()
        action(content, "sign_in", "ui.login.sign_in") {
            capture()
            request<CloudAccount>({ signIn(email, password, it) }, ::welcome)
        }
        action(content, "register", "ui.login.register", "secondary") {
            capture()
            if (password.length < MIN_PASSWORD) {
                problem = text("ui.login.password_rule")
                rebuild()
            } else {
                request<CloudAccount>({ register(email, password, displayName.ifEmpty { email.substringBefore('@') }, it) }, ::welcome)
            }
        }
        if (email.isEmpty()) content.stage?.keyboardFocus = first
    }

    private fun welcome(account: CloudAccount) {
        context.navigator.toast(text("ui.login.welcome", account.displayName))
        context.navigator.back()
    }

    companion object {
        const val MIN_PASSWORD: Int = 10
    }
}

class SaveConflictScreen(context: ScreenContext, args: ScreenArgs) : CloudScreen(GameScreenId.BOOT_SAVE_CONFLICT, context, args) {

    override val titleKey = "ui.conflict.title"
    private val slot = args["slot"]
    private val cloudMeta = CloudSaveMeta(slot, args["revision"].toLong(), args["updatedAt"].toLong())
    private var cloudState: GameState? = null
    private var requested = false

    override fun onShow() {
        if (requested) return
        requested = true
        request<Pair<CloudSaveMeta, GameState>>({ preview(slot, it) }) { cloudState = it.second }
    }

    override fun body(content: Table) {
        content.add(ui.label(text("ui.conflict.body", cloudMeta.revision), "body", testId("message"), wrap = true)).width(760f).colspan(2).padBottom(Tokens.SPACE_M).row()
        statusRows(content)
        val local = runCatching { context.services.saves.load(slot) }.getOrNull()
        fun column(titleKey: String, state: GameState?, element: String): Table = ui.panel().apply {
            add(ui.label(text(titleKey), "heading")).left().row()
            add(ui.label(state?.let { "${it.profile.name}\n${summary(it)}" } ?: "-", "body", testId(element))).left()
        }
        content.add(column("ui.conflict.this_device", local, "local")).width(370f).padRight(Tokens.SPACE_S)
        content.add(column("ui.conflict.cloud", cloudState, "cloud")).width(370f).row()
        content.add().height(Tokens.SPACE_M).colspan(2).row()
        action(content, "keep_local", "ui.conflict.keep_local", enabled = local != null) { keepLocal() }
        action(content, "take_cloud", "ui.conflict.take_cloud", "secondary", enabled = cloudState != null) { takeCloud() }
    }

    private fun keepLocal() {
        val sync = cloud ?: return
        busy = true
        rebuild()
        sync.keepLocal(slot, cloudMeta) { result ->
            busy = false
            when (result) {
                is CloudResult.Ok -> {
                    context.navigator.toast(text("ui.account.uploaded", result.value.revision))
                    context.navigator.back()
                }
                is CloudResult.Conflict -> context.navigator.replace(GameScreenId.BOOT_SAVE_CONFLICT, AccountScreen.conflictArgs(result.current))
                else -> {
                    problem = describe(result)
                    rebuild()
                }
            }
        }
    }

    private fun takeCloud() = request<GameState>({ takeCloud(slot, it) }) { state ->
        context.navigator.toast(text("ui.conflict.taken"))
        if (context.session.store?.slot == slot) SlotLoading.start(context, slot, state) else context.navigator.back()
    }
}

class CloudRestoreScreen(context: ScreenContext, args: ScreenArgs) : CloudScreen(GameScreenId.BOOT_CLOUD_RESTORE, context, args) {

    override val titleKey = "ui.restore.title"
    private var saves: List<CloudSaveMeta>? = null
    private var requested = false

    override fun onShow() {
        if (requested) return
        requested = true
        request<List<CloudSaveMeta>>({ list(it) }) { saves = it }
    }

    override fun body(content: Table) {
        statusRows(content)
        val listed = saves ?: return
        if (listed.isEmpty()) {
            content.add(ui.label(text("ui.restore.empty"), "muted", testId("empty"))).row()
            return
        }
        val local = context.services.saves.slots().toSet()
        listed.forEach { meta ->
            val row = ui.panel()
            row.add(ui.label(text("ui.restore.row", meta.slot, meta.revision), "body")).expandX().left()
            row.add(ui.button(testId("restore/${meta.slot}"), text("ui.restore.restore"), enabled = !busy) { restore(meta, meta.slot in local) }).padLeft(Tokens.SPACE_S)
            content.add(row).width(760f).padBottom(Tokens.SPACE_S).row()
        }
    }

    private fun restore(meta: CloudSaveMeta, existsLocally: Boolean) {
        val synced = cloud?.syncedRevision(meta.slot) ?: 0L
        if (existsLocally && synced != meta.revision) {
            context.navigator.open(GameScreenId.BOOT_SAVE_CONFLICT, AccountScreen.conflictArgs(meta))
            return
        }
        request<GameState>({ takeCloud(meta.slot, it) }) { state ->
            context.navigator.toast(text("ui.restore.done", meta.slot))
            SlotLoading.start(context, meta.slot, state)
        }
    }
}

class MailInboxScreen(context: ScreenContext, args: ScreenArgs) : CloudScreen(GameScreenId.SOCIAL_MAIL_INBOX, context, args) {

    override val titleKey = "ui.mail.title"
    private var mail: List<CloudMail>? = null
    private var requested = false

    override fun onShow() {
        if (requested) return
        requested = true
        load()
    }

    private fun load() = request<List<CloudMail>>({ mail(it) }) { mail = it }

    override fun body(content: Table) {
        statusRows(content)
        val listed = mail ?: return
        if (listed.isEmpty()) {
            content.add(ui.label(text("ui.mail.empty"), "muted", testId("empty"))).row()
            return
        }
        val inGame = context.session.store != null
        if (!inGame) content.add(ui.label(text("ui.mail.need_game"), "muted")).padBottom(Tokens.SPACE_S).row()
        val lookup = Lookup(context)
        val list = Table().top()
        listed.forEach { item ->
            val row = ui.panel()
            val info = Table()
            info.add(ui.label(item.subject, "heading")).left().row()
            val grants = CloudSync.grantsOf(item, context.services.catalog)
            info.add(ui.label(grants.joinToString(", ") { lookup.grantLabel(it) }, "muted")).left()
            row.add(info).expandX().left()
            if (item.claimed) {
                row.add(ui.label(text("ui.mail.claimed"), "muted", testId("claimed/${item.id}")))
            } else {
                row.add(ui.button(testId("claim/${item.id}"), text("ui.mail.claim"), enabled = inGame && !busy) { claim(item) }).padLeft(Tokens.SPACE_S)
            }
            list.add(row).width(760f).padBottom(Tokens.SPACE_S).row()
        }
        content.add(ui.scroll(list, testId("list"))).grow()
    }

    private fun claim(item: CloudMail) = request<List<com.pxworld.application.Grant>>({ claim(item.id, context.services.catalog, it) }) { grants ->
        context.act(text("ui.mail.claimed")) { state -> context.services.rules.grant(state, grants, CloudSync.mailReason(item.id)) }
        mail = mail?.map { if (it.id == item.id) it.copy(claimed = true) else it }
    }
}
