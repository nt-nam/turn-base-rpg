package com.pxworld.domain.progression

import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.LedgerEntry
import com.pxworld.domain.economy.Wallet
import com.pxworld.domain.stats.StatFormula

enum class EquipmentSlot { WEAPON, ARMOR, JEWELRY, SUPPORT }

data class OwnedHero(
    val instanceId: String,
    val heroId: String,
    val level: Int = 1,
    val star: Int = 0,
    val experience: Long = 0,
    val locked: Boolean = false,
) {
    init {
        require(level in 1..ExperienceCurve.MAX_LEVEL) { "level must be in 1..${ExperienceCurve.MAX_LEVEL}" }
        require(star in 0..StatFormula.MAX_STAR) { "star must be in 0..${StatFormula.MAX_STAR}" }
        require(experience >= 0) { "experience must not be negative" }
    }
}

data class EquipmentInstance(
    val instanceId: String,
    val equipmentId: String,
    val slot: EquipmentSlot,
    val level: Int = 1,
    val equippedBy: String? = null,
)

data class Inventory(
    val items: Map<String, Long> = emptyMap(),
    val equipment: List<EquipmentInstance> = emptyList(),
) {
    fun quantity(itemId: String): Long = items[itemId] ?: 0L

    fun addItem(itemId: String, amount: Long): Inventory {
        require(amount > 0) { "amount must be positive" }
        return copy(items = items + (itemId to quantity(itemId) + amount))
    }

    fun removeItem(itemId: String, amount: Long): Inventory {
        require(amount > 0) { "amount must be positive" }
        val remaining = quantity(itemId) - amount
        check(remaining >= 0) { "not enough $itemId: have ${quantity(itemId)}, need $amount" }
        return copy(items = if (remaining == 0L) items - itemId else items + (itemId to remaining))
    }

    fun equipmentInstance(instanceId: String): EquipmentInstance =
        equipment.firstOrNull { it.instanceId == instanceId } ?: throw IllegalArgumentException("no equipment $instanceId")

    fun equippedOn(heroInstanceId: String): List<EquipmentInstance> = equipment.filter { it.equippedBy == heroInstanceId }
}

data class Lineup(val cells: Map<GridCell, String> = emptyMap(), val capacity: Int = 1) {
    init {
        require(capacity in 1..GridCell.GRID_SIZE * GridCell.GRID_SIZE) { "capacity must fit in the grid" }
        require(cells.size <= capacity) { "lineup holds ${cells.size} heroes but capacity is $capacity" }
        require(cells.values.toSet().size == cells.size) { "a hero may occupy only one cell" }
    }

    fun contains(heroInstanceId: String): Boolean = heroInstanceId in cells.values
}

data class PlayerProfile(
    val name: String,
    val level: Int = 1,
    val experience: Long = 0,
    val starterHeroId: String,
)

data class WorldPosition(val mapId: String, val x: Int = -1, val y: Int = -1, val spawnIndex: Int = 0)

data class PlayerSettings(
    val musicEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val locale: String = "vi",
    val textScalePercent: Int = 100,
    val reducedMotion: Boolean = false,
    val analyticsConsent: Boolean = false,
    val battleSpeed: Int = 1,
)

data class RecruitRecord(val heroId: String, val epochMillis: Long)

data class PlayerJournal(
    val visitedMaps: Set<String> = emptySet(),
    val seenEnemies: Set<String> = emptySet(),
    val seenItems: Set<String> = emptySet(),
    val recruitHistory: List<RecruitRecord> = emptyList(),
    val lineupPresets: Map<String, Map<GridCell, String>> = emptyMap(),
    val lastIdleClaimMillis: Long? = null,
    val playSeconds: Long = 0,
    val tutorialsSeen: Set<String> = emptySet(),
) {
    companion object {
        const val RECRUIT_HISTORY_SIZE: Int = 50
        const val MAX_PRESETS: Int = 5
    }
}

data class CheckinProgress(val tableId: String, val claimedDays: Int = 0, val lastClaimEpochDay: Long? = null)

data class QuestProgress(val questId: String, val progress: Int = 0, val completed: Boolean = false, val claimed: Boolean = false)

data class LifetimeStats(val counters: Map<String, Long> = emptyMap()) {
    fun value(counter: String): Long = counters[counter] ?: 0L

    fun increment(counter: String, amount: Long = 1): LifetimeStats =
        if (amount == 0L) this else copy(counters = counters + (counter to value(counter) + amount))
}

data class GameState(
    val profile: PlayerProfile,
    val wallet: Wallet = Wallet(),
    val ledgerTail: List<LedgerEntry> = emptyList(),
    val heroes: List<OwnedHero> = emptyList(),
    val lineup: Lineup = Lineup(),
    val inventory: Inventory = Inventory(),
    val quests: List<QuestProgress> = emptyList(),
    val claimedAchievementTiers: Map<String, Int> = emptyMap(),
    val checkin: CheckinProgress,
    val position: WorldPosition,
    val settings: PlayerSettings = PlayerSettings(),
    val stats: LifetimeStats = LifetimeStats(),
    val journal: PlayerJournal = PlayerJournal(),
    val nextInstanceNumber: Long = 1,
) {
    init {
        val heroIds = heroes.map { it.instanceId }
        require(heroIds.toSet().size == heroIds.size) { "duplicate hero instance id" }
        require(lineup.cells.values.all { it in heroIds }) { "lineup references a hero that is not owned" }
        inventory.equipment.forEach { equip ->
            require(equip.equippedBy == null || equip.equippedBy in heroIds) { "${equip.instanceId} is equipped by an unknown hero" }
        }
        inventory.equipment.filter { it.equippedBy != null }.groupBy { it.equippedBy to it.slot }.forEach { (key, list) ->
            require(list.size == 1) { "hero ${key.first} has more than one ${key.second} equipped" }
        }
        require(inventory.items.values.all { it > 0 }) { "item stacks must be positive" }
        require(wallet.balances.values.all { it >= 0 }) { "currency balances must not be negative" }
    }

    fun hero(instanceId: String): OwnedHero =
        heroes.firstOrNull { it.instanceId == instanceId } ?: throw IllegalArgumentException("no hero $instanceId")

    fun allocateInstanceId(prefix: String): Pair<String, GameState> =
        "$prefix-$nextInstanceNumber" to copy(nextInstanceNumber = nextInstanceNumber + 1)

    companion object {
        const val LEDGER_TAIL_SIZE: Int = 200
    }
}

object ExperienceCurve {
    const val MAX_LEVEL: Int = 60

    fun requiredForNextLevel(level: Int): Long = 100L * level

    fun addExperience(level: Int, experience: Long, gained: Long): Pair<Int, Long> {
        require(gained >= 0) { "gained experience must not be negative" }
        var currentLevel = level
        var pool = experience + gained
        while (currentLevel < MAX_LEVEL && pool >= requiredForNextLevel(currentLevel)) {
            pool -= requiredForNextLevel(currentLevel)
            currentLevel += 1
        }
        if (currentLevel == MAX_LEVEL) pool = 0
        return currentLevel to pool
    }
}
