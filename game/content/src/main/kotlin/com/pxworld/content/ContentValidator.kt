package com.pxworld.content

import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.stats.StatFormula

enum class IssueSeverity { ERROR, WARNING }

data class ContentIssue(val severity: IssueSeverity, val recordId: String, val message: String) {
    override fun toString(): String = "${severity.name.padEnd(7)} $recordId: $message"
}

fun interface AssetExistence {
    fun exists(legacyReference: String): Boolean
}

object ContentValidator {

    const val PRIMARY_LOCALE: String = "vi"
    private val ID_PATTERN = Regex("^[a-z]+(\\.[a-z0-9_]+)+$")
    val STAT_NAMES: Set<String> = setOf(
        "hp", "attack", "defense", "speed", "critRate", "critDamage", "accuracy", "evasion", "effectHit", "effectResistance",
    )
    private val OBJECTIVE_KINDS = setOf("win_encounter", "collect_item", "collect_item_category", "defeat_enemies", "reach_map")
    private val ACHIEVEMENT_COUNTERS = setOf(
        "heroes_recruited", "enemies_defeated", "battles_won", "gold_earned", "gems_spent", "equipment_obtained",
    )
    private val EQUIPMENT_SLOTS = setOf("weapon", "armor", "jewelry", "support")

    fun validate(bundle: ContentBundle, assets: AssetExistence): List<ContentIssue> {
        val issues = mutableListOf<ContentIssue>()
        val error = { id: String, message: String -> issues += ContentIssue(IssueSeverity.ERROR, id, message) }
        val warning = { id: String, message: String -> issues += ContentIssue(IssueSeverity.WARNING, id, message) }

        val ids = bundle.allIds()
        ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.forEach { error(it, "duplicate id") }
        ids.filterNot { ID_PATTERN.matches(it) }.forEach { error(it, "id must match <kind>.<slug> in lowercase snake_case") }

        fun expectPrefix(records: List<String>, prefix: String) =
            records.filterNot { it.startsWith("$prefix.") }.forEach { error(it, "id must start with $prefix.") }
        expectPrefix(bundle.currencies.map { it.id }, "currency")
        expectPrefix(bundle.heroClasses.map { it.id }, "class")
        expectPrefix(bundle.statuses.map { it.id }, "status")
        expectPrefix(bundle.skills.map { it.id }, "skill")
        expectPrefix(bundle.heroes.map { it.id }, "hero")
        expectPrefix(bundle.enemies.map { it.id }, "enemy")
        expectPrefix(bundle.encounters.map { it.id }, "encounter")
        expectPrefix(bundle.items.map { it.id }, "item")
        expectPrefix(bundle.equipment.map { it.id }, "equip")
        expectPrefix(bundle.quests.map { it.id }, "quest")
        expectPrefix(bundle.achievements.map { it.id }, "achievement")
        expectPrefix(bundle.checkinTables.map { it.id }, "checkin")
        expectPrefix(bundle.battleRules.map { it.id }, "balance")
        expectPrefix(bundle.maps.map { it.id }, "map")

        val classIds = bundle.heroClasses.map { it.id }.toSet()
        val statusIds = bundle.statuses.map { it.id }.toSet()
        val skillsById = bundle.skills.associateBy { it.id }
        val currencyIds = bundle.currencies.map { it.id }.toSet()
        val itemIds = bundle.items.map { it.id }.toSet()
        val equipmentIds = bundle.equipment.map { it.id }.toSet()
        val heroIds = bundle.heroes.map { it.id }.toSet()
        val enemyIds = bundle.enemies.map { it.id }.toSet()
        val encounterIds = bundle.encounters.map { it.id }.toSet()
        val itemCategories = bundle.items.map { it.category }.toSet()
        val mapIds = bundle.maps.map { it.id }.toSet()

        val primary = bundle.localization[PRIMARY_LOCALE]
        if (primary == null) error("localization", "missing primary locale $PRIMARY_LOCALE")
        val missingTranslations = mutableMapOf<String, Int>()
        fun text(owner: String, key: String) {
            if (primary != null && key !in primary) error(owner, "missing $PRIMARY_LOCALE text for $key")
            bundle.localization.filterKeys { it != PRIMARY_LOCALE }.forEach { (locale, table) ->
                if (key !in table) missingTranslations[locale] = (missingTranslations[locale] ?: 0) + 1
            }
        }
        fun asset(owner: String, key: String) {
            val reference = bundle.assetMap[key]
            when {
                reference == null -> error(owner, "asset key $key has no mapping")
                !assets.exists(reference) -> error(owner, "asset key $key maps to missing $reference")
            }
        }
        fun rewards(owner: String, list: List<RewardRecord>) {
            if (list.isEmpty()) warning(owner, "no rewards")
            list.forEach { reward ->
                if (reward.quantity <= 0) error(owner, "reward ${reward.id} must have positive quantity")
                val known = when (reward.kind) {
                    RewardKindName.CURRENCY -> reward.id in currencyIds
                    RewardKindName.ITEM -> reward.id in itemIds
                    RewardKindName.EQUIPMENT -> reward.id in equipmentIds
                    RewardKindName.HERO -> reward.id in heroIds
                }
                if (!known) error(owner, "reward references unknown ${reward.kind.name.lowercase()} ${reward.id}")
            }
        }
        fun stats(owner: String, stats: StatsRecord) {
            if (stats.hp <= 0 || stats.attack < 0 || stats.defense < 0 || stats.speed <= 0) error(owner, "hp and speed must be positive, attack and defense non-negative")
            if (stats.critRate !in 0..1000) error(owner, "critRate must be 0..1000 permille")
            if (stats.critDamage < 1000) error(owner, "critDamage must be at least 1000 permille")
        }
        fun kit(owner: String, skills: List<String>) {
            val resolved = skills.mapNotNull { id -> skillsById[id].also { if (it == null) error(owner, "unknown skill $id") } }
            val slots = resolved.groupingBy { it.slot }.eachCount()
            if (slots[SkillSlotName.BASIC] != 1) error(owner, "needs exactly one basic skill")
            if ((slots[SkillSlotName.ULTIMATE] ?: 0) > 1) error(owner, "has more than one ultimate")
        }

        bundle.currencies.forEach { text(it.id, it.name); text(it.id, it.description); asset(it.id, it.icon) }

        bundle.heroClasses.forEach { heroClass ->
            text(heroClass.id, heroClass.name)
            heroClass.counters.forEach { if (it !in classIds) error(heroClass.id, "counters unknown class $it") }
            if (heroClass.id in heroClass.counters) error(heroClass.id, "cannot counter itself")
            heroClass.counters.forEach { target ->
                if (bundle.heroClasses.firstOrNull { it.id == target }?.counters?.contains(heroClass.id) == true) {
                    warning(heroClass.id, "mutual counter with $target cancels out")
                }
            }
        }

        bundle.statuses.forEach { status ->
            text(status.id, status.name)
            if (status.durationTurns <= 0) error(status.id, "durationTurns must be positive")
            if (status.maxStacks <= 0) error(status.id, "maxStacks must be positive")
            val needsStat = status.kind == StatusKindName.STAT_MODIFIER || status.kind == StatusKindName.STAT_BONUS
            if (needsStat && status.stat !in STAT_NAMES) error(status.id, "stat must be one of $STAT_NAMES")
            if (!needsStat && status.stat != null) error(status.id, "stat only applies to stat_modifier and stat_bonus")
            val needsMagnitude = status.kind !in setOf(StatusKindName.STUN, StatusKindName.SILENCE, StatusKindName.TAUNT)
            if (needsMagnitude && status.magnitude == 0) error(status.id, "magnitude must be non-zero for ${status.kind.name.lowercase()}")
        }

        bundle.skills.forEach { skill ->
            text(skill.id, skill.name)
            text(skill.id, skill.description)
            asset(skill.id, skill.vfx)
            if (skill.effects.isEmpty()) error(skill.id, "needs at least one effect")
            if (skill.slot == SkillSlotName.ULTIMATE && skill.energyCost == 0) error(skill.id, "ultimate must cost energy")
            if (skill.slot != SkillSlotName.ULTIMATE && skill.energyCost != 0) error(skill.id, "only ultimates spend energy")
            if (skill.slot == SkillSlotName.SKILL && skill.cooldownTurns <= 0) error(skill.id, "skill slot needs a cooldown")
            val counted = skill.targeting.kind == TargetingKindName.LOWEST_HP_ALLIES || skill.targeting.kind == TargetingKindName.RANDOM_ENEMIES
            if (counted && (skill.targeting.count ?: 0) <= 0) error(skill.id, "targeting ${skill.targeting.kind.name.lowercase()} needs a positive count")
            if (!counted && skill.targeting.count != null) error(skill.id, "targeting count only applies to counted targeting")
            skill.effects.forEach { effect ->
                when (effect.kind) {
                    EffectKindName.DAMAGE -> if ((effect.power ?: 0) <= 0 || (effect.hits ?: 1) <= 0) error(skill.id, "damage needs positive power and hits")
                    EffectKindName.HEAL -> if ((effect.power ?: 0) <= 0) error(skill.id, "heal needs positive power")
                    EffectKindName.APPLY_STATUS -> {
                        if (effect.status !in statusIds) error(skill.id, "unknown status ${effect.status}")
                        if ((effect.chance ?: 1000) !in 1..1000) error(skill.id, "status chance must be 1..1000")
                    }
                    EffectKindName.GAIN_ENERGY -> if ((effect.amount ?: 0) == 0) error(skill.id, "gain_energy needs a non-zero amount")
                }
            }
        }

        bundle.heroes.forEach { hero ->
            listOf(hero.name, hero.title, hero.description).forEach { text(hero.id, it) }
            if (hero.classId !in classIds) error(hero.id, "unknown class ${hero.classId}")
            stats(hero.id, hero.baseStats)
            kit(hero.id, hero.skills)
            asset(hero.id, hero.sprite)
        }
        if (bundle.heroes.none { it.starter }) error("heroes", "at least one hero must be selectable as starter")

        bundle.enemies.forEach { enemy ->
            text(enemy.id, enemy.name)
            if (enemy.classId !in classIds) error(enemy.id, "unknown class ${enemy.classId}")
            stats(enemy.id, enemy.baseStats)
            kit(enemy.id, enemy.skills)
            asset(enemy.id, enemy.sprite)
        }

        bundle.encounters.forEach { encounter ->
            text(encounter.id, encounter.name)
            if (encounter.map !in mapIds) error(encounter.id, "unknown map ${encounter.map}")
            if (encounter.enemies.isEmpty()) error(encounter.id, "needs at least one enemy")
            if (encounter.enemies.size > GridCell.GRID_SIZE * GridCell.GRID_SIZE) error(encounter.id, "more enemies than grid cells")
            encounter.enemies.forEach { placed ->
                if (placed.enemy !in enemyIds) error(encounter.id, "unknown enemy ${placed.enemy}")
                if (placed.level < 1) error(encounter.id, "enemy level must be at least 1")
                if (placed.star !in 0..StatFormula.MAX_STAR) error(encounter.id, "enemy star must be 0..${StatFormula.MAX_STAR}")
                if (placed.cell.lane !in 0 until GridCell.GRID_SIZE || placed.cell.depth !in 0 until GridCell.GRID_SIZE) error(encounter.id, "cell out of grid")
            }
            if (encounter.enemies.map { it.cell }.toSet().size != encounter.enemies.size) error(encounter.id, "two enemies share a cell")
            if (encounter.recommendedLevel < encounter.enemies.maxOf { it.level }) warning(encounter.id, "recommendedLevel is below the strongest enemy level")
            rewards(encounter.id, encounter.rewards)
        }
        bundle.encounters.groupBy { it.map to it.mapObjectId }.filterValues { it.size > 1 }
            .forEach { (key, list) -> error(list.first().id, "map object ${key.second} on ${key.first} is used by ${list.map { it.id }}") }

        bundle.maps.forEach { map ->
            text(map.id, map.name)
            asset(map.id, map.asset)
        }
        bundle.maps.groupBy { it.legacyName }.filterValues { it.size > 1 }.forEach { (legacy, list) -> error(list.first().id, "legacy map $legacy is claimed by ${list.map { it.id }}") }

        bundle.items.forEach { item ->
            text(item.id, item.name)
            asset(item.id, item.icon)
            if (item.tier <= 0) error(item.id, "tier must be positive")
            item.shop?.let { shop ->
                if (shop.currency !in currencyIds) error(item.id, "shop currency ${shop.currency} is unknown")
                if (shop.price <= 0) error(item.id, "price must be positive")
            }
            item.use?.let { use ->
                if (use.kind != "hero_exp") error(item.id, "unknown use kind ${use.kind}")
                if (use.amount <= 0) error(item.id, "use amount must be positive")
            }
        }

        bundle.equipment.forEach { equip ->
            text(equip.id, equip.name)
            asset(equip.id, equip.icon)
            if (equip.slot !in EQUIPMENT_SLOTS) error(equip.id, "slot must be one of $EQUIPMENT_SLOTS")
            if (equip.stats.isEmpty()) warning(equip.id, "grants no stats")
            equip.stats.keys.filterNot { it in STAT_NAMES }.forEach { error(equip.id, "unknown stat $it") }
            if (equip.shop.currency !in currencyIds) error(equip.id, "shop currency ${equip.shop.currency} is unknown")
        }

        bundle.quests.forEach { quest ->
            text(quest.id, quest.name)
            text(quest.id, quest.description)
            val objective = quest.objective
            if (objective.kind !in OBJECTIVE_KINDS) error(quest.id, "unknown objective kind ${objective.kind}")
            if (objective.count <= 0) error(quest.id, "objective count must be positive")
            when (objective.kind) {
                "win_encounter" -> if (objective.target !in encounterIds) error(quest.id, "unknown encounter ${objective.target}")
                "collect_item" -> if (objective.target !in itemIds) error(quest.id, "unknown item ${objective.target}")
                "collect_item_category" -> if (objective.target !in itemCategories) error(quest.id, "unknown item category ${objective.target}")
                "reach_map" -> if (objective.target !in mapIds) error(quest.id, "unknown map ${objective.target}")
                "defeat_enemies" -> if (objective.target != null && objective.target !in enemyIds) error(quest.id, "unknown enemy ${objective.target}")
            }
            rewards(quest.id, quest.rewards)
        }

        bundle.achievements.forEach { achievement ->
            text(achievement.id, achievement.name)
            text(achievement.id, achievement.description)
            if (achievement.counter !in ACHIEVEMENT_COUNTERS) error(achievement.id, "unknown counter ${achievement.counter}")
            val targets = achievement.tiers.map { it.target }
            if (targets.isEmpty() || targets != targets.sorted() || targets.toSet().size != targets.size || targets.first() <= 0) {
                error(achievement.id, "tier targets must be positive and strictly increasing")
            }
            achievement.tiers.forEach { rewards(achievement.id, it.rewards) }
        }

        bundle.checkinTables.forEach { table ->
            text(table.id, table.name)
            if (table.days.map { it.day } != (1..table.days.size).toList()) error(table.id, "days must be numbered 1..${table.days.size} in order")
            table.days.forEach { rewards(table.id, it.rewards) }
        }

        if (bundle.battleRules.size != 1) error("balance", "exactly one balance.battle_rules record is required")
        bundle.battleRules.forEach { rules ->
            if (rules.damageTakenByDepthPermille.size != GridCell.GRID_SIZE) error(rules.id, "damageTakenByDepthPermille needs ${GridCell.GRID_SIZE} values")
            if (rules.startingEnergy > rules.maxEnergy) error(rules.id, "startingEnergy exceeds maxEnergy")
            if (rules.maxRounds <= 0) error(rules.id, "maxRounds must be positive")
        }

        val usedAssets = (bundle.currencies.map { it.icon } + bundle.skills.map { it.vfx } + bundle.heroes.map { it.sprite } +
            bundle.enemies.map { it.sprite } + bundle.items.map { it.icon } + bundle.equipment.map { it.icon } + bundle.maps.map { it.asset }).toSet()
        (bundle.assetMap.keys - usedAssets).forEach { warning("assets", "asset key $it is mapped but never used") }

        missingTranslations.forEach { (locale, count) -> warning("localization.$locale", "$count keys have no $locale translation yet") }

        return issues.sortedWith(compareBy({ it.severity }, { it.recordId }, { it.message }))
    }
}
