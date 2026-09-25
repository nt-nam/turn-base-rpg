package com.pxworld.simulation

import com.pxworld.content.balance.SeedRange
import com.pxworld.domain.battle.GridCell
import com.pxworld.domain.progression.ExperienceCurve
import com.pxworld.domain.stats.StatFormula

class UsageException(message: String) : IllegalArgumentException(message)

enum class OutputFormat { TABLE, JSON }

sealed interface EncounterSelection {
    object All : EncounterSelection {
        override fun toString(): String = "All"
    }

    data class Only(val encounterId: String) : EncounterSelection
}

data class HeroRequest(val heroId: String, val level: Int? = null, val star: Int? = null, val cell: GridCell? = null, val equipment: List<String> = emptyList())

sealed interface LineupSelection {
    object Reference : LineupSelection {
        override fun toString(): String = "Reference"
    }

    data class Heroes(val heroes: List<HeroRequest>) : LineupSelection
}

data class SimulationRequest(
    val encounter: EncounterSelection = EncounterSelection.All,
    val lineup: LineupSelection = LineupSelection.Reference,
    val seeds: SeedRange = SeedRange.DEFAULT,
    val defaultLevel: Int? = null,
    val defaultStar: Int = 0,
    val format: OutputFormat = OutputFormat.TABLE,
    val contentDirectory: String = DEFAULT_CONTENT_DIRECTORY,
) {
    companion object {
        const val DEFAULT_CONTENT_DIRECTORY: String = "content"
    }
}

sealed interface CommandLine {
    object ShowUsage : CommandLine {
        override fun toString(): String = "ShowUsage"
    }

    data class Run(val request: SimulationRequest) : CommandLine
}

object SimulationArguments {

    const val MAXIMUM_SEEDS: Int = 100_000
    private const val HERO_PREFIX = "hero."
    private const val EQUIPMENT_PREFIX = "equip."
    private val OPTIONS = setOf("encounter", "heroes", "seeds", "seed-start", "level", "stars", "format", "content", "help")

    val USAGE: String = """
        |usage: sim-cli [options]
        |
        |  --encounter <id|all>      encounter to simulate (default: all)
        |  --heroes <spec,...>       explicit lineup; spec = heroId[:level[:stars]][@lane-depth][+equipmentId...]
        |                            e.g. hero.aldric:3+sword_001,mirae:3:1@2-2 (hero. and equip. prefixes are optional)
        |  --seeds <n>               battles per encounter (default: ${SeedRange.DEFAULT_COUNT})
        |  --seed-start <n>          first seed (default: ${SeedRange.DEFAULT_FIRST})
        |  --level <n>               level for heroes without one (default: the encounter's recommended level)
        |  --stars <n>               stars for heroes without them (default: 0)
        |  --format table|json       output format (default: table)
        |  --content <dir>           content directory (default: ${SimulationRequest.DEFAULT_CONTENT_DIRECTORY})
        |  --help                    show this message
        |
        |Without --heroes every encounter is fought by the reference lineup (one hero of each class).
        """.trimMargin()

    fun parse(arguments: List<String>): CommandLine {
        val options = options(arguments)
        if ("help" in options) return CommandLine.ShowUsage
        val defaults = SimulationRequest()
        val request = SimulationRequest(
            encounter = options["encounter"]?.let(::encounter) ?: defaults.encounter,
            lineup = options["heroes"]?.let(::heroes) ?: defaults.lineup,
            seeds = SeedRange(
                first = options["seed-start"]?.let { long("--seed-start", it) } ?: SeedRange.DEFAULT_FIRST,
                count = options["seeds"]?.let { integer("--seeds", it, 1..MAXIMUM_SEEDS) } ?: SeedRange.DEFAULT_COUNT,
            ),
            defaultLevel = options["level"]?.let { level("--level", it) },
            defaultStar = options["stars"]?.let { star("--stars", it) } ?: defaults.defaultStar,
            format = options["format"]?.let(::format) ?: defaults.format,
            contentDirectory = options["content"]?.also { if (it.isBlank()) throw UsageException("--content needs a directory") } ?: defaults.contentDirectory,
        )
        return CommandLine.Run(request)
    }

    private fun options(arguments: List<String>): Map<String, String> {
        val options = linkedMapOf<String, String>()
        var index = 0
        while (index < arguments.size) {
            val token = arguments[index]
            if (!token.startsWith("--")) throw UsageException("unexpected argument '$token'")
            val name = token.removePrefix("--").substringBefore("=")
            if (name !in OPTIONS) throw UsageException("unknown option --$name")
            if (name in options) throw UsageException("--$name given more than once")
            val value = when {
                name == "help" -> ""
                "=" in token -> token.substringAfter("=")
                index + 1 < arguments.size && !arguments[index + 1].startsWith("--") -> arguments[++index]
                else -> throw UsageException("--$name needs a value")
            }
            options[name] = value
            index += 1
        }
        return options
    }

    private fun encounter(value: String): EncounterSelection = when {
        value.isBlank() -> throw UsageException("--encounter needs an encounter id or 'all'")
        value == "all" -> EncounterSelection.All
        else -> EncounterSelection.Only(value)
    }

    private fun heroes(value: String): LineupSelection.Heroes {
        val entries = value.split(",").map { it.trim() }
        if (entries.any { it.isEmpty() }) throw UsageException("--heroes has an empty entry in '$value'")
        return LineupSelection.Heroes(entries.map(::hero))
    }

    private fun hero(spec: String): HeroRequest {
        val pieces = spec.split("+")
        val body = pieces.first()
        val equipment = pieces.drop(1).map { piece ->
            if (piece.isBlank()) throw UsageException("hero spec '$spec' has an empty equipment entry")
            if (piece.startsWith(EQUIPMENT_PREFIX)) piece else EQUIPMENT_PREFIX + piece
        }
        val parts = body.substringBefore("@").split(":")
        if (parts.size > 3 || parts[0].isBlank()) throw UsageException("hero spec '$spec' must look like heroId[:level[:stars]][@lane-depth][+equipmentId...]")
        return HeroRequest(
            heroId = parts[0].let { if (it.startsWith(HERO_PREFIX)) it else HERO_PREFIX + it },
            level = parts.getOrNull(1)?.let { level("level in '$spec'", it) },
            star = parts.getOrNull(2)?.let { star("stars in '$spec'", it) },
            cell = if ("@" in body) cell(spec, body.substringAfter("@")) else null,
            equipment = equipment,
        )
    }

    private fun cell(spec: String, placement: String): GridCell {
        val coordinates = placement.split("-")
        val range = 0 until GridCell.GRID_SIZE
        if (coordinates.size != 2) throw UsageException("placement in '$spec' must be @lane-depth, e.g. @1-0")
        val lane = integer("lane in '$spec'", coordinates[0], range)
        val depth = integer("depth in '$spec'", coordinates[1], range)
        return GridCell(lane, depth)
    }

    private fun format(value: String): OutputFormat =
        OutputFormat.values().firstOrNull { it.name.equals(value, ignoreCase = true) } ?: throw UsageException("--format must be table or json, was '$value'")

    private fun level(label: String, value: String): Int = integer(label, value, 1..ExperienceCurve.MAX_LEVEL)

    private fun star(label: String, value: String): Int = integer(label, value, 0..StatFormula.MAX_STAR)

    private fun integer(label: String, value: String, range: IntRange): Int {
        val number = value.toIntOrNull() ?: throw UsageException("$label must be a whole number, was '$value'")
        if (number !in range) throw UsageException("$label must be in ${range.first}..${range.last}, was $number")
        return number
    }

    private fun long(label: String, value: String): Long = value.toLongOrNull() ?: throw UsageException("$label must be a whole number, was '$value'")
}
