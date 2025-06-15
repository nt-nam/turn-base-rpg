package com.game.ecs.components;

import com.badlogic.ashley.core.Component;

public class StatsComponent implements Component {
    public int hp, maxHp, atk, def, mp, maxMp, level;
    public StatsComponent(int hp, int maxHp, int atk, int def, int mp, int maxMp, int level) {
        this.hp = hp;
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
        this.mp = mp;
        this.maxMp = maxMp;
        this.level = level;
    }
}
