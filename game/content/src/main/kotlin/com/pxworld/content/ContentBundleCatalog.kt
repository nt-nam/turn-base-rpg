package com.pxworld.content

import com.pxworld.application.AchievementSummary
import com.pxworld.application.AchievementTier
import com.pxworld.application.ContentCatalog
import com.pxworld.application.Currencies
import com.pxworld.application.EncounterSummary
import com.pxworld.application.Grant
import com.pxworld.application.GrantKind
import com.pxworld.application.Price
import com.pxworld.application.QuestSummary
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.stats.StatBlock

class ContentBundleCatalog(private val bundle: ContentBundle) : ContentCatalog {

    private val heroes = bundle.heroes.associateBy { it.id }
    private val items = bundle.items.associateBy { it.id }
    private val equipment = bundle.equipment.associateBy { it.id }
    private val encounters = bundle.encounters.associateBy { it.id }
    private val checkinTables = bundle.checkinTables.associateBy { it.id }
    private val enemies = bundle.enemies.associateBy { it.id }

    val itemsByIcon: Map<String, String> = bundle.items.associate { it.icon to it.id }
    val heroesByClass: Map<String, String> = bundle.heroes.associate { it.classId to it.id }
    val starterHeroId: String = bundle.heroes.first { it.starter }.id

    override fun heroExists(heroId: String): Boolean = heroId in heroes

    override fun heroBaseStats(heroId: String): StatBlock =
        BattleContentAssembler.statBlock(requireNotNull(heroes[heroId]) { "unknown hero $heroId" }.baseStats)

    override fun recruitableHeroes(): List<String> = bundle.heroes.map { it.id }

    override fun recruitPrice(): Price = RECRUIT_PRICE

    override fun itemExperience(itemId: String): Long? =
        items[itemId]?.use?.takeIf { it.kind == "hero_exp" }?.amount?.toLong()

    override fun itemPrice(itemId: String): Price? =
        items[itemId]?.shop?.takeIf { it.listed }?.let { Price(it.currency, it.price.toLong()) }

    override fun equipmentSlot(equipmentId: String): EquipmentSlot =
        EquipmentSlot.valueOf(requireNotNull(equipment[equipmentId]) { "unknown equipment $equipmentId" }.slot.uppercase())

    override fun equipmentStats(equipmentId: String): StatBlock =
        requireNotNull(equipment[equipmentId]) { "unknown equipment $equipmentId" }.stats.entries
            .fold(StatBlock.EMPTY) { block, (stat, value) -> block.with(BattleContentAssembler.statKind(stat), value) }

    override fun equipmentPrice(equipmentId: String): Price? =
        equipment[equipmentId]?.shop?.takeIf { it.listed }?.let { Price(it.currency, it.price.toLong()) }

    override fun encounter(encounterId: String): EncounterSummary {
        val record = requireNotNull(encounters[encounterId]) { "unknown encounter $encounterId" }
        return EncounterSummary(
            id = record.id,
            highestEnemyLevel = record.enemies.maxOf { it.level },
            enemyCount = record.enemies.size,
            rewards = record.rewards.map(::grant),
        )
    }

    override fun checkinRewards(tableId: String, day: Int): List<Grant>? =
        checkinTables[tableId]?.days?.firstOrNull { it.day == day }?.rewards?.map(::grant)

    override fun checkinLength(tableId: String): Int = requireNotNull(checkinTables[tableId]) { "unknown check-in table $tableId" }.days.size

    override fun itemCategory(itemId: String): String? = items[itemId]?.category

    override fun quests(): List<QuestSummary> = bundle.quests.map { quest ->
        QuestSummary(quest.id, quest.objective.kind, quest.objective.target, quest.objective.count, quest.rewards.map(::grant))
    }

    override fun achievements(): List<AchievementSummary> = bundle.achievements.map { achievement ->
        AchievementSummary(achievement.id, achievement.counter, achievement.tiers.map { AchievementTier(it.target.toLong(), it.rewards.map(::grant)) })
    }

    override fun startingGrants(): List<Grant> = STARTING_GRANTS

    override fun starterHeroes(): List<String> = bundle.heroes.filter { it.starter }.map { it.id }

    override fun startingMap(): String = STARTING_MAP

    override fun defaultCheckinTable(): String = bundle.checkinTables.first().id

    fun enemyExists(enemyId: String): Boolean = enemyId in enemies

    private fun grant(record: RewardRecord): Grant = Grant(
        kind = when (record.kind) {
            RewardKindName.CURRENCY -> GrantKind.CURRENCY
            RewardKindName.ITEM -> GrantKind.ITEM
            RewardKindName.EQUIPMENT -> GrantKind.EQUIPMENT
            RewardKindName.HERO -> GrantKind.HERO
        },
        id = record.id,
        quantity = record.quantity.toLong(),
    )

    companion object {
        val RECRUIT_PRICE: Price = Price(Currencies.GEM, 5)
        const val STARTING_MAP: String = "map.dawnvillage_01"
        val STARTING_GRANTS: List<Grant> = listOf(Grant(GrantKind.CURRENCY, Currencies.GOLD, 300), Grant(GrantKind.CURRENCY, Currencies.GEM, 20))
    }
}
