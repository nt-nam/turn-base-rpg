package com.pxworld.client.core

import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

class LogBuffer(private val delegate: ApplicationLogger?, private val capacity: Int = 400) : ApplicationLogger {

    private val lines = ArrayDeque<String>()

    val entries: List<String> get() = synchronized(lines) { lines.toList() }

    private fun keep(level: String, tag: String, message: String, failure: Throwable? = null) {
        synchronized(lines) {
            lines.addLast("$level $tag: $message${failure?.let { " (${it.javaClass.simpleName}: ${it.message})" } ?: ""}")
            while (lines.size > capacity) lines.removeFirst()
        }
    }

    override fun log(tag: String, message: String) { keep("I", tag, message); delegate?.log(tag, message) }
    override fun log(tag: String, message: String, exception: Throwable) { keep("I", tag, message, exception); delegate?.log(tag, message, exception) }
    override fun error(tag: String, message: String) { keep("E", tag, message); delegate?.error(tag, message) }
    override fun error(tag: String, message: String, exception: Throwable) { keep("E", tag, message, exception); delegate?.error(tag, message, exception) }
    override fun debug(tag: String, message: String) { keep("D", tag, message); delegate?.debug(tag, message) }
    override fun debug(tag: String, message: String, exception: Throwable) { keep("D", tag, message, exception); delegate?.debug(tag, message, exception) }
}

class DebugFlags {
    var showPerformance: Boolean = false
    var showColliders: Boolean = false
    var fastBattles: Boolean = false
}

class AppPreferences(private val store: Preferences) {

    var legalAccepted: Boolean
        get() = store.getBoolean(LEGAL, false)
        set(value) = store.putBoolean(LEGAL, value).flush()

    var privacyAnswered: Boolean
        get() = store.getBoolean(PRIVACY, false)
        set(value) = store.putBoolean(PRIVACY, value).flush()

    var analyticsConsent: Boolean
        get() = store.getBoolean(ANALYTICS, false)
        set(value) = store.putBoolean(ANALYTICS, value).flush()

    var locale: String?
        get() = store.getString(LOCALE, "").ifEmpty { null }
        set(value) = store.putString(LOCALE, value ?: "").flush()

    companion object {
        private const val LEGAL = "legal_accepted_v1"
        private const val PRIVACY = "privacy_answered_v1"
        private const val ANALYTICS = "analytics_consent"
        private const val LOCALE = "locale"

        fun open(name: String): AppPreferences = AppPreferences(Gdx.app.getPreferences(name))
    }
}
