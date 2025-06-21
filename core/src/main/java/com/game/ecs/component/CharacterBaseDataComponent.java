package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.game.utils.data.CharacterBaseData;

public class CharacterBaseDataComponent implements Component {
    public String characterId;
    public String classType;
    public String role;
    public String name;
    public String desc;

    public int hp, mp, atk, def, agi, crit;

    public Array<String> skills;
    public Array<String> counters;
    public Array<String> weakAgainst;

    public static CharacterBaseDataComponent from(CharacterBaseData c) {
        CharacterBaseDataComponent comp = new CharacterBaseDataComponent();
        comp.characterId = c.characterId;
        comp.classType = c.classType;
        comp.role = c.role;
        comp.name = c.name;
        comp.desc = c.desc;

        comp.hp = c.hp;
        comp.mp = c.mp;
        comp.atk = c.atk;
        comp.def = c.def;
        comp.agi = c.agi;
        comp.crit = c.crit;

        comp.skills = new Array<>(c.skills);
        comp.counters = new Array<>(c.counters);
        comp.weakAgainst = new Array<>(c.weakAgainst);
        return comp;
    }
}
