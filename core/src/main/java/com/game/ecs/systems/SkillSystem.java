package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.game.ecs.component.MoveToComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SkillStateComponent;
import com.game.ecs.component.SpriteComponent;

public class SkillSystem extends EntitySystem {
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpriteComponent> spr = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<SkillStateComponent> ssm = ComponentMapper.getFor(SkillStateComponent.class);
    private final ComponentMapper<MoveToComponent> mtm = ComponentMapper.getFor(MoveToComponent.class);

    private ImmutableArray<Entity> skills;
    private static final float SPEED = 500f;

    public SkillSystem(Engine engine) {
        // không nhận start/end ở system mà truyền khi tạo entity
        // mỗi entity skill sẽ chứa MoveToComponent riêng!
    }

    @Override
    public void addedToEngine(Engine engine) {
        skills = engine.getEntitiesFor(Family.all(
            PositionComponent.class,
            SpriteComponent.class,
            SkillStateComponent.class,
            MoveToComponent.class
        ).get());
    }

        @Override
    public void update(float deltaTime) {
        for (Entity entity : skills) {
            PositionComponent pos = pm.get(entity);
            SkillStateComponent state = ssm.get(entity);
            MoveToComponent move = mtm.get(entity);

            // Chỉ di chuyển khi ATTACK hoặc ATTACK_BIG
            if(move.duration==0){
                if (state.current == SkillStateComponent.State.ATTACK || state.current == SkillStateComponent.State.ATTACK_BIG) {
                    Vector2 from = new Vector2(pos.x, pos.y);
                    Vector2 to = new Vector2(move.endX, move.endY);
                    Vector2 dir = to.cpy().sub(from);
                    float distance = dir.len();

                    if (distance < SPEED * deltaTime) {
                        // Đã tới đích, cập nhật trạng thái EXPLODE
                        pos.x = move.endX;
                        pos.y = move.endY;
                        state.current = SkillStateComponent.State.EXPLODE;
                    } else {
                        // Cập nhật vị trí mới
                        dir.nor();
                        pos.x += dir.x * SPEED * deltaTime;
                        pos.y += dir.y * SPEED * deltaTime;
                    }
                }

                if (state.current == SkillStateComponent.State.HIDE) {
                    pos.x=move.startX;
                    pos.y=move.startY;
                }

                if (state.current == SkillStateComponent.State.ULTIMATE) {
                    pos.x=move.endX;
                    pos.y=move.endY;
                }
            }else{
                // Di chuyển theo thời gian (duration)
                if (state.current == SkillStateComponent.State.ATTACK || state.current == SkillStateComponent.State.ATTACK_BIG) {
                    move.elapsed += deltaTime;
                    float alpha = Math.min(1f, move.elapsed / move.duration); // 0..1

                    // Nội suy vị trí
                    pos.x = move.startX + (move.endX - move.startX) * alpha;
                    pos.y = move.startY + (move.endY - move.startY) * alpha;

                    if (alpha >= 1f) {
                        // Đã tới đích, chuyển sang EXPLODE
                        pos.x = move.endX;
                        pos.y = move.endY;
                        state.current = SkillStateComponent.State.EXPLODE;
                    }
                }

                // Trạng thái HIDE: reset về điểm bắt đầu
                if (state.current == SkillStateComponent.State.HIDE) {
                    pos.x = move.startX;
                    pos.y = move.startY;
                    move.elapsed = 0f;
                }

                // Trạng thái ULTIMATE: nhảy về điểm cuối
                if (state.current == SkillStateComponent.State.ULTIMATE) {
                    pos.x = move.endX;
                    pos.y = move.endY;
                    move.elapsed = move.duration;
                }
            }

        }
    }

}
