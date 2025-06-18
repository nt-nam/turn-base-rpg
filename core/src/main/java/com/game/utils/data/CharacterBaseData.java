package com.game.utils.data;

import com.badlogic.gdx.utils.Array;

public class CharacterBaseData {
    public String characterId;
    public String type; // knight, npc, enemy, boss, ...
    public String name;
    public int hp, mp, atk, def, agi, crit;
    public String desc;
    public Array<String> skills;

}
