package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.ecs.component.SpriteComponent;
import com.badlogic.gdx.graphics.g2d.*;
import com.game.ecs.component.PositionComponent;
import com.game.utils.data.AnimationCache;

public class SpriteRenderSystem extends EntitySystem {
    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private ComponentMapper<PositionComponent> posM = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> sprM = ComponentMapper.getFor(SpriteComponent.class);

    private Engine engine;

    public SpriteRenderSystem(Engine engine, OrthographicCamera camera) {
        this.engine = engine;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PositionComponent.class, SpriteComponent.class).get());
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity entity : entities) {
            PositionComponent pos = posM.get(entity);
            SpriteComponent spr = sprM.get(entity);

            // Cập nhật stateTime
            spr.stateTime += deltaTime;

            // Lấy Animation từ cache
            Animation<TextureRegion> anim = AnimationCache.get(spr.characterId, spr.animationName);
            if (anim == null) continue; // Không có animation thì bỏ qua

            // Lấy frame hiện tại
            TextureRegion frame = anim.getKeyFrame(spr.stateTime, true);

            // Render tại vị trí pos.x, pos.y
            batch.draw(frame, pos.x, pos.y);
        }
        batch.end();
    }
}
