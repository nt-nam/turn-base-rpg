package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.game.core.BattleConfig;
import com.game.core.Mappers;
import com.game.core.TurnResult;
import com.game.core.TurnResultPool;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.SkillComponent;
import com.game.ecs.component.StatComponent;

public class TurnExecutionSystem extends EntitySystem {
    private SkillSystem skillSystem;

    public TurnExecutionSystem() {
        skillSystem = new SkillSystem();
    }

    public TurnResult executeTurn(Entity actor, Entity target, Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        CharacterComponent ca = Mappers.base.get(actor);
        StatComponent a = Mappers.stat.get(actor);
        if (ca == null || a == null) {
            Gdx.app.error("TurnExecution", "Actor " + (ca != null ? ca.characterId : "null") +
                " missing components");
            return TurnResultPool.getInstance().obtain();
        }

        ListSkillComponent listSkill = Mappers.skills.get(actor);
        if (listSkill == null || listSkill.skills == null || listSkill.skills.isEmpty()) {
            Gdx.app.error("TurnExecution", "Actor " + ca.characterId + " has no skills");
            return TurnResultPool.getInstance().obtain();
        }

        // Select skill
        SkillComponent selectedSkill = null;
        int mpCost = 0;

        // Prioritize skill 3 if enough MP
        for (SkillComponent skill : listSkill.skills) {
            if (skill.id == 3 && a.mp >= BattleConfig.getMpSkill3Cost()) {
                selectedSkill = skill;
                mpCost = BattleConfig.getMpSkill3Cost();
                break;
            }
        }

        // Try skill 2
        if (selectedSkill == null) {
            for (SkillComponent skill : listSkill.skills) {
                if (skill.id == 2 && a.mp >= BattleConfig.getMpSkill2Cost()) {
                    selectedSkill = skill;
                    mpCost = BattleConfig.getMpSkill2Cost();
                    break;
                }
            }
        }

        // Fallback to basic attack
        if (selectedSkill == null) {
            for (SkillComponent skill : listSkill.skills) {
                if (skill.id == 1) {
                    selectedSkill = skill;
                    mpCost = 0;
                    break;
                }
            }
        }

        if (selectedSkill == null) {
            Gdx.app.error("TurnExecution", "No valid skill found for actor " + ca.characterId);
            return TurnResultPool.getInstance().obtain();
        }

        // Apply skill
        TurnResult turn = skillSystem.applySkill(actor, target, selectedSkill, playerTeam, enemyTeam);
        turn.actorId = ca.characterId;
        turn.targetId = Mappers.base.get(target) != null ? Mappers.base.get(target).characterId : "unknown";
        turn.skillUsed = selectedSkill.name;
        turn.targetEntity = target;

        // Update MP
        if (selectedSkill.id == 1) {
            a.mp += BattleConfig.getMpBasicAttackGain();
        } else {
            a.mp -= mpCost;
        }
        StatComponent t = Mappers.stat.get(target);
        if (t != null && turn.damage > 0) {
            t.mp = Math.max(t.mp - BattleConfig.getMpTakenDamageLoss(), 0);
        }

        Gdx.app.log("TurnExecution", ca.characterId + " used " + selectedSkill.name + " on " +
            turn.targetId + ", damage=" + turn.damage);
        return turn;
    }
}
