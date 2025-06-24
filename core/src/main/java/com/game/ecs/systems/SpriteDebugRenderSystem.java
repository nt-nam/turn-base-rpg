package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.SpriteDebugComponent;
import com.game.ecs.component.SizeComponent;
import com.game.utils.data.AnimationCache;

public class SpriteDebugRenderSystem extends EntitySystem {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final OrthographicCamera camera;
    private final ComponentMapper<PositionComponent> posM = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpriteComponent> sprM = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<SpriteDebugComponent> debugM = ComponentMapper.getFor(SpriteDebugComponent.class);
    private final ComponentMapper<SizeComponent> sizeM = ComponentMapper.getFor(SizeComponent.class);
    private final Engine engine;

    public SpriteDebugRenderSystem(Engine engine, OrthographicCamera camera) {
        this.engine = engine;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        ImmutableArray<Entity> entities = engine.getEntitiesFor(
            Family.all(PositionComponent.class, SpriteComponent.class, SpriteDebugComponent.class).get()
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Entity entity : entities) {
            PositionComponent pos = posM.get(entity);
            SpriteComponent spr = sprM.get(entity);
            SizeComponent size = sizeM.get(entity);

            TextureRegion frame = null;

            // Lấy frame từ animation nếu có
            if (spr.spriteId != null && spr.animationName != null) {
                Animation<TextureRegion> anim = AnimationCache.get(spr.spriteId, spr.animationName);
                if (anim != null) {
                    boolean looping = anim.getPlayMode() == Animation.PlayMode.LOOP || anim.getPlayMode() == Animation.PlayMode.LOOP_PINGPONG;
                    frame = anim.getKeyFrame(spr.stateTime, looping);
                }
            }

            // Nếu không có animation, dùng staticRegion
            if (frame == null && spr.staticRegion != null) {
                frame = spr.staticRegion;
            }

            if (frame == null) continue;

            // Kích thước khung: ưu tiên SizeComponent nếu có, nếu không dùng mặc định
            float width = sizeM.has(entity) ? size.width * size.scaleX : frame.getRegionWidth();
            float height = sizeM.has(entity) ? size.height * size.scaleY : frame.getRegionHeight();
            // Điều chỉnh vị trí dựa trên origin và tỷ lệ kích thước
            float scaleX = sizeM.has(entity) ? width / frame.getRegionWidth() : 1;
            float scaleY = sizeM.has(entity) ? height / frame.getRegionHeight() : 1;
            float x = pos.x - (spr.originX * scaleX);
            float y = pos.y - (spr.originY * scaleY);

            // Vẽ khung
            shapeRenderer.rect(x, y, width, height);
        }

        shapeRenderer.end();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        shapeRenderer.dispose();
    }
}
