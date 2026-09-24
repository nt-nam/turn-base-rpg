package com.pxworld.infrastructure.save

import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.LedgerEntry
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.economy.Wallet
import com.pxworld.domain.progression.CheckinProgress
import com.pxworld.domain.progression.EquipmentInstance
import com.pxworld.domain.progression.EquipmentSlot
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Inventory
import com.pxworld.domain.progression.LifetimeStats
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.progression.PlayerProfile
import com.pxworld.domain.progression.PlayerSettings
import com.pxworld.domain.progression.QuestProgress
import com.pxworld.domain.progression.WorldPosition
import kotlinx.serialization.Serializable

@Serializable
data class SaveEnvelope(val schemaVersion: Int, val checksum: String, val state: SaveGameDocument)

@Serializable
data class SaveGameDocument(
    val profile: ProfileDocument,
    val wallet: WalletDocument,
    val ledgerTail: List<LedgerEntryDocument>,
    val heroes: List<HeroDocument>,
    val lineup: LineupDocument,
    val items: Map<String, Long>,
    val equipment: List<EquipmentDocument>,
    val quests: List<QuestDocument>,
    val claimedAchievementTiers: Map<String, Int>,
    val checkin: CheckinDocument,
    val position: PositionDocument,
    val settings: SettingsDocument,
    val counters: Map<String, Long>,
    val nextInstanceNumber: Long,
)

@Serializable
data class ProfileDocument(val name: String, val level: Int, val experience: Long, val starterHeroId: String)

@Serializable
data class WalletDocument(val balances: Map<String, Long>, val nextSequence: Long)

@Serializable
data class LedgerEntryDocument(val sequence: Long, val currency: String, val delta: Long, val balanceAfter: Long, val reasonKind: String, val reasonReference: String)

@Serializable
data class HeroDocument(val instanceId: String, val heroId: String, val level: Int, val star: Int, val experience: Long, val locked: Boolean)

@Serializable
data class LineupDocument(val capacity: Int, val cells: Map<String, String>)

@Serializable
data class EquipmentDocument(val instanceId: String, val equipmentId: String, val slot: String, val level: Int, val equippedBy: String? = null)

@Serializable
data class QuestDocument(val questId: String, val progress: Int, val completed: Boolean, val claimed: Boolean)

@Serializable
data class CheckinDocument(val tableId: String, val claimedDays: Int, val lastClaimEpochDay: Long? = null)

@Serializable
data class PositionDocument(val mapId: String, val x: Int, val y: Int, val spawnIndex: Int)

@Serializable
data class SettingsDocument(val musicEnabled: Boolean, val soundEnabled: Boolean, val locale: String)

object SaveGameMapper {

    fun toDocument(state: GameState): SaveGameDocument = SaveGameDocument(
        profile = ProfileDocument(state.profile.name, state.profile.level, state.profile.experience, state.profile.starterHeroId),
        wallet = WalletDocument(state.wallet.balances.toSortedMap(), state.wallet.nextSequence),
        ledgerTail = state.ledgerTail.map {
            LedgerEntryDocument(it.sequence, it.currency, it.delta, it.balanceAfter, it.reason.kind, it.reason.reference)
        },
        heroes = state.heroes.map { HeroDocument(it.instanceId, it.heroId, it.level, it.star, it.experience, it.locked) },
        lineup = LineupDocument(
            capacity = state.lineup.capacity,
            cells = state.lineup.cells.entries
                .sortedWith(compareBy({ it.key.depth }, { it.key.lane }))
                .associate { (cell, hero) -> "${cell.lane},${cell.depth}" to hero },
        ),
        items = state.inventory.items.toSortedMap(),
        equipment = state.inventory.equipment.map { EquipmentDocument(it.instanceId, it.equipmentId, it.slot.name.lowercase(), it.level, it.equippedBy) },
        quests = state.quests.map { QuestDocument(it.questId, it.progress, it.completed, it.claimed) },
        claimedAchievementTiers = state.claimedAchievementTiers.toSortedMap(),
        checkin = CheckinDocument(state.checkin.tableId, state.checkin.claimedDays, state.checkin.lastClaimEpochDay),
        position = PositionDocument(state.position.mapId, state.position.x, state.position.y, state.position.spawnIndex),
        settings = SettingsDocument(state.settings.musicEnabled, state.settings.soundEnabled, state.settings.locale),
        counters = state.stats.counters.toSortedMap(),
        nextInstanceNumber = state.nextInstanceNumber,
    )

    fun toState(document: SaveGameDocument): GameState = GameState(
        profile = PlayerProfile(document.profile.name, document.profile.level, document.profile.experience, document.profile.starterHeroId),
        wallet = Wallet(document.wallet.balances, document.wallet.nextSequence),
        ledgerTail = document.ledgerTail.map {
            LedgerEntry(it.sequence, it.currency, it.delta, it.balanceAfter, LedgerReason(it.reasonKind, it.reasonReference))
        },
        heroes = document.heroes.map { OwnedHero(it.instanceId, it.heroId, it.level, it.star, it.experience, it.locked) },
        lineup = Lineup(
            cells = document.lineup.cells.entries.associate { (key, hero) ->
                val (lane, depth) = key.split(",").map(String::toInt)
                GridCell(lane, depth) to hero
            },
            capacity = document.lineup.capacity,
        ),
        inventory = Inventory(
            items = document.items,
            equipment = document.equipment.map {
                EquipmentInstance(it.instanceId, it.equipmentId, EquipmentSlot.valueOf(it.slot.uppercase()), it.level, it.equippedBy)
            },
        ),
        quests = document.quests.map { QuestProgress(it.questId, it.progress, it.completed, it.claimed) },
        claimedAchievementTiers = document.claimedAchievementTiers,
        checkin = CheckinProgress(document.checkin.tableId, document.checkin.claimedDays, document.checkin.lastClaimEpochDay),
        position = WorldPosition(document.position.mapId, document.position.x, document.position.y, document.position.spawnIndex),
        settings = PlayerSettings(document.settings.musicEnabled, document.settings.soundEnabled, document.settings.locale),
        stats = LifetimeStats(document.counters),
        nextInstanceNumber = document.nextInstanceNumber,
    )
}
