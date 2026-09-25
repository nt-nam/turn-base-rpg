package com.pxworld.server

import com.pxworld.content.BattleContentAssembler
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.domain.battle.BattleEngine
import com.pxworld.infrastructure.replay.ReplayCommandDocument
import com.pxworld.infrastructure.replay.ReplayDocument
import com.pxworld.infrastructure.replay.ReplaySlotDocument
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.file.Files
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerTest {

    private var clock = System.currentTimeMillis()
    private val contentCopy: File = Files.createTempDirectory("pxworld-content").toFile().also { File(System.getProperty("contentDir")).copyRecursively(it) }
    private val services = Services(
        ServerConfig.fromEnvironment { key ->
            mapOf(
                "PXWORLD_DB_URL" to "jdbc:h2:mem:${UUID.randomUUID()};MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                "PXWORLD_CONTENT_DIR" to contentCopy.path,
                "PXWORLD_LEGACY_ASSETS_DIR" to System.getProperty("legacyAssetsDir"),
            )[key]
        },
        clock = { clock },
    )

    @AfterTest
    fun cleanUp() {
        services.close()
        contentCopy.deleteRecursively()
    }

    private fun serve(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application { pxworld(services) }
        block()
    }

    private suspend fun HttpClient.send(method: String, path: String, token: String? = null, body: String? = null): HttpResponse {
        val configure: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {
            token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            body?.let { contentType(ContentType.Application.Json); setBody(it) }
        }
        return when (method) {
            "GET" -> get(path, configure)
            "POST" -> post(path, configure)
            "PUT" -> put(path, configure)
            else -> error(method)
        }
    }

    private suspend fun HttpResponse.json(): JsonObject = ApiJson.parseToJsonElement(bodyAsText()).jsonObject
    private fun JsonObject.text(key: String): String = getValue(key).jsonPrimitive.content

    private suspend fun HttpClient.token(path: String, body: String): String = send("POST", path, body = body).json().text("token")
    private suspend fun HttpClient.admin(): String = token("/auth/login", """{"email":"admin@pxworld.local","password":"admin-dev-password"}""")
    private suspend fun HttpClient.staff(adminToken: String, email: String, role: String): String {
        assertEquals(HttpStatusCode.Created, send("POST", "/admin/staff", adminToken, """{"email":"$email","password":"staff-password-1","displayName":"Staff","roles":["$role"]}""").status)
        return token("/auth/login", """{"email":"$email","password":"staff-password-1"}""")
    }

    @Test
    fun healthReportsPromotedContent() = serve {
        val health = client.send("GET", "/health").json()
        assertEquals("ok", health.text("status"))
        val manifest = client.send("GET", "/content/manifest").json()
        assertEquals(health.text("contentVersion"), manifest.text("version"))
        val pack = client.send("GET", "/content/packs/${manifest.text("version")}")
        assertEquals(HttpStatusCode.OK, pack.status)
        assertEquals(manifest.text("sha256"), ContentCompilation.sha256(pack.bodyAsText()))
    }

    @Test
    fun cloudSavesDetectRevisionConflictsAndKeepHistory() = serve {
        val token = client.token("/auth/register", """{"email":"hero@example.com","password":"long-password","displayName":"Hero"}""")
        assertEquals(HttpStatusCode.Unauthorized, client.send("GET", "/saves").status)
        assertEquals(HttpStatusCode.OK, client.send("PUT", "/saves/main", token, """{"expectedRevision":0,"body":"{\"gold\":1}"}""").status)
        clock += 10
        assertEquals("2", client.send("PUT", "/saves/main", token, """{"expectedRevision":1,"body":"{\"gold\":2}"}""").json().text("revision"))
        val conflict = client.send("PUT", "/saves/main", token, """{"expectedRevision":1,"body":"{\"gold\":3}"}""")
        assertEquals(HttpStatusCode.Conflict, conflict.status)
        assertEquals("2", conflict.json().getValue("current").jsonObject.text("revision"))
        assertEquals("{\"gold\":2}", client.send("GET", "/saves/main", token).json().text("body"))
        assertEquals("{\"gold\":1}", client.send("GET", "/saves/main/history/1", token).json().text("body"))
        assertEquals(HttpStatusCode.BadRequest, client.send("PUT", "/saves/main", token, """{"expectedRevision":2,"body":"not json"}""").status)
    }

    @Test
    fun battleValidationReplaysHonestAndRejectsForgedResults() = serve {
        val token = client.token("/auth/guest", """{"displayName":"Guest"}""")
        val bundle = services.content.bundle
        val encounter = bundle.encounters.first()
        val lineup = ContentCompilation.referenceLineup(bundle, encounter.recommendedLevel)
        val record = BattleEngine.runAuto(BattleContentAssembler(bundle).battle(7, lineup, encounter.id))
        val honest = ReplayDocument(
            id = "r1",
            encounterId = encounter.id,
            seed = 7,
            lineup = lineup.map { ReplaySlotDocument(it.heroId, it.level, it.star, it.cell.lane, it.cell.depth, emptyMap()) },
            commands = record.commands.map { ReplayCommandDocument(it.actor.toString(), it.skillId, it.target?.toString()) },
            outcome = record.finalState.outcome!!.name,
            rounds = record.finalState.round,
            recordedAtMillis = 0,
        )
        val encode = { document: ReplayDocument -> ApiJson.encodeToString(ReplayDocument.serializer(), document) }
        assertEquals("true", client.send("POST", "/battles/validate", token, encode(honest)).json().text("valid"))
        val forgedOutcome = if (honest.outcome == "VICTORY") "DEFEAT" else "VICTORY"
        assertEquals("false", client.send("POST", "/battles/validate", token, encode(honest.copy(outcome = forgedOutcome))).json().text("valid"))
        val boosted = honest.copy(lineup = honest.lineup.map { it.copy(level = 999) })
        assertEquals("REJECTED", client.send("POST", "/battles/validate", token, encode(boosted)).json().text("replayedOutcome"))
        val admin = client.admin()
        assertEquals("2", client.send("GET", "/admin/dashboard", admin).json().text("rejectedBattles"))
    }

    @Test
    fun rolesGuardStaffEndpoints() = serve {
        val player = client.token("/auth/register", """{"email":"p@example.com","password":"long-password","displayName":"Player"}""")
        assertEquals(HttpStatusCode.Forbidden, client.send("GET", "/admin/dashboard", player).status)
        assertEquals(HttpStatusCode.Forbidden, client.send("GET", "/admin/players?q=p", player).status)
        val admin = client.admin()
        val qa = client.staff(admin, "qa@pxworld.local", Roles.QA)
        assertEquals(HttpStatusCode.Forbidden, client.send("GET", "/admin/players?q=p", qa).status)
        assertEquals(HttpStatusCode.Forbidden, client.send("POST", "/content/promote", qa, """{"env":"prod","version":"x"}""").status)
        assertEquals(HttpStatusCode.OK, client.send("GET", "/admin/players?q=p", admin).status)
        val orphan = services.tokens.issue(AccountRow("missing-account", null, null, "Ghost", Services.KIND_PLAYER, setOf(Roles.PLAYER), null, clock), clock)
        assertEquals(HttpStatusCode.Unauthorized, client.send("GET", "/saves", orphan).status)
        assertEquals(HttpStatusCode.Unauthorized, client.send("GET", "/me", orphan).status)
    }

    @Test
    fun supportGrantsMailOnceAndSanctionsBlockTheAccount() = serve {
        val player = client.token("/auth/register", """{"email":"mail@example.com","password":"long-password","displayName":"Mailer"}""")
        val admin = client.admin()
        val support = client.staff(admin, "support@pxworld.local", Roles.SUPPORT)
        val found = ApiJson.parseToJsonElement(client.send("GET", "/admin/players?q=mail@", support).bodyAsText()).jsonArray
        val accountId = found.single().jsonObject.text("id")
        val currency = services.content.bundle.currencies.first().id
        assertEquals(HttpStatusCode.BadRequest, client.send("POST", "/admin/players/$accountId/grant", support, """{"subject":"x","grants":{"$currency":10},"reason":""}""").status)
        assertEquals(HttpStatusCode.BadRequest, client.send("POST", "/admin/players/$accountId/grant", support, """{"subject":"x","grants":{"nothing.real":10},"reason":"r"}""").status)
        val mailId = client.send("POST", "/admin/players/$accountId/grant", support, """{"subject":"Sorry","grants":{"$currency":500},"reason":"ticket 42"}""").json().text("id")
        assertEquals(HttpStatusCode.OK, client.send("POST", "/mail/$mailId/claim", player).status)
        assertEquals(HttpStatusCode.Conflict, client.send("POST", "/mail/$mailId/claim", player).status)
        assertEquals(HttpStatusCode.OK, client.send("POST", "/admin/players/$accountId/sanction", support, """{"hours":2,"reason":"abuse"}""").status)
        assertEquals(HttpStatusCode.Forbidden, client.send("GET", "/saves", player).status)
        assertEquals(HttpStatusCode.Forbidden, client.send("POST", "/auth/login", body = """{"email":"mail@example.com","password":"long-password"}""").status)
        clock += 3 * 60 * 60 * 1000L
        assertEquals(HttpStatusCode.OK, client.send("GET", "/saves", player).status)
        val audit = ApiJson.parseToJsonElement(client.send("GET", "/admin/audit?target=account:$accountId", support).bodyAsText()).jsonArray
        assertEquals(listOf("player.sanction", "player.grant"), audit.map { it.jsonObject.text("action") })
    }

    @Test
    fun studioValidatesBeforeWritingAndLiveopsPromotes() = serve {
        val admin = client.admin()
        val creator = client.staff(admin, "creator@pxworld.local", Roles.CREATOR)
        val liveops = client.staff(admin, "liveops@pxworld.local", Roles.LIVEOPS)
        val kinds = ApiJson.parseToJsonElement(client.send("GET", "/studio/kinds", creator).bodyAsText()).jsonArray.map { it.jsonPrimitive.content }
        assertTrue("items" in kinds && "localization" !in kinds, kinds.toString())
        val schema = client.send("GET", "/studio/kinds/items/schema", creator).json()
        assertTrue("shop" in schema.getValue("properties").jsonObject, schema.toString())
        assertEquals(HttpStatusCode.BadRequest, client.send("GET", "/studio/kinds/nope/schema", creator).status)
        val item = client.send("GET", "/studio/kinds/items", creator).json().getValue("records").jsonArray.first().jsonObject
        val broken = JsonObject(item + ("name" to JsonPrimitive("text.missing.key")))
        val rejected = client.send("PUT", "/studio/kinds/items", creator, broken.toString())
        assertEquals(HttpStatusCode.UnprocessableEntity, rejected.status, rejected.bodyAsText())
        val before = services.content.pack
        val shop = item.getValue("shop").jsonObject
        val renamed = JsonObject(item + ("shop" to JsonObject(shop + ("price" to JsonPrimitive(shop.text("price").toInt() + 1)))))
        assertEquals(HttpStatusCode.OK, client.send("PUT", "/studio/kinds/items?dryRun=true", creator, renamed.toString()).status)
        assertEquals(before, services.content.pack)
        val applied = client.send("PUT", "/studio/kinds/items", creator, renamed.toString())
        assertEquals(HttpStatusCode.OK, applied.status, applied.bodyAsText())
        val version = client.send("POST", "/content/releases", liveops).json().text("id")
        assertEquals(HttpStatusCode.Forbidden, client.send("POST", "/content/promote", creator, """{"env":"prod","version":"$version"}""").status)
        assertEquals(version, client.send("POST", "/content/promote", liveops, """{"env":"prod","version":"$version"}""").json().text("version"))
        assertEquals(version, client.send("GET", "/content/manifest?env=prod").json().text("version"))
    }

    @Test
    fun agentRunsAndTelemetryAreRecorded() = serve {
        val admin = client.admin()
        val qa = client.staff(admin, "qa2@pxworld.local", Roles.QA)
        val report = """{"mode":"explore","status":"passed","coverage":{"visitedCount":127,"registeredCount":128,"launchPercent":42.5}}"""
        val id = client.send("POST", "/qa/agent-runs", qa, report).json().text("id")
        val runs = ApiJson.parseToJsonElement(client.send("GET", "/qa/agent-runs", qa).bodyAsText()).jsonArray
        assertEquals("127", runs.single().jsonObject.text("visited"))
        assertEquals(report, client.send("GET", "/qa/agent-runs/$id", qa).bodyAsText())
        assertEquals(HttpStatusCode.Accepted, client.send("POST", "/telemetry", body = """{"events":[{"name":"session.start"},{"name":"battle.end","payload":{"won":true}}]}""").status)
        assertEquals(HttpStatusCode.BadRequest, client.send("POST", "/telemetry", body = """{"events":[{"name":"Bad Name"}]}""").status)
        val dashboard = client.send("GET", "/admin/dashboard", admin).json()
        assertEquals(JsonObject(mapOf("battle.end" to JsonPrimitive(1), "session.start" to JsonPrimitive(1))), dashboard.getValue("telemetryLastDay"))
        assertTrue(client.send("GET", "/metrics").bodyAsText().contains("pxworld_agent_runs_total 1"))
    }
}
