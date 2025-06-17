package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class CharacterBaseDataComponent implements Component {
    public String knightId;
    public String name;
    public int hp, mp, atk, def, agi, crit;
    public String desc;
}
