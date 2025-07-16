package com.game.utils.json;

import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

//hero_full.json
public class Hero {
    public String characterId;
    public String characterBaseId;
    public String grid;
    public int star;
    public int level;
    public Equip equip;

    public Hero() {
    }

    public Hero(JsonValue jsonValue) {
        characterId = jsonValue.getString("characterId", "characterIdDefault");
        characterBaseId = jsonValue.getString("characterBaseId", "characterBaseIdDefault");
        grid = jsonValue.getString("grid", "empty");
        star = jsonValue.getInt("star", 0);
        level = jsonValue.getInt("level", 1);
        equip.weapon = jsonValue.get("equip").getString("weapon");
        equip.armor = jsonValue.get("equip").getString("armor");
        equip.jewelry = jsonValue.get("equip").getString("jewelry");
        equip.support = jsonValue.get("equip").getString("support");

    }

    public static class Equip {
        public String weapon;
        public String armor;
        public String jewelry;
        public String support;
    }
}
