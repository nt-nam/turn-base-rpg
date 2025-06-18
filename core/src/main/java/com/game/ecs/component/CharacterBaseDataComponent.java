package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.game.utils.data.CharacterBaseData;

public class CharacterBaseDataComponent implements Component {
    public String characterId, type, name, desc;
    public int hp, mp, atk, def, agi, crit;
//    public Array<String> skills;

    public static CharacterBaseDataComponent from(CharacterBaseData c) {
        CharacterBaseDataComponent comp = new CharacterBaseDataComponent();
        comp.characterId = c.characterId;
        comp.type = c.type;
        comp.name = c.name;
        comp.hp = c.hp;
        comp.mp = c.mp;
        comp.atk = c.atk;
        comp.def = c.def;
        comp.agi = c.agi;
        comp.crit = c.crit;
        comp.desc = c.desc;
//        comp.skills = c.skills;
        return comp;
    }
}
