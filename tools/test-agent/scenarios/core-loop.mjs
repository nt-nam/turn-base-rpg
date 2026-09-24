import { assert, sleep } from "../automation-client.mjs";

const S = (id) => `game.${id}`;
const gold = (state) => state.balances["currency.gold"] ?? 0;
const gem = (state) => state.balances["currency.gem"] ?? 0;

export const name = "core-loop";
export const description = "Create a hero, claim check-in, shop, explore, battle, recruit, equip, lineup, quests, settings";

export async function run(agent, shot) {
  await agent.waitScreen(S("boot.main_menu"), 20_000);
  await shot("01-main-menu");

  await agent.tap(S("boot.main_menu/new_game"));
  await agent.waitScreen(S("onboarding.hero_create_class"));
  await shot("02-choose-hero");
  await agent.tap(S("onboarding.hero_create_class/pick/aldric"));
  await agent.waitScreen(S("onboarding.hero_create_name"));
  await agent.type(S("onboarding.hero_create_name/name"), "Agent Tester");
  await agent.tap(S("onboarding.hero_create_name/next"));
  await agent.waitScreen(S("onboarding.hero_create_confirm"));
  await shot("03-confirm");
  await agent.tap(S("onboarding.hero_create_confirm/start"));
  await agent.waitScreen(S("world.world_explore"));
  await sleep(400);
  await shot("04-world");

  let state = await agent.state();
  assert(state.name === "Agent Tester", "player name stored");
  assert(gold(state) === 300 && gem(state) === 20, `starting grants 300 gold / 20 gems, got ${JSON.stringify(state.balances)}`);
  assert(state.heroes.length === 1 && state.heroes[0].hero === "hero.aldric", "starter hero");
  await agent.assertInvariants("new game");

  await agent.tap(S("world.world_explore/menu"));
  await agent.waitScreen(S("world.pause_menu"));
  await shot("05-pause-menu");
  await agent.tap(S("world.pause_menu/checkin"));
  await agent.waitScreen(S("economy.daily_checkin"));
  await agent.tap(S("economy.daily_checkin/claim"));
  state = await agent.waitFor(async () => { const next = await agent.state(); return next.checkinDays === 1 ? next : null; }, 5000, "check-in claimed");
  assert(gold(state) === 400, `check-in day 1 grants 100 gold, balance ${gold(state)}`);
  await shot("06-checkin");
  await agent.tap(S("economy.daily_checkin/back"));
  await agent.waitScreen(S("world.world_explore"));

  await agent.tap(S("world.world_explore/menu"));
  await agent.tap(S("world.pause_menu/shop"));
  await agent.waitScreen(S("economy.shop_home"));
  await shot("07-shop");
  await agent.tap(S("economy.shop_home/buy/item.food_t1"));
  await agent.waitScreen(S("economy.purchase_confirm"));
  await agent.tap(S("economy.purchase_confirm/confirm"));
  await agent.waitScreen(S("economy.shop_home"));
  state = await agent.state();
  assert(state.items["item.food_t1"] === 1, `bought food: ${JSON.stringify(state.items)}`);
  await agent.assertInvariants("shop");
  await agent.tap(S("economy.shop_home/back"));
  await agent.waitScreen(S("world.world_explore"));

  const world = await agent.call("world.info");
  assert(world.encounters.length > 0, "starting map has an encounter");
  const encounter = world.encounters.find((entry) => entry.encounter) ?? world.encounters[0];
  await agent.call("world.moveTo", { x: encounter.x, y: encounter.y });
  await agent.waitFor(async () => (await agent.node(S("world.world_explore/inspect_enemy")))?.enabled, 20_000, "reach enemy");
  await shot("08-near-enemy");
  await agent.tap(S("world.world_explore/inspect_enemy"));
  await agent.waitScreen(S("world.encounter_preview"));
  await shot("09-encounter-preview");
  await agent.tap(S("world.encounter_preview/fight"));
  await agent.waitScreen(S("battle.battle_main"));
  await sleep(600);
  await shot("10-battle");

  const battle = await agent.waitFor(async () => { const info = await agent.call("battle.state"); return info.awaitingPlayer ? info : null; }, 15_000, "player turn");
  const firstCommand = battle.commands[0];
  await agent.call("battle.command", { skill: firstCommand.skill, target: firstCommand.target });
  await sleep(500);
  await shot("11-battle-after-command");
  await agent.call("battle.auto", { enabled: true });
  await agent.waitFor(async () => {
    const screen = await agent.screen();
    return ["battle.battle_victory", "battle.battle_defeat", "battle.battle_draw"].map(S).includes(screen);
  }, 90_000, "battle result");
  await shot("12-battle-result");
  const afterBattle = await agent.state();
  assert(afterBattle.counters.enemies_defeated >= 0, "counters exist");
  await agent.assertInvariants("battle");
  await agent.tap((await agent.tree()).find((node) => node.testId.endsWith("/continue")).testId);
  await agent.waitScreen(S("world.world_explore"));

  await agent.tap(S("world.world_explore/menu"));
  await agent.tap(S("world.pause_menu/recruit"));
  await agent.waitScreen(S("economy.recruit_home"));
  await shot("13-recruit");
  await agent.tap(S("economy.recruit_home/recruit"));
  await agent.waitScreen(S("economy.recruit_result_single"));
  await shot("14-recruit-result");
  state = await agent.state();
  assert(state.heroes.length === 2, "recruited a second hero");
  await agent.tap(S("economy.recruit_result_single/back"));

  await agent.call("screen.open", { screenId: S("debug.debug_cheats") });
  await agent.waitScreen(S("debug.debug_cheats"));
  await agent.tap(S("debug.debug_cheats/gem"));
  await agent.tap(S("debug.debug_cheats/back"));
  await agent.call("screen.open", { screenId: S("economy.shop_home"), args: { tab: "equipment" } });
  await agent.waitScreen(S("economy.shop_home"));
  await agent.tap(S("economy.shop_home/buy/equip.sword_000"));
  await agent.tap(S("economy.purchase_confirm/confirm"));
  await agent.waitScreen(S("economy.shop_home"));
  state = await agent.state();
  const sword = state.equipment.find((entry) => entry.equipment === "equip.sword_000");
  assert(sword, "bought a sword");
  const starter = state.heroes.find((entry) => entry.hero === "hero.aldric").instance;
  await agent.call("screen.open", { screenId: S("heroes.hero_overview"), args: { hero: starter } });
  await agent.waitScreen(S("heroes.hero_overview"));
  const powerBefore = (await agent.node(S("heroes.hero_overview/power"))).text;
  await agent.tap(S("heroes.hero_overview/equip/weapon"));
  await agent.waitScreen(S("inventory.bag_equipment"));
  await agent.tap(S(`inventory.bag_equipment/equip/${sword.instance}`));
  await agent.waitScreen(S("heroes.hero_overview"));
  const powerAfter = (await agent.node(S("heroes.hero_overview/power"))).text;
  await shot("15-hero-equipped");
  assert(powerBefore !== powerAfter, `equipping changes power (${powerBefore} -> ${powerAfter})`);
  await agent.assertInvariants("equip");

  await agent.call("screen.open", { screenId: S("heroes.lineup_editor") });
  await agent.waitScreen(S("heroes.lineup_editor"));
  const recruit = (await agent.state()).heroes.find((entry) => entry.instance !== starter).instance;
  await agent.tap(S("heroes.lineup_editor/cell/0-2"));
  await agent.tap(S(`heroes.lineup_editor/assign/${recruit}`));
  await agent.waitFor(async () => (await agent.state()).lineup.length === 2, 5000, "lineup of two");
  await shot("16-lineup");

  for (const screenId of ["progression.quest_side", "progression.achievement_list", "heroes.hero_roster", "inventory.bag_consumables"]) {
    await agent.call("screen.open", { screenId: S(screenId) });
    await agent.waitScreen(S(screenId));
    await shot(`17-${screenId.replace(".", "-")}`);
  }

  await agent.call("screen.open", { screenId: S("settings.settings_home") });
  await agent.waitScreen(S("settings.settings_home"));
  await agent.tap(S("settings.settings_home/language/en"));
  await sleep(200);
  await shot("18-settings-english");
  const title = await agent.node(S("settings.settings_home/title"));
  assert(title.text === "Settings", `english title, got ${title.text}`);
  await agent.tap(S("settings.settings_home/language/vi"));
  await agent.assertInvariants("settings");
}
