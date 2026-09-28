package com.pxworld.web

import com.pxworld.client.GameApp
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class BrowserAutomation(private val app: GameApp) {

    fun publish() {
        publishAutomation(
            screen = TextQuery { app.context.navigator.current?.id?.id.orEmpty() },
            stack = TextQuery { JsonArray(app.context.navigator.stackIds.map(::JsonPrimitive)).toString() },
            nodes = TextQuery { nodes() },
        )
    }

    private fun nodes(): String {
        val driver = app.automation
        return JsonArray(
            driver.visibleNodes().map { node ->
                val center = driver.screenCenter(node)
                JsonObject(
                    mapOf(
                        "testId" to JsonPrimitive(node.testId),
                        "type" to JsonPrimitive(node.type),
                        "text" to JsonPrimitive(node.text),
                        "x" to JsonPrimitive(center.x),
                        "y" to JsonPrimitive(center.y),
                        "enabled" to JsonPrimitive(node.enabled),
                    ),
                )
            },
        ).toString()
    }
}
