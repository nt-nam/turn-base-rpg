package com.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.core.BattleSimulationResult;
import com.game.core.TurnResult;
import com.game.ecs.component.ActionQueueComponent;
import com.game.ecs.component.SkillStateComponent;

public class TurnProcessor {
    public void processTurns(Entity skill, BattleSimulationResult listResult) {
        ActionQueueComponent queue = skill.getComponent(ActionQueueComponent.class);
        if (queue == null) {
            queue = new ActionQueueComponent();
            skill.add(queue);
        }

        ///custom----------------
        for (Array<TurnResult> round : listResult.rounds) {
            for (TurnResult turn : round) {
                ObjectMap note = new ObjectMap();


                ///show action turn mới
                queue.actions.addLast(new ActionQueueComponent.Action(
                    turn.actorEntity,
                    turn.actorEntity,
                    1f, // Duration of movement
                    SkillStateComponent.State.ATTACK,
                    "",note
                ));
                queue.actions.addLast(new ActionQueueComponent.Action(
                    turn.actorEntity,
                    turn.targetEntity,
                    0.5f, // Duration of movement
                    SkillStateComponent.State.ULTIMATE,
                    "",note
                ));

                queue.actions.addLast(new ActionQueueComponent.Action(
                    turn.targetEntity,
                    turn.targetEntity,
                    0.5f,
                    SkillStateComponent.State.HIDE,
                    "",note
                ));


                if(turn.isCritical)
                    queue.actions.addLast(new ActionQueueComponent.Action(
                        turn.targetEntity,
                        turn.targetEntity,
                        0.0f,
                        SkillStateComponent.State.HIDE,"critical",note));
                if(turn.isMiss)
                    queue.actions.addLast(new ActionQueueComponent.Action(
                        turn.targetEntity,
                        turn.targetEntity,
                        0.0f,
                        SkillStateComponent.State.HIDE,"miss",note));

                if(turn.damage > 0){
                    note.put("damage",turn.damage);
                    queue.actions.addLast(new ActionQueueComponent.Action(
                        turn.targetEntity,
                        turn.targetEntity,
                        0.0f,
                        SkillStateComponent.State.HIDE,"damage",note));
                }
                if(turn.targetDead){
                    queue.actions.addLast(new ActionQueueComponent.Action(
                        turn.targetEntity,
                        turn.targetEntity,
                        1f,
                        SkillStateComponent.State.HIDE,"dead",note));
                }

            }
            ///show() popup round mới
        }
        ///end custom----------------
        queue.actions.addLast(new ActionQueueComponent.Action(
            null,
            null,
            1f,
            SkillStateComponent.State.HIDE,listResult.winner, null));
    }
}
