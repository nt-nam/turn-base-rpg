package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class CharacterComponent implements Component {
    public String characterId;
    public String classType;// Warrior, Assassin, Mage, Ranger, Support, Tank
    public String role; // Fighter, DPS, Caster, Healer, Tank
    public String name;
    public String desc;

    public int hp, mp, atk, def, agi, crit;

    public Array<String> skills;
    public Array<String> counters;
    public Array<String> weakAgainst;

    public CharacterComponent(){
        skills = new Array<>();
        counters = new Array<>();
        weakAgainst = new Array<>();
    }
}
