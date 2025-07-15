package com.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.ecs.component.EquipComponent;
import com.game.ecs.component.InfoComponent;
import com.game.utils.json.Achievement;
import com.game.utils.json.Bag;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.DailyReward;
import com.game.utils.json.EquipBase;
import com.game.utils.json.GridData;
import com.game.utils.json.Hero;
import com.game.utils.json.ItemBase;
import com.game.utils.json.Lineup;
import com.game.utils.json.Account;
import com.game.utils.json.Mission;
import com.game.utils.json.Reward;
import com.game.utils.json.skill.EffectSkill;
import com.game.utils.json.skill.Skill;
import com.game.utils.json.skill.SkillBase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonHelper {
    public static List<Account> listAccount = new ArrayList<>();
    public static List<ItemBase> items = new ArrayList<>();
    public static List<EquipBase> equips = new ArrayList<>();
    public static List<CharacterBase> baseHero = new ArrayList<>();
    public static List<SkillBase> skillBaseList = new ArrayList<>();

    public static List<Bag> bags = new ArrayList<>();
    public static List<InfoComponent> infoComponents = new ArrayList<>();
    public static List<Lineup> lineups = new ArrayList<>();
    public static List<GridData> gridDataList = new ArrayList<>();
    public static List<Hero> fullHero = new ArrayList<>();
    public static List<Achievement> achievementList = new ArrayList<>();
    public static List<Mission> missions = new ArrayList<>();
    public static List<DailyReward> dailyRewards = new ArrayList<>();



    public static List<Mission> loadProfile(String filePath, boolean b) {
        if (b || missions.isEmpty()) {
            missions.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);
            for (JsonValue child : root) {
                Mission newChild = new Mission();
                newChild.missionId = child.getString("missionId");
                newChild.title = child.getString("title");
                newChild.description = child.getString("description");
                newChild.progress = child.getInt("progress");
                newChild.targetAmount = child.getInt("targetAmount");
                for (JsonValue a : child.get("rewards")) {
                    Reward reward = new Reward();
                    reward.id = a.getString("id");
                    reward.type = a.getString("type");
                    reward.quantity = a.getInt("quantity");
                    newChild.rewards.add(reward);
                }
                missions.add(newChild);
            }
        }
        return missions;
    }

    public static List<Mission> loadMissions(String filePath, boolean b) {
        if (b || missions.isEmpty()) {
            missions.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue child : root) {
                Mission newChild = new Mission();
                newChild.missionId = child.getString("missionId");
                newChild.title = child.getString("title");
                newChild.description = child.getString("description");
                newChild.progress = child.getInt("progress");
                newChild.targetAmount = child.getInt("targetAmount");
                for (JsonValue a : child.get("rewards")) {
                    Reward reward = new Reward();
                    reward.id = a.getString("id");
                    reward.type = a.getString("type");
                    reward.quantity = a.getInt("quantity");
                    newChild.rewards.add(reward);
                }
                missions.add(newChild);
            }
        }
        return missions;
    }

    public static List<Achievement> loadAchievements(String filePath, boolean b) {
        if (b || achievementList.isEmpty()) {
            achievementList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue lineup : root) {
                Achievement newChild = new Achievement();
                newChild.name = lineup.getString("name", "empty");
                newChild.dec = lineup.getString("dec", "");
                newChild.number = lineup.getInt("number", 0);
                achievementList.add(newChild);
            }
        }
        return achievementList;
    }

    public static List<Account> loadMaiInfo(String filePath, boolean b) {
        if (b || listAccount.isEmpty()) {
            listAccount.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            if(!fileHandle.exists()){
                return null;
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                Account newItem = new Account();
                newItem.id = item.getString("id");
                newItem.level = item.getInt("level");
                newItem.characterSelect = item.getString("characterSelect", null);

                listAccount.add(newItem);
            }
        }
        return listAccount;
    }

    public static List<CharacterBase> loadHeroBase(String filePath, boolean b) {
        if (b || baseHero.isEmpty()) {
            baseHero.clear();
            FileHandle fileHandle = Gdx.files.internal(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                CharacterBase newItem = new CharacterBase();

                // Đọc các trường cơ bản
                newItem.characterId = item.getString("id", "");
                newItem.characterBaseId = item.getString("characterBaseId", "");
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
                baseHero.add(newItem);
            }
        }
        return baseHero;
    }
    public static List<SkillBase> loadSkillBase(String filePath, boolean b) {
        if (b || skillBaseList.isEmpty()) {
            skillBaseList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                SkillBase newItem = new SkillBase();
                newItem.name = item.name;

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

                skillBaseList.add(newItem);
            }
        }
        return skillBaseList;
    }
    public static List<ItemBase> loadItems(String filePath, boolean b) {
        if (b || items.isEmpty()) {
            items.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                ItemBase newItem = new ItemBase();
                newItem.id = item.getString("id");
                newItem.name = item.getString("name");
                newItem.level = item.getInt("level");
                newItem.price = item.getInt("price");

                items.add(newItem);
            }
        }
        return items;
    }

    public static List<EquipBase> loadEquips(String filePath, boolean b) {
        if (b || equips.isEmpty()) {
            equips.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                EquipBase newEquip = new EquipBase();
                newEquip.id = equip.getString("id");
                newEquip.name = equip.getString("name");
                newEquip.category = equip.getString("category", "default");
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

                equips.add(newEquip);
            }
        }
        return equips;
    }

    public static List<Bag> loadBags(String filePath, boolean b) {
        if (b || bags.isEmpty()) {
            bags.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                if (equip.isEmpty()) return null;
                Bag newBag = new Bag();
                newBag.id = equip.getString("id");
                newBag.type = equip.getString("type");
                newBag.index = equip.getInt("index", 1);
                bags.add(newBag);
            }
        }
        return bags;
    }

    public static Bag getBag(String keyId, boolean b) {
        if (bags != null && !bags.isEmpty()) {
            for (Bag item : bags) {
                if (item.id.equals(keyId)) {
                    return item;
                }
            }
        }
        return null;
    }


    public static List<Lineup> loadLineups(String filePath, boolean b) {
        if (b || lineups.isEmpty()) {
            lineups.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue lineup : root) {
                Lineup newLineup = new Lineup();
                newLineup.grid = lineup.getString("grid", "empty");
                newLineup.hero = new Hero();
                newLineup.hero.characterId = lineup.getString("characterId");
//                newLineup.hero.characterBase = lineup.getString("characterBaseId");
                lineups.add(newLineup);
            }
        }
        return lineups;
    }

    public static List<GridData> loadGrids(String filePath, boolean b) {
        if (b || gridDataList.isEmpty()) {
            gridDataList.clear();
            FileHandle fileHandle = Gdx.files.internal(filePath);
            if (!fileHandle.exists()) {
                fileHandle = Gdx.files.local(filePath);

                if (!fileHandle.exists()) {
                    System.err.println("File not found in both internal and local: " + filePath);
                    return gridDataList;
                }
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);
            if(root.get("reward")!= null){
                root = root.get("grid");
            }

            for (JsonValue jsv : root) {
                GridData c = new GridData();
                c.grid = jsv.getString("grid", "empty");
                c.characterId =jsv.getString("characterId","defaultCharacterId");
                c.getCharacterId =jsv.getString("characterBaseId","defaultCharacterBaseId");
                gridDataList.add(c);
            }
        }
        return gridDataList;
    }

    public static List<Hero> loadFullHero(String filePath, boolean b) {
        if (b || fullHero.isEmpty()) {
            fullHero.clear();
            FileHandle fileHandle = Gdx.files.internal(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue hero : root) {
                Hero newHero = new Hero();
                newHero.characterId = hero.getString("characterId","characterIdDefault");
                newHero.characterBaseId = hero.getString("characterBaseId","characterBaseIdDefault");
                newHero.grid = hero.getString("grid","empty");
                newHero.star = hero.getInt("star",0);
                newHero.level = hero.getInt("level",1);

                JsonValue equip = hero.get("equip");
                newHero.equip = new HashMap<>();
                if (equip != null) {
                    for (JsonValue equipItem : equip) {
                        newHero.equip.put(equipItem.name(), equipItem.asInt());
                    }
                }

                fullHero.add(newHero);
            }
        }
        return fullHero;
    }

    public static List<InfoComponent> loadInfoComponent(String filePath, boolean b) {
        if (b || infoComponents.isEmpty()) {
            infoComponents.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);

            if (!fileHandle.exists()) {
                return null;  // Hoặc return new ArrayList<>(); tùy nhu cầu.
            }

            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            // Kiểm tra nếu root là rỗng, trả về null hoặc danh sách rỗng
            if (root == null || root.size == 0) {
                return null;  // Hoặc return new ArrayList<>();
            }

            for (JsonValue equip : root) {
                InfoComponent newBag = new InfoComponent();
                newBag.characterId = equip.getString("id");
                newBag.characterBaseId = equip.getString("type");
                newBag.star = equip.getInt("index", 1);
                newBag.level = equip.getInt("index", 1);

                JsonValue equipItem = equip.get("equip");
                newBag.equip = new InfoComponent.Equipment();

                // Chỉnh sửa để làm việc với EquipComponent nếu cần, không chỉ là chuỗi
                newBag.equip.weapon = new EquipComponent(equipItem.get("weapon"));
                newBag.equip.armor = new EquipComponent(equipItem.get("armor"));
                newBag.equip.jewelry = new EquipComponent(equipItem.get("jewelry"));
                newBag.equip.support = new EquipComponent(equipItem.get("support"));

                infoComponents.add(newBag);
            }
        }

        // Trả về danh sách nếu có dữ liệu
        return infoComponents.isEmpty() ? null : infoComponents;
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

}
