package com.pxworld.domain.stats

enum class StatKind(val growsWithLevel: Boolean) {
    HP(true),
    ATTACK(true),
    DEFENSE(true),
    SPEED(false),
    CRIT_RATE(false),
    CRIT_DAMAGE(false),
    ACCURACY(false),
    EVASION(false),
    EFFECT_HIT(false),
    EFFECT_RESISTANCE(false),
}

class StatBlock private constructor(private val values: IntArray) {

    operator fun get(kind: StatKind): Int = values[kind.ordinal]

    fun with(kind: StatKind, value: Int): StatBlock {
        val copy = values.copyOf()
        copy[kind.ordinal] = value
        return StatBlock(copy)
    }

    operator fun plus(other: StatBlock): StatBlock =
        StatBlock(IntArray(values.size) { values[it] + other.values[it] })

    fun map(transform: (StatKind, Int) -> Int): StatBlock =
        StatBlock(IntArray(values.size) { transform(KINDS[it], values[it]) })

    fun toMap(): Map<StatKind, Int> = KINDS.associateWith { values[it.ordinal] }

    override fun equals(other: Any?): Boolean = other is StatBlock && other.values.contentEquals(values)

    override fun hashCode(): Int = values.contentHashCode()

    override fun toString(): String =
        KINDS.filter { values[it.ordinal] != 0 }.joinToString(prefix = "StatBlock(", postfix = ")") { "${it.name}=${values[it.ordinal]}" }

    companion object {
        private val KINDS = StatKind.values().toList()

        val EMPTY = StatBlock(IntArray(KINDS.size))

        fun of(vararg entries: Pair<StatKind, Int>): StatBlock =
            entries.fold(EMPTY) { block, (kind, value) -> block.with(kind, value) }
    }
}
