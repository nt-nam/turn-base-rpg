package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.ecs.component.ActionQueueComponent;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.MoveToComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SkillStateComponent;
import com.badlogic.ashley.core.ComponentMapper;

public class ActionQueueSystem extends IteratingSystem {
    private ComponentMapper<ActionQueueComponent> queueMapper = ComponentMapper.getFor(ActionQueueComponent.class);
    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SkillStateComponent> stateMapper = ComponentMapper.getFor(SkillStateComponent.class);

    public ActionQueueSystem() {
        super(Family.all(ActionQueueComponent.class, PositionComponent.class).exclude(MoveToComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ActionQueueComponent queue = queueMapper.get(entity);
        PositionComponent position = positionMapper.get(entity);
        SkillStateComponent state = stateMapper.get(entity);

        if (!queue.actions.isEmpty() && !queue.isProcessing) {
            ActionQueueComponent.Action action = queue.actions.removeFirst();
            if (action.target == null || action.actor == null) return;
            queue.isProcessing = true;
            float startX = action.actor.getComponent(PositionComponent.class).x, startY = action.actor.getComponent(PositionComponent.class).y;
            float endX = action.target.getComponent(PositionComponent.class).x, endY = action.target.getComponent(PositionComponent.class).y;
            // Set the initial position to startX, startY
            position.prevX = position.x;
            position.prevY = position.y;
            position.x = startX;
            position.y = startY;

//            System.out.println(action.actor.getComponent(AnimationStateComponent.class).requested.toString());
//            System.out.println("actu");
            action.actor.getComponent(AnimationStateComponent.class).requested = AnimationStateComponent.State.ATTACK;
            action.target.getComponent(AnimationStateComponent.class).requested = AnimationStateComponent.State.HURT;
            // Add MoveToComponent to start the movement
            MoveToComponent moveTo = new MoveToComponent(startX, startY, endX, endY, action.duration);
            entity.add(moveTo);

            // Set the requested state if provided
            if (state != null && action.state != null) {
                state.requested = action.state;
            }
        }
    }
}
