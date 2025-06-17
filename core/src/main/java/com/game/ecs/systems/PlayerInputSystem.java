package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.game.ecs.component.InputComponent;
import com.game.ecs.component.PositionComponent;

public class PlayerInputSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public PlayerInputSystem() {
        super(Family.all(PositionComponent.class, InputComponent.class).get());
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = pm.get(entity);
        int v = 100;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  pos.x -= v * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) pos.x += v * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    pos.y += v * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  pos.y -= v * deltaTime;
    }
}
