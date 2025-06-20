package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.PositionComponent;
import com.game.utils.data.AnimationCache;

public class SpriteRenderSystem extends EntitySystem {
    private final SpriteBatch batch = new SpriteBatch();
    private final OrthographicCamera camera;
    private final ComponentMapper<PositionComponent> posM = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpriteComponent> sprM = ComponentMapper.getFor(SpriteComponent.class);
    private final Engine engine;

    public SpriteRenderSystem(Engine engine, OrthographicCamera camera) {
        this.engine = engine;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        // Nếu muốn hỗ trợ render layer, hãy sort entities trước (code mẫu để lại comment)
        ImmutableArray<Entity> entities = engine.getEntitiesFor(
            Family.all(PositionComponent.class, SpriteComponent.class).get()
        );

        batch.begin();
        for (Entity entity : entities) {
            PositionComponent pos = posM.get(entity);
            SpriteComponent spr = sprM.get(entity);

            TextureRegion frame = null;

            // Nếu có animation thì render animation
            if (spr.characterId != null && spr.animationName != null) {
                Animation<TextureRegion> anim = AnimationCache.get(spr.characterId, spr.animationName);
                if (anim != null) {
                    spr.stateTime += deltaTime;
                    boolean looping = anim.getPlayMode() == Animation.PlayMode.LOOP || anim.getPlayMode() == Animation.PlayMode.LOOP_PINGPONG;
                    frame = anim.getKeyFrame(spr.stateTime, looping);
                }
            }

            // Nếu không có animation, dùng staticRegion
            if (frame == null && spr.staticRegion != null) {
                frame = spr.staticRegion;
            }

            if (frame == null) continue; // Không có gì để vẽ

            // Xử lý flipX, flipY
            if (spr.flipX != frame.isFlipX()) frame.flip(true, false);
            if (spr.flipY != frame.isFlipY()) frame.flip(false, true);

            // Áp dụng color, alpha
            batch.setColor(spr.tint.r, spr.tint.g, spr.tint.b, spr.alpha);

            // Vẽ, hỗ trợ origin & rotation nếu muốn
            float width = frame.getRegionWidth() * spr.scale;
            float height = frame.getRegionHeight() * spr.scale;
            float originX = spr.originX;
            float originY = spr.originY;
            float rotation = spr.rotation;

            batch.draw(
                frame,
                pos.x, pos.y,
                originX, originY,
                width, height,
                1f, 1f,
                rotation
            );

            // Reset lại màu batch về mặc định để tránh ảnh hưởng entity tiếp theo
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }
}
