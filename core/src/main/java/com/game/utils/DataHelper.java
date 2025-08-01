package com.game.utils;

import static com.game.utils.Constants.ACHIEVEMENT_JSON;
import static com.game.utils.Constants.CHARACTER_BASE_JSON;
import static com.game.utils.Constants.CHECK_MAP_JSON;
import static com.game.utils.Constants.DAILY_REWARD_JSON;
import static com.game.utils.Constants.EQUIPS_JSON;
import static com.game.utils.Constants.EQUIP_JSON;
import static com.game.utils.Constants.INFO_JSON;
import static com.game.utils.Constants.ITEMS_JSON;
import static com.game.utils.Constants.ITEM_JSON;
import static com.game.utils.Constants.LINEUP_ATTACK;
import static com.game.utils.Constants.MAININFO_JSON_LOCAL;
import static com.game.utils.Constants.MISSION_JSON;
import static com.game.utils.Constants.SKILL_JSON;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.ecs.component.EquipComponent;
import com.game.ecs.component.InfoComponent;
import com.game.utils.json.Achievement;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.CheckMap;
import com.game.utils.json.DailyReward;
import com.game.utils.json.Equip;
import com.game.utils.json.EquipBase;
import com.game.utils.json.Item;
import com.game.utils.json.Lineup;
import com.game.utils.json.Hero;
import com.game.utils.json.ItemBase;
import com.game.utils.json.Account;
import com.game.utils.json.MapBattle;
import com.game.utils.json.Mission;
import com.game.utils.json.Profile;
import com.game.utils.json.Reward;
import com.game.utils.json.Stat;
import com.game.utils.json.skill.EffectSkill;
import com.game.utils.json.skill.Skill;
import com.game.utils.json.skill.SkillBase;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DataHelper {

    public static List<Account> loadAccountList(boolean b) {
        if (b || GameSession.accountList.isEmpty()) {
            GameSession.accountList.clear();
            FileHandle fileHandle = Gdx.files.local(MAININFO_JSON_LOCAL);
            if (!fileHandle.exists()) {
                return null;
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);
if(root == null){
    return null;
}
            for (JsonValue item : root) {
                Account newItem = new Account();
                newItem.id = item.getString("id");
                newItem.level = item.getInt("level");
                newItem.characterSelect = item.getString("characterSelect", null);

                GameSession.accountList.add(newItem);
            }
        }
        return GameSession.accountList;
    }

    public static Profile loadProfile(boolean b) {
        if (b || GameSession.profile != null) {
            FileHandle fileHandle = Gdx.files.local(INFO_JSON);
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
            newItem.numberOfTeammatesRecruited = root.getInt("numberOfTeammatesRecruited");
            newItem.equipment = root.getInt("equipment");
            newItem.numberOfEnemies = root.getInt("numberOfEnemies");
            GameSession.profile = newItem;
        }
        return GameSession.profile;
    }

    public static List<Achievement> loadAchievementList(boolean b) {
        if (b || GameSession.achievementList.isEmpty()) {
            GameSession.achievementList.clear();
            FileHandle fileHandle = Gdx.files.local(ACHIEVEMENT_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue c : root) {
                Achievement newChild = new Achievement();
                newChild.idBase = c.getString("idBase");
                newChild.name = c.getString("name");
                newChild.dec = c.getString("dec", "");
                newChild.number = c.getInt("number", 0);
                GameSession.achievementList.add(newChild);
            }
        }
        return GameSession.achievementList;
    }

    public static List<CheckMap> loadCheckMapList(boolean b) {
        if (b || GameSession.checkMapList.isEmpty()) {
            GameSession.checkMapList.clear();
            FileHandle fileHandle = Gdx.files.local(CHECK_MAP_JSON);
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
                GameSession.checkMapList.add(child);
            }
        }
        return GameSession.checkMapList;
    }

    public static List<CharacterBase> loadCharacterBaseList() {
        if (GameSession.characterBaseList.isEmpty()) {
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
                GameSession.characterBaseList.add(newItem);
            }
        }
        return GameSession.characterBaseList;
    }

    public static List<DailyReward> loadDailyRewardList(boolean b) {
        String filePath = DAILY_REWARD_JSON;
        if (b || GameSession.dailyRewardList.isEmpty()) {
            GameSession.dailyRewardList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue child : root) {
                DailyReward newChild = new DailyReward();
                newChild.id = child.getInt("id");
                newChild.typereward = child.getString("typereward");
                newChild.confirm = child.getBoolean("confirm");
                newChild.number = child.getInt("number");
                GameSession.dailyRewardList.add(newChild);
            }
        }
        return GameSession.dailyRewardList;
    }

    public static List<EquipBase> loadEquipBaseList(boolean b) {
        if (b || GameSession.equipBaseList.isEmpty()) {
            GameSession.equipBaseList.clear();
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

                GameSession.equipBaseList.add(newEquip);
            }
        }
        return GameSession.equipBaseList;
    }

    public static List<Equip> loadEquipList(boolean b) {
        if (b || GameSession.equipList.isEmpty()) {
            GameSession.equipList.clear();
            FileHandle fileHandle = Gdx.files.local(EQUIPS_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                Equip newEquip = new Equip();
                newEquip.id = equip.getString("id");
                newEquip.nameRegion = equip.getString("nameRegion");
                newEquip.level = equip.getInt("level");
                newEquip.target = equip.getString("target","default");

                GameSession.equipList.add(newEquip);
            }
        }
        return GameSession.equipList;
    }

    public static List<ItemBase> loadItemBaseList(boolean b) {
        if (b || GameSession.itemBaseList.isEmpty()) {
            GameSession.itemBaseList.clear();
            FileHandle fileHandle = Gdx.files.internal(ITEM_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                ItemBase newItem = new ItemBase();
                newItem.nameRegion = item.getString("nameRegion", "empty");
                newItem.name = item.getString("name");
                newItem.tier = item.getInt("tier");
                newItem.show = item.getBoolean("show");
                newItem.price = item.getInt("price");
                GameSession.itemBaseList.add(newItem);
            }
        }
        return GameSession.itemBaseList;
    }

    public static List<Item> loadItemList(boolean b) {
        if (b || GameSession.itemList.isEmpty()) {
            GameSession.itemList.clear();
            FileHandle fileHandle = Gdx.files.local(ITEMS_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                Item newItem = new Item();
                newItem.nameRegion = item.getString("nameRegion", "empty");
                newItem.index = item.getInt("index");
                GameSession.itemList.add(newItem);
            }
        }
        return GameSession.itemList;
    }

    public static List<Mission> loadMissionList(boolean b) {
        String filePath = MISSION_JSON;
        if (b || GameSession.missionList.isEmpty()) {
            GameSession.missionList.clear();
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
                GameSession.missionList.add(newChild);
            }
        }
        return GameSession.missionList;
    }

    public static List<SkillBase> loadSkillBaseList(boolean b) {
        if (b || GameSession.skillBaseList.isEmpty()) {
            GameSession.skillBaseList.clear();
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

                GameSession.skillBaseList.add(newItem);
            }
        }
        return GameSession.skillBaseList;
    }

    public static List<Lineup> loadLineupList( boolean b) {
        if (b || GameSession.lineupList.isEmpty()) {
            GameSession.lineupList.clear();

            for (Hero hero : GameSession.heroList) {
                if(hero.grid.equals("empty")) continue;
                Lineup c = new Lineup();
                c.grid = hero.grid;
                c.characterId = hero.characterId;
                c.nameRegion = hero.nameRegion;
                GameSession.lineupList.add(c);
            }
        }
        return GameSession.lineupList;
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
        boolean player = filePath.equals(Constants.HERO_FULL);
        if (player) {
            if (reload || GameSession.heroList.isEmpty()) {
                return sortHero(loadHeroListPrivate(filePath, player));
            } else {
                return GameSession.heroList;
            }
        } else {
            if (reload || GameSession.heroEnemyList.isEmpty()) {
                return loadEnemyListPrivate(filePath, player);
            } else {
                return GameSession.heroEnemyList;
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
            GameSession.heroList = heroes;
            return GameSession.heroList;
        } else {
            GameSession.heroEnemyList = heroes;
            return GameSession.heroEnemyList;
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
            GameSession.heroList = heroes;
            return GameSession.heroList;
        } else {
            GameSession.heroEnemyList = heroes;
            return GameSession.heroEnemyList;
        }
    }

    public static MapBattle loadMapBattle(String filePath) {
        GameSession.mapBattle = new MapBattle();
        GameSession.mapBattle.heroEnemyList = loadHeroList(filePath, true);
        GameSession.mapBattle.rewardList = loadRewardBattle(filePath);
        return GameSession.mapBattle;
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
        if (b || GameSession.infoComponentList.isEmpty()) {
            GameSession.infoComponentList.clear();
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

                GameSession.infoComponentList.add(newBag);
            }
        }

        // Trả về danh sách nếu có dữ liệu
        return GameSession.infoComponentList.isEmpty() ? null : GameSession.infoComponentList;
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

}
