package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.ecs.components.PositionComponent;
import com.game.ecs.components.SpriteComponent;

public class RenderSystem extends IteratingSystem {
    private SpriteBatch batch;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);


    public RenderSystem(Family family) {
        super(Family.all(PositionComponent.class, SpriteComponent.class).get());
        this.batch = batch;
    }

    public RenderSystem(Family family, int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = pm.get(entity);
        SpriteComponent sprite = sm.get(entity);
        batch.draw(sprite.region, pos.x, pos.y);
    }
}
