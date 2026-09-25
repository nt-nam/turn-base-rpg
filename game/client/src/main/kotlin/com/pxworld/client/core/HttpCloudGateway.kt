package com.pxworld.client.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.net.HttpRequestBuilder
import com.pxworld.application.CloudAccount
import com.pxworld.application.CloudCredentialStore
import com.pxworld.application.CloudCredentials
import com.pxworld.application.CloudGateway
import com.pxworld.application.CloudMail
import com.pxworld.application.CloudResult
import com.pxworld.application.CloudSave
import com.pxworld.application.CloudSaveMeta
import com.pxworld.application.TelemetryEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class HttpCloudGateway(
    private val baseUrl: String,
    private val deliver: (() -> Unit) -> Unit,
    private val net: Net = Gdx.net,
) : CloudGateway {

    override fun guest(displayName: String, done: (CloudResult<CloudCredentials>) -> Unit) =
        send(Net.HttpMethods.POST, "/auth/guest", null, obj("displayName" to displayName), done, ::credentials)

    override fun login(email: String, password: String, done: (CloudResult<CloudCredentials>) -> Unit) =
        send(Net.HttpMethods.POST, "/auth/login", null, obj("email" to email, "password" to password), done, ::credentials)

    override fun register(email: String, password: String, displayName: String, done: (CloudResult<CloudCredentials>) -> Unit) =
        send(Net.HttpMethods.POST, "/auth/register", null, obj("email" to email, "password" to password, "displayName" to displayName), done, ::credentials)

    override fun saves(token: String, done: (CloudResult<List<CloudSaveMeta>>) -> Unit) =
        send(Net.HttpMethods.GET, "/saves", token, null, done) { element -> element.jsonArray.map { meta(it.jsonObject) } }

    override fun download(token: String, slot: String, done: (CloudResult<CloudSave>) -> Unit) =
        send(Net.HttpMethods.GET, "/saves/$slot", token, null, done) { element ->
            val document = element.jsonObject
            CloudSave(CloudSaveMeta(slot, document.long("revision"), document.long("updatedAt")), document.string("body"))
        }

    override fun upload(token: String, slot: String, expectedRevision: Long, body: String, done: (CloudResult<CloudSaveMeta>) -> Unit) =
        send(Net.HttpMethods.PUT, "/saves/$slot", token, JsonObject(mapOf("expectedRevision" to JsonPrimitive(expectedRevision), "body" to JsonPrimitive(body))), done) { meta(it.jsonObject) }

    override fun mail(token: String, done: (CloudResult<List<CloudMail>>) -> Unit) =
        send(Net.HttpMethods.GET, "/mail", token, null, done) { element -> element.jsonArray.map { mail(it.jsonObject) } }

    override fun claimMail(token: String, mailId: String, done: (CloudResult<CloudMail>) -> Unit) =
        send(Net.HttpMethods.POST, "/mail/$mailId/claim", token, null, done) { mail(it.jsonObject) }

    override fun telemetry(token: String?, clientVersion: String, events: List<TelemetryEvent>, done: (CloudResult<Unit>) -> Unit) {
        val batch = JsonObject(
            mapOf(
                "clientVersion" to JsonPrimitive(clientVersion),
                "events" to JsonArray(events.map { event -> JsonObject(mapOf("name" to JsonPrimitive(event.name), "payload" to JsonObject(event.payload.mapValues { JsonPrimitive(it.value) }))) }),
            ),
        )
        send(Net.HttpMethods.POST, "/telemetry", token, batch, done) { }
    }

    private fun <T> send(method: String, path: String, token: String?, body: JsonObject?, reply: (CloudResult<T>) -> Unit, parse: (JsonElement) -> T) {
        val done: (CloudResult<T>) -> Unit = { result -> deliver { reply(result) } }
        val builder = HttpRequestBuilder().newRequest().method(method).url(baseUrl.trimEnd('/') + path).timeout(TIMEOUT_MILLIS)
        token?.let { builder.header("Authorization", "Bearer $it") }
        body?.let { builder.header("Content-Type", "application/json").content(it.toString()) }
        net.sendHttpRequest(builder.build(), object : Net.HttpResponseListener {
            override fun handleHttpResponse(response: Net.HttpResponse) {
                val status = response.status.statusCode
                val text = response.resultAsString.orEmpty()
                done(
                    try {
                        when {
                            status in 200..299 -> CloudResult.Ok(parse(if (text.isBlank()) JsonObject(emptyMap()) else Json.parseToJsonElement(text)))
                            status == CONFLICT && path.startsWith("/saves/") -> CloudResult.Conflict(meta(Json.parseToJsonElement(text).jsonObject.getValue("current").jsonObject))
                            else -> CloudResult.Rejected(status, errorMessage(text) ?: "HTTP $status")
                        }
                    } catch (malformed: IllegalArgumentException) {
                        CloudResult.Unreachable("malformed response: ${malformed.message}")
                    },
                )
            }

            override fun failed(failure: Throwable) = done(CloudResult.Unreachable(failure.message ?: failure.javaClass.simpleName))

            override fun cancelled() = done(CloudResult.Unreachable("cancelled"))
        })
    }

    private fun credentials(element: JsonElement): CloudCredentials {
        val session = element.jsonObject
        val account = session.getValue("account").jsonObject
        return CloudCredentials(session.string("token"), CloudAccount(account.string("id"), account.string("displayName"), account.string("kind")))
    }

    private fun meta(document: JsonObject) = CloudSaveMeta(document.string("slot"), document.long("revision"), document.long("updatedAt"))

    private fun mail(document: JsonObject): CloudMail {
        val grants = Json.parseToJsonElement(document.string("grants")).jsonObject.mapValues { it.value.jsonPrimitive.long }
        return CloudMail(document.string("id"), document.string("subject"), grants, document.getValue("claimed").jsonPrimitive.content.toBoolean())
    }

    private fun errorMessage(text: String): String? =
        runCatching { (Json.parseToJsonElement(text).jsonObject["error"] as? JsonPrimitive)?.content }.getOrNull()

    private fun obj(vararg pairs: Pair<String, String>) = JsonObject(pairs.associate { (key, value) -> key to JsonPrimitive(value) })

    private fun JsonObject.string(key: String): String = getValue(key).jsonPrimitive.content
    private fun JsonObject.long(key: String): Long = getValue(key).jsonPrimitive.long

    companion object {
        const val TIMEOUT_MILLIS: Int = 10_000
        const val CONFLICT: Int = 409
    }
}

class PreferencesCredentialStore(private val preferences: Preferences) : CloudCredentialStore {

    override var credentials: CloudCredentials?
        get() {
            val token = preferences.getString(TOKEN, "").ifEmpty { return null }
            return CloudCredentials(token, CloudAccount(preferences.getString(ACCOUNT_ID), preferences.getString(NAME), preferences.getString(KIND)))
        }
        set(value) {
            if (value == null) {
                preferences.remove(TOKEN)
                preferences.remove(ACCOUNT_ID)
                preferences.remove(NAME)
                preferences.remove(KIND)
                clearRevisions()
            } else {
                if (preferences.getString(ACCOUNT_ID, "") != value.account.id) clearRevisions()
                preferences.putString(TOKEN, value.token).putString(ACCOUNT_ID, value.account.id).putString(NAME, value.account.displayName).putString(KIND, value.account.kind)
            }
            preferences.flush()
        }

    private fun clearRevisions() = preferences.get().keys.filter { it.startsWith(REVISION_PREFIX) }.forEach(preferences::remove)

    override fun syncedRevision(slot: String): Long = preferences.getLong(REVISION_PREFIX + slot, 0L)

    override fun markSynced(slot: String, revision: Long) {
        preferences.putLong(REVISION_PREFIX + slot, revision).flush()
    }

    companion object {
        private const val TOKEN = "cloud.token"
        private const val ACCOUNT_ID = "cloud.account_id"
        private const val NAME = "cloud.display_name"
        private const val KIND = "cloud.kind"
        private const val REVISION_PREFIX = "cloud.revision."

        fun preferencesFor(baseUrl: String): String = "pxworld.cloud." + baseUrl.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
    }
}
