package com.pxworld.content.compiler

import com.pxworld.content.AssetExistence
import com.pxworld.content.BattleContentAssembler
import com.pxworld.content.ContentBundle
import com.pxworld.content.ContentIssue
import com.pxworld.content.ContentLoader
import com.pxworld.content.ContentValidator
import com.pxworld.content.IssueSeverity
import com.pxworld.content.LineupSlot
import com.pxworld.domain.battle.BattleEngine
import com.pxworld.domain.battle.BattleOutcome
import com.pxworld.domain.battle.GridCell
import java.io.File
import java.security.MessageDigest
import kotlin.system.exitProcess

class LegacyAssetExistence(private val assetsRoot: File) : AssetExistence {

    companion object {
        val GENERATED_PREFIXES: List<String> = listOf("fonts/", "backgrounds/")
    }


    private val atlasRegions = mutableMapOf<String, Set<String>>()

    override fun exists(legacyReference: String): Boolean {
        val path = legacyReference.substringBefore("#")
        if (GENERATED_PREFIXES.any { path.startsWith(it) }) return true
        val file = File(assetsRoot, path)
        if (!file.isFile) return false
        val region = legacyReference.substringAfter("#", missingDelimiterValue = "")
        if (region.isEmpty()) return true
        val regions = atlasRegions.getOrPut(path) {
            file.readLines().map { it.trim() }.filter { it.isNotEmpty() && !it.contains(':') && !it.endsWith(".png") }.toSet()
        }
        return region in regions
    }
}

data class WinRate(val victories: Int, val draws: Int, val simulations: Int) {
    val permille: Int get() = victories * 1000 / simulations

    override fun toString(): String = "${permille / 10}.${permille % 10}%".padStart(6) + " (draws $draws)"
}

data class EncounterBalance(val encounterId: String, val recommendedLevel: Int, val fullTeam: WinRate, val starterAlone: WinRate)

object ContentCompilation {

    const val SIMULATIONS_PER_ENCOUNTER: Int = 200
    const val MINIMUM_WIN_RATE_PERMILLE: Int = 600

    fun readTree(contentDir: File): Map<String, String> =
        contentDir.walkTopDown()
            .filter { it.isFile && it.extension == "json" }
            .associate { it.relativeTo(contentDir).invariantSeparatorsPath to it.readText() }

    fun referenceLineup(bundle: ContentBundle, level: Int): List<LineupSlot> {
        val formation = mapOf(
            "class.tank" to GridCell(lane = 1, depth = 0),
            "class.warrior" to GridCell(lane = 0, depth = 0),
            "class.assassin" to GridCell(lane = 2, depth = 0),
            "class.ranger" to GridCell(lane = 0, depth = 1),
            "class.mage" to GridCell(lane = 1, depth = 2),
            "class.support" to GridCell(lane = 2, depth = 2),
        )
        return bundle.heroes.mapNotNull { hero -> formation[hero.classId]?.let { LineupSlot(hero.id, level, 0, it) } }
    }

    fun simulate(bundle: ContentBundle): List<EncounterBalance> {
        val assembler = BattleContentAssembler(bundle)
        return bundle.encounters.map { encounter ->
            fun winRate(lineup: List<LineupSlot>): WinRate {
                val outcomes = (1..SIMULATIONS_PER_ENCOUNTER).map { seed ->
                    BattleEngine.runAuto(assembler.battle(seed.toLong(), lineup, encounter.id)).finalState.outcome
                }
                return WinRate(outcomes.count { it == BattleOutcome.VICTORY }, outcomes.count { it == BattleOutcome.DRAW }, SIMULATIONS_PER_ENCOUNTER)
            }
            val starter = bundle.heroes.first { it.starter }
            EncounterBalance(
                encounterId = encounter.id,
                recommendedLevel = encounter.recommendedLevel,
                fullTeam = winRate(referenceLineup(bundle, encounter.recommendedLevel)),
                starterAlone = winRate(listOf(LineupSlot(starter.id, encounter.recommendedLevel, 0, GridCell(lane = 1, depth = 0)))),
            )
        }
    }

    fun packJson(bundle: ContentBundle): String = ContentLoader.json.encodeToString(ContentBundle.serializer(), bundle)

    fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
}

fun main(arguments: Array<String>) {
    require(arguments.size == 3) { "usage: ContentCompiler <contentDir> <outputDir> <legacyAssetsDir>" }
    val (contentDir, outputDir, assetsDir) = arguments.map(::File)

    val bundle = ContentLoader.load(ContentCompilation.readTree(contentDir))
    val issues: List<ContentIssue> = ContentValidator.validate(bundle, LegacyAssetExistence(assetsDir))
    issues.forEach(::println)
    val errors = issues.count { it.severity == IssueSeverity.ERROR }
    println("validation: $errors errors, ${issues.size - errors} warnings, ${bundle.allIds().size} records")
    if (errors > 0) exitProcess(1)

    println()
    println("encounter balance at recommended level, ${ContentCompilation.SIMULATIONS_PER_ENCOUNTER} seeds each:")
    println("  ${"encounter".padEnd(32)} lvl  ${"all heroes".padEnd(20)} starter alone")
    ContentCompilation.simulate(bundle).forEach { balance ->
        val flag = if (balance.fullTeam.permille < ContentCompilation.MINIMUM_WIN_RATE_PERMILLE) "  <-- too hard" else ""
        println("  ${balance.encounterId.padEnd(32)} ${balance.recommendedLevel.toString().padStart(3)}  ${balance.fullTeam.toString().padEnd(20)} ${balance.starterAlone}$flag")
    }

    val pack = ContentCompilation.packJson(bundle)
    val hash = ContentCompilation.sha256(pack)
    outputDir.mkdirs()
    val packFile = File(outputDir, "content-pack-${hash.take(12)}.json")
    packFile.writeText(pack)
    File(outputDir, "content-pack.json").writeText(pack)
    File(outputDir, "manifest.json").writeText(
        """{"version":"${hash.take(12)}","sha256":"$hash","file":"${packFile.name}","records":${bundle.allIds().size},"bytes":${pack.toByteArray().size}}""" + "\n",
    )
    println()
    println("content pack: ${packFile.path}")
}
