package com.game.utils.data;

import static com.game.utils.Constants.CHARACTER_BASE_JSON;
import static com.game.utils.Constants.SKILL_JSON;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.managers.GameSessionManager;
import com.game.models.entity.CharacterBase;
import com.game.models.entity.skill.EffectSkill;
import com.game.models.entity.skill.Skill;
import com.game.models.entity.skill.SkillBase;

import java.util.List;

/**
 * Loader cho SkillBase + CharacterBase (du lieu goc noi bo).
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class SkillCharacterLoader {

    private SkillCharacterLoader() {
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

    public static List<CharacterBase> loadCharacterBaseList() {
        if (GameSessionManager.getInstance().characterBaseList.isEmpty()) {
            FileHandle fileHandle = Gdx.files.internal(CHARACTER_BASE_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                CharacterBase newItem = new CharacterBase();

                newItem.characterId = item.getString("id", "");
                newItem.nameRegion = item.getString("nameRegion", "");
                newItem.classType = item.getString("classType", "");
                newItem.role = item.getString("role", "");
                newItem.name = item.getString("name", "");
                newItem.desc = item.getString("desc", "");

                newItem.hp = item.getInt("hp", 0);
                newItem.mp = item.getInt("mp", 0);
                newItem.atk = item.getInt("atk", 0);
                newItem.def = item.getInt("def", 0);
                newItem.agi = item.getInt("agi", 0);
                newItem.crit = item.getInt("crit", 0);

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

                GameSessionManager.getInstance().characterBaseList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().characterBaseList;
    }
}
