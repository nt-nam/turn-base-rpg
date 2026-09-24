package com.pxworld.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyRecord(val id: String, val name: String, val description: String, val icon: String)

@Serializable
data class HeroClassRecord(val id: String, val name: String, val role: String, val counters: List<String>)

@Serializable
enum class StatusKindName {
    @SerialName("stun") STUN,
    @SerialName("silence") SILENCE,
    @SerialName("taunt") TAUNT,
    @SerialName("shield") SHIELD,
    @SerialName("damage_over_time") DAMAGE_OVER_TIME,
    @SerialName("heal_over_time") HEAL_OVER_TIME,
    @SerialName("stat_modifier") STAT_MODIFIER,
    @SerialName("stat_bonus") STAT_BONUS,
}

@Serializable
data class StatusRecord(
    val id: String,
    val name: String,
    val kind: StatusKindName,
    val stat: String? = null,
    val magnitude: Int = 0,
    val durationTurns: Int,
    val maxStacks: Int = 1,
    val dispellable: Boolean = true,
)

@Serializable
enum class SkillSlotName {
    @SerialName("basic") BASIC,
    @SerialName("skill") SKILL,
    @SerialName("ultimate") ULTIMATE,
}

@Serializable
enum class TargetingKindName {
    @SerialName("single_enemy") SINGLE_ENEMY,
    @SerialName("enemy_row") ENEMY_ROW,
    @SerialName("enemy_lane") ENEMY_LANE,
    @SerialName("all_enemies") ALL_ENEMIES,
    @SerialName("single_ally") SINGLE_ALLY,
    @SerialName("all_allies") ALL_ALLIES,
    @SerialName("self") SELF,
    @SerialName("lowest_hp_allies") LOWEST_HP_ALLIES,
    @SerialName("random_enemies") RANDOM_ENEMIES,
}

@Serializable
data class TargetingRecord(val kind: TargetingKindName, val count: Int? = null)

@Serializable
enum class EffectKindName {
    @SerialName("damage") DAMAGE,
    @SerialName("heal") HEAL,
    @SerialName("apply_status") APPLY_STATUS,
    @SerialName("gain_energy") GAIN_ENERGY,
}

@Serializable
data class EffectRecord(
    val kind: EffectKindName,
    val power: Int? = null,
    val hits: Int? = null,
    val status: String? = null,
    val chance: Int? = null,
    val onSelf: Boolean? = null,
    val amount: Int? = null,
)

@Serializable
data class SkillRecord(
    val id: String,
    val name: String,
    val description: String,
    val slot: SkillSlotName,
    val energyCost: Int,
    val cooldownTurns: Int,
    val targeting: TargetingRecord,
    val effects: List<EffectRecord>,
    val vfx: String,
)

@Serializable
data class StatsRecord(
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val critRate: Int = 0,
    val critDamage: Int = 1500,
    val accuracy: Int = 0,
    val evasion: Int = 0,
    val effectHit: Int = 0,
    val effectResistance: Int = 0,
)

@Serializable
data class HeroRecord(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val classId: String,
    val baseStats: StatsRecord,
    val skills: List<String>,
    val sprite: String,
    val starter: Boolean = false,
)

@Serializable
data class EnemyRecord(
    val id: String,
    val name: String,
    val classId: String,
    val baseStats: StatsRecord,
    val skills: List<String>,
    val sprite: String,
    val tags: List<String> = emptyList(),
)

@Serializable
data class MapRecord(
    val id: String,
    val name: String,
    val region: String,
    val asset: String,
    val legacyName: String,
    val recommendedLevel: Int,
    val battleBackground: String? = null,
)

@Serializable
data class CellRecord(val lane: Int, val depth: Int)

@Serializable
data class EncounterEnemyRecord(val enemy: String, val level: Int, val star: Int, val cell: CellRecord)

@Serializable
enum class RewardKindName {
    @SerialName("currency") CURRENCY,
    @SerialName("item") ITEM,
    @SerialName("equipment") EQUIPMENT,
    @SerialName("hero") HERO,
}

@Serializable
data class RewardRecord(val kind: RewardKindName, val id: String, val quantity: Int)

@Serializable
data class EncounterRecord(
    val id: String,
    val name: String,
    val map: String,
    val mapObjectId: Int,
    val recommendedLevel: Int,
    val enemies: List<EncounterEnemyRecord>,
    val rewards: List<RewardRecord>,
)

@Serializable
data class ShopListingRecord(val currency: String, val price: Int, val listed: Boolean)

@Serializable
data class ItemUseRecord(val kind: String, val amount: Int)

@Serializable
data class ItemRecord(
    val id: String,
    val name: String,
    val category: String,
    val tier: Int,
    val icon: String,
    val shop: ShopListingRecord? = null,
    val use: ItemUseRecord? = null,
    val materialValue: Int? = null,
)

@Serializable
data class EquipmentRecord(
    val id: String,
    val name: String,
    val slot: String,
    val icon: String,
    val stats: Map<String, Int>,
    val shop: ShopListingRecord,
)

@Serializable
data class ObjectiveRecord(val kind: String, val target: String? = null, val count: Int)

@Serializable
data class QuestRecord(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val objective: ObjectiveRecord,
    val rewards: List<RewardRecord>,
    val requires: String? = null,
)

@Serializable
data class NpcPlacementRecord(val map: String, @SerialName("object") val objectName: String)

@Serializable
data class NpcDialogueRuleRecord(val dialogue: String, val whenQuestActive: String? = null)

@Serializable
data class NpcRecord(
    val id: String,
    val name: String,
    val sprite: String,
    val placements: List<NpcPlacementRecord>,
    val dialogues: List<NpcDialogueRuleRecord>,
)

@Serializable
data class DialogueChoiceRecord(val text: String, val next: String? = null, val action: String? = null)

@Serializable
data class DialogueNodeRecord(
    val id: String,
    val speaker: String,
    val text: String,
    val next: String? = null,
    val choices: List<DialogueChoiceRecord> = emptyList(),
)

@Serializable
data class DialogueRecord(val id: String, val start: String, val nodes: List<DialogueNodeRecord>)

@Serializable
data class AchievementTierRecord(val target: Int, val rewards: List<RewardRecord>)

@Serializable
data class AchievementRecord(
    val id: String,
    val name: String,
    val description: String,
    val counter: String,
    val tiers: List<AchievementTierRecord>,
)

@Serializable
data class CheckinDayRecord(val day: Int, val rewards: List<RewardRecord>)

@Serializable
data class CheckinTableRecord(val id: String, val name: String, val days: List<CheckinDayRecord>)

@Serializable
data class BattleRulesRecord(
    val id: String,
    val maxRounds: Int,
    val timeUnitsPerRound: Int,
    val referenceSpeed: Int,
    val startingEnergy: Int,
    val maxEnergy: Int,
    val energyPerAction: Int,
    val energyWhenHit: Int,
    val baseHitPermille: Int,
    val minimumHitPermille: Int,
    val criticalRateCapPermille: Int,
    val damageVariancePermille: Int,
    val defenseConstantBase: Int,
    val defenseConstantPerLevel: Int,
    val damageTakenByDepthPermille: List<Int>,
    val matchupAdvantagePermille: Int,
    val matchupDisadvantagePermille: Int,
)

@Serializable
data class AudioCueRecord(val id: String, val kind: String, val asset: String, val volumePercent: Int = 100)

@Serializable
data class ContentBundle(
    val currencies: List<CurrencyRecord>,
    val heroClasses: List<HeroClassRecord>,
    val statuses: List<StatusRecord>,
    val skills: List<SkillRecord>,
    val heroes: List<HeroRecord>,
    val enemies: List<EnemyRecord>,
    val encounters: List<EncounterRecord>,
    val items: List<ItemRecord>,
    val equipment: List<EquipmentRecord>,
    val quests: List<QuestRecord>,
    val achievements: List<AchievementRecord>,
    val checkinTables: List<CheckinTableRecord>,
    val battleRules: List<BattleRulesRecord>,
    val maps: List<MapRecord> = emptyList(),
    val npcs: List<NpcRecord> = emptyList(),
    val dialogues: List<DialogueRecord> = emptyList(),
    val audioCues: List<AudioCueRecord> = emptyList(),
    val localization: Map<String, Map<String, String>>,
    val assetMap: Map<String, String>,
) {
    fun allIds(): List<String> =
        currencies.map { it.id } + heroClasses.map { it.id } + statuses.map { it.id } + skills.map { it.id } +
            heroes.map { it.id } + enemies.map { it.id } + encounters.map { it.id } + items.map { it.id } +
            equipment.map { it.id } + quests.map { it.id } + achievements.map { it.id } + checkinTables.map { it.id } +
            battleRules.map { it.id } + maps.map { it.id } + npcs.map { it.id } + dialogues.map { it.id } + audioCues.map { it.id }
}
