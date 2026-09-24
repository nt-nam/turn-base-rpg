package com.pxworld.domain.stats

import com.pxworld.domain.random.Pcg32
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class StatFormulaTest {

    private val base = StatBlock.of(StatKind.HP to 1000, StatKind.ATTACK to 100, StatKind.SPEED to 110, StatKind.CRIT_RATE to 150)

    @Test
    fun `level one star zero keeps base stats`() {
        assertEquals(base, StatFormula.grow(base, level = 1, star = 0))
    }

    @Test
    fun `level growth is eight percent of base per level`() {
        val grown = StatFormula.grow(base, level = 11, star = 0)
        assertEquals(1800, grown[StatKind.HP])
        assertEquals(180, grown[StatKind.ATTACK])
    }

    @Test
    fun `star multiplier stacks on top of level growth`() {
        val grown = StatFormula.grow(base, level = 11, star = 5)
        assertEquals(3618, grown[StatKind.HP])
    }

    @Test
    fun `rate stats and speed do not grow with level`() {
        val grown = StatFormula.grow(base, level = 50, star = 5)
        assertEquals(110, grown[StatKind.SPEED])
        assertEquals(150, grown[StatKind.CRIT_RATE])
    }

    @Test
    fun `flat then percent bonuses apply after growth`() {
        val result = StatFormula.finalStats(
            base, level = 1, star = 0,
            flatBonus = StatBlock.of(StatKind.ATTACK to 50),
            percentBonus = StatBlock.of(StatKind.ATTACK to 200),
        )
        assertEquals(180, result[StatKind.ATTACK])
    }

    @Test
    fun `invalid star and level are rejected`() {
        assertThrows<IllegalArgumentException> { StatFormula.grow(base, level = 1, star = 6) }
        assertThrows<IllegalArgumentException> { StatFormula.grow(base, level = 0, star = 0) }
    }
}

class Pcg32Test {

    @Test
    fun `same seed yields same sequence`() {
        fun sequence(seed: Long): List<Int> {
            var generator = Pcg32.seeded(seed)
            return List(20) { generator.next().also { generator = it.generator }.value }
        }
        assertEquals(sequence(123), sequence(123))
        assertNotEquals(sequence(123), sequence(124))
    }

    @Test
    fun `bounded draws stay inside bound and cover it`() {
        var generator = Pcg32.seeded(99)
        val seen = mutableSetOf<Int>()
        repeat(10_000) {
            val draw = generator.nextBelow(10)
            generator = draw.generator
            assertTrue(draw.value in 0 until 10)
            seen += draw.value
        }
        assertEquals((0 until 10).toSet(), seen)
    }

    @Test
    fun `first values are pinned so every platform must agree`() {
        var generator = Pcg32.seeded(42)
        val values = List(4) { generator.next().also { generator = it.generator }.value }
        assertEquals(PINNED_SEED_42, values)
    }

    private companion object {
        val PINNED_SEED_42 = listOf(492690617, 1919685028, -732973376, 683038915)
    }
}
