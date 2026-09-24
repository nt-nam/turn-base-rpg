import { readFileSync, writeFileSync, mkdirSync, rmSync, existsSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "../..");
const legacy = (path) => resolve(root, "assets/data", path);
const out = resolve(root, "content");

const lenientJson = (text) => JSON.parse(
  text.replace(/^﻿/, "").replace(/^\s*\/\/.*$/gm, "").replace(/,(\s*[\]}])/g, "$1"),
);
const readLegacy = (path) => lenientJson(readFileSync(legacy(path), "utf8"));

const vi = {};
const en = {};
const report = [];
const text = (key, viText, enText) => {
  vi[key] = viText;
  if (enText !== undefined) en[key] = enText;
  return key;
};
const note = (line) => report.push(line);

const LEGACY_CLASS_TO_ID = {
  Warrior: "class.warrior",
  Assassin: "class.assassin",
  Mage: "class.mage",
  Archer: "class.ranger",
  Ranger: "class.ranger",
  Support: "class.support",
  Tank: "class.tank",
};
const CLASS_BY_ATLAS = {
  warrior_Knight: "warrior",
  assassin_Knight: "assassin",
  mage_Knight: "mage",
  ranger_Knight: "ranger",
  support_Knight: "support",
  tank_Knight: "tank",
};
const CLASS_NAMES_EN = { warrior: "Warrior", assassin: "Assassin", mage: "Mage", ranger: "Ranger", support: "Support", tank: "Tank" };
const CLASS_ROLES = { warrior: "bruiser", assassin: "burst", mage: "caster", ranger: "marksman", support: "healer", tank: "guardian" };

const HERO_IDENTITIES = {
  warrior: { slug: "aldric", name: "Aldric", titleEn: "Dawnblade" },
  assassin: { slug: "nyx", name: "Nyx", titleEn: "Duskstep" },
  mage: { slug: "selene", name: "Selene", titleEn: "Emberseer" },
  ranger: { slug: "fenn", name: "Fenn", titleEn: "Farshot" },
  support: { slug: "mirae", name: "Mirae", titleEn: "Lampkeeper" },
  tank: { slug: "borin", name: "Borin", titleEn: "Ironwall" },
};

const LEGACY_MAP_TO_ID = (legacyName) => {
  const village = legacyName.match(/^village_(\d)$/);
  if (village) return `map.dawnvillage_${String(Number(village[1]) + 1).padStart(2, "0")}`;
  const garden = legacyName.match(/^garden(\d)$/);
  if (garden) return `map.mistgarden_${String(Number(garden[1]) + 1).padStart(2, "0")}`;
  const waste = legacyName.match(/^wasteland(\d)$/);
  if (waste) return `map.ashwaste_${waste[1].padStart(2, "0")}`;
  throw new Error(`unknown legacy map ${legacyName}`);
};

const CURRENCY_BY_LEGACY = { coin: "currency.gold", gem: "currency.gem", gem_pink: "currency.gem" };

const scaleStats = (legacyStats) => ({
  hp: legacyStats.hp * 10,
  attack: legacyStats.atk * 10,
  defense: legacyStats.def * 10,
  speed: 80 + legacyStats.agi * 3,
  critRate: legacyStats.crit * 20,
  critDamage: 1500,
  accuracy: 0,
  evasion: 0,
  effectHit: 0,
  effectResistance: 0,
});
note("Chỉ số anh hùng/quái: hp×10, atk×10, def×10, speed = 80 + agi×3, critRate‰ = crit×20, critDamage 1500‰.");

const currencies = [
  ["gold", "Vàng", "Gold", "Tiền tệ cơ bản từ trận đấu và bán đồ.", "Basic currency from battles and selling items.", "icon:currency/gold"],
  ["gem", "Ngọc", "Gem", "Tiền tệ cao cấp cho chiêu mộ và cửa hàng ngọc.", "Premium currency for recruiting and the gem shop.", "icon:currency/gem"],
  ["energy", "Năng lượng", "Energy", "Hồi theo thời gian, dùng để vào trận ngoài cốt truyện.", "Regenerates over time; spent to enter non-story battles.", "icon:currency/energy"],
  ["guild_token", "Huy hiệu bang", "Guild Token", "Nhận từ hoạt động bang hội.", "Earned from guild activities.", "icon:currency/guild_token"],
  ["event_token", "Token sự kiện", "Event Token", "Nhận trong sự kiện, đổi ở cửa hàng sự kiện.", "Earned during events; spent in the event shop.", "icon:currency/event_token"],
].map(([slug, nameVi, nameEn, descVi, descEn, icon]) => ({
  id: `currency.${slug}`,
  name: text(`currency.${slug}.name`, nameVi, nameEn),
  description: text(`currency.${slug}.description`, descVi, descEn),
  icon,
}));

const legacyCharacters = readLegacy("base/character_base.json");
const heroClasses = legacyCharacters.map((character) => {
  const classSlug = CLASS_BY_ATLAS[character.nameRegion];
  const id = `class.${classSlug}`;
  const counters = character.counters.map((name) => LEGACY_CLASS_TO_ID[name]);
  if (character.counters.includes("Archer") || character.weakAgainst.includes("Archer")) {
    note(`${id}: đổi tham chiếu lớp "Archer" thành class.ranger.`);
  }
  return {
    id,
    name: text(`${id}.name`, character.name, CLASS_NAMES_EN[classSlug]),
    role: CLASS_ROLES[classSlug],
    counters,
    legacyWeakAgainst: character.weakAgainst.map((name) => LEGACY_CLASS_TO_ID[name]),
  };
});
for (const heroClass of heroClasses) {
  const derivedWeak = heroClasses.filter((other) => other.counters.includes(heroClass.id)).map((other) => other.id).sort();
  const declaredWeak = [...heroClass.legacyWeakAgainst].sort();
  if (JSON.stringify(derivedWeak) !== JSON.stringify(declaredWeak)) {
    note(`${heroClass.id}: weakAgainst cũ ${JSON.stringify(declaredWeak)} không khớp với counters của lớp khác ${JSON.stringify(derivedWeak)}; nguồn sự thật mới chỉ là counters.`);
  }
  for (const target of heroClass.counters) {
    const reverse = heroClasses.find((c) => c.id === target);
    if (reverse && reverse.counters.includes(heroClass.id)) note(`${heroClass.id} và ${target} khắc chế lẫn nhau — cần creator quyết định.`);
  }
  delete heroClass.legacyWeakAgainst;
}

const statusSpecs = [
  ["stun", "Choáng", "Stun", { kind: "stun" }, 1, 1],
  ["silence", "Câm lặng", "Silence", { kind: "silence" }, 2, 1],
  ["taunt", "Khiêu khích", "Taunt", { kind: "taunt" }, 2, 1],
  ["shield", "Khiên", "Shield", { kind: "shield", magnitude: 1500 }, 2, 1],
  ["burn", "Bỏng", "Burn", { kind: "damage_over_time", magnitude: 60 }, 3, 3],
  ["poison", "Độc", "Poison", { kind: "damage_over_time", magnitude: 40 }, 4, 5],
  ["bleed", "Chảy máu", "Bleed", { kind: "damage_over_time", magnitude: 80 }, 2, 2],
  ["regen", "Hồi phục", "Regeneration", { kind: "heal_over_time", magnitude: 50 }, 3, 1],
  ["attack_up", "Tăng công", "Attack Up", { kind: "stat_modifier", stat: "attack", magnitude: 300 }, 3, 1],
  ["attack_down", "Giảm công", "Attack Down", { kind: "stat_modifier", stat: "attack", magnitude: -250 }, 2, 1],
  ["defense_up", "Tăng thủ", "Defense Up", { kind: "stat_modifier", stat: "defense", magnitude: 400 }, 3, 1],
  ["defense_down", "Giảm thủ", "Defense Down", { kind: "stat_modifier", stat: "defense", magnitude: -300 }, 2, 1],
  ["speed_up", "Tăng tốc", "Speed Up", { kind: "stat_modifier", stat: "speed", magnitude: 250 }, 2, 1],
  ["speed_down", "Giảm tốc", "Speed Down", { kind: "stat_modifier", stat: "speed", magnitude: -250 }, 2, 1],
  ["crit_up", "Tăng chí mạng", "Crit Up", { kind: "stat_bonus", stat: "critRate", magnitude: 250 }, 2, 1],
  ["evasion_up", "Tăng né tránh", "Evasion Up", { kind: "stat_bonus", stat: "evasion", magnitude: 300 }, 2, 1],
];
const statuses = statusSpecs.map(([slug, nameVi, nameEn, kind, durationTurns, maxStacks]) => ({
  id: `status.${slug}`,
  name: text(`status.${slug}.name`, nameVi, nameEn),
  ...kind,
  durationTurns,
  maxStacks,
  dispellable: true,
}));
note("crit_up (+250‰ chí mạng) và evasion_up (+300‰ né) là stat_bonus cộng phẳng, vì chỉ số né gốc = 0 khiến hệ số ‰ vô tác dụng.");

const damage = (power, hits = 1) => ({ kind: "damage", power, hits });
const heal = (power) => ({ kind: "heal", power });
const applyStatus = (status, chance = 1000, onSelf = false) => ({ kind: "apply_status", status: `status.${status}`, chance, onSelf });
const skill = (classSlug, slot, key, nameVi, nameEn, descVi, targeting, effects, vfx, cooldownTurns = 0) => {
  const id = `skill.${classSlug}.${key}`;
  return {
    id,
    name: text(`${id}.name`, nameVi, nameEn),
    description: text(`${id}.description`, descVi),
    slot,
    energyCost: slot === "ultimate" ? 100 : 0,
    cooldownTurns: slot === "skill" ? cooldownTurns || 2 : 0,
    targeting,
    effects,
    vfx,
  };
};
const single = { kind: "single_enemy" };
const legacySkillNames = Object.fromEntries(legacyCharacters.map((c) => [CLASS_BY_ATLAS[c.nameRegion], c.skills]));
const skillKits = {
  warrior: [
    skill("warrior", "basic", "slash", legacySkillNames.warrior[0], "Slash", "Chém một kẻ địch.", single, [damage(1000)], "vfx:orange/attack"),
    skill("warrior", "skill", "shield_bash", legacySkillNames.warrior[1], "Shield Bash", "Đập khiên gây sát thương và tăng thủ bản thân.", single, [damage(900), applyStatus("defense_up", 1000, true)], "vfx:orange/attack_big"),
    skill("warrior", "ultimate", "dawn_cleave", "Nhát Chém Bình Minh", "Dawn Cleave", "Chém cả hàng địch, có thể gây choáng.", { kind: "enemy_row" }, [damage(1800), applyStatus("stun", 500)], "vfx:orange/ultimate"),
  ],
  assassin: [
    skill("assassin", "basic", "backstab", legacySkillNames.assassin[0], "Backstab", "Đâm một kẻ địch.", single, [damage(1000)], "vfx:pink/attack"),
    skill("assassin", "skill", "shadow_step", legacySkillNames.assassin[1], "Shadow Step", "Lướt bóng: tăng né tránh rồi đâm hai nhát.", single, [applyStatus("evasion_up", 1000, true), damage(650, 2)], "vfx:pink/attack_big"),
    skill("assassin", "ultimate", "deathmark", "Ấn Tử Thần", "Deathmark", "Tăng chí mạng rồi đâm ba nhát gây chảy máu.", single, [applyStatus("crit_up", 1000, true), damage(700, 3), applyStatus("bleed", 700)], "vfx:pink/ultimate"),
  ],
  mage: [
    skill("mage", "basic", "fireball", legacySkillNames.mage[0], "Fireball", "Cầu lửa có thể gây bỏng.", single, [damage(1000), applyStatus("burn", 300)], "vfx:yellow/attack"),
    skill("mage", "skill", "arcane_storm", legacySkillNames.mage[1], "Arcane Storm", "Bão phép đánh toàn bộ kẻ địch.", { kind: "all_enemies" }, [damage(550)], "vfx:yellow/explode", 3),
    skill("mage", "ultimate", "sunfall", "Mặt Trời Rơi", "Sunfall", "Thiêu toàn bộ kẻ địch, gây bỏng và có thể câm lặng.", { kind: "all_enemies" }, [damage(1200), applyStatus("burn", 800), applyStatus("silence", 350)], "vfx:yellow/ultimate"),
  ],
  ranger: [
    skill("ranger", "basic", "piercing_arrow", legacySkillNames.ranger[0], "Piercing Arrow", "Mũi tên xuyên cả làn.", { kind: "enemy_lane" }, [damage(800)], "vfx:green/attack"),
    skill("ranger", "skill", "volley", legacySkillNames.ranger[1], "Volley", "Bắn hai phát và giảm thủ mục tiêu.", single, [damage(650, 2), applyStatus("defense_down", 800)], "vfx:green/attack_big"),
    skill("ranger", "ultimate", "arrow_rain", "Mưa Tên", "Arrow Rain", "Năm mũi tên rơi ngẫu nhiên.", { kind: "random_enemies", count: 5 }, [damage(750)], "vfx:green/ultimate"),
  ],
  support: [
    skill("support", "basic", "lamp_strike", "Đèn Soi", "Lamp Strike", "Đánh nhẹ một kẻ địch.", single, [damage(700)], "vfx:blue/attack"),
    skill("support", "skill", "mend", legacySkillNames.support[0], "Mend", "Hồi máu hai đồng đội thấp máu nhất.", { kind: "lowest_hp_allies", count: 2 }, [heal(1600), applyStatus("regen")], "vfx:blue/heal"),
    skill("support", "ultimate", "swift_hymn", legacySkillNames.support[1], "Swift Hymn", "Hồi máu và tăng tốc toàn đội.", { kind: "all_allies" }, [heal(1000), applyStatus("speed_up"), { kind: "gain_energy", amount: 15 }], "vfx:blue/ultimate"),
  ],
  tank: [
    skill("tank", "basic", "shield_slam", "Đập Khiên", "Shield Slam", "Đập khiên vào một kẻ địch.", single, [damage(800)], "vfx:green/attack"),
    skill("tank", "skill", "provoke", legacySkillNames.tank[0], "Provoke", "Khiêu khích kẻ địch và dựng khiên.", { kind: "self" }, [applyStatus("taunt"), applyStatus("shield")], "vfx:green/heal", 3),
    skill("tank", "ultimate", "iron_wall", legacySkillNames.tank[1], "Iron Wall", "Dựng khiên cho toàn đội và tăng thủ.", { kind: "all_allies" }, [applyStatus("shield"), applyStatus("defense_up")], "vfx:green/explode"),
  ],
};
note("Bộ kỹ năng: giữ tên kỹ năng tiếng Việt từ character_base.json; ý đồ hiệu ứng từ skill_base.json (armor→defense_up, dodgeChance→evasion_up, critChance→crit_up, heal/targets→lowest_hp_allies, attackTimes→hits). damageReflection chưa có StatusKind — tank dùng khiên + tăng thủ thay thế.");

const heroes = legacyCharacters.map((character) => {
  const classSlug = CLASS_BY_ATLAS[character.nameRegion];
  const identity = HERO_IDENTITIES[classSlug];
  const id = `hero.${identity.slug}`;
  return {
    id,
    name: text(`${id}.name`, identity.name, identity.name),
    title: text(`${id}.title`, character.name, identity.titleEn),
    description: text(`${id}.description`, character.desc),
    classId: `class.${classSlug}`,
    baseStats: scaleStats(character),
    skills: skillKits[classSlug].map((s) => s.id),
    sprite: `sprite:hero/${identity.slug}`,
    starter: classSlug === "warrior",
  };
});

const enemies = Object.entries(CLASS_BY_ATLAS).map(([atlas, classSlug]) => {
  const character = legacyCharacters.find((c) => c.nameRegion === atlas);
  const id = `enemy.bandit_${classSlug}`;
  return {
    id,
    name: text(`${id}.name`, `Thảo khấu ${character.name.toLowerCase()}`, `Bandit ${CLASS_NAMES_EN[classSlug]}`),
    classId: `class.${classSlug}`,
    baseStats: scaleStats(character),
    skills: skillKits[classSlug].map((s) => s.id),
    sprite: `sprite:enemy/bandit_${classSlug}`,
    tags: ["art_pending"],
  };
});
note("Quái: 6 enemy.bandit_* dùng tạm sprite của lớp anh hùng tương ứng (tag art_pending). Atlas 02/04/08/10Knight sẽ được gán khi có concept quái (P3).");

const legacyEncounters = ["village_0_0", "village_0_1", "wasteland1_0", "wasteland1_1", "wasteland1_2", "wasteland1_3", "wasteland1_4", "wasteland1_5"];
const encounterRecord = (legacyName, source) => {
  const [, mapName, index] = legacyName.match(/^(.*)_(\d+)$/);
  const map = LEGACY_MAP_TO_ID(mapName);
  const id = `encounter.${map.slice(4)}.e${index}`;
  const levels = source.grid.map((cell) => cell.level);
  return {
    id,
    name: text(`${id}.name`, `Trận ${map.slice(4)} #${index}`, `${map.slice(4)} battle #${index}`),
    map,
    mapObjectId: Number(index),
    recommendedLevel: Math.max(...levels),
    enemies: source.grid.map((cell) => {
      const [column, row] = cell.grid.split(",").map(Number);
      return { enemy: `enemy.bandit_${CLASS_BY_ATLAS[cell.nameRegion]}`, level: cell.level, star: cell.star, cell: { lane: row, depth: column } };
    }),
    rewards: source.reward.map((reward) => ({ kind: "currency", id: CURRENCY_BY_LEGACY[reward.id], quantity: reward.quantity })),
  };
};
const encounters = legacyEncounters.map((name) => encounterRecord(name, readLegacy(`enemy/${name}.json`)));
note("Trận: lưới cũ \"cột,hàng\" của phe địch → depth = cột (cột 0 gần phe ta nhất = hàng trước), lane = hàng.");
encounters.push(
  {
    id: "encounter.ashwaste_02.e1",
    name: text("encounter.ashwaste_02.e1.name", "Phục kích cồn cát", "Dune Ambush"),
    map: "map.ashwaste_02",
    mapObjectId: 1,
    recommendedLevel: 3,
    enemies: [
      { enemy: "enemy.bandit_ranger", level: 3, star: 0, cell: { lane: 0, depth: 1 } },
      { enemy: "enemy.bandit_ranger", level: 3, star: 0, cell: { lane: 2, depth: 1 } },
      { enemy: "enemy.bandit_warrior", level: 3, star: 0, cell: { lane: 1, depth: 0 } },
    ],
    rewards: [{ kind: "currency", id: "currency.gold", quantity: 80 }, { kind: "currency", id: "currency.gem", quantity: 2 }],
  },
  {
    id: "encounter.ashwaste_02.e2",
    name: text("encounter.ashwaste_02.e2.name", "Pháp sư tro tàn", "Ashen Hexers"),
    map: "map.ashwaste_02",
    mapObjectId: 2,
    recommendedLevel: 4,
    enemies: [
      { enemy: "enemy.bandit_tank", level: 4, star: 0, cell: { lane: 1, depth: 0 } },
      { enemy: "enemy.bandit_mage", level: 4, star: 0, cell: { lane: 0, depth: 2 } },
      { enemy: "enemy.bandit_mage", level: 4, star: 0, cell: { lane: 2, depth: 2 } },
    ],
    rewards: [{ kind: "currency", id: "currency.gold", quantity: 120 }, { kind: "currency", id: "currency.gem", quantity: 3 }],
  },
);
note("Tạo mới encounter.ashwaste_02.e1/e2 cho 2 đối tượng quái trên wasteland2.tmx vốn không có file trận.");
note("encounter.dawnvillage_01.e1 (village_0_1 cũ) không có đối tượng quái tương ứng trong village_0.tmx — validator sẽ cảnh báo đến khi map được migrate.");

const itemConfig = readLegacy("config/itemConfig.json")[0];
const legacyItems = readLegacy("base/items_base.json");
const ITEM_CATEGORY_NAMES_EN = { cup: "Trophy", shovel: "Shovel", food: "Food", metal: "Metal", water: "Water", diamond: "Diamond", gemstore: "Gem Ore", gem: "Gem", flour: "Flour", key: "Key", egg: "Egg", arrowbox: "Quiver" };
const items = legacyItems.map((item) => {
  const category = item.nameRegion.split("_")[0];
  const id = `item.${category}_t${item.tier}`;
  const record = {
    id,
    name: text(`${id}.name`, item.name, `${ITEM_CATEGORY_NAMES_EN[category]} T${item.tier}`),
    category,
    tier: item.tier,
    icon: `icon:item/${item.nameRegion}`,
  };
  if (item.price > 0) record.shop = { currency: CURRENCY_BY_LEGACY[item.currency], price: item.price, listed: item.show };
  else note(`${id}: giá cũ ${item.price} là giá trị quy ước "không bán" → không có mục shop.`);
  if (category === "food") record.use = { kind: "hero_exp", amount: item.tier * 100 };
  const configured = itemConfig[category]?.[`tier${item.tier}`];
  if (configured !== undefined && category !== "food") record.materialValue = configured;
  return record;
});
note("Vật phẩm: food dùng được (+tier×100 EXP như code cũ); giá trị itemConfig của metal/water/diamond/gem/gemstore giữ ở materialValue.");

const EQUIP_STAT_MAP = { atk: ["attack", 10], def: ["defense", 10], hp: ["hp", 10], agi: ["speed", 3], crit: ["critRate", 20], mp: ["effectHit", 10] };
const legacyEquipment = readLegacy("base/equip_base.json");
const equipment = legacyEquipment.map((equip) => {
  const id = `equip.${equip.nameRegion}`;
  return {
    id,
    name: text(`${id}.name`, equip.name),
    slot: equip.category,
    icon: `icon:item/${equip.nameRegion}`,
    stats: Object.fromEntries(Object.entries(equip.stats ?? {}).map(([key, value]) => {
      const [stat, factor] = EQUIP_STAT_MAP[key];
      return [stat, value * factor];
    })),
    shop: { currency: CURRENCY_BY_LEGACY[equip.currency], price: equip.price, listed: equip.show },
  };
});
note("Trang bị: atk/def/hp ×10, agi→speed ×3, crit→critRate‰ ×20, mp (không dùng trong trận cũ) → effectHit ×10.");

const questSpecs = {
  mission_004: { slug: "defend_the_village", objective: { kind: "win_encounter", target: "encounter.dawnvillage_01.e0", count: 1 }, item: "item.food_t1" },
  mission_001: { slug: "the_hidden_stone", objective: { kind: "collect_item", target: "item.gemstore_t1", count: 5 }, item: "item.diamond_t1" },
  mission_002: { slug: "forge_supplies", objective: { kind: "collect_item_category", target: "metal", count: 10 }, item: "item.metal_t2" },
  mission_003: { slug: "monster_hunt", objective: { kind: "defeat_enemies", count: 15 }, item: "item.food_t2" },
  mission_005: { slug: "dungeon_secret", objective: { kind: "reach_map", target: "map.ashwaste_06", count: 1 }, item: "item.key_t1" },
};
const quests = readLegacy("base/mission_base.json").map((mission) => {
  const spec = questSpecs[mission.idBase];
  const id = `quest.side.${spec.slug}`;
  const itemReward = mission.rewards.find((r) => r.type === "item");
  const goldReward = mission.rewards.find((r) => r.type === "currency");
  note(`${mission.idBase} → ${id}: ${itemReward.nameRegion} (khóa treo) → ${spec.item}; ${goldReward.nameRegion} → currency.gold.`);
  return {
    id,
    name: text(`${id}.name`, mission.title),
    description: text(`${id}.description`, mission.description),
    category: "side",
    objective: spec.objective,
    rewards: [
      { kind: "item", id: spec.item, quantity: itemReward.quantity },
      { kind: "currency", id: "currency.gold", quantity: goldReward.quantity },
    ],
  };
});

const achievementSpecs = {
  Recruited: ["recruiter", "heroes_recruited", [5, 20, 50]],
  Kill: ["slayer", "enemies_defeated", [50, 500, 5000]],
  Win: ["victor", "battles_won", [10, 100, 1000]],
  CoinSum: ["treasurer", "gold_earned", [10_000, 100_000, 1_000_000]],
  GemPay: ["patron", "gems_spent", [100, 1000, 10_000]],
  Equip: ["armorer", "equipment_obtained", [10, 50, 200]],
};
const achievements = readLegacy("base/achievement.json").map((achievement) => {
  const [slug, counter, targets] = achievementSpecs[achievement.idBase];
  const id = `achievement.${slug}`;
  return {
    id,
    name: text(`${id}.name`, achievement.name),
    description: text(`${id}.description`, achievement.dec.replace("từn ", "từng ")),
    counter,
    tiers: targets.map((target, index) => ({ target, rewards: [{ kind: "currency", id: "currency.gem", quantity: 10 * (index + 1) ** 2 }] })),
  };
});
note("Thành tựu: trường dec → description (sửa lỗi chính tả \"từn\"); thêm 3 bậc mục tiêu + thưởng ngọc (dữ liệu cũ chỉ có bộ đếm).");

const checkinTables = [{
  id: "checkin.standard_30",
  name: text("checkin.standard_30.name", "Điểm danh 30 ngày", "30-Day Check-in"),
  days: readLegacy("base/daily_rewards.json").map((day) => ({
    day: day.id,
    rewards: [{ kind: "currency", id: CURRENCY_BY_LEGACY[day.typereward], quantity: day.number }],
  })),
}];

const legacyBattle = readLegacy("config/battle_config.json");
const balance = [{
  id: "balance.battle_rules",
  maxRounds: 30,
  timeUnitsPerRound: 10_000,
  referenceSpeed: 100,
  startingEnergy: 25,
  maxEnergy: 100,
  energyPerAction: 20,
  energyWhenHit: 10,
  baseHitPermille: 950,
  minimumHitPermille: 600,
  criticalRateCapPermille: 750,
  damageVariancePermille: 50,
  defenseConstantBase: 100,
  defenseConstantPerLevel: 10,
  damageTakenByDepthPermille: [1000, 900, 800],
  matchupAdvantagePermille: Math.round(legacyBattle.counterMultiplier * 1000),
  matchupDisadvantagePermille: 850,
}];
note(`Luật trận: counterMultiplier ${legacyBattle.counterMultiplier} → 1250‰; maxRounds 100 hành động → 30 vòng CTB; MP cũ (mpSkill2Cost/mpSkill3Cost) thay bằng hồi chiêu + năng lượng tuyệt kỹ.`);

const legacyAssets = {};
for (const hero of heroes) {
  const classSlug = hero.classId.slice(6);
  legacyAssets[hero.sprite] = `atlas/characters/${classSlug}_Knight.atlas`;
}
for (const enemy of enemies) legacyAssets[enemy.sprite] = `atlas/characters/${enemy.classId.slice(6)}_Knight.atlas`;
for (const record of [...items, ...equipment]) legacyAssets[record.icon] = `atlas/inventory/item.atlas#${record.icon.slice("icon:item/".length)}`;
for (const kit of Object.values(skillKits)) for (const s of kit) legacyAssets[s.vfx] = `atlas/skill/skill.atlas#${s.vfx.slice(4).replace("/", "_")}`;
legacyAssets["icon:currency/gold"] = "atlas/ui/popup.atlas#coin";
legacyAssets["icon:currency/gem"] = "atlas/ui/popup.atlas#gem_pink";
legacyAssets["icon:currency/energy"] = "atlas/ui/popup.atlas#icon_speed";
legacyAssets["icon:currency/guild_token"] = "atlas/ui/popup.atlas#gem_blue";
legacyAssets["icon:currency/event_token"] = "atlas/ui/popup.atlas#icon_star";

const write = (relative, value) => {
  const path = resolve(out, relative);
  mkdirSync(dirname(path), { recursive: true });
  writeFileSync(path, JSON.stringify(value, null, 2) + "\n", "utf8");
};

if (existsSync(out)) rmSync(out, { recursive: true });
write("currencies/currencies.json", currencies);
write("hero_classes/hero_classes.json", heroClasses);
write("statuses/statuses.json", statuses);
for (const [classSlug, kit] of Object.entries(skillKits)) write(`skills/${classSlug}.json`, kit);
write("heroes/heroes.json", heroes);
write("enemies/bandits.json", enemies);
const byRegion = {};
for (const encounter of encounters) (byRegion[encounter.map.slice(4).split("_")[0]] ??= []).push(encounter);
for (const [region, list] of Object.entries(byRegion)) write(`encounters/${region}.json`, list);
write("items/items.json", items);
for (const slot of ["weapon", "armor", "jewelry", "support"]) write(`equipment/${slot}.json`, equipment.filter((e) => e.slot === slot));
write("quests/side.json", quests);
write("achievements/achievements.json", achievements);
write("checkin_tables/checkin_tables.json", checkinTables);
write("balance/battle_rules.json", balance);
write("assets/legacy_asset_map.json", legacyAssets);
write("localization/vi.json", Object.fromEntries(Object.entries(vi).sort()));
write("localization/en.json", Object.fromEntries(Object.entries(en).sort()));

const summary = `${currencies.length} currencies, ${heroClasses.length} classes, ${statuses.length} statuses, ${Object.values(skillKits).flat().length} skills, ${heroes.length} heroes, ${enemies.length} enemies, ${encounters.length} encounters, ${items.length} items, ${equipment.length} equipment, ${quests.length} quests, ${achievements.length} achievements, ${checkinTables.length} check-in tables, ${Object.keys(vi).length} vi keys, ${Object.keys(en).length} en keys`;
writeFileSync(resolve(out, "MIGRATION_REPORT.md"), [
  "# Báo cáo migrate dữ liệu legacy → content/",
  "",
  "> Sinh bởi `node tools/content-migrator/migrate-legacy.mjs` từ `assets/data` (tag `legacy-baseline`).",
  "",
  `**Kết quả:** ${summary}.`,
  "",
  "## Quyết định và điểm cần creator duyệt",
  "",
  ...report.map((line) => `- ${line}`),
  "",
].join("\n"), "utf8");
console.log(summary);
