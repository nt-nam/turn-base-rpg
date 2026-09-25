package com.pxworld.application

import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.CheckinProgress
import com.pxworld.domain.progression.PlayerProfile
import com.pxworld.domain.progression.WorldPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class CloudSyncTest {

    private class MemorySaves(private val exported: MutableMap<String, GameState>) : SaveRepository {
        val states = mutableMapOf<String, GameState>()

        override fun slots() = states.keys.sorted()
        override fun load(slot: String) = states[slot] ?: throw IllegalStateException("no save for slot $slot")
        override fun save(slot: String, state: GameState) {
            states[slot] = state
        }
        override fun delete(slot: String) {
            states.remove(slot)
        }
        override fun export(slot: String): String = "export:${exported.size}".also { exported[it] = load(slot) }
        override fun decode(exported: String): GameState = this.exported[exported] ?: throw IllegalStateException("unreadable")
    }

    private class MemoryCredentials : CloudCredentialStore {
        override var credentials: CloudCredentials? = null
        val revisions = mutableMapOf<String, Long>()
        override fun syncedRevision(slot: String) = revisions[slot] ?: 0L
        override fun markSynced(slot: String, revision: Long) {
            revisions[slot] = revision
        }
    }

    private class FakeServer : CloudGateway {
        val saves = mutableMapOf<String, CloudSave>()
        val mailbox = mutableMapOf<String, CloudMail>()
        var validToken = "token-1"

        private fun <T> guarded(token: String, done: (CloudResult<T>) -> Unit, work: () -> CloudResult<T>) =
            done(if (token == validToken) work() else CloudResult.Rejected(401, "expired"))

        override fun guest(displayName: String, done: (CloudResult<CloudCredentials>) -> Unit) = done(CloudResult.Ok(CloudCredentials(validToken, CloudAccount("a1", displayName, "guest"))))
        override fun login(email: String, password: String, done: (CloudResult<CloudCredentials>) -> Unit) =
            done(if (password == "right-password") CloudResult.Ok(CloudCredentials(validToken, CloudAccount("a1", "Hero", "player"))) else CloudResult.Rejected(401, "wrong email or password"))
        override fun register(email: String, password: String, displayName: String, done: (CloudResult<CloudCredentials>) -> Unit) = guest(displayName, done)
        override fun saves(token: String, done: (CloudResult<List<CloudSaveMeta>>) -> Unit) = guarded(token, done) { CloudResult.Ok(saves.values.map { it.meta }) }
        override fun download(token: String, slot: String, done: (CloudResult<CloudSave>) -> Unit) =
            guarded(token, done) { saves[slot]?.let { CloudResult.Ok(it) } ?: CloudResult.Rejected(404, "no save in slot") }
        override fun upload(token: String, slot: String, expectedRevision: Long, body: String, done: (CloudResult<CloudSaveMeta>) -> Unit) = guarded(token, done) {
            val current = saves[slot]?.meta?.revision ?: 0L
            if (current != expectedRevision) {
                CloudResult.Conflict(saves.getValue(slot).meta)
            } else {
                val meta = CloudSaveMeta(slot, current + 1, 1000L * (current + 1))
                saves[slot] = CloudSave(meta, body)
                CloudResult.Ok(meta)
            }
        }
        override fun mail(token: String, done: (CloudResult<List<CloudMail>>) -> Unit) = guarded(token, done) { CloudResult.Ok(mailbox.values.toList()) }
        override fun claimMail(token: String, mailId: String, done: (CloudResult<CloudMail>) -> Unit) = guarded(token, done) {
            val mail = mailbox[mailId]
            if (mail == null || mail.claimed) CloudResult.Rejected(409, "mail missing or already claimed") else CloudResult.Ok(mail).also { mailbox[mailId] = mail.copy(claimed = true) }
        }
        override fun telemetry(token: String?, clientVersion: String, events: List<TelemetryEvent>, done: (CloudResult<Unit>) -> Unit) = done(CloudResult.Ok(Unit))
    }

    private val server = FakeServer()
    private val exports = mutableMapOf<String, GameState>()

    private fun device(): Triple<CloudSync, MemorySaves, MemoryCredentials> {
        val saves = MemorySaves(exports)
        val credentials = MemoryCredentials()
        return Triple(CloudSync(server, saves, credentials) { it() }, saves, credentials)
    }

    private fun state(name: String) = GameState(
        profile = PlayerProfile(name, starterHeroId = "hero.aldric"),
        checkin = CheckinProgress("checkin.standard_30"),
        position = WorldPosition("map.dawnvillage_01"),
    )

    private fun <T> CloudSync.await(call: CloudSync.((CloudResult<T>) -> Unit) -> Unit): CloudResult<T> {
        var captured: CloudResult<T>? = null
        call { captured = it }
        return captured ?: error("callback was not invoked")
    }

    @Test
    fun secondDeviceHitsConflictAndCanKeepItsCopyOrTakeTheCloud() {
        val (laptop, laptopSaves) = device()
        val (phone, phoneSaves, phoneCredentials) = device()
        laptopSaves.save("main", state("laptop"))
        phoneSaves.save("main", state("phone"))
        laptop.await<CloudAccount> { signInAsGuest("Hero", it) }
        phone.await<CloudAccount> { signIn("hero@example.com", "right-password", it) }

        assertEquals(1L, assertIs<CloudResult.Ok<CloudSaveMeta>>(laptop.await { upload("main", it) }).value.revision)
        val conflict = assertIs<CloudResult.Conflict>(phone.await<CloudSaveMeta> { upload("main", it) })
        assertEquals(1L, conflict.current.revision)

        val preview = assertIs<CloudResult.Ok<Pair<CloudSaveMeta, GameState>>>(phone.await { preview("main", it) })
        assertEquals("laptop", preview.value.second.profile.name)

        assertEquals(2L, assertIs<CloudResult.Ok<CloudSaveMeta>>(phone.await { keepLocal("main", conflict.current, it) }).value.revision)
        assertEquals(2L, phoneCredentials.syncedRevision("main"))

        assertIs<CloudResult.Conflict>(laptop.await<CloudSaveMeta> { upload("main", it) })
        val taken = assertIs<CloudResult.Ok<GameState>>(laptop.await { takeCloud("main", it) })
        assertEquals("phone", taken.value.profile.name)
        assertEquals("phone", laptopSaves.states.getValue("main").profile.name)
        assertEquals(2L, laptop.syncedRevision("main"))
        assertIs<CloudResult.Ok<CloudSaveMeta>>(laptop.await { upload("main", it) })
    }

    @Test
    fun expiredTokenSignsOutAndSignedOutCallsAreRejectedLocally() {
        val (sync, saves, credentials) = device()
        saves.save("main", state("hero"))
        assertEquals(CloudResult.Rejected(401, CloudSync.SIGNED_OUT), sync.await<CloudSaveMeta> { upload("main", it) })
        sync.await<CloudAccount> { signInAsGuest("Hero", it) }
        server.validToken = "token-2"
        assertIs<CloudResult.Rejected>(sync.await<List<CloudSaveMeta>> { list(it) })
        assertNull(credentials.credentials)
        assertNull(sync.account)
    }

    @Test
    fun wrongPasswordKeepsTheDeviceSignedOut() {
        val (sync, _, credentials) = device()
        assertIs<CloudResult.Rejected>(sync.await<CloudAccount> { signIn("hero@example.com", "wrong", it) })
        assertNull(credentials.credentials)
    }

    @Test
    fun corruptCloudSaveIsRejectedWithoutTouchingTheLocalSlot() {
        val (sync, saves) = device()
        saves.save("main", state("local"))
        sync.await<CloudAccount> { signInAsGuest("Hero", it) }
        server.saves["main"] = CloudSave(CloudSaveMeta("main", 4, 0), "garbage")
        assertEquals(CloudResult.Rejected(0, CloudSync.CORRUPT_CLOUD_SAVE), sync.await<GameState> { takeCloud("main", it) })
        assertEquals("local", saves.states.getValue("main").profile.name)
        assertEquals(0L, sync.syncedRevision("main"))
    }

    @Test
    fun claimedMailBecomesGrantsOnceAndUnknownIdsAreDropped() {
        val (sync) = device()
        sync.await<CloudAccount> { signInAsGuest("Hero", it) }
        server.mailbox["m1"] = CloudMail("m1", "Sorry", mapOf(Currencies.GOLD to 250L, "item.food_t1" to 2L, "retired.thing" to 9L), claimed = false)
        val grants = assertIs<CloudResult.Ok<List<Grant>>>(sync.await { claim("m1", FakeCatalog, it) }).value
        assertEquals(listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 250), Grant(GrantKind.ITEM, "item.food_t1", 2)), grants)
        assertEquals(CloudResult.Rejected(409, "mail missing or already claimed"), sync.await<List<Grant>> { claim("m1", FakeCatalog, it) })
    }
}

class TelemetryTest {

    @Test
    fun gameplayEventsMapToTelemetryAndFailedBatchesAreRequeuedInOrder() {
        val battle = TelemetryMapping.of(GameEvent.BattleFinished("encounter.a", com.pxworld.domain.battle.BattleOutcome.VICTORY, 3))
        assertEquals(TelemetryEvent("battle.end", mapOf("encounter" to "encounter.a", "outcome" to "victory", "defeated" to "3")), battle)
        assertNull(TelemetryMapping.of(GameEvent.NpcTalked("npc.a")))

        val buffer = TelemetryBuffer(capacity = 3)
        (1..4).forEach { buffer.record(TelemetryEvent("e$it")) }
        val batch = buffer.drain(2)
        assertEquals(listOf("e2", "e3"), batch.map { it.name })
        buffer.restore(batch)
        assertEquals(listOf("e2", "e3", "e4"), buffer.drain(10).map { it.name })
    }
}
