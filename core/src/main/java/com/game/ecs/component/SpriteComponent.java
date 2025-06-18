package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class SpriteComponent implements Component {
    public String characterId;
    public String animationName;
    public float stateTime = 0f;

    public SpriteComponent(String characterId, String anim) {
        this.characterId = characterId; this.animationName = anim;
    }
}
