package com.pxworld.server

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ErrorView(val error: String, val detail: ValidationView? = null)

class Services(val config: ServerConfig, val clock: () -> Long = System::currentTimeMillis) : AutoCloseable {
    val database = Database(config.databaseUrl, config.databaseUser, config.databasePassword).also { it.migrate() }
    val repositories = Repositories(database, clock)
    val tokens = Tokens(config.jwtSecret, config.jwtIssuer)
    val content = ContentService(config.contentDir, config.legacyAssetsDir, config.contentWritable)

    init {
        bootstrapAdmin()
        publishCurrentContent("system")
    }

    fun publishCurrentContent(actorId: String): String {
        val pack = content.pack
        val sha = com.pxworld.content.compiler.ContentCompilation.sha256(pack)
        val version = sha.take(VERSION_LENGTH)
        repositories.addRelease(version, sha, pack, content.bundle.allIds().size, actorId)
        if (repositories.channels()[config.env] == null) repositories.promote(config.env, version, actorId)
        return version
    }

    private fun bootstrapAdmin() {
        val email = config.adminEmail ?: return
        val password = config.adminPassword ?: return
        if (repositories.staffCount() > 0 || repositories.accountByEmail(email) != null) return
        repositories.createAccount(email, Passwords.hash(password), "Administrator", KIND_STAFF, setOf(Roles.ADMIN))
    }

    override fun close() = database.close()

    companion object {
        const val VERSION_LENGTH: Int = 12
        const val KIND_STAFF: String = "staff"
        const val KIND_PLAYER: String = "player"
        const val KIND_GUEST: String = "guest"
    }
}

val ApiJson: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false }

fun Application.pxworld(services: Services) {
    install(ContentNegotiation) { json(ApiJson) }
    install(CallLogging)
    install(CORS) {
        services.config.corsHosts.forEach { allowHost(it, schemes = listOf("http", "https")) }
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
    install(Authentication) {
        jwt(AUTH) {
            verifier(services.tokens.verifier)
            validate { credential -> credential.payload.subject?.let { JWTPrincipal(credential.payload) } }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized, ErrorView("authentication required")) }
        }
    }
    install(StatusPages) {
        exception<Unauthenticated> { call, cause -> call.respond(HttpStatusCode.Unauthorized, ErrorView(cause.message ?: "authentication required")) }
        exception<Forbidden> { call, cause -> call.respond(HttpStatusCode.Forbidden, ErrorView(cause.message ?: "forbidden")) }
        exception<ContentRejected> { call, cause -> call.respond(HttpStatusCode.UnprocessableEntity, ErrorView("content rejected", cause.validation)) }
        exception<IllegalArgumentException> { call, cause -> call.respond(HttpStatusCode.BadRequest, ErrorView(cause.message ?: "bad request")) }
        exception<IllegalStateException> { call, cause -> call.respond(HttpStatusCode.Conflict, ErrorView(cause.message ?: "conflict")) }
        exception<kotlinx.serialization.SerializationException> { call, cause -> call.respond(HttpStatusCode.BadRequest, ErrorView(cause.message ?: "malformed body")) }
        exception<io.ktor.server.plugins.BadRequestException> { call, cause -> call.respond(HttpStatusCode.BadRequest, ErrorView(cause.message ?: "malformed body")) }
    }
    routes(services)
}

const val AUTH: String = "pxworld"

fun main() {
    val services = Services(ServerConfig.fromEnvironment())
    Runtime.getRuntime().addShutdownHook(Thread(services::close))
    embeddedServer(Netty, port = services.config.port) { pxworld(services) }.start(wait = true)
}
