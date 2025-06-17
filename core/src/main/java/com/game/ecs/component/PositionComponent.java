package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class PositionComponent implements Component {
    public float x = 0, y = 0;

    public PositionComponent() {
    }

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
