package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.game.core.Mappers;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;

public class BattleStateSystem extends EntitySystem {
    public boolean isDead(Entity entity) {
        CharacterComponent c = Mappers.base.get(entity);
        StatComponent s = Mappers.stat.get(entity);
        return c == null || s == null || s.hp <= 0;
    }

    public boolean isBattleOver(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        return isTeamDead(playerTeam) || isTeamDead(enemyTeam);
    }

    private boolean isTeamDead(Array<Entity> team) {
        if (team.isEmpty()) return true;
        for (Entity e : team) {
            if (!isDead(e)) return false;
        }
        return true;
    }

    public String checkWinner(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        if (isTeamDead(enemyTeam)) return "player";
        if (isTeamDead(playerTeam)) return "enemy";
        return "draw checkWinner";
    }
}
