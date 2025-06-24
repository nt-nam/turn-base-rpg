package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class SizeComponent implements Component {
    public float width;
    public float height;
    public float scaleX = 1;
    public float scaleY = 1;

    public SizeComponent(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public SizeComponent(float width, float height, float scale) {
        this.width = width;
        this.height = height;
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public SizeComponent(float width, float height, float scaleX, float scaleY) {
        this.width = width;
        this.height = height;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}
