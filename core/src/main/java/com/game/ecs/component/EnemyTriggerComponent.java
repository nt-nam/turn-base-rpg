package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class EnemyTriggerComponent implements Component {
    public String characterId; // name từ map = characterId
    public int level;

    public EnemyTriggerComponent(String characterId, int level) {
        this.characterId = characterId;
        this.level = level;
    }
}
