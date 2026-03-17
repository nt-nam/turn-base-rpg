package com.game.utils;


import static com.game.utils.Constants.CHARACTER_BASE_JSON;



import static com.game.utils.Constants.EQUIP_JSON;


import static com.game.utils.Constants.ITEM_JSON;

import static com.game.utils.Constants.MAININFO_JSON_LOCAL;

import static com.game.utils.Constants.SKILL_JSON;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.ecs.component.EquipComponent;
import com.game.ecs.component.InfoComponent;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Achievement;
import com.game.models.entity.CharacterBase;
import com.game.models.entity.CheckMap;
import com.game.models.entity.DailyReward;
import com.game.models.entity.Equip;
import com.game.models.entity.EquipBase;
import com.game.models.entity.Item;
import com.game.models.entity.Lineup;
import com.game.models.entity.Hero;
import com.game.models.entity.ItemBase;
import com.game.models.entity.Account;
import com.game.models.entity.MapBattle;
import com.game.models.entity.Mission;
import com.game.models.entity.Profile;
import com.game.models.entity.Reward;
import com.game.models.entity.Stat;
import com.game.models.entity.skill.EffectSkill;
import com.game.models.entity.skill.Skill;
import com.game.models.entity.skill.SkillBase;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DataHelper {

    public static List<Account> loadAccountList(boolean b) {
        if (b || GameSessionManager.getInstance().accountList.isEmpty()) {
            GameSessionManager.getInstance().accountList.clear();
            FileHandle fileHandle = Gdx.files.local(MAININFO_JSON_LOCAL);
            if (!fileHandle.exists()) {
                return null;
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);
            if (root == null) {
                return null;
            }
            for (JsonValue item : root) {
                Account newItem = new Account();
                newItem.id = item.getString("id");
                newItem.level = item.getInt("level");
                newItem.characterSelect = item.getString("characterSelect", null);

                GameSessionManager.getInstance().accountList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().accountList;
    }

    public static Profile loadProfile(boolean b) {
        if (b || GameSessionManager.getInstance().profile != null) {
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("info.json"));
            if (!fileHandle.exists()) {
                return null;
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            Profile newItem = new Profile(root.getString("name"), root.getString("characterSelect"));
            newItem.level = root.getInt("level");
            newItem.area = root.getString("area");
            newItem.pos = new Vector2(root.get("pos").getInt("x"), root.get("pos").getInt("y"));
            newItem.dailyCheck = root.getString("dailyCheck");
            newItem.sizeTeam = root.getInt("sizeTeam");
            newItem.exp = root.getInt("exp");
            newItem.coin = root.getInt("coin");
            newItem.gem = root.getInt("gem");
            newItem.energy = root.getInt("energy", 0);
            newItem.energyTime = root.getString("energy", "empty");
            newItem.numberOfTeammatesRecruited = root.getInt("numberOfTeammatesRecruited");
            newItem.equipment = root.getInt("equipment");
            newItem.numberOfEnemies = root.getInt("numberOfEnemies");
            newItem.playMusic = root.getBoolean("playMusic",true);
            newItem.playSound = root.getBoolean("playSound",true);
            GameSessionManager.getInstance().profile = newItem;
        }
        return GameSessionManager.getInstance().profile;
    }

    public static List<Achievement> loadAchievementList(boolean b) {
        if (b || GameSessionManager.getInstance().achievementList.isEmpty()) {
            GameSessionManager.getInstance().achievementList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("achievement.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue c : root) {
                Achievement newChild = new Achievement();
                newChild.idBase = c.getString("idBase");
                newChild.name = c.getString("name");
                newChild.dec = c.getString("dec", "");
                newChild.number = c.getInt("number", 0);
                GameSessionManager.getInstance().achievementList.add(newChild);
            }
        }
        return GameSessionManager.getInstance().achievementList;
    }

    public static List<CheckMap> loadCheckMapList(boolean b) {
        if (b || GameSessionManager.getInstance().checkMapList.isEmpty()) {
            GameSessionManager.getInstance().checkMapList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("check_enemy_map.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                if (equip.isEmpty()) return null;
                CheckMap child = new CheckMap();
                child.name = equip.getString("name");
                child.sum = equip.getInt("sum");
                child.battleList = new ArrayList<>();
                for (JsonValue a : equip.get("battleList")) {
                    CheckMap.BattleDes battle = new CheckMap.BattleDes();
                    battle.id = a.getString("id");
                    battle.dayCheck = LocalDate.parse(a.getString("dayCheck"));
                    child.battleList.add(battle);
                }
                GameSessionManager.getInstance().checkMapList.add(child);
            }
        }
        return GameSessionManager.getInstance().checkMapList;
    }

    public static List<CharacterBase> loadCharacterBaseList() {
        if (GameSessionManager.getInstance().characterBaseList.isEmpty()) {
            FileHandle fileHandle = Gdx.files.internal(CHARACTER_BASE_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                CharacterBase newItem = new CharacterBase();

                // Đọc các trường cơ bản
                newItem.characterId = item.getString("id", "");
                newItem.nameRegion = item.getString("nameRegion", "");
                newItem.classType = item.getString("classType", "");
                newItem.role = item.getString("role", "");
                newItem.name = item.getString("name", "");
                newItem.desc = item.getString("desc", "");

                // Đọc các thuộc tính số
                newItem.hp = item.getInt("hp", 0);
                newItem.mp = item.getInt("mp", 0);
                newItem.atk = item.getInt("atk", 0);
                newItem.def = item.getInt("def", 0);
                newItem.agi = item.getInt("agi", 0);
                newItem.crit = item.getInt("crit", 0);

                // Xử lý các trường Array (skills, counters, weakAgainst)
                newItem.skills = new Array<>();
                if (item.has("skills")) {
                    for (JsonValue skill : item.get("skills")) {
                        newItem.skills.add(skill.asString());
                    }
                }

                newItem.counters = new Array<>();
                if (item.has("counters")) {
                    for (JsonValue counter : item.get("counters")) {
                        newItem.counters.add(counter.asString());
                    }
                }

                newItem.weakAgainst = new Array<>();
                if (item.has("weakAgainst")) {
                    for (JsonValue weak : item.get("weakAgainst")) {
                        newItem.weakAgainst.add(weak.asString());
                    }
                }

                // Thêm đối tượng vào danh sách BaseHero
                GameSessionManager.getInstance().characterBaseList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().characterBaseList;
    }

    public static List<DailyReward> loadDailyRewardList(boolean b) {
        String filePath = Constants.playerPath("daily_rewards.json");
        if (b || GameSessionManager.getInstance().dailyRewardList.isEmpty()) {
            GameSessionManager.getInstance().dailyRewardList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue child : root) {
                DailyReward newChild = new DailyReward();
                newChild.id = child.getInt("id");
                newChild.typereward = child.getString("typereward");
                newChild.confirm = child.getBoolean("confirm");
                newChild.number = child.getInt("number");
                GameSessionManager.getInstance().dailyRewardList.add(newChild);
            }
        }
        return GameSessionManager.getInstance().dailyRewardList;
    }

    public static List<EquipBase> loadEquipBaseList(boolean b) {
        if (b || GameSessionManager.getInstance().equipBaseList.isEmpty()) {
            GameSessionManager.getInstance().equipBaseList.clear();
            FileHandle fileHandle = Gdx.files.internal(EQUIP_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                EquipBase newEquip = new EquipBase();
                newEquip.nameRegion = equip.getString("nameRegion");
                newEquip.name = equip.getString("name");
                newEquip.show = equip.getBoolean("show");
                newEquip.category = equip.getString("category", "default");
                newEquip.currency = equip.getString("currency", "default");
                newEquip.price = equip.getInt("price", -1);

                JsonValue stats = equip.get("stats");
                newEquip.stats = new HashMap<>();
                if (stats != null) {
                    for (JsonValue stat : stats) {
                        newEquip.stats.put(stat.name(), stat.asInt());
                    }
                } else {
                    System.out.println(" stats null");
                }

                GameSessionManager.getInstance().equipBaseList.add(newEquip);
            }
        }
        return GameSessionManager.getInstance().equipBaseList;
    }

    public static List<Equip> loadEquipList(boolean b) {
        if (b || GameSessionManager.getInstance().equipList.isEmpty()) {
            GameSessionManager.getInstance().equipList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("equips.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                Equip newEquip = new Equip();
                newEquip.id = equip.getString("id");
                newEquip.nameRegion = equip.getString("nameRegion");
                newEquip.level = equip.getInt("level");
                newEquip.target = equip.getString("target", "default");

                GameSessionManager.getInstance().equipList.add(newEquip);
            }
        }
        return GameSessionManager.getInstance().equipList;
    }

    public static List<ItemBase> loadItemBaseList(boolean b) {
        if (b || GameSessionManager.getInstance().itemBaseList.isEmpty()) {
            GameSessionManager.getInstance().itemBaseList.clear();
            FileHandle fileHandle = Gdx.files.internal(ITEM_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                ItemBase newItem = new ItemBase();
                newItem.nameRegion = item.getString("nameRegion", "empty");
                newItem.name = item.getString("name");
                newItem.detail = item.getString("detail", "-- || --");
                newItem.tier = item.getInt("tier");
                newItem.show = item.getBoolean("show");
                newItem.currency = item.getString("currency");
                newItem.price = item.getInt("price");
                GameSessionManager.getInstance().itemBaseList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().itemBaseList;
    }

    public static List<Item> loadItemList(boolean b) {
        if (b || GameSessionManager.getInstance().itemList.isEmpty()) {
            GameSessionManager.getInstance().itemList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("items.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                Item newItem = new Item();
                newItem.nameRegion = item.getString("nameRegion", "empty");
                newItem.index = item.getInt("index");
                GameSessionManager.getInstance().itemList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().itemList;
    }


    public static List<Mission> loadMissionList(boolean b) {
        String filePath = Constants.playerPath("mission.json");
        if (b || GameSessionManager.getInstance().missionList.isEmpty()) {
            GameSessionManager.getInstance().missionList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue child : root) {
                Mission newChild = new Mission();
                newChild.idBase = child.getString("idBase");
                newChild.title = child.getString("title");
                newChild.description = child.getString("description");
                newChild.progress = child.getInt("progress");
                newChild.targetAmount = child.getInt("targetAmount");
                for (JsonValue a : child.get("rewards")) {
                    Reward reward = new Reward();
                    reward.nameRegion = a.getString("nameRegion");
                    reward.type = a.getString("type");
                    reward.quantity = a.getInt("quantity");
                    newChild.rewards.add(reward);
                }
                GameSessionManager.getInstance().missionList.add(newChild);
            }
        }
        return GameSessionManager.getInstance().missionList;
    }

    public static List<SkillBase> loadSkillBaseList(boolean b) {
        if (b || GameSessionManager.getInstance().skillBaseList.isEmpty()) {
            GameSessionManager.getInstance().skillBaseList.clear();
            FileHandle fileHandle = Gdx.files.internal(SKILL_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                SkillBase newItem = new SkillBase();
                newItem.name = item.getString("name");

                JsonValue skillJS1 = item.get("1");
                newItem.skill1 = new Skill();
                newItem.skill1.name = skillJS1.getString("name");
                newItem.skill1.description = skillJS1.getString("description");
                newItem.skill1.effectSkill = new EffectSkill();
                JsonValue effect1 = skillJS1.get("effect");
                newItem.skill1.effectSkill.name = effect1.get(0).name;
                newItem.skill1.effectSkill.value = effect1.getInt(0);

                JsonValue skillJS2 = item.get("2");
                newItem.skill2 = new Skill();
                newItem.skill2.name = skillJS2.getString("name");
                newItem.skill2.description = skillJS2.getString("description");
                newItem.skill2.effectSkill = new EffectSkill();
                JsonValue effect2 = skillJS2.get("effect");
                newItem.skill2.effectSkill.name = effect2.get(0).name;
                newItem.skill2.effectSkill.value = effect2.getInt(0);

                JsonValue skillJS3 = item.get("3");
                newItem.skill3 = new Skill();
                newItem.skill3.name = skillJS3.getString("name");
                newItem.skill3.description = skillJS3.getString("description");
                newItem.skill3.effectSkill = new EffectSkill();
                JsonValue effect3 = skillJS3.get("effect");
                newItem.skill3.effectSkill.name = effect3.get(0).name;
                newItem.skill3.effectSkill.value = effect3.getInt(0);

                GameSessionManager.getInstance().skillBaseList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().skillBaseList;
    }

    public static List<Lineup> loadLineupList(boolean b) {
        if (b || GameSessionManager.getInstance().lineupList.isEmpty()) {
            GameSessionManager.getInstance().lineupList.clear();

            for (Hero hero : GameSessionManager.getInstance().heroList) {
                if (hero.grid.equals("empty")) continue;
                Lineup c = new Lineup();
                c.grid = hero.grid;
                c.characterId = hero.characterId;
                c.nameRegion = hero.nameRegion;
                GameSessionManager.getInstance().lineupList.add(c);
            }
        }
        return GameSessionManager.getInstance().lineupList;
    }

    public static List<Hero> sortHero(List<Hero> heroList) {
        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                return Integer.compare(hero2.star, hero1.star);
            }
        });
        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                return Integer.compare(hero2.level, hero1.level);
            }
        });

        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                String grid1 = hero1.grid;
                String grid2 = hero2.grid;

                if (!hero1.grid.equals("empty") && grid2.equals("empty")) {
                    return -1;
                } else if (grid1.equals("empty") && !grid2.equals("empty")) {
                    return 1;
                }
                return 0;
            }
        });
        return heroList;
    }

    public static List<Hero> loadHeroList(String filePath, boolean reload) {
        boolean player = filePath.equals(Constants.playerPath("hero_full.json"));
        if (player) {
            if (reload || GameSessionManager.getInstance().heroList.isEmpty()) {
                return sortHero(loadHeroListPrivate(filePath, player));
            } else {
                return GameSessionManager.getInstance().heroList;
            }
        } else {
            if (reload || GameSessionManager.getInstance().heroEnemyList.isEmpty()) {
                return loadEnemyListPrivate(filePath, player);
            } else {
                return GameSessionManager.getInstance().heroEnemyList;
            }
        }
    }

    private static List<Hero> loadHeroListPrivate(String filePath, boolean b) {
        List<Hero> heroes = new ArrayList<>();

        FileHandle fileHandle = Gdx.files.local(filePath);
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(fileHandle);

        if (root.get("reward") != null) {
            root = root.get("grid");
        }

        for (JsonValue hero : root) {
            Hero newHero = new Hero();
            newHero.characterId = hero.getString("characterId", "characterIdDefault");
            newHero.nameRegion = hero.getString("nameRegion", "nameRegionDefault");
            newHero.grid = hero.getString("grid", "empty");
            newHero.star = hero.getInt("star", 0);
            newHero.level = hero.getInt("level", 1);
            newHero.exp = hero.getInt("exp", 0);

            JsonValue equip = hero.get("equip");
            newHero.equip = new Hero.Equip();
            if (equip != null) {
                newHero.equip.weapon = equip.getString("weapon", "empty");
                newHero.equip.armor = equip.getString("armor", "empty");
                newHero.equip.jewelry = equip.getString("jewelry", "empty");
                newHero.equip.support = equip.getString("support", "empty");
            }


            heroes.add(newHero);
        }
        if (b) {
            GameSessionManager.getInstance().heroList = heroes;
            return GameSessionManager.getInstance().heroList;
        } else {
            GameSessionManager.getInstance().heroEnemyList = heroes;
            return GameSessionManager.getInstance().heroEnemyList;
        }
    }

    private static List<Hero> loadEnemyListPrivate(String filePath, boolean b) {
        List<Hero> heroes = new ArrayList<>();

        FileHandle fileHandle = Gdx.files.internal(filePath);
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(fileHandle);

        if (root.get("reward") != null) {
            root = root.get("grid");
        }

        for (JsonValue hero : root) {
            Hero newHero = new Hero();
            newHero.characterId = hero.getString("characterId", "characterIdDefault");
            newHero.nameRegion = hero.getString("nameRegion", "nameRegionDefault");
            newHero.grid = hero.getString("grid", "empty");
            newHero.star = hero.getInt("star", 0);
            newHero.level = hero.getInt("level", 1);

            JsonValue equip = hero.get("equip");
            newHero.equip = new Hero.Equip();
            if (equip != null) {
                newHero.equip.weapon = equip.getString("weapon", "empty");
                newHero.equip.armor = equip.getString("armor", "empty");
                newHero.equip.jewelry = equip.getString("jewelry", "empty");
                newHero.equip.support = equip.getString("support", "empty");
            }


            heroes.add(newHero);
        }
        if (b) {
            GameSessionManager.getInstance().heroList = heroes;
            return GameSessionManager.getInstance().heroList;
        } else {
            GameSessionManager.getInstance().heroEnemyList = heroes;
            return GameSessionManager.getInstance().heroEnemyList;
        }
    }

    public static MapBattle loadMapBattle(String filePath) {
        GameSessionManager.getInstance().mapBattle = new MapBattle();
        GameSessionManager.getInstance().mapBattle.heroEnemyList = loadHeroList(filePath, true);
        GameSessionManager.getInstance().mapBattle.rewardList = loadRewardBattle(filePath);
        return GameSessionManager.getInstance().mapBattle;
    }

    private static List<Reward> loadRewardBattle(String filePath) {
        FileHandle fileHandle = Gdx.files.internal(filePath);
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(fileHandle);
        if (root.get("reward") != null) {
            root = root.get("reward");
        }
        List<Reward> rewards = new ArrayList<>();
        for (JsonValue hero : root) {
            Reward reward = new Reward();
            reward.nameRegion = hero.getString("id", "idBaseDefault");
            reward.type = hero.getString("type", "typeDefault");
            reward.quantity = hero.getInt("quantity", 0);
            rewards.add(reward);
        }
        return rewards;
    }

    public static List<InfoComponent> loadInfoComponentList(String filePath, boolean b) {
        if (b || GameSessionManager.getInstance().infoComponentList.isEmpty()) {
            GameSessionManager.getInstance().infoComponentList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);

            if (!fileHandle.exists()) {
                return null;  // Hoặc return new ArrayList<>(); tùy nhu cầu.
            }

            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            // Kiểm tra nếu root là rỗng, trả về null hoặc danh sách rỗng
            if (root == null || root.size == 0) {
                return null;
            }

            for (JsonValue equip : root) {
                InfoComponent newBag = new InfoComponent();
                newBag.characterId = equip.getString("id");
                newBag.nameRegion = equip.getString("type");
                newBag.star = equip.getInt("index", 1);
                newBag.level = equip.getInt("index", 1);

                JsonValue equipItem = equip.get("equip");
                newBag.equip = new InfoComponent.Equipment();

                // Chỉnh sửa để làm việc với EquipComponent nếu cần, không chỉ là chuỗi
                newBag.equip.weapon = new EquipComponent(equipItem.get("weapon"));
                newBag.equip.armor = new EquipComponent(equipItem.get("armor"));
                newBag.equip.jewelry = new EquipComponent(equipItem.get("jewelry"));
                newBag.equip.support = new EquipComponent(equipItem.get("support"));

                GameSessionManager.getInstance().infoComponentList.add(newBag);
            }
        }

        // Trả về danh sách nếu có dữ liệu
        return GameSessionManager.getInstance().infoComponentList.isEmpty() ? null : GameSessionManager.getInstance().infoComponentList;
    }

    public static <T> T get(List<T> list, String key, Object value) {
        for (T item : list) {
            try {
                Field field = item.getClass().getDeclaredField(key);
                field.setAccessible(true);
                Object fieldValue = field.get(item);

                if (fieldValue != null && fieldValue.equals(value)) {
                    return item;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JsonValue getJsonValue(String filePath) {
        JsonReader reader = new JsonReader();

        FileHandle fileHandle = Gdx.files.internal(filePath);

        if (!fileHandle.exists()) {
            System.err.println("[JsonHelper:JsonValue] File not found: " + filePath);
            return null;
        }

        return reader.parse(fileHandle);
    }

    public static void clearDataProfile() {
        GameSessionManager.getInstance().profile = null;
        GameSessionManager.getInstance().profile = new Profile();
        GameSessionManager.getInstance().achievementList.clear();
        GameSessionManager.getInstance().dailyRewardList.clear();
        GameSessionManager.getInstance().equipList.clear();
        GameSessionManager.getInstance().heroList.clear();
        GameSessionManager.getInstance().itemList.clear();
        GameSessionManager.getInstance().lineupList.clear();
        GameSessionManager.getInstance().missionList.clear();
        GameSessionManager.getInstance().checkMapList.clear();


    }
}
