package com.pxworld.client

import com.pxworld.client.screens.DefaultScreens
import com.pxworld.content.ContentLoader
import com.pxworld.content.compiler.ContentCompilation
import com.pxworld.screens.GameScreenId
import com.pxworld.screens.ReleaseSeason
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClientTextTest {

    private val sources = File(System.getProperty("clientSources"))
    private val bundle = ContentLoader.load(ContentCompilation.readTree(File(System.getProperty("contentDir"))))
    private val keyPattern = Regex(""""(ui\.[a-z0-9_.]+[a-z0-9_])"""")
    private val dynamicFamilies = mapOf(
        "ui.pause." to "heroes lineup bag shop recruit checkin quests achievements settings map minimap tracker codex exchange idle replays tips".split(" "),
        "ui.slot." to "weapon armor jewelry support".split(" "),
        "ui.stat." to "hp attack defense speed crit_rate crit_damage accuracy evasion effect_hit effect_resistance".split(" "),
        "ui.settings." to "music sound audio graphics controls language_page accessibility privacy playtime_report help_center faq bug_report credits fullscreen reduced_motion analytics".split(" "),
        "ui.language." to "vi en".split(" "),
        "ui.debug." to "screen_jump cheats atlas_browser grant_gold grant_gem grant_food grant_hero battle_sandbox map_inspector save_editor flags performance logs locale_preview automation".split(" "),
        "ui.debug.flag." to "performance colliders fast_battles".split(" "),
        "ui.battle.pause_" to "timeline weakness inspect skills".split(" "),
        "ui.skill.slot." to "basic skill ultimate".split(" "),
        "ui.skill.target." to "single_enemy enemy_row enemy_lane all_enemies single_ally all_allies self lowest_hp_allies random_enemies".split(" "),
        "ui.heroes.link." to "stats skills gear lore level_up compare".split(" "),
        "ui.heroes.tool." to "lineup presets analysis synergy counters".split(" "),
        "ui.bag.action." to "assign compare upgrade".split(" "),
        "ui.recruit.link." to "rates history pity".split(" "),
        "ui.outcome." to "victory defeat draw".split(" "),
        "ui.quests.objective." to "win_encounter collect_item collect_item_category defeat_enemies reach_map talk_to_npc".split(" "),
        "ui.region." to "dawnvillage mistgarden ashwaste".split(" "),
        "ui.counter." to "heroes_recruited enemies_defeated battles_won gold_earned gems_spent equipment_obtained".split(" "),
        "ui.result." to "rewards breakdown level_ups quests replays".split(" "),
        "ui.tips." to "1.title 1.body 2.title 2.body 3.title 3.body 4.title 4.body 5.title 5.body 6.title 6.body".split(" "),
        "ui.glossary." to "energy.title energy.body cooldown.title cooldown.body counter.title counter.body depth.title depth.body star.title star.body shield.title shield.body status.title status.body power.title power.body".split(" "),
        "ui.controls." to "move.title move.body back.title back.body debug.title debug.body touch.title touch.body".split(" "),
        "ui.credits." to "team.title team.body engine.title engine.body art.title art.body fonts.title fonts.body".split(" "),
        "ui.help." to "start.title start.body battle.title battle.body heroes.title heroes.body economy.title economy.body saves.title saves.body".split(" "),
        "ui.faq." to "1.q 1.a 2.q 2.a 3.q 3.a 4.q 4.a 5.q 5.a".split(" "),
        "ui.patch." to "battle.title battle.body heroes.title heroes.body economy.title economy.body saves.title saves.body tools.title tools.body".split(" "),
        "ui.tutorial_move." to "title body".split(" "),
        "ui.tutorial_battle." to "title body".split(" "),
        "ui.tutorial_lineup." to "title body".split(" "),
        "ui.tutorial_reward." to "title body".split(" "),
    )

    private fun usedKeys(): Set<String> =
        sources.walkTopDown().filter { it.extension == "kt" }
            .flatMap { file -> keyPattern.findAll(file.readText()).map { it.groupValues[1] } }
            .filterNot { key -> dynamicFamilies.keys.any { key == it.dropLast(1) || it.startsWith(key + ".") } }
            .toSet() + dynamicFamilies.flatMap { (prefix, suffixes) -> suffixes.map { prefix + it } }

    @Test
    fun `every ui key used by the client exists in every locale`() {
        val keys = usedKeys()
        assertTrue(keys.size > 100)
        listOf("vi", "en").forEach { locale ->
            val table = bundle.localization[locale].orEmpty()
            assertEquals(emptyList(), keys.filterNot { it in table }.sorted(), "missing $locale keys")
        }
    }

    @Test
    fun `every registered screen belongs to the launch catalog`() {
        val registered = DefaultScreens.registry().registered
        assertTrue(registered.size >= 110)
        assertTrue(registered.all { it.season == ReleaseSeason.LAUNCH })
        assertTrue(GameScreenId.BOOT_SPLASH in registered)
    }
}
