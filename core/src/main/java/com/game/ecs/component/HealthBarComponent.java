package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class HealthBarComponent implements Component {
    public float percent;
    public float maxHp;
    public float currentHp;
    public float x;
    public float y;
    public float width;
    public float height;
    
    public HealthBarComponent(float percent, float x, float y, float width, float height) {
        this.percent = percent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
