package com.pxworld.server

import com.pxworld.infrastructure.replay.ReplayDocument
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable data class RegisterRequest(val email: String, val password: String, val displayName: String)
@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class GuestRequest(val displayName: String)
@Serializable data class SessionView(val token: String, val account: AccountView)
@Serializable data class AccountView(val id: String, val email: String?, val displayName: String, val kind: String, val roles: List<String>, val bannedUntil: Long?, val createdAt: Long)
@Serializable data class PutSaveRequest(val expectedRevision: Long, val body: String)
@Serializable data class SaveMetaView(val slot: String, val revision: Long, val updatedAt: Long, val bytes: Int)
@Serializable data class SaveConflictView(val error: String, val current: SaveMetaView)
@Serializable data class ManifestView(val env: String, val version: String, val sha256: String, val bytes: Long, val records: Int)
@Serializable data class PromoteRequest(val env: String, val version: String)
@Serializable data class TelemetryEvent(val name: String, val payload: JsonObject = JsonObject(emptyMap()))
@Serializable data class TelemetryBatch(val clientVersion: String? = null, val events: List<TelemetryEvent>)
@Serializable data class GrantRequest(val subject: String, val grants: Map<String, Long>, val reason: String)
@Serializable data class SanctionRequest(val hours: Int, val reason: String)
@Serializable data class LiftRequest(val reason: String)
@Serializable data class RestoreRequest(val revision: Long, val reason: String)
@Serializable data class StaffRequest(val email: String, val password: String, val displayName: String, val roles: List<String>)
@Serializable data class PlayerDetailView(val account: AccountView, val saves: List<SaveMetaView>, val mail: List<MailRow>, val audit: List<AuditRow>)
@Serializable data class DashboardView(val env: String, val contentVersion: String?, val accounts: Int, val players: Int, val saves: Int, val telemetryLastDay: Map<String, Int>, val battleValidations: Int, val rejectedBattles: Int, val agentRuns: Int)
@Serializable data class IdView(val id: String)
@Serializable data class HealthView(val status: String, val env: String, val contentVersion: String?)
@Serializable data class StudioRecordsView(val kind: String, val records: List<JsonObject>)

fun AccountRow.view() = AccountView(id, email, displayName, kind, roles.sorted(), bannedUntil, createdAt)
fun SaveRow.meta() = SaveMetaView(slot, revision, updatedAt, body.length)

fun Application.routes(services: Services) {
    val repositories = services.repositories
    val now = services.clock

    fun session(account: AccountRow) = SessionView(services.tokens.issue(account, now()), account.view())

    fun ensureNotBanned(account: AccountRow) {
        val until = account.bannedUntil ?: return
        if (until > now()) throw Forbidden("account suspended until $until")
    }

    fun ApplicationCall.activeCaller(): Caller {
        val caller = caller()
        val account = repositories.account(caller.accountId) ?: throw Unauthenticated("account no longer exists")
        ensureNotBanned(account)
        return caller
    }

    fun manifest(env: String): ManifestView? {
        val version = repositories.channels()[env] ?: return null
        val release = repositories.releases().firstOrNull { it.version == version } ?: return null
        return ManifestView(env, release.version, release.sha256, release.bytes, release.records)
    }

    routing {
        get("/health") { call.respond(HealthView("ok", services.config.env, repositories.channels()[services.config.env])) }

        get("/metrics") {
            val lines = buildString {
                appendLine("pxworld_accounts_total ${repositories.count("accounts")}")
                appendLine("pxworld_saves_total ${repositories.count("saves")}")
                appendLine("pxworld_telemetry_events_total ${repositories.count("telemetry_events")}")
                appendLine("pxworld_battle_validations_total ${repositories.count("battle_validations")}")
                appendLine("pxworld_battle_validations_rejected_total ${repositories.count("battle_validations", "valid = ?", false)}")
                appendLine("pxworld_agent_runs_total ${repositories.count("agent_runs")}")
            }
            call.respondText(lines, ContentType.Text.Plain)
        }

        route("/auth") {
            post("/register") {
                val request = call.receive<RegisterRequest>()
                require(EMAIL.matches(request.email)) { "invalid email" }
                require(request.password.length >= MIN_PASSWORD) { "password needs at least $MIN_PASSWORD characters" }
                require(request.displayName.trim().length in 2..MAX_NAME) { "display name needs 2-$MAX_NAME characters" }
                check(repositories.accountByEmail(request.email) == null) { "email already registered" }
                val account = repositories.createAccount(request.email, Passwords.hash(request.password), request.displayName.trim(), Services.KIND_PLAYER, setOf(Roles.PLAYER))
                call.respond(HttpStatusCode.Created, session(account))
            }
            post("/guest") {
                val request = call.receive<GuestRequest>()
                require(request.displayName.trim().length in 2..MAX_NAME) { "display name needs 2-$MAX_NAME characters" }
                call.respond(HttpStatusCode.Created, session(repositories.createAccount(null, null, request.displayName.trim(), Services.KIND_GUEST, setOf(Roles.PLAYER))))
            }
            post("/login") {
                val request = call.receive<LoginRequest>()
                val account = repositories.accountByEmail(request.email)?.takeIf { row -> row.passwordHash?.let { Passwords.verify(request.password, it) } == true }
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorView("wrong email or password"))
                ensureNotBanned(account)
                call.respond(session(account))
            }
        }

        get("/content/manifest") {
            val env = call.request.queryParameters["env"] ?: services.config.env
            manifest(env)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound, ErrorView("no release promoted to $env"))
        }
        get("/content/packs/{version}") {
            val body = repositories.releaseBody(call.parameters["version"].orEmpty()) ?: return@get call.respond(HttpStatusCode.NotFound, ErrorView("unknown release"))
            call.respondText(body, ContentType.Application.Json)
        }

        authenticate(AUTH, optional = true) {
            post("/telemetry") {
                val batch = call.receive<TelemetryBatch>()
                require(batch.events.size <= MAX_TELEMETRY_BATCH) { "at most $MAX_TELEMETRY_BATCH events per batch" }
                val accountId = runCatching { call.caller().accountId }.getOrNull()
                batch.events.forEach { event ->
                    require(TELEMETRY_NAME.matches(event.name)) { "invalid event name ${event.name}" }
                    repositories.addTelemetry(accountId, event.name, ApiJson.encodeToString(JsonObject.serializer(), event.payload), batch.clientVersion)
                }
                call.respond(HttpStatusCode.Accepted, mapOf("accepted" to batch.events.size))
            }
        }

        authenticate(AUTH) {
            get("/me") {
                val caller = call.caller()
                call.respond(repositories.account(caller.accountId)?.view() ?: throw Unauthenticated("account no longer exists"))
            }

            route("/saves") {
                get { call.respond(repositories.saves(call.activeCaller().accountId).map { it.meta() }) }
                get("/{slot}") {
                    val save = repositories.save(call.activeCaller().accountId, call.slot()) ?: return@get call.respond(HttpStatusCode.NotFound, ErrorView("no save in slot"))
                    call.respond(save)
                }
                put("/{slot}") {
                    val caller = call.activeCaller()
                    val slot = call.slot()
                    val request = call.receive<PutSaveRequest>()
                    require(request.body.length <= MAX_SAVE_BYTES) { "save exceeds $MAX_SAVE_BYTES bytes" }
                    runCatching { ApiJson.parseToJsonElement(request.body).jsonObject }.getOrElse { throw IllegalArgumentException("save body must be a JSON object") }
                    val stored = repositories.putSave(caller.accountId, slot, request.expectedRevision, request.body)
                    if (stored == null) {
                        val current = repositories.save(caller.accountId, slot)
                        call.respond(HttpStatusCode.Conflict, SaveConflictView("revision conflict", current?.meta() ?: SaveMetaView(slot, 0, 0, 0)))
                    } else {
                        call.respond(stored.meta())
                    }
                }
                get("/{slot}/history") { call.respond(repositories.saveHistory(call.activeCaller().accountId, call.slot()).map { it.meta() }) }
                get("/{slot}/history/{revision}") {
                    val revision = call.parameters["revision"]?.toLongOrNull() ?: throw IllegalArgumentException("revision must be a number")
                    val row = repositories.saveHistory(call.activeCaller().accountId, call.slot()).firstOrNull { it.revision == revision }
                        ?: return@get call.respond(HttpStatusCode.NotFound, ErrorView("revision not kept"))
                    call.respond(row)
                }
            }

            post("/battles/validate") {
                val caller = call.activeCaller()
                val document = ApiJson.decodeFromString(ReplayDocument.serializer(), call.receiveText())
                val verdict = services.content.validateReplay(document)
                repositories.addBattleValidation(caller.accountId, document.encounterId, document.outcome, verdict.replayedOutcome, verdict.valid)
                call.respond(verdict)
            }

            route("/mail") {
                get { call.respond(repositories.mail(call.activeCaller().accountId)) }
                post("/{id}/claim") {
                    val claimed = repositories.claimMail(call.activeCaller().accountId, call.parameters["id"].orEmpty())
                        ?: return@post call.respond(HttpStatusCode.Conflict, ErrorView("mail missing or already claimed"))
                    call.respond(claimed)
                }
            }

            route("/content") {
                get("/releases") {
                    call.requireRole(Roles.LIVEOPS, Roles.DEV, Roles.CREATOR, Roles.QA)
                    call.respond(repositories.releases())
                }
                get("/releases/{from}/diff/{to}") {
                    call.requireRole(Roles.LIVEOPS, Roles.DEV, Roles.CREATOR, Roles.QA)
                    val from = call.parameters["from"].orEmpty()
                    val to = call.parameters["to"].orEmpty()
                    val before = repositories.releaseBody(from) ?: throw IllegalArgumentException("unknown release $from")
                    val after = repositories.releaseBody(to) ?: throw IllegalArgumentException("unknown release $to")
                    call.respond(ContentDiff.between(from, to, before, after))
                }
                get("/channels") {
                    call.requireRole(Roles.LIVEOPS, Roles.DEV, Roles.CREATOR, Roles.QA)
                    call.respond(repositories.channels())
                }
                post("/releases") {
                    val caller = call.requireRole(Roles.LIVEOPS, Roles.DEV)
                    val version = services.publishCurrentContent(caller.accountId)
                    repositories.audit(caller.accountId, "content.publish", "content:$version", "publish current content", "{}")
                    call.respond(HttpStatusCode.Created, IdView(version))
                }
                post("/promote") {
                    val caller = call.requireRole(Roles.LIVEOPS)
                    val request = call.receive<PromoteRequest>()
                    require(request.env in ENVIRONMENTS) { "env must be one of $ENVIRONMENTS" }
                    require(repositories.releases().any { it.version == request.version }) { "unknown release ${request.version}" }
                    repositories.promote(request.env, request.version, caller.accountId)
                    repositories.audit(caller.accountId, "content.promote", "channel:${request.env}", "promote ${request.version}", "{}")
                    call.respond(manifest(request.env) ?: error("promotion did not persist"))
                }
            }

            route("/admin") {
                get("/dashboard") {
                    if (!call.caller().isStaff) throw Forbidden("staff only")
                    call.respond(
                        DashboardView(
                            env = services.config.env,
                            contentVersion = repositories.channels()[services.config.env],
                            accounts = repositories.count("accounts"),
                            players = repositories.count("accounts", "kind <> ?", Services.KIND_STAFF),
                            saves = repositories.count("saves"),
                            telemetryLastDay = repositories.telemetryByName(now() - DAY),
                            battleValidations = repositories.count("battle_validations"),
                            rejectedBattles = repositories.count("battle_validations", "valid = ?", false),
                            agentRuns = repositories.count("agent_runs"),
                        ),
                    )
                }
                get("/players") {
                    call.requireRole(Roles.SUPPORT, Roles.LIVEOPS)
                    val query = call.request.queryParameters["q"].orEmpty().trim()
                    call.respond(repositories.searchAccounts(query, SEARCH_LIMIT).map { it.view() })
                }
                get("/players/{id}") {
                    call.requireRole(Roles.SUPPORT, Roles.LIVEOPS)
                    val account = call.targetAccount(repositories)
                    call.respond(PlayerDetailView(account.view(), repositories.saves(account.id).map { it.meta() }, repositories.mail(account.id), repositories.auditLog("account:${account.id}", AUDIT_LIMIT)))
                }
                post("/players/{id}/grant") {
                    val caller = call.requireRole(Roles.SUPPORT, Roles.LIVEOPS)
                    val account = call.targetAccount(repositories)
                    val request = call.receive<GrantRequest>()
                    require(request.reason.isNotBlank()) { "a reason is required" }
                    require(request.grants.isNotEmpty() && request.grants.values.all { it in 1..MAX_GRANT }) { "grants must be 1..$MAX_GRANT" }
                    val unknown = request.grants.keys.filterNot { key -> key in services.content.bundle.allIds() }
                    require(unknown.isEmpty()) { "unknown grant ids $unknown" }
                    val grants = JsonObject(request.grants.mapValues { JsonPrimitive(it.value) }).toString()
                    val mailId = repositories.addMail(account.id, request.subject, grants)
                    repositories.audit(caller.accountId, "player.grant", "account:${account.id}", request.reason, grants)
                    call.respond(HttpStatusCode.Created, IdView(mailId))
                }
                get("/players/{id}/saves/{slot}/history") {
                    call.requireRole(Roles.SUPPORT)
                    val account = call.targetAccount(repositories)
                    call.respond(repositories.saveHistory(account.id, call.slot()).map { it.meta() })
                }
                post("/players/{id}/saves/{slot}/restore") {
                    val caller = call.requireRole(Roles.SUPPORT)
                    val account = call.targetAccount(repositories)
                    val slot = call.slot()
                    val request = call.receive<RestoreRequest>()
                    require(request.reason.isNotBlank()) { "a reason is required" }
                    val source = repositories.saveHistory(account.id, slot).firstOrNull { it.revision == request.revision }
                        ?: throw IllegalArgumentException("revision ${request.revision} is not kept")
                    val current = repositories.save(account.id, slot)?.revision ?: 0L
                    val restored = repositories.putSave(account.id, slot, current, source.body) ?: error("save changed during restore, retry")
                    val payload = JsonObject(mapOf("slot" to JsonPrimitive(slot), "from" to JsonPrimitive(request.revision), "to" to JsonPrimitive(restored.revision))).toString()
                    repositories.audit(caller.accountId, "player.save_restore", "account:${account.id}", request.reason, payload)
                    call.respond(restored.meta())
                }
                post("/players/{id}/sanction") {
                    val caller = call.requireRole(Roles.SUPPORT)
                    val account = call.targetAccount(repositories)
                    val request = call.receive<SanctionRequest>()
                    require(request.reason.isNotBlank()) { "a reason is required" }
                    require(request.hours in 1..MAX_SANCTION_HOURS) { "hours must be 1..$MAX_SANCTION_HOURS" }
                    require(Roles.STAFF.none { it in account.roles }) { "staff accounts cannot be sanctioned here" }
                    val until = now() + request.hours * HOUR
                    repositories.setBan(account.id, until)
                    repositories.audit(caller.accountId, "player.sanction", "account:${account.id}", request.reason, """{"until":$until}""")
                    call.respond(repositories.account(account.id)!!.view())
                }
                post("/players/{id}/lift") {
                    val caller = call.requireRole(Roles.SUPPORT)
                    val account = call.targetAccount(repositories)
                    val request = call.receive<LiftRequest>()
                    require(request.reason.isNotBlank()) { "a reason is required" }
                    repositories.setBan(account.id, null)
                    repositories.audit(caller.accountId, "player.lift", "account:${account.id}", request.reason, "{}")
                    call.respond(repositories.account(account.id)!!.view())
                }
                get("/audit") {
                    call.requireRole(Roles.SUPPORT, Roles.LIVEOPS)
                    call.respond(repositories.auditLog(call.request.queryParameters["target"], AUDIT_LIMIT))
                }
                telemetryExplorer(repositories)
                post("/staff") {
                    val caller = call.requireRole(Roles.ADMIN)
                    val request = call.receive<StaffRequest>()
                    require(request.roles.isNotEmpty() && request.roles.all { it in Roles.STAFF }) { "roles must be staff roles" }
                    require(request.password.length >= MIN_PASSWORD) { "password needs at least $MIN_PASSWORD characters" }
                    check(repositories.accountByEmail(request.email) == null) { "email already registered" }
                    val account = repositories.createAccount(request.email, Passwords.hash(request.password), request.displayName, Services.KIND_STAFF, request.roles.toSet())
                    repositories.audit(caller.accountId, "staff.create", "account:${account.id}", "create staff", """{"roles":"${request.roles.joinToString(",")}"}""")
                    call.respond(HttpStatusCode.Created, account.view())
                }
            }

            route("/qa/agent-runs") {
                post {
                    val caller = call.requireRole(Roles.QA, Roles.DEV)
                    val text = call.receiveText()
                    require(text.length <= MAX_REPORT_BYTES) { "report exceeds $MAX_REPORT_BYTES bytes" }
                    val report = ApiJson.parseToJsonElement(text).jsonObject
                    val coverage = report["coverage"] as? JsonObject
                    val id = repositories.addAgentRun(
                        mode = report.string("mode") ?: "unknown",
                        status = report.string("status") ?: "unknown",
                        visited = (coverage?.get("visitedCount") as? JsonPrimitive)?.intOrNull ?: 0,
                        registered = (coverage?.get("registeredCount") as? JsonPrimitive)?.intOrNull ?: 0,
                        launchPercent = (coverage?.get("launchPercent") as? JsonPrimitive)?.doubleOrNull ?: 0.0,
                        report = text,
                        uploadedBy = caller.accountId,
                    )
                    call.respond(HttpStatusCode.Created, IdView(id))
                }
                get {
                    call.requireRole(Roles.QA, Roles.DEV)
                    call.respond(repositories.agentRuns(AGENT_RUN_LIMIT))
                }
                get("/{id}") {
                    call.requireRole(Roles.QA, Roles.DEV)
                    val report = repositories.agentRunReport(call.parameters["id"].orEmpty()) ?: return@get call.respond(HttpStatusCode.NotFound, ErrorView("unknown run"))
                    call.respondText(report, ContentType.Application.Json)
                }
            }

            route("/studio") {
                get("/kinds") {
                    call.requireRole(Roles.CREATOR, Roles.DEV)
                    call.respond(services.content.kinds())
                }
                get("/kinds/{kind}") {
                    call.requireRole(Roles.CREATOR, Roles.DEV)
                    val kind = call.parameters["kind"].orEmpty()
                    call.respond(StudioRecordsView(kind, services.content.records(kind)))
                }
                get("/kinds/{kind}/schema") {
                    call.requireRole(Roles.CREATOR, Roles.DEV)
                    val kind = com.pxworld.content.ContentKinds.byDirectory(call.parameters["kind"].orEmpty()) ?: throw IllegalArgumentException("unknown kind")
                    call.respond(com.pxworld.content.ContentSchema.record(kind))
                }
                put("/kinds/{kind}") {
                    val caller = call.requireRole(Roles.CREATOR, Roles.DEV)
                    val kind = call.parameters["kind"].orEmpty()
                    val record = call.receive<JsonObject>()
                    val dryRun = call.request.queryParameters["dryRun"].toBoolean()
                    val validation = services.content.upsert(kind, record, dryRun)
                    if (!dryRun) repositories.audit(caller.accountId, "studio.upsert", "record:${record.string("id")}", "edit $kind", "{}")
                    call.respond(validation)
                }
            }
        }
    }
}

private fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content

private fun ApplicationCall.slot(): String {
    val slot = parameters["slot"].orEmpty()
    require(SLOT.matches(slot)) { "slot must match ${SLOT.pattern}" }
    return slot
}

private fun ApplicationCall.targetAccount(repositories: Repositories): AccountRow =
    repositories.account(parameters["id"].orEmpty()) ?: throw IllegalArgumentException("unknown account")

private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
private val SLOT = Regex("^[a-z0-9_-]{1,32}$")
private val TELEMETRY_NAME = Regex("^[a-z][a-z0-9_.]{1,63}$")
private val ENVIRONMENTS = setOf("dev", "qa", "staging", "prod")
private const val MIN_PASSWORD = 10
private const val MAX_NAME = 32
private const val MAX_SAVE_BYTES = 2_000_000
private const val MAX_REPORT_BYTES = 5_000_000
private const val MAX_TELEMETRY_BATCH = 200
private const val MAX_GRANT = 1_000_000L
private const val MAX_SANCTION_HOURS = 24 * 365
private const val SEARCH_LIMIT = 50
private const val AUDIT_LIMIT = 100
private const val AGENT_RUN_LIMIT = 50
private const val HOUR = 60 * 60 * 1000L
private const val DAY = 24 * HOUR
