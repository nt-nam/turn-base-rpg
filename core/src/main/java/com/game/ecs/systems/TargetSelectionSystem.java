package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.core.Mappers;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.StatComponent;

public class TargetSelectionSystem extends EntitySystem {
    private ObjectMap<Integer, Array<Entity>> playerByCol;
    private ObjectMap<Integer, Array<Entity>> enemyByCol;

    public TargetSelectionSystem() {
        playerByCol = new ObjectMap<>();
        enemyByCol = new ObjectMap<>();
    }

    public void updateGridState(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        playerByCol.clear();
        enemyByCol.clear();

        for (Entity e : playerTeam) {
            addToGrid(e, playerByCol);
        }
        for (Entity e : enemyTeam) {
            addToGrid(e, enemyByCol);
        }
        Gdx.app.log("TargetSelection", "Grid state updated: player cols=" + playerByCol.size +
            ", enemy cols=" + enemyByCol.size);
    }

    private void addToGrid(Entity e, ObjectMap<Integer, Array<Entity>> byCol) {
        GridComponent g = Mappers.grid.get(e);
        StatComponent s = Mappers.stat.get(e);
        CharacterComponent c = Mappers.base.get(e);
        if (g == null || s == null || s.hp <= 0 || c == null) {
            Gdx.app.log("TargetSelection", "Skipping entity " + (c != null ? c.characterId : "null") +
                ": missing components or dead");
            return;
        }

        Array<Entity> entities = byCol.get(g.col, new Array<>());
        byCol.put(g.col, entities);
        entities.add(e);
        Gdx.app.log("TargetSelection", "Added " + c.characterId + " to col " + g.col);
    }

    public Entity selectTarget(Entity actor, Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        CharacterComponent ca = Mappers.base.get(actor);
        GridComponent actorGrid = Mappers.grid.get(actor);
        StatComponent actorStats = Mappers.stat.get(actor);
        if (ca == null || actorGrid == null || actorStats == null || actorStats.hp <= 0) {
            Gdx.app.log("TargetSelection", "Invalid actor: " + (ca != null ? ca.characterId : "null"));
            return null;
        }

        boolean isEnemy = enemyTeam.contains(actor, true);
        ObjectMap<Integer, Array<Entity>> opposingByCol = isEnemy ? playerByCol : enemyByCol;

        // Try any column with valid targets
        Array<Entity> allTargets = new Array<>();
        for (Array<Entity> targets : opposingByCol.values()) {
            for (Entity e : targets) {
                if (isValidTarget(e)) allTargets.add(e);
            }
        }

        if (!allTargets.isEmpty()) {
            Entity target = allTargets.first();
            Gdx.app.log("TargetSelection", ca.characterId + " selected target: " +
                Mappers.base.get(target).characterId);
            return target;
        }

        Gdx.app.log("TargetSelection", "No valid target found for actor: " + ca.characterId);
        return null;
    }

    private boolean isValidTarget(Entity e) {
        StatComponent stat = Mappers.stat.get(e);
        CharacterComponent c = Mappers.base.get(e);
        return e != null && stat != null && stat.hp > 0 && c != null;
    }
}
