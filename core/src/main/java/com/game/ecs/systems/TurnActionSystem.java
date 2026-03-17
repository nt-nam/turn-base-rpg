package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.game.ecs.component.ActionQueueComponent;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.LabelComponent;
import com.game.ecs.component.MoveToComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.HealthBarComponent;
import com.game.ecs.component.SkillStateComponent;
import com.game.ecs.component.StatComponent;
import com.game.screens.battle.BattleScreen;

import java.util.Objects;

public class TurnActionSystem extends IteratingSystem {
    private ComponentMapper<MoveToComponent> moveToMapper = ComponentMapper.getFor(MoveToComponent.class);
    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SkillStateComponent> stateMapper = ComponentMapper.getFor(SkillStateComponent.class);
    private ComponentMapper<ActionQueueComponent> queueMapper = ComponentMapper.getFor(ActionQueueComponent.class);

    public TurnActionSystem() {
        super(Family.all(MoveToComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MoveToComponent moveTo = moveToMapper.get(entity);
        PositionComponent position = positionMapper.get(entity);
        SkillStateComponent state = stateMapper.get(entity);
        ActionQueueComponent queue = queueMapper.get(entity);

        if (moveTo.reached) {
            // If movement is complete and there's a queue, start the next action
            if (queue != null && !queue.actions.isEmpty() && !queue.isProcessing) {
                ActionQueueComponent.Action nextAction = queue.actions.removeFirst();

                if(nextAction.action.equals("player")){
                    BattleScreen.instance.triggerWin();
                    return;
                }
                if(nextAction.action.equals("enemy")){
                    BattleScreen.instance.triggerFail();
                    return;
                }
                if (!queue.isProcessing) {
                    BattleScreen.setTarget(nextAction.target);
//                    BattleScreen.skill = nextAction.actor;
                }
                queue.isProcessing = true;

                // Update MoveToComponent for the next action
                moveTo.startX = nextAction.startX;
                moveTo.startY = nextAction.startY;
                moveTo.endX = nextAction.endX;
                moveTo.endY = nextAction.endY;
                moveTo.duration = nextAction.duration;
                moveTo.elapsed = 0;
                moveTo.reached = false;

                nextAction.actor.getComponent(AnimationStateComponent.class).requested = AnimationStateComponent.State.ATTACK;

                AnimationStateComponent targetAnimationState = nextAction.target.getComponent(AnimationStateComponent.class);
                AnimationStateComponent.State targetState = (targetAnimationState != null) ? targetAnimationState.current : null;

                if (nextAction.state == SkillStateComponent.State.HIDE) {
                    targetState = AnimationStateComponent.State.HURT;
                    LabelComponent labelComponent = nextAction.target.getComponent(LabelComponent.class);
                    float x = nextAction.target.getComponent(PositionComponent.class).x;
                    float y = nextAction.target.getComponent(PositionComponent.class).y;

                    if (Objects.equals(nextAction.action, "damage")) {
                        int damage = nextAction.note.get("damage");
                        int hp = nextAction.target.getComponent(StatComponent.class).hp -= damage;
                        nextAction.target.getComponent(HealthBarComponent.class).currentHp = hp;
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("-" + damage, x, y, false));
                        System.out.println("Damage: " + damage);
                    }
                    if (Objects.equals(nextAction.action, "critical")) {
                        int damage = nextAction.note.get("damage");
                        int hp = nextAction.target.getComponent(StatComponent.class).hp -= damage;
                        nextAction.target.getComponent(HealthBarComponent.class).currentHp = hp;
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("-" + damage, x, y, true));
                        System.out.println("Critical: " + damage);
                    }
                    if (Objects.equals(nextAction.action, "miss")) {
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("Miss", x, y, false));
                        System.out.println("Miss!!!");
                    }

                    if (Objects.equals(nextAction.action, "dead")) {
                        targetState = AnimationStateComponent.State.DIE;
                        System.out.println("Die: ");
                    }


                    // Cập nhật lại trạng thái của đối tượng mục tiêu
                    if (targetAnimationState != null) {
                        targetAnimationState.current = targetState;
                    }
                }


                // Set the requested state if provided
                if (state != null && nextAction.state != null) {
                    state.requested = nextAction.state;
                }
            }
            return;
        }

        // Update elapsed time
        moveTo.elapsed += deltaTime;

        // Calculate interpolation factor (t) between 0 and 1
        float t = moveTo.duration > 0 ? Math.min(moveTo.elapsed / moveTo.duration, 1f) : 1f;

        // Linear interpolation for smooth movement
        position.prevX = position.x;
        position.prevY = position.y;
        position.x = lerp(moveTo.startX, moveTo.endX, t);
        position.y = lerp(moveTo.startY, moveTo.endY, t);

        // Check if destination is reached
        if (t >= 1f) {
            moveTo.reached = true;
            position.x = moveTo.endX;
            position.y = moveTo.endY;

            // Update state if SkillStateComponent exists
            if (state != null && state.requested != null) {
                state.current = state.requested;
                state.requested = null;
            }

            // Mark queue as ready for the next action
            if (queue != null) {
                queue.isProcessing = false;
            }
        }
    }

    // Linear interpolation helper method
    private float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }
}
