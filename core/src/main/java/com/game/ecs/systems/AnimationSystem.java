package com.game.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class AnimationSystem extends EntitySystem {
    private Engine engine;

    public AnimationSystem(Engine engine) {
        this.engine = engine;
    }
    @Override
    public void update(float deltaTime) {

    }

}
