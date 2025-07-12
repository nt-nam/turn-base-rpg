package com.game.utils.json;

import com.badlogic.gdx.utils.Array;

public class CharacterBase {
    public String characterId;
    public String characterBaseId;
    public String classType;
    public String role;
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
