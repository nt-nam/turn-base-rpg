package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.game.utils.json.CharacterBase;

/**
 * CharacterComponent chứa các thông tin cơ bản của nhân vật trong toàn bộ game được load từ file Json: character_base.json
 * Dùng để truy cập dữ liệu cơ bản của từng nhân vật.
 */
public class CharacterComponent implements Component {
    public String characterBaseId;  // Cập nhật theo cấu trúc mới
    public String classType;        // Warrior, Assassin, Mage, Ranger, Support, Tank
    public String role;
    public String name;
    public String desc;

    public int hp, mp, atk, def, agi, crit;

    public Array<String> skills;
    public Array<String> counters;
    public Array<String> weakAgainst;


    public CharacterComponent() {
        skills = new Array<>();
        counters = new Array<>();
        weakAgainst = new Array<>();
    }

    public CharacterComponent(CharacterBase characterBase) {
        new CharacterComponent();
        if (characterBase != null) {
            characterBaseId = characterBase.characterBaseId;
            classType = characterBase.classType;
            role = characterBase.role;
            name = characterBase.name;
            desc = characterBase.desc;

            hp = characterBase.hp;
            mp = characterBase.mp;
            atk = characterBase.atk;
            def = characterBase.def;
            agi = characterBase.agi;
            crit = characterBase.crit;

            skills = characterBase.skills;
            counters = characterBase.counters;
            weakAgainst = characterBase.weakAgainst;

        }
    }

    public CharacterComponent fromJson(JsonValue json) {
        if (json == null) {
            return null;
        }

        // Đọc các trường cơ bản
        characterBaseId = json.getString("characterBaseId", "");
        classType = json.getString("classType", "");
        role = json.getString("role", "");
        name = json.getString("name", "");
        desc = json.getString("desc", "");

        hp = json.getInt("hp", 0);
        mp = json.getInt("mp", 0);
        atk = json.getInt("atk", 0);
        def = json.getInt("def", 0);
        agi = json.getInt("agi", 0);
        crit = json.getInt("crit", 0);

        // Đọc mảng skills
        if (json.has("skills")) {
            for (JsonValue skillJson : json.get("skills")) {
                skills.add(skillJson.asString());
            }
        }

        // Đọc mảng counters
        if (json.has("counters")) {
            for (JsonValue counterJson : json.get("counters")) {
                counters.add(counterJson.asString());
            }
        }

        // Đọc mảng weakAgainst
        if (json.has("weakAgainst")) {
            for (JsonValue weakJson : json.get("weakAgainst")) {
                weakAgainst.add(weakJson.asString());
            }
        }

        return this;
    }
}
