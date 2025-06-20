package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteComponent implements Component {
    public String characterId;
    public String animationName;
    public TextureRegion staticRegion = null;
    public float stateTime = 0;
    public float scale = 1f;
    public boolean flipX = false;
    public boolean flipY = false;

    public float rotation = 0f; // độ xoay
    public float originX = 0f, originY = 0f; // tâm xoay
    public float alpha = 1f; // trong suốt
    public Color tint = Color.WHITE.cpy(); // màu vẽ

    public SpriteComponent(TextureRegion staticRegion, float scale, boolean flipX) {
        this.staticRegion = staticRegion;
        this.scale = scale;
        this.flipX = flipX;
    }

    public SpriteComponent(String characterId, String animationName) {
        this.characterId = characterId;
        this.animationName = animationName;
    }

    public SpriteComponent(String characterId, String animationName, float scale) {
        this.characterId = characterId;
        this.animationName = animationName;
        this.scale = scale;
    }

    public SpriteComponent(String id, String anim, float scale, boolean flipX) {
        this.characterId = id;
        this.animationName = anim;
        this.scale = scale;
        this.flipX = flipX;
    }
}
