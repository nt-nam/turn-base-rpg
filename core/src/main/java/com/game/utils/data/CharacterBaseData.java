package com.game.utils.data;

import com.badlogic.gdx.utils.Array;

public class CharacterBaseData {
    public String characterId;
    public String classType; // Warrior, Assassin, Mage, Ranger, Support, Tank
    public String role; // Fighter, DPS, Caster, Healer, Tank
    public String name;
    public String desc;

    public int hp;
    public int mp;
    public int atk;
    public int def;
    public int agi;
    public int crit;

    public Array<String> skills;
    public Array<String> counters;
    public Array<String> weakAgainst;
}
