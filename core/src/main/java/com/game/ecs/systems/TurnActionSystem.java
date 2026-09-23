package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
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
                    Gdx.app.debug("TurnActionSystem", "Player won the battle! Triggering win.");
                    if (BattleScreen.instance != null) {
                        BattleScreen.instance.triggerWin();
                    } else {
                        Gdx.app.error("TurnActionSystem", "BattleScreen.instance is null when triggering win");
                    }
                    return;
                }
                if(nextAction.action.equals("enemy") || nextAction.action.startsWith("draw")){
                    Gdx.app.debug("TurnActionSystem", "Enemy won or match drawn (" + nextAction.action + "). Triggering fail.");
                    if (BattleScreen.instance != null) {
                        BattleScreen.instance.triggerFail();
                    } else {
                        Gdx.app.error("TurnActionSystem", "BattleScreen.instance is null when triggering fail");
                    }
                    return;
                }
                if (!queue.isProcessing) {
                    // Target tracking removed - user deleted BattleScreen.setTarget
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

                if (nextAction.actor != null) {
                    AnimationStateComponent actorAnim = nextAction.actor.getComponent(AnimationStateComponent.class);
                    if (actorAnim != null) {
                        actorAnim.requested = AnimationStateComponent.State.ATTACK;
                    }
                }

                AnimationStateComponent targetAnimationState = null;
                if (nextAction.target != null) {
                    targetAnimationState = nextAction.target.getComponent(AnimationStateComponent.class);
                }
                AnimationStateComponent.State targetState = (targetAnimationState != null) ? targetAnimationState.current : null;

                if (nextAction.state == SkillStateComponent.State.HIDE && nextAction.target != null) {
                    targetState = AnimationStateComponent.State.HURT;
                    LabelComponent labelComponent = nextAction.target.getComponent(LabelComponent.class);
                    float x = nextAction.target.getComponent(PositionComponent.class).x;
                    float y = nextAction.target.getComponent(PositionComponent.class).y;

                    if (Objects.equals(nextAction.action, "damage")) {
                        int damage = nextAction.note.get("damage");
                        HealthBarComponent healthBar = nextAction.target.getComponent(HealthBarComponent.class);
                        if (healthBar != null) {
                             healthBar.currentHp = Math.max(0, healthBar.currentHp - damage);
                        }
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("-" + damage, x, y, false));
                        Gdx.app.debug("TurnActionSystem", "Damage: " + damage);
                    }
                    if (Objects.equals(nextAction.action, "critical")) {
                        int damage = nextAction.note.get("damage");
                        HealthBarComponent healthBar = nextAction.target.getComponent(HealthBarComponent.class);
                        if (healthBar != null) {
                             healthBar.currentHp = Math.max(0, healthBar.currentHp - damage);
                        }
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("-" + damage, x, y, true));
                        Gdx.app.debug("TurnActionSystem", "Critical: " + damage);
                    }
                    if (Objects.equals(nextAction.action, "heal")) {
                        int heal = nextAction.note.get("heal");
                        HealthBarComponent healthBar = nextAction.target.getComponent(HealthBarComponent.class);
                        StatComponent stat = nextAction.target.getComponent(StatComponent.class);
                        if (healthBar != null && stat != null) {
                             healthBar.currentHp = Math.min(stat.maxHp, healthBar.currentHp + heal);
                        }
                        // spawn a heal label (green, non-critical, isHeal = true)
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("+" + heal, x, y, false, true));
                        Gdx.app.debug("TurnActionSystem", "Heal: " + heal);
                    }
                    if (Objects.equals(nextAction.action, "miss")) {
                        labelComponent.activeLabels.add(new LabelComponent.DamageText("Miss", x, y, false));
                        Gdx.app.debug("TurnActionSystem", "Miss!!!");
                    }

                    if (Objects.equals(nextAction.action, "dead")) {
                        targetState = AnimationStateComponent.State.DIE;
                        Gdx.app.debug("TurnActionSystem", "Die: ");
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
