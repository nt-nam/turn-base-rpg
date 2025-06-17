package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteComponent implements Component {
    public TextureRegion region;
    public float offsetX, offsetY;

    public SpriteComponent(TextureRegion region, float offsetX, float offsetY) {
        this.region = region;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
