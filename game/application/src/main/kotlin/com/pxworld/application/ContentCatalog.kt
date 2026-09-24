package com.pxworld.application

import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.stats.StatBlock

enum class GrantKind { CURRENCY, ITEM, EQUIPMENT, HERO }

data class Grant(val kind: GrantKind, val id: String, val quantity: Long)

data class Price(val currency: String, val amount: Long)

data class QuestSummary(val id: String, val objectiveKind: String, val target: String?, val count: Int, val rewards: List<Grant>)

data class AchievementTier(val target: Long, val rewards: List<Grant>)

data class AchievementSummary(val id: String, val counter: String, val tiers: List<AchievementTier>)

data class EncounterSummary(val id: String, val highestEnemyLevel: Int, val enemyCount: Int, val rewards: List<Grant>)

interface ContentCatalog {
    fun heroExists(heroId: String): Boolean
    fun heroBaseStats(heroId: String): StatBlock
    fun recruitableHeroes(): List<String>
    fun recruitPrice(): Price
    fun itemExperience(itemId: String): Long?
    fun itemPrice(itemId: String): Price?
    fun equipmentSlot(equipmentId: String): EquipmentSlot
    fun equipmentStats(equipmentId: String): StatBlock
    fun equipmentPrice(equipmentId: String): Price?
    fun encounter(encounterId: String): EncounterSummary
    fun checkinRewards(tableId: String, day: Int): List<Grant>?
    fun checkinLength(tableId: String): Int
    fun itemCategory(itemId: String): String?
    fun quests(): List<QuestSummary>
    fun achievements(): List<AchievementSummary>
    fun startingGrants(): List<Grant>
    fun starterHeroes(): List<String>
    fun startingMap(): String
    fun defaultCheckinTable(): String
}
