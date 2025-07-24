package com.game.utils.json;

import com.badlogic.gdx.utils.JsonValue;

//hero_full.json
public class Hero {
    public String characterId;
    public String nameRegion;
    public String grid;
    public int star;
    public int level;
    public int exp;
    public Stat stat;
    public Equip equip;

    public Hero() {
    }

    public Hero(JsonValue jsonValue) {
        characterId = jsonValue.getString("characterId", "characterIdDefault");
        nameRegion = jsonValue.getString("nameRegion", "nameRegionDefault");
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

    public boolean checkLevel() {
        if (this.exp >= this.level * 100) {
            this.level++;
            this.exp -= this.level * 100;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Hero{" + "characterId='" + characterId + '\'' + ", nameRegion='" + nameRegion + '\'' + ", grid='" + grid + '\'' + ", star=" + star + ", level=" + level + ", exp=" + exp + ", stat=" + stat + ", equip=" + equip + '}';
    }
}
