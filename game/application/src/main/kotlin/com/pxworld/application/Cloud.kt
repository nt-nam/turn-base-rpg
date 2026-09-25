package com.pxworld.application

import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.progression.GameState

data class CloudAccount(val id: String, val displayName: String, val kind: String)

data class CloudCredentials(val token: String, val account: CloudAccount)

data class CloudSaveMeta(val slot: String, val revision: Long, val updatedAtMillis: Long)

data class CloudSave(val meta: CloudSaveMeta, val body: String)

data class CloudMail(val id: String, val subject: String, val grants: Map<String, Long>, val claimed: Boolean)

data class TelemetryEvent(val name: String, val payload: Map<String, String> = emptyMap())

sealed interface CloudResult<out T> {
    data class Ok<T>(val value: T) : CloudResult<T>
    data class Conflict(val current: CloudSaveMeta) : CloudResult<Nothing>
    data class Rejected(val status: Int, val message: String) : CloudResult<Nothing>
    data class Unreachable(val message: String) : CloudResult<Nothing>
}

interface CloudGateway {
    fun guest(displayName: String, done: (CloudResult<CloudCredentials>) -> Unit)
    fun login(email: String, password: String, done: (CloudResult<CloudCredentials>) -> Unit)
    fun register(email: String, password: String, displayName: String, done: (CloudResult<CloudCredentials>) -> Unit)
    fun saves(token: String, done: (CloudResult<List<CloudSaveMeta>>) -> Unit)
    fun download(token: String, slot: String, done: (CloudResult<CloudSave>) -> Unit)
    fun upload(token: String, slot: String, expectedRevision: Long, body: String, done: (CloudResult<CloudSaveMeta>) -> Unit)
    fun mail(token: String, done: (CloudResult<List<CloudMail>>) -> Unit)
    fun claimMail(token: String, mailId: String, done: (CloudResult<CloudMail>) -> Unit)
    fun telemetry(token: String?, clientVersion: String, events: List<TelemetryEvent>, done: (CloudResult<Unit>) -> Unit)
}

interface CloudCredentialStore {
    var credentials: CloudCredentials?
    fun syncedRevision(slot: String): Long
    fun markSynced(slot: String, revision: Long)
}

class CloudSync(
    private val gateway: CloudGateway,
    private val saves: SaveRepository,
    private val store: CloudCredentialStore,
    private val deliver: (() -> Unit) -> Unit,
) {

    val account: CloudAccount? get() = store.credentials?.account

    fun signInAsGuest(displayName: String, done: (CloudResult<CloudAccount>) -> Unit) = gateway.guest(displayName, signedIn(done))

    fun signIn(email: String, password: String, done: (CloudResult<CloudAccount>) -> Unit) = gateway.login(email, password, signedIn(done))

    fun register(email: String, password: String, displayName: String, done: (CloudResult<CloudAccount>) -> Unit) =
        gateway.register(email, password, displayName, signedIn(done))

    fun signOut() {
        store.credentials = null
    }

    fun syncedRevision(slot: String): Long = store.syncedRevision(slot)

    fun list(done: (CloudResult<List<CloudSaveMeta>>) -> Unit) = authorized(done) { token, reply -> gateway.saves(token, reply) }

    fun upload(slot: String, done: (CloudResult<CloudSaveMeta>) -> Unit) = push(slot, store.syncedRevision(slot), done)

    fun keepLocal(slot: String, cloud: CloudSaveMeta, done: (CloudResult<CloudSaveMeta>) -> Unit) = push(slot, cloud.revision, done)

    fun preview(slot: String, done: (CloudResult<Pair<CloudSaveMeta, GameState>>) -> Unit) =
        authorized(done) { token, reply ->
            gateway.download(token, slot) { result ->
                reply(result.then { save -> decoded(save.body)?.let { CloudResult.Ok(save.meta to it) } ?: corrupt() })
            }
        }

    fun takeCloud(slot: String, done: (CloudResult<GameState>) -> Unit) =
        authorized(done) { token, reply ->
            gateway.download(token, slot) { result ->
                reply(
                    result.then { save ->
                        val state = decoded(save.body) ?: return@then corrupt()
                        saves.save(slot, state)
                        store.markSynced(slot, save.meta.revision)
                        CloudResult.Ok(state)
                    },
                )
            }
        }

    fun mail(done: (CloudResult<List<CloudMail>>) -> Unit) = authorized(done) { token, reply -> gateway.mail(token, reply) }

    fun claim(mailId: String, catalog: ContentCatalog, done: (CloudResult<List<Grant>>) -> Unit) =
        authorized(done) { token, reply ->
            gateway.claimMail(token, mailId) { result -> reply(result.then { mail -> CloudResult.Ok(grantsOf(mail, catalog)) }) }
        }

    fun report(clientVersion: String, events: List<TelemetryEvent>, done: (CloudResult<Unit>) -> Unit = {}) =
        gateway.telemetry(store.credentials?.token, clientVersion, events) { result -> deliver { done(result) } }

    private fun push(slot: String, expectedRevision: Long, done: (CloudResult<CloudSaveMeta>) -> Unit) {
        val body = try {
            saves.export(slot)
        } catch (failure: IllegalStateException) {
            return deliver { done(CloudResult.Rejected(0, failure.message ?: "no local save")) }
        }
        authorized(done) { token, reply ->
            gateway.upload(token, slot, expectedRevision, body) { result ->
                if (result is CloudResult.Ok) store.markSynced(slot, result.value.revision)
                reply(result)
            }
        }
    }

    private fun signedIn(done: (CloudResult<CloudAccount>) -> Unit): (CloudResult<CloudCredentials>) -> Unit = { result ->
        if (result is CloudResult.Ok) store.credentials = result.value
        deliver { done(result.then { CloudResult.Ok(it.account) }) }
    }

    private fun <T> authorized(done: (CloudResult<T>) -> Unit, call: (String, (CloudResult<T>) -> Unit) -> Unit) {
        val token = store.credentials?.token ?: return deliver { done(CloudResult.Rejected(UNAUTHORIZED, SIGNED_OUT)) }
        call(token) { result ->
            if (result is CloudResult.Rejected && result.status == UNAUTHORIZED) store.credentials = null
            deliver { done(result) }
        }
    }

    private fun decoded(body: String): GameState? = try {
        saves.decode(body)
    } catch (corrupt: IllegalStateException) {
        null
    } catch (invalid: IllegalArgumentException) {
        null
    }

    private fun corrupt() = CloudResult.Rejected(0, CORRUPT_CLOUD_SAVE)

    companion object {
        const val UNAUTHORIZED: Int = 401
        const val SIGNED_OUT: String = "signed out"
        const val CORRUPT_CLOUD_SAVE: String = "cloud save is unreadable"

        fun mailReason(mailId: String): LedgerReason = LedgerReason("mail", mailId)

        fun grantsOf(mail: CloudMail, catalog: ContentCatalog): List<Grant> =
            mail.grants.mapNotNull { (id, quantity) -> catalog.grantKindOf(id)?.let { Grant(it, id, quantity) } }
    }
}

inline fun <T, R> CloudResult<T>.then(next: (T) -> CloudResult<R>): CloudResult<R> = when (this) {
    is CloudResult.Ok -> next(value)
    is CloudResult.Conflict -> this
    is CloudResult.Rejected -> this
    is CloudResult.Unreachable -> this
}

object TelemetryMapping {

    fun of(event: GameEvent): TelemetryEvent? = when (event) {
        is GameEvent.BattleFinished -> TelemetryEvent("battle.end", mapOf("encounter" to event.encounterId, "outcome" to event.outcome.name.lowercase(), "defeated" to event.enemiesDefeated.toString()))
        is GameEvent.QuestCompleted -> TelemetryEvent("quest.complete", mapOf("quest" to event.questId))
        is GameEvent.HeroRecruited -> TelemetryEvent("hero.recruit", mapOf("hero" to event.heroId))
        is GameEvent.ProfileLeveledUp -> TelemetryEvent("profile.level_up", mapOf("level" to event.level.toString()))
        is GameEvent.MapEntered -> TelemetryEvent("map.enter", mapOf("map" to event.mapId))
        is GameEvent.CheckinClaimed -> TelemetryEvent("checkin.claim", mapOf("day" to event.day.toString()))
        else -> null
    }
}

class TelemetryBuffer(private val capacity: Int = 200) {

    private val pending = ArrayDeque<TelemetryEvent>()

    val size: Int get() = pending.size

    fun record(event: TelemetryEvent) {
        pending.addLast(event)
        while (pending.size > capacity) pending.removeFirst()
    }

    fun drain(limit: Int): List<TelemetryEvent> = List(minOf(limit, pending.size)) { pending.removeFirst() }

    fun restore(events: List<TelemetryEvent>) {
        events.asReversed().forEach(pending::addFirst)
        while (pending.size > capacity) pending.removeLast()
    }
}
