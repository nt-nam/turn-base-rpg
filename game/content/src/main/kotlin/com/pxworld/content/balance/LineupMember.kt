package com.pxworld.content.balance

import com.pxworld.content.ContentBundle
import com.pxworld.content.ContentBundleCatalog
import com.pxworld.content.LineupSlot
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.stats.StatBlock

data class LineupMember(val heroId: String, val level: Int, val star: Int, val cell: GridCell, val equipment: List<String> = emptyList())

class LineupAssembler(bundle: ContentBundle) {

    private val catalog = ContentBundleCatalog(bundle)

    fun slots(members: List<LineupMember>): List<LineupSlot> =
        members.map { LineupSlot(it.heroId, it.level, it.star, it.cell, equipmentBonus(it.equipment)) }

    fun equipmentBonus(equipment: List<String>): StatBlock =
        equipment.fold(StatBlock.EMPTY) { total, equipmentId -> total + catalog.equipmentStats(equipmentId) }
}
