package com.pxworld.domain.stats

import com.pxworld.domain.PERMILLE

object StatFormula {

    const val LEVEL_GROWTH_PERMILLE: Int = 80
    const val MAX_STAR: Int = 5
    private val STAR_MULTIPLIERS = intArrayOf(1000, 1150, 1320, 1520, 1750, 2010)

    fun starMultiplier(star: Int): Int {
        require(star in 0..MAX_STAR) { "star must be in 0..$MAX_STAR, was $star" }
        return STAR_MULTIPLIERS[star]
    }

    fun levelMultiplier(level: Int): Int {
        require(level >= 1) { "level must be at least 1, was $level" }
        return PERMILLE + LEVEL_GROWTH_PERMILLE * (level - 1)
    }

    fun grow(base: StatBlock, level: Int, star: Int): StatBlock {
        val levelFactor = levelMultiplier(level).toLong()
        val starFactor = starMultiplier(star).toLong()
        return base.map { kind, value ->
            if (kind.growsWithLevel) (value * levelFactor / PERMILLE * starFactor / PERMILLE).toInt() else value
        }
    }

    fun finalStats(base: StatBlock, level: Int, star: Int, flatBonus: StatBlock, percentBonus: StatBlock): StatBlock {
        val withFlat = grow(base, level, star) + flatBonus
        return withFlat.map { kind, value ->
            (value.toLong() * (PERMILLE + percentBonus[kind]) / PERMILLE).toInt()
        }
    }
}
