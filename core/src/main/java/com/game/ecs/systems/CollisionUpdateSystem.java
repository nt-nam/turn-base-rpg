package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.PositionComponent;

public class CollisionUpdateSystem extends EntitySystem {
    private final ComponentMapper<BoundComponent> cm = ComponentMapper.getFor(BoundComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(
            Family.all(BoundComponent.class, PositionComponent.class).get()
        );
        for (Entity entity : entities) {
            BoundComponent cc = cm.get(entity);
            PositionComponent pos = pm.get(entity);
            if (cc.type == BoundComponent.BoundType.RECT) {
                cc.rect.setPosition(pos.x, pos.y);
            } else if (cc.type == BoundComponent.BoundType.POLYGON) {
                cc.polygon.setPosition(pos.x, pos.y);
            }
        }
    }
}
