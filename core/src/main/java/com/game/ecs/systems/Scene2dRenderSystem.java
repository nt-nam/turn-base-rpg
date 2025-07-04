package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.ecs.component.LabelComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.Scene2dComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.utils.data.AnimationCache;

public class Scene2dRenderSystem extends EntitySystem {
    private final SpriteBatch batch = new SpriteBatch();
    private Engine engine;
    private final ComponentMapper<PositionComponent> posM = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<Scene2dComponent> scene2dM = ComponentMapper.getFor(Scene2dComponent.class);
    private final OrthographicCamera camera;
    public Scene2dRenderSystem(Engine engine, OrthographicCamera camera) {
        this.engine = engine;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        ImmutableArray<Entity> entities = engine.getEntitiesFor(
            Family.all(Scene2dComponent.class, LabelComponent.class).get()
        );

        batch.begin();
        for (Entity entity : entities) {
            PositionComponent pos = posM.get(entity);
            entity.getComponent(LabelComponent.class).label.draw(batch,1);
            // Reset lại màu batch
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }
    @Override
    public void removedFromEngine(Engine engine) {
        batch.dispose();
    }
}
