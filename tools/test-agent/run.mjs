import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { AutomationClient, sleep } from "./automation-client.mjs";

const here = dirname(fileURLToPath(import.meta.url));
const root = resolve(here, "../..");
const args = Object.fromEntries(process.argv.slice(2).map((arg) => arg.replace(/^--/, "").split("=")));
const port = args.port ?? "47017";
const mode = args.mode ?? "scenario";
const stamp = new Date().toISOString().replace(/[:.]/g, "-");
const reportDir = resolve(args.out ?? resolve(here, "reports", stamp));
mkdirSync(reportDir, { recursive: true });

const agent = await new AutomationClient(`ws://127.0.0.1:${port}`).connect();
const shots = [];
const shot = async (label) => {
  const path = resolve(reportDir, `${label}.png`);
  await agent.screenshot(path);
  shots.push(path);
};

const catalog = JSON.parse(readFileSync(resolve(root, "docs/screens/screens.json"), "utf8"));
const launchGameScreens = catalog.filter((screen) => screen.surface === "game" && screen.season === "launch").map((screen) => screen.id);
const report = { mode, startedAt: new Date().toISOString(), status: "passed", error: null, trace: agent.trace, screenshots: shots };

try {
  if (mode === "scenario") {
    const scenario = await import(`./scenarios/${args.scenario ?? "core-loop"}.mjs`);
    report.scenario = scenario.name;
    await scenario.run(agent, shot);
  } else if (mode === "explore") {
    await explore(agent, shot);
  } else {
    throw new Error(`unknown mode ${mode}`);
  }
} catch (error) {
  report.status = "failed";
  report.error = error.stack ?? String(error);
  await shot("failure").catch(() => {});
}

const registered = await agent.call("screen.registered").catch(() => []);
report.coverage = {
  visited: [...agent.visited].sort(),
  visitedCount: agent.visited.size,
  registeredCount: registered.length,
  launchCatalogCount: launchGameScreens.length,
  registeredNotVisited: registered.filter((id) => !agent.visited.has(id)),
  launchPercent: Math.round((agent.visited.size / launchGameScreens.length) * 1000) / 10,
};
report.finishedAt = new Date().toISOString();
writeFileSync(resolve(reportDir, "report.json"), JSON.stringify(report, null, 2));
if (args.exit !== "false") await agent.call("app.exit").catch(() => {});
agent.close();
console.log(`${report.status.toUpperCase()} ${mode} — visited ${report.coverage.visitedCount}/${report.coverage.registeredCount} registered screens (${report.coverage.launchPercent}% of launch catalog)`);
if (report.error) console.log(report.error);
console.log(`report: ${reportDir}`);
process.exit(report.status === "passed" ? 0 : 1);

async function explore(client, capture) {
  const denied = /\/(exit|delete|flee|main_menu|confirm|consume|recruit|claim|buy|assign|clear_cell|equip|unequip|use_on|equip_to|start|play|continue|gold|gem|food|language|dismiss|salvage|sell_selected|fight|go|travel|export|submit|upgrade|feed|exchange|lock|apply|save|watch|fullscreen|text|speed|analytics|reduced_motion|write_sample|toggle|more|less|choose_hero|pause|again)(\/|$)|joystick|\/cell\//;
  const registered = await client.call("screen.registered");
  const primeMerge = async () => {
    await client.call("screen.open", { screenId: "game.debug.debug_cheats" });
    await client.tap("game.debug.debug_cheats/hero");
    await client.call("screen.back");
  };
  await client.call("app.resetFirstRun");
  await client.screen();
  await sleep(1200);
  await client.passFirstRun();
  await client.waitScreen("game.boot.main_menu", 20_000);
  if (await client.node("game.boot.main_menu/continue")) {
    await client.tap("game.boot.main_menu/continue");
    await client.waitScreen("game.world.world_explore");
  }
  if ((await client.screen()) === "game.world.world_explore") await primeMerge();
  const state = await client.state();
  const world = state.loaded ? await client.call("world.info").catch(() => null) : null;
  const firstHero = state.heroes?.[0]?.instance;
  const firstEquipment = state.equipment?.[0]?.instance;
  const firstItem = Object.keys(state.items ?? {})[0];
  const firstQuest = state.quests?.[0]?.quest;
  const encounterId = world?.encounters?.find((entry) => entry.encounter)?.encounter;
  const argsFor = {
    "game.heroes.hero_stats": firstHero && { hero: firstHero },
    "game.heroes.hero_skills": firstHero && { hero: firstHero },
    "game.heroes.hero_equipment": firstHero && { hero: firstHero },
    "game.heroes.hero_lore": firstHero && { hero: firstHero },
    "game.heroes.hero_level_up": firstHero && { hero: firstHero },
    "game.heroes.hero_compare": firstHero && { first: firstHero },
    "game.heroes.hero_dismiss": state.heroes?.[1] && { hero: state.heroes[1].instance },
    "game.heroes.hero_merge_confirm": null,
    "game.battle.replay_viewer": null,
    "game.inventory.equipment_compare": firstEquipment && { equipment: firstEquipment, hero: firstHero },
    "game.inventory.equipment_assign": firstEquipment && { equipment: firstEquipment },
    "game.inventory.equipment_upgrade": firstEquipment && { equipment: firstEquipment },
    "game.inventory.equipment_salvage": firstEquipment && { equipment: firstEquipment },
    "game.inventory.sell_confirm": firstItem && { item: firstItem },
    "game.inventory.item_use_target": firstItem && { item: firstItem },
    "game.inventory.item_use_result": firstHero && { hero: firstHero, before: "1" },
    "game.economy.offer_detail": { kind: "item", id: "item.food_t1" },
    "game.economy.purchase_result": { kind: "item", id: "item.food_t1" },
    "game.economy.checkin_claim": { day: "1" },
    "game.progression.quest_detail": firstQuest && { quest: firstQuest },
    "game.progression.quest_claim": firstQuest && { quest: firstQuest },
    "game.progression.achievement_detail": { achievement: "achievement.victor" },
    "game.battle.retry_confirm": encounterId && { encounter: encounterId },
    "game.world.map_transition": { map: "map.dawnvillage_01" },
    "game.heroes.hero_overview": firstHero && { hero: firstHero },
    "game.heroes.hero_star_up": firstHero && { hero: firstHero },
    "game.inventory.equipment_detail": state.equipment?.[0] && { equipment: state.equipment[0].instance },
    "game.inventory.item_detail": Object.keys(state.items ?? {})[0] && { item: Object.keys(state.items)[0] },
    "game.world.encounter_preview": world?.encounters?.find((entry) => entry.encounter) && { encounter: world.encounters.find((entry) => entry.encounter).encounter },
    "game.battle.battle_main": world?.encounters?.find((entry) => entry.encounter) && { encounter: world.encounters.find((entry) => entry.encounter).encounter },
    "game.boot.slot_delete_confirm": state.loaded && { slot: (await client.call("session.info")).slot },
    "game.onboarding.hero_create_name": { hero: "hero.aldric" },
    "game.onboarding.hero_create_confirm": { hero: "hero.aldric", name: "Explorer" },
    "game.economy.purchase_confirm": { kind: "item", id: "item.food_t1" },
    "game.economy.recruit_result_single": firstHero && { hero: firstHero },
    "game.battle.battle_victory": world?.encounters?.find((entry) => entry.encounter) && { encounter: world.encounters.find((entry) => entry.encounter).encounter },
    "game.battle.battle_defeat": world?.encounters?.find((entry) => entry.encounter) && { encounter: world.encounters.find((entry) => entry.encounter).encounter },
    "game.battle.battle_draw": world?.encounters?.find((entry) => entry.encounter) && { encounter: world.encounters.find((entry) => entry.encounter).encounter },
  };
  const destructive = new Set(["game.boot.splash", "game.boot.main_menu", "game.world.world_explore", "game.boot.legal_notice", "game.boot.privacy_consent", "game.boot.language_pick", "game.battle.flee_confirm", "game.battle.battle_pause", "game.battle.skill_select", "game.battle.target_select", "game.battle.unit_inspect", "game.battle.turn_timeline", "game.battle.weakness_hint"]);
  const copies = state.heroes?.filter((entry) => entry.hero === state.heroes?.[0]?.hero && entry.star === state.heroes?.[0]?.star) ?? [];
  if (copies.length >= 2) argsFor["game.heroes.hero_merge_confirm"] = { target: copies[0].instance, fodder: copies[copies.length - 1].instance };
  const deferred = ["game.battle.replay_viewer"];
  for (const screenId of [...registered.filter((id) => !deferred.includes(id)), ...deferred]) {
    if (screenId === "game.battle.replay_viewer") {
      const replay = (await client.call("replays.list"))[0];
      if (replay) argsFor[screenId] = { encounter: replay.encounter, replay: replay.id };
    }
    if (destructive.has(screenId)) continue;
    if (screenId in argsFor && !argsFor[screenId]) {
      client.trace.push(`explore skipped ${screenId}: no data for its arguments`);
      continue;
    }
    try {
      await client.call("screen.open", { screenId, args: argsFor[screenId] ?? {} });
      if (screenId === "game.battle.replay_viewer") {
        await client.waitFor(async () => (await client.screen()) !== "game.battle.replay_viewer" || (await client.tree()).some((node) => node.testId.includes("toast")), 60_000, "replay finished").catch(() => {});
        await capture("explore-game-battle-replay_viewer");
        await client.call("screen.back");
        continue;
      }
      if (screenId === "game.battle.battle_main") {
        await client.settle("game.battle.battle_main");
        for (const modal of ["game.battle.battle_pause", "game.battle.skill_select", "game.battle.target_select", "game.battle.unit_inspect", "game.battle.turn_timeline", "game.battle.weakness_hint", "game.battle.flee_confirm", "game.battle.battle_log"]) {
          await client.call("screen.open", { screenId: modal });
          await sleep(200);
          await client.screen();
          await capture(`explore-${modal.replace(/\./g, "-")}`);
          await client.assertInvariants(`explore ${modal}`);
          await client.call("screen.back");
          await sleep(100);
        }
        await client.call("battle.auto", { enabled: true });
        await client.waitFor(async () => (await client.screen()).startsWith("game.battle.battle_") && (await client.screen()) !== "game.battle.battle_main", 90_000, "battle result");
        await client.settle();
        await client.call("screen.back");
        continue;
      }
      if (registered.includes("game.battle.replay_viewer") && screenId === "game.battle.replay_list") {
        await sleep(200);
      }
      await sleep(250);
      await client.settle(screenId);
      await client.screen();
      await capture(`explore-${screenId.replace(/\./g, "-")}`);
      const buttons = (await client.tree()).filter((node) => node.enabled && node.type === "TextButton" && node.testId.startsWith(screenId) && !denied.test(node.testId));
      for (const button of buttons.slice(0, 6)) {
        const before = await client.screen();
        await client.call("ui.tap", { testId: button.testId }).catch(() => {});
        await sleep(150);
        const after = await client.screen();
        if (after !== before) await client.call("screen.back").catch(() => {});
      }
      await client.assertInvariants(`explore ${screenId}`);
    } catch (error) {
      client.trace.push(`explore error on ${screenId}: ${error.message}`);
    }
  }
}
