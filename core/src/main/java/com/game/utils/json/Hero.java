package com.game.utils.json;

import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class Hero {
    public String characterId;
    public String characterBaseId;
    public String grid;
    public int star;
    public int level;
    public CharacterBase characterBase;
    public Map<String, Integer> equip;

    public Hero() {
    }

    public Hero(JsonValue jsonValue) {
        characterId = jsonValue.getString("characterId", "characterIdDefault");
        characterBaseId = jsonValue.getString("characterBaseId","characterBaseIdDefault");
        grid = jsonValue.getString("grid", "empty");
        star = jsonValue.getInt("star", 0);
        level = jsonValue.getInt("level", 1);
        characterBase = new CharacterBase();
        equip = new HashMap<>();
        for (JsonValue item : jsonValue.get("equip")) {
            equip.put(item.name, item.asInt());
        }
    }
}
