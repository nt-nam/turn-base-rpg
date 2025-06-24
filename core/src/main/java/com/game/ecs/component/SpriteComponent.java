package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteComponent implements Component {
    public String spriteId;
    public String animationName;
    public TextureRegion staticRegion = null;
    public float stateTime = 0;
    public boolean flipX = false;
    public boolean flipY = false;

    public float rotation = 0f; // độ xoay
    public float originX = 0f, originY = 0f; // tâm xoay
    public float alpha = 1f; // trong suốt
    public Color tint = Color.WHITE.cpy(); // màu vẽ

    public SpriteComponent(TextureRegion staticRegion, boolean flipX) {
        this.staticRegion = staticRegion;
        this.flipX = flipX;
    }

    public SpriteComponent(String spriteId, String animationName) {
        this.spriteId = spriteId;
        this.animationName = animationName;
    }


    public SpriteComponent(String id, String anim, boolean flipX) {
        this.spriteId = id;
        this.animationName = anim;
        this.flipX = flipX;
    }
}
