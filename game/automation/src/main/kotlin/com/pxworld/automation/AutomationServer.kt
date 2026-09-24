package com.pxworld.automation

import com.pxworld.client.automation.StageAutomationDriver
import com.pxworld.client.automation.UiNode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class AutomationProtocol(private val driver: () -> StageAutomationDriver?) {

    fun handle(request: String): String {
        val message = try {
            Json.parseToJsonElement(request).jsonObject
        } catch (failure: IllegalArgumentException) {
            return error(JsonNull, "parse error: ${failure.message}")
        }
        val id = message["id"] ?: JsonNull
        val method = message["method"]?.jsonPrimitive?.contentOrNull ?: return error(id, "missing method")
        val params = message["params"] as? JsonObject ?: JsonObject(emptyMap())
        val automation = driver() ?: return error(id, "game is still starting")
        return try {
            result(id, dispatch(automation, method, params))
        } catch (failure: Exception) {
            error(id, "${failure.javaClass.simpleName}: ${failure.message}")
        }
    }

    private fun dispatch(automation: StageAutomationDriver, method: String, params: JsonObject): Any? {
        fun text(key: String): String = params[key]?.jsonPrimitive?.contentOrNull ?: throw IllegalArgumentException("missing param $key")
        fun optionalText(key: String): String? = (params[key] as? JsonPrimitive)?.contentOrNull
        fun number(key: String): Float = params[key]?.jsonPrimitive?.floatOrNull ?: throw IllegalArgumentException("missing number $key")
        return when (method) {
            "session.info" -> automation.session()
            "app.exit" -> automation.exit()
            "app.resetFirstRun" -> automation.resetFirstRun()
            "replays.list" -> automation.replays()
            "screen.registered" -> automation.registeredScreens()
            "screen.tree" -> automation.tree()
            "screen.open" -> automation.open(text("screenId"), (params["args"] as? JsonObject)?.mapValues { it.value.jsonPrimitive.content }.orEmpty())
            "screen.reset" -> automation.reset(text("screenId"))
            "screen.back" -> automation.back()
            "ui.tap" -> automation.tap(text("testId"))
            "ui.type" -> automation.type(text("testId"), text("text"))
            "world.info" -> automation.worldInfo()
            "world.moveTo" -> automation.moveTo(number("x"), number("y"))
            "battle.state" -> automation.battleInfo()
            "battle.command" -> automation.battleCommand(text("skill"), optionalText("target"))
            "battle.auto" -> automation.battleAuto(params["enabled"]?.jsonPrimitive?.booleanOrNull ?: true)
            "state.get" -> automation.gameState()
            "invariants.check" -> automation.invariants()
            "capture.screenshot" -> automation.screenshot(text("path"))
            else -> throw IllegalArgumentException("unknown method $method")
        }
    }

    private fun result(id: JsonElement, value: Any?): String = buildJsonObject {
        put("jsonrpc", "2.0")
        put("id", id)
        put("result", toJson(value))
    }.toString()

    private fun error(id: JsonElement, message: String): String = buildJsonObject {
        put("jsonrpc", "2.0")
        put("id", id)
        put("error", buildJsonObject { put("message", message) })
    }.toString()

    companion object {
        fun toJson(value: Any?): JsonElement = when (value) {
            null -> JsonNull
            is JsonElement -> value
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> JsonObject(value.entries.associate { (key, entry) -> key.toString() to toJson(entry) })
            is Iterable<*> -> JsonArray(value.map(::toJson))
            is UiNode -> toJson(
                mapOf(
                    "testId" to value.testId, "type" to value.type, "text" to value.text, "x" to value.x, "y" to value.y,
                    "width" to value.width, "height" to value.height, "enabled" to value.enabled,
                ),
            )
            else -> JsonPrimitive(value.toString())
        }
    }
}

class AutomationServer(port: Int, private val protocol: AutomationProtocol) : WebSocketServer(InetSocketAddress("127.0.0.1", port)) {

    init {
        isReuseAddr = true
    }

    override fun onOpen(connection: WebSocket, handshake: ClientHandshake) {}

    override fun onClose(connection: WebSocket, code: Int, reason: String, remote: Boolean) {}

    override fun onMessage(connection: WebSocket, message: String) {
        connection.send(protocol.handle(message))
    }

    override fun onError(connection: WebSocket?, failure: Exception) {
        System.err.println("automation server error: ${failure.message}")
    }

    override fun onStart() {
        println("automation server listening on ${address.hostString}:${address.port}")
    }
}
