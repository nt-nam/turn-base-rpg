package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class PositionComponent implements Component {
    public float x = 0, y = 0;
    public float prevX = 0, prevY = 0;

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
    }
}
