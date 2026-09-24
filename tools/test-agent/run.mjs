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
  const needsArgs = new Set(["game.heroes.hero_overview", "game.heroes.hero_star_up", "game.inventory.equipment_detail", "game.inventory.item_detail",
    "game.world.encounter_preview", "game.battle.battle_main", "game.boot.slot_delete_confirm", "game.onboarding.hero_create_name",
    "game.onboarding.hero_create_confirm", "game.economy.purchase_confirm", "game.economy.recruit_result_single"]);
  for (const screenId of registered) {
    if (needsArgs.has(screenId) || screenId === "game.boot.splash") continue;
    try {
      await client.call("screen.open", { screenId });
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
