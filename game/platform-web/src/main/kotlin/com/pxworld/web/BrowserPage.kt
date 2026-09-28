package com.pxworld.web

import org.teavm.jso.JSBody
import org.teavm.jso.JSFunctor
import org.teavm.jso.JSObject

@JSFunctor
fun interface TextQuery : JSObject {
    fun answer(): String
}

@JSBody(params = ["name"], script = "return new URLSearchParams(window.location.search).get(name) || '';")
external fun queryParameter(name: String): String

@JSBody(script = "return new Date().getTimezoneOffset();")
external fun timezoneOffsetMinutes(): Int

@JSBody(
    params = ["screen", "stack", "nodes"],
    script = "window.pxworld = { screen: function() { return screen(); }, stack: function() { return JSON.parse(stack()); }, nodes: function() { return JSON.parse(nodes()); } };",
)
external fun publishAutomation(screen: TextQuery, stack: TextQuery, nodes: TextQuery)
