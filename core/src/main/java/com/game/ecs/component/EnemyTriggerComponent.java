package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class EnemyTriggerComponent implements Component {
    public int id;
    public String mapEnemy; // name từ map = characterId
    public int level;

    public EnemyTriggerComponent(int id, String mapEnemy, int level) {
        this.id = id;
        this.mapEnemy = mapEnemy;
        this.level = level;
    }
}
