package com.pxworld.infrastructure.legacy

import com.pxworld.application.Counters
import com.pxworld.application.Currencies
import com.pxworld.content.ContentBundle
import com.pxworld.content.ContentBundleCatalog
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.economy.LedgerEntry
import com.pxworld.domain.economy.LedgerReason
import com.pxworld.domain.economy.Wallet
import com.pxworld.domain.progression.CheckinProgress
import com.pxworld.domain.progression.EquipmentInstance
import com.pxworld.domain.progression.ExperienceCurve
import com.pxworld.domain.progression.GameState
import com.pxworld.domain.progression.Inventory
import com.pxworld.domain.progression.LifetimeStats
import com.pxworld.domain.progression.Lineup
import com.pxworld.domain.progression.OwnedHero
import com.pxworld.domain.progression.PlayerProfile
import com.pxworld.domain.progression.PlayerSettings
import com.pxworld.domain.progression.QuestProgress
import com.pxworld.domain.progression.WorldPosition
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.File
import java.time.LocalDate

data class LegacyImport(val state: GameState, val notes: List<String>)

class LegacyV1Importer(private val bundle: ContentBundle) {

    private val catalog = ContentBundleCatalog(bundle)
    private val equipmentIds = bundle.equipment.map { it.id }.toSet()
    private val mapIds = bundle.encounters.map { it.map }.toSet()

    fun import(saveFolder: File): LegacyImport {
        val notes = mutableListOf<String>()
        fun read(name: String): JsonElement? =
            File(saveFolder, "$name.json").takeIf { it.isFile }?.let { Json.parseToJsonElement(it.readText()) }
                .also { if (it == null) notes += "missing $name.json; using defaults" }

        val info = read("info")?.jsonObject ?: throw IllegalArgumentException("${saveFolder.path} has no info.json")
        val legacyHeroes = read("hero_full")?.jsonArray.orEmpty().map { it.jsonObject }
        val legacyEquipment = read("equips")?.jsonArray.orEmpty().map { it.jsonObject }
        val legacyItems = read("items")?.jsonArray.orEmpty().map { it.jsonObject }
        val legacyMissions = read("mission")?.jsonArray.orEmpty().map { it.jsonObject }
        val legacyAchievements = read("achievement")?.jsonArray.orEmpty().map { it.jsonObject }
        val legacyCheckin = read("daily_rewards")?.jsonArray.orEmpty().map { it.jsonObject }

        var instanceNumber = 1L
        val heroInstanceByLegacyId = mutableMapOf<String, String>()
        val heroes = legacyHeroes.mapNotNull { legacy ->
            val heroId = heroForAtlas(legacy.text("nameRegion"))
            if (heroId == null) {
                notes += "dropped hero ${legacy.text("characterId")}: unknown atlas ${legacy.text("nameRegion")}"
                return@mapNotNull null
            }
            val level = legacy.long("level").toInt().coerceIn(1, ExperienceCurve.MAX_LEVEL)
            val instanceId = "hero-${instanceNumber++}"
            heroInstanceByLegacyId[legacy.text("characterId")] = instanceId
            OwnedHero(instanceId, heroId, level, legacy.long("star").toInt().coerceIn(0, 5), remainingExperience(level, legacy.long("exp")))
        }

        val cells = legacyHeroes.mapNotNull { legacy ->
            val grid = legacy.text("grid")
            val instanceId = heroInstanceByLegacyId[legacy.text("characterId")]
            if (grid == "empty" || instanceId == null) return@mapNotNull null
            val (column, row) = grid.split(",").map(String::toInt)
            GridCell(lane = row, depth = GridCell.GRID_SIZE - 1 - column) to instanceId
        }.toMap()
        val capacity = maxOf(info.long("sizeTeam").toInt(), cells.size, 1)
        val starterInstance = heroes.firstOrNull()?.instanceId
        val lineupCells = if (cells.isEmpty() && starterInstance != null) {
            notes += "legacy lineup was empty; placed first hero in the front middle cell"
            mapOf(GridCell(1, 0) to starterInstance)
        } else {
            cells
        }

        val equipment = legacyEquipment.mapNotNull { legacy ->
            val equipmentId = "equip.${legacy.text("nameRegion")}"
            if (equipmentId !in equipmentIds) {
                notes += "dropped equipment ${legacy.text("id")}: unknown ${legacy.text("nameRegion")}"
                return@mapNotNull null
            }
            val owner = legacy.text("target").takeIf { it != "empty" }?.let(heroInstanceByLegacyId::get)
            EquipmentInstance("equip-${instanceNumber++}", equipmentId, catalog.equipmentSlot(equipmentId), legacy.long("level").toInt().coerceAtLeast(1), owner)
        }.let(::resolveSlotConflicts).let { (resolved, conflicts) ->
            notes += conflicts
            resolved
        }

        val items = legacyItems.mapNotNull { legacy ->
            val itemId = catalog.itemsByIcon["icon:item/${legacy.text("nameRegion")}"]
            val quantity = legacy.long("quantity")
            when {
                itemId == null -> null.also { notes += "dropped item ${legacy.text("nameRegion")}: not in content" }
                quantity <= 0 -> null
                else -> itemId to quantity
            }
        }.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }

        val wallet = listOf(Currencies.GOLD to info.long("coin"), Currencies.GEM to info.long("gem"))
            .filter { it.second > 0 }
            .fold(Wallet() to emptyList<LedgerEntry>()) { (wallet, ledger), (currency, amount) ->
                val change = wallet.credit(currency, amount, LedgerReason("legacy_import"))
                change.wallet to ledger + change.entry
            }

        val achievementNumbers = legacyAchievements.associate { it.text("idBase") to it.long("number") }
        val counters = mapOf(
            Counters.HEROES_RECRUITED to maxOf(info.long("numberOfTeammatesRecruited"), achievementNumbers["Recruited"] ?: 0),
            Counters.ENEMIES_DEFEATED to maxOf(info.long("numberOfEnemies"), achievementNumbers["Kill"] ?: 0),
            Counters.BATTLES_WON to maxOf(info.long("win"), achievementNumbers["Win"] ?: 0),
            Counters.GOLD_EARNED to maxOf(info.long("coinSum"), achievementNumbers["CoinSum"] ?: 0),
            Counters.GEMS_SPENT to maxOf(info.long("gemPay"), achievementNumbers["GemPay"] ?: 0),
            Counters.EQUIPMENT_OBTAINED to maxOf(info.long("equipment"), achievementNumbers["Equip"] ?: 0),
        ).filterValues { it > 0 }

        val quests = legacyMissions.mapNotNull { legacy ->
            val questId = LEGACY_MISSIONS[legacy.text("idBase")] ?: return@mapNotNull null.also { notes += "dropped mission ${legacy.text("idBase")}" }
            val progress = legacy.long("progress").toInt()
            QuestProgress(questId, progress, completed = progress >= legacy.long("targetAmount"), claimed = false)
        }

        val claimedDays = legacyCheckin.count { it.boolean("confirm") }
        val lastClaim = info.text("dailyCheck").takeIf { claimedDays > 0 }?.let { runCatching { LocalDate.parse(it).toEpochDay() }.getOrNull() }

        val legacyArea = info.text("area")
        val mapId = runCatching { legacyMapId(legacyArea) }.getOrElse {
            notes += "unknown legacy area $legacyArea; moved to the starting village"
            STARTING_MAP
        }
        if (mapId !in mapIds && mapId != STARTING_MAP) notes += "map $mapId has no encounters yet"
        val position = info["pos"]?.jsonObject
        val x = position?.get("x")?.jsonPrimitive?.doubleOrNull?.toInt() ?: -1
        val y = position?.get("y")?.jsonPrimitive?.doubleOrNull?.toInt() ?: -1

        val profileLevel = info.long("level").toInt().coerceIn(1, ExperienceCurve.MAX_LEVEL)
        val state = GameState(
            profile = PlayerProfile(
                name = info.text("name"),
                level = profileLevel,
                experience = remainingExperience(profileLevel, info.long("exp")),
                starterHeroId = heroForAtlas(info.text("characterSelect")) ?: catalog.starterHeroId,
            ),
            wallet = wallet.first,
            ledgerTail = wallet.second,
            heroes = heroes,
            lineup = Lineup(lineupCells, capacity),
            inventory = Inventory(items, equipment),
            quests = quests,
            checkin = CheckinProgress(CHECKIN_TABLE, claimedDays, lastClaim),
            position = WorldPosition(mapId, x, y),
            settings = PlayerSettings(info.boolean("playMusic", default = true), info.boolean("playSound", default = true)),
            stats = LifetimeStats(counters),
            nextInstanceNumber = instanceNumber,
        )
        return LegacyImport(state, notes)
    }

    private fun heroForAtlas(atlas: String): String? {
        val classSlug = atlas.removeSuffix("_Knight").lowercase()
        return catalog.heroesByClass["class.$classSlug"]
    }

    private fun resolveSlotConflicts(equipment: List<EquipmentInstance>): Pair<List<EquipmentInstance>, List<String>> {
        val seen = mutableSetOf<Pair<String, Any>>()
        val notes = mutableListOf<String>()
        val resolved = equipment.map { equip ->
            val owner = equip.equippedBy ?: return@map equip
            if (seen.add(owner to equip.slot)) equip else equip.copy(equippedBy = null).also { notes += "unequipped ${equip.instanceId}: $owner already had a ${equip.slot.name.lowercase()}" }
        }
        return resolved to notes
    }

    companion object {
        const val STARTING_MAP: String = "map.dawnvillage_01"
        const val CHECKIN_TABLE: String = "checkin.standard_30"

        val LEGACY_MISSIONS: Map<String, String> = mapOf(
            "mission_004" to "quest.side.defend_the_village",
            "mission_001" to "quest.side.the_hidden_stone",
            "mission_002" to "quest.side.forge_supplies",
            "mission_003" to "quest.side.monster_hunt",
            "mission_005" to "quest.side.dungeon_secret",
        )

        fun legacyMapId(legacyArea: String): String {
            Regex("^village_(\\d)$").find(legacyArea)?.let { return "map.dawnvillage_%02d".format(it.groupValues[1].toInt() + 1) }
            Regex("^garden(\\d)$").find(legacyArea)?.let { return "map.mistgarden_%02d".format(it.groupValues[1].toInt() + 1) }
            Regex("^wasteland(\\d)$").find(legacyArea)?.let { return "map.ashwaste_%02d".format(it.groupValues[1].toInt()) }
            throw IllegalArgumentException("unknown legacy area $legacyArea")
        }

        fun remainingExperience(level: Int, legacyCumulative: Long): Long =
            (legacyCumulative - 100L * (level - 1)).coerceIn(0, ExperienceCurve.requiredForNextLevel(level) - 1)

        private fun JsonObject.text(key: String): String = (this[key] as? JsonPrimitive)?.content ?: ""
        private fun JsonObject.long(key: String): Long = (this[key] as? JsonPrimitive)?.let { it.longOrNull ?: it.doubleOrNull?.toLong() } ?: 0L
        private fun JsonObject.boolean(key: String, default: Boolean = false): Boolean = (this[key] as? JsonPrimitive)?.booleanOrNull ?: default
    }
}
