package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.ecs.component.SkillStateComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.utils.data.AnimationCache;

public class SkillStateSystem extends EntitySystem {
    private final ComponentMapper<SpriteComponent> sprM = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<SkillStateComponent> stateM = ComponentMapper.getFor(SkillStateComponent.class);

    private final Engine engine;

    public SkillStateSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(
            Family.all(SpriteComponent.class, SkillStateComponent.class).get()
        );

        for (Entity entity : entities) {
            SpriteComponent spr = sprM.get(entity);
            SkillStateComponent state = stateM.get(entity);

            String targetAnim = spr.spriteId + "_" + state.current.name().toLowerCase();
            if (!spr.animationName.equals(targetAnim)) {
                spr.animationName = targetAnim;
                spr.stateTime = 0f;
            }

            Animation<TextureRegion> anim = AnimationCache.get(spr.spriteId, spr.animationName);
            if (anim == null) {
                System.out.println("[SkillStateSystem] Không tìm thấy animation: " + spr.spriteId + " / " + spr.animationName);
                continue;
            }

            if ((anim.getPlayMode() == Animation.PlayMode.NORMAL || anim.getPlayMode() == Animation.PlayMode.REVERSED)
                && anim.isAnimationFinished(spr.stateTime)) {
                state.current = SkillStateComponent.State.HIDE;
            }
        }
    }
}
