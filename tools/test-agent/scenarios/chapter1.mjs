import { assert, sleep } from "../automation-client.mjs";

const S = (id) => `game.${id}`;
export const name = "chapter1";
export const description = "Play chapter 1 end to end: elder, intruder, smith, traveler, west gate";

const questDone = async (agent, questId) => (await agent.state()).quests.find((quest) => quest.quest === questId)?.completed === true;

async function walkTo(agent, x, y, arrived, label) {
  await agent.call("world.moveTo", { x, y });
  await agent.waitFor(async () => arrived(await agent.call("world.info")), 45_000, `reach ${label}`);
}

async function talk(agent, shot, npcId, label) {
  const world = await agent.call("world.info");
  const npc = world.npcs.find((entry) => entry.npc === npcId);
  assert(npc, `${npcId} is placed on ${world.map}`);
  await walkTo(agent, npc.x, npc.y, (info) => info.nearbyNpc === npcId, label);
  await agent.tap(S("world.world_explore/talk"));
  await agent.waitScreen(S("world.npc_dialogue"));
  await shot(`dialogue-${label}`);
  for (let step = 0; step < 8; step += 1) {
    const screen = await agent.screen();
    if (screen === S("world.world_explore")) return;
    if (screen === S("world.dialogue_choice")) {
      await agent.tap(S("world.dialogue_choice/choice/0"));
      continue;
    }
    const tree = await agent.tree();
    const button = ["next", "answer", "close"].map((key) => `${S("world.npc_dialogue")}/${key}`).find((id) => tree.some((node) => node.testId === id && node.enabled));
    if (!button) break;
    await agent.tap(button);
  }
  await agent.waitScreen(S("world.world_explore"));
}

async function winIntruder(agent, shot) {
  for (let attempt = 1; attempt <= 3; attempt += 1) {
    const world = await agent.call("world.info");
    const encounter = world.encounters.find((entry) => entry.encounter === "encounter.dawnvillage_01.e0");
    await walkTo(agent, encounter.x, encounter.y, (info) => info.nearbyEncounter === "encounter.dawnvillage_01.e0", "intruder");
    await agent.tap(S("world.world_explore/inspect_enemy"));
    await agent.tap(S("world.encounter_preview/fight"));
    await agent.waitScreen(S("battle.battle_main"));
    await agent.call("battle.auto", { enabled: true });
    await agent.waitFor(async () => {
      await agent.settle();
      return (await agent.screen()).startsWith(S("battle.battle_")) && (await agent.screen()) !== S("battle.battle_main");
    }, 90_000, "battle result");
    const won = (await agent.screen()) === S("battle.battle_victory");
    await shot(`intruder-attempt-${attempt}`);
    await agent.tap((await agent.tree()).find((node) => node.testId.endsWith("/continue")).testId);
    await agent.waitScreen(S("world.world_explore"));
    if (won) return;
  }
  throw new Error("could not defeat the intruder in three attempts");
}

export async function run(agent, shot) {
  await agent.passFirstRun();
  await agent.waitScreen(S("boot.main_menu"), 20_000);
  await agent.tap(S("boot.main_menu/new_game"));
  await agent.tap(S("onboarding.hero_create_class/pick/aldric"));
  await agent.type(S("onboarding.hero_create_name/name"), "Chapter Runner");
  await agent.tap(S("onboarding.hero_create_name/next"));
  await agent.tap(S("onboarding.hero_create_confirm/start"));
  await agent.waitScreen(S("world.world_explore"));
  await sleep(300);
  const objective = await agent.node(S("world.world_explore/objective"));
  assert(objective?.text?.length > 0, `objective shown on the HUD: ${JSON.stringify(objective)}`);
  await shot("01-objective");

  await talk(agent, shot, "npc.dawn_elder", "elder");
  assert(await questDone(agent, "quest.main.ch1_01"), "talked to the elder");

  await winIntruder(agent, shot);
  assert(await questDone(agent, "quest.main.ch1_02"), "intruder defeated");

  await talk(agent, shot, "npc.dawn_smith", "smith");
  assert(await questDone(agent, "quest.main.ch1_03"), "visited the smith");

  await talk(agent, shot, "npc.dawn_traveler", "traveler");
  assert(await questDone(agent, "quest.main.ch1_04"), "heard the traveler");

  const world = await agent.call("world.info");
  const westGate = world.teleports.find((entry) => entry.target === "wasteland1");
  assert(westGate, "village has a gate to the ash waste");
  await walkTo(agent, westGate.x, westGate.y, (info) => info.nearbyTeleport === "wasteland1", "west gate");
  await agent.tap(S("world.world_explore/travel"));
  await agent.waitScreen(S("world.world_explore"));
  await agent.waitFor(async () => (await agent.call("world.info")).map === "map.ashwaste_01", 15_000, "arrive in the ash waste");
  await sleep(400);
  await shot("02-ash-waste");
  assert(await questDone(agent, "quest.main.ch1_05"), "chapter 1 finale reached");

  await agent.call("screen.open", { screenId: S("progression.quest_side") });
  await agent.waitScreen(S("progression.quest_side"));
  await shot("03-quests");
  await agent.assertInvariants("chapter 1");
}
