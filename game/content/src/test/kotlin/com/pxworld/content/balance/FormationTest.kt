package com.pxworld.content.balance

import com.pxworld.domain.battle.GridCell
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class FormationTest {

    @Test
    fun `each class takes its preferred cell`() {
        val classes = listOf("class.tank", "class.warrior", "class.assassin", "class.ranger", "class.mage", "class.support")
        assertEquals(
            listOf(GridCell(1, 0), GridCell(0, 0), GridCell(2, 0), GridCell(0, 1), GridCell(1, 2), GridCell(2, 2)),
            Formation.place(classes),
        )
    }

    @Test
    fun `a second hero of the same class moves to the nearest free cell of the same depth`() {
        assertEquals(listOf(GridCell(1, 2), GridCell(0, 2), GridCell(2, 2)), Formation.place(listOf("class.mage", "class.mage", "class.mage")))
    }

    @Test
    fun `fixed cells are honoured before automatic placement`() {
        val cells = Formation.place(listOf("class.warrior", "class.tank"), fixed = mapOf(1 to GridCell(0, 0)))
        assertEquals(listOf(GridCell(1, 0), GridCell(0, 0)), cells)
    }

    @Test
    fun `a full grid places nine heroes on distinct cells and rejects a tenth`() {
        assertEquals(Formation.CAPACITY, Formation.place(List(Formation.CAPACITY) { "class.ranger" }).toSet().size)
        assertThrows<IllegalArgumentException> { Formation.place(List(Formation.CAPACITY + 1) { "class.ranger" }) }
    }
}
