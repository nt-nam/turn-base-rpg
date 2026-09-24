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
  const denied = /\/(exit|delete|flee|main_menu|confirm|consume|recruit|claim|buy|assign|clear_cell|equip|unequip|use_on|equip_to|start|play|continue|gold|gem|food)(\/|$)|joystick|\/cell\//;
  const registered = await client.call("screen.registered");
  await client.waitScreen("game.boot.main_menu", 20_000);
  if (await client.node("game.boot.main_menu/continue")) {
    await client.tap("game.boot.main_menu/continue");
    await client.waitScreen("game.world.world_explore");
  }
  const state = await client.state();
  const world = state.loaded ? await client.call("world.info").catch(() => null) : null;
  const firstHero = state.heroes?.[0]?.instance;
  const argsFor = {
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
  const destructive = new Set(["game.boot.splash", "game.boot.main_menu", "game.world.world_explore"]);
  for (const screenId of registered) {
    if (destructive.has(screenId)) continue;
    if (screenId in argsFor && !argsFor[screenId]) {
      client.trace.push(`explore skipped ${screenId}: no data for its arguments`);
      continue;
    }
    try {
      await client.call("screen.open", { screenId, args: argsFor[screenId] ?? {} });
      if (screenId === "game.battle.battle_main") {
        await client.call("battle.auto", { enabled: true });
        await client.waitFor(async () => (await client.screen()).startsWith("game.battle.battle_") && (await client.screen()) !== "game.battle.battle_main", 90_000, "battle result");
        await client.call("screen.back");
        continue;
      }
      await sleep(250);
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
