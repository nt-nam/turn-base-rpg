package com.pxworld.client.core

class Localization(private val tables: Map<String, Map<String, String>>, locale: String) {

    var locale: String = locale
        private set

    val availableLocales: List<String> get() = tables.keys.sorted()

    fun switchTo(newLocale: String) {
        require(newLocale in tables) { "unknown locale $newLocale" }
        locale = newLocale
    }

    operator fun invoke(key: String, vararg arguments: Any): String {
        val template = tables[locale]?.get(key) ?: tables[FALLBACK]?.get(key) ?: key
        return arguments.foldIndexed(template) { index, text, argument -> text.replace("{$index}", argument.toString()) }
    }

    fun has(key: String): Boolean = tables[locale]?.containsKey(key) == true || tables[FALLBACK]?.containsKey(key) == true

    companion object {
        const val FALLBACK: String = "vi"
    }
}
