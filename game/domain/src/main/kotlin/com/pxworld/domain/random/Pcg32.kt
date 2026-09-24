package com.pxworld.domain.random

class Pcg32 private constructor(val state: Long, val increment: Long) {

    fun next(): RandomDraw {
        val advanced = state * MULTIPLIER + increment
        val xorShifted = (((state ushr 18) xor state) ushr 27).toInt()
        val rotation = (state ushr 59).toInt()
        val value = (xorShifted ushr rotation) or (xorShifted shl ((-rotation) and 31))
        return RandomDraw(value, Pcg32(advanced, increment))
    }

    fun nextBelow(bound: Int): RandomDraw {
        require(bound > 0) { "bound must be positive, was $bound" }
        val draw = next()
        val unsigned = draw.value.toLong() and UNSIGNED_MASK
        return RandomDraw(((unsigned * bound) ushr 32).toInt(), draw.generator)
    }

    override fun equals(other: Any?): Boolean =
        other is Pcg32 && other.state == state && other.increment == increment

    override fun hashCode(): Int = (state xor increment).hashCode()

    override fun toString(): String = "Pcg32(state=$state, increment=$increment)"

    companion object {
        private const val MULTIPLIER = 6364136223846793005L
        private const val UNSIGNED_MASK = 0xFFFFFFFFL
        private const val DEFAULT_STREAM = 1442695040888963407L

        fun seeded(seed: Long, stream: Long = DEFAULT_STREAM): Pcg32 {
            val increment = (stream shl 1) or 1L
            val primed = Pcg32(0L, increment).next().generator
            val mixed = Pcg32(primed.state + seed, increment)
            return mixed.next().generator
        }
    }
}

data class RandomDraw(val value: Int, val generator: Pcg32)

class RandomCursor(private var generator: Pcg32) {

    val current: Pcg32 get() = generator

    fun below(bound: Int): Int {
        val draw = generator.nextBelow(bound)
        generator = draw.generator
        return draw.value
    }

    fun permilleRoll(): Int = below(1000)
}
