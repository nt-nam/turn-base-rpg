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
import com.game.utils.json.EquipBase;
import com.game.utils.json.Lineup;
import com.game.utils.json.Hero;
import com.game.utils.json.ItemBase;
import com.game.utils.json.Account;
import com.game.utils.json.Mission;
import com.game.utils.json.Reward;
import com.game.utils.json.skill.EffectSkill;
import com.game.utils.json.skill.Skill;
import com.game.utils.json.skill.SkillBase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class JsonHelper {


    public static List<Mission> loadMissionList(String filePath, boolean b) {
        if (b || GameSession.missionList.isEmpty()) {
            GameSession.missionList.clear();
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
                    reward.id = a.getString("idBase");
                    reward.type = a.getString("type");
                    reward.quantity = a.getInt("quantity");
                    newChild.rewards.add(reward);
                }
                GameSession.missionList.add(newChild);
            }
        }
        return GameSession.missionList;
    }

    public static List<Achievement> loadAchievementList(String filePath, boolean b) {
        if (b || GameSession.achievementList.isEmpty()) {
            GameSession.achievementList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue lineup : root) {
                Achievement newChild = new Achievement();
                newChild.name = lineup.getString("name", "empty");
                newChild.dec = lineup.getString("dec", "");
                newChild.number = lineup.getInt("number", 0);
                GameSession.achievementList.add(newChild);
            }
        }
        return GameSession.achievementList;
    }

    public static List<Account> loadAccountList(String filePath, boolean b) {
        if (b || GameSession.accountList.isEmpty()) {
            GameSession.accountList.clear();
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

                GameSession.accountList.add(newItem);
            }
        }
        return GameSession.accountList;
    }

    public static List<CharacterBase> loadCharacterBaseList(String filePath, boolean b) {
        if (b || GameSession.characterBaseList.isEmpty()) {
            GameSession.characterBaseList.clear();
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
                GameSession.characterBaseList.add(newItem);
            }
        }
        return GameSession.characterBaseList;
    }
    public static List<SkillBase> loadSkillBaseList(String filePath, boolean b) {
        if (b || GameSession.skillBaseList.isEmpty()) {
            GameSession.skillBaseList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
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
    public static List<ItemBase> loadItemBaseList(String filePath, boolean b) {
        if (b || GameSession.itemBaseList.isEmpty()) {
            GameSession.itemBaseList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                ItemBase newItem = new ItemBase();
                newItem.id = item.getString("id","empty");
                newItem.name = item.getString("name");
                newItem.level = item.getInt("level");
                newItem.price = item.getInt("price");

                GameSession.itemBaseList.add(newItem);
            }
        }
        return GameSession.itemBaseList;
    }

    public static List<EquipBase> loadEquipBaseList(String filePath, boolean b) {
        if (b || GameSession.equipBaseList.isEmpty()) {
            GameSession.equipBaseList.clear();
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

                GameSession.equipBaseList.add(newEquip);
            }
        }
        return GameSession.equipBaseList;
    }

    public static List<Bag> loadBagList(String filePath, boolean b) {
        if (b || GameSession.bagList.isEmpty()) {
            GameSession.bagList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                if (equip.isEmpty()) return null;
                Bag newBag = new Bag();
                newBag.id = equip.getString("id");
                newBag.type = equip.getString("type");
                newBag.index = equip.getInt("index", 1);
                GameSession.bagList.add(newBag);
            }
        }
        return GameSession.bagList;
    }

    public static Bag getBag(String keyId, boolean b) {
        if (GameSession.bagList != null && !GameSession.bagList.isEmpty()) {
            for (Bag item : GameSession.bagList) {
                if (item.id.equals(keyId)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<Lineup> loadLineupList(String filePath, boolean b) {
        if (b || GameSession.lineupList.isEmpty()) {
            GameSession.lineupList.clear();
            FileHandle fileHandle = Gdx.files.internal(filePath);
            if (!fileHandle.exists()) {
                fileHandle = Gdx.files.local(filePath);

                if (!fileHandle.exists()) {
                    System.err.println("File not found in both internal and local: " + filePath);
                    return GameSession.lineupList;
                }
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);
            if(root.get("reward")!= null){
                root = root.get("grid");
            }

            for (JsonValue jsv : root) {
                Lineup c = new Lineup();
                c.grid = jsv.getString("grid", "empty");
                c.characterId =jsv.getString("characterId","defaultCharacterId");
                c.characterBaseId =jsv.getString("characterBaseId","defaultCharacterBaseId");
                GameSession.lineupList.add(c);
            }
        }
        return GameSession.lineupList;
    }

    public static List<Hero> loadHeroList(String filePath, boolean b) {
        if (b || GameSession.heroList.isEmpty()) {
            GameSession.heroList.clear();
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
                newHero.equip = new Hero.Equip();
                if(equip != null){
                    newHero.equip.weapon = equip.getString("weapon","empty");
                    newHero.equip.armor = equip.getString("armor","empty");
                    newHero.equip.jewelry = equip.getString("jewelry","empty");
                    newHero.equip.support = equip.getString("support","empty");
                }



                GameSession.heroList.add(newHero);
            }
        }
        return GameSession.heroList;
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
