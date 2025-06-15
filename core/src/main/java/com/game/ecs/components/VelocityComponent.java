package com.game.ecs.components;

import com.badlogic.ashley.core.Component;

public class VelocityComponent implements Component {
    public float vx = 0, vy = 0;
    public VelocityComponent() {}
    public VelocityComponent(float vx, float vy) { this.vx = vx; this.vy = vy; }
}
