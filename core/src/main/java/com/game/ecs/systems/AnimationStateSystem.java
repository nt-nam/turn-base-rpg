package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.utils.data.AnimationCache;

public class AnimationStateSystem extends EntitySystem {
    private final ComponentMapper<SpriteComponent> sprM = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<AnimationStateComponent> stateM = ComponentMapper.getFor(AnimationStateComponent.class);

    private final Engine engine;

    public AnimationStateSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(
            Family.all(SpriteComponent.class,AnimationStateComponent.class).get()
        );
        for (Entity entity : entities) {
            SpriteComponent spr = sprM.get(entity);
            AnimationStateComponent state = stateM.get(entity);

            // 1. Đồng bộ state sang animationName nếu state thay đổi
            String targetAnim = state.requested.name().toLowerCase();
            if (!spr.animationName.equals(targetAnim)) {
                spr.animationName = targetAnim;
                spr.stateTime = 0f;
            }

            // 2. Lấy animation hiện tại
            Animation<TextureRegion> anim = AnimationCache.get(spr.spriteId, spr.animationName);
            if (anim == null) continue;

            // 3. Nếu animation chỉ chạy 1 lần (NORMAL/REVERSED) và đã hết thì chuyển lại trạng thái phù hợp
            if ((anim.getPlayMode() == Animation.PlayMode.NORMAL || anim.getPlayMode() == Animation.PlayMode.REVERSED)
                && anim.isAnimationFinished(spr.stateTime)) {
                switch (state.requested) {
                    case DIE:
                        break;
                    default:
//                        state.current = AnimationStateComponent.State.IDLE;
                        state.requested =state.current;
                        break;
                }
                // Khi đổi state sẽ tự đồng bộ sang animationName lần sau
            }
        }
    }
}
