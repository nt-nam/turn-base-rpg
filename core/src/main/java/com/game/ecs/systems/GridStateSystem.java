package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.core.Mappers;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.StatComponent;

public class GridStateSystem extends IteratingSystem {
    private ObjectMap<Integer, Array<Entity>> playerByCol;
    private ObjectMap<Integer, Array<Entity>> enemyByCol;
    private Array<Entity> playerTeam;
    private Array<Entity> enemyTeam;

    public GridStateSystem(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        super(Family.all(GridComponent.class, StatComponent.class).get());
        this.playerByCol = new ObjectMap<>();
        this.enemyByCol = new ObjectMap<>();
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GridComponent g = Mappers.grid.get(entity);
        StatComponent s = Mappers.stat.get(entity);
        if (g == null || s == null || s.hp <= 0) return;

        ObjectMap<Integer, Array<Entity>> byCol = isEnemy(entity) ? enemyByCol : playerByCol;
        Array<Entity> entities = byCol.get(g.col);
        if (entities == null) {
            entities = new Array<>();
            byCol.put(g.col, entities);
        }
        entities.add(entity);
    }

    private boolean isEnemy(Entity entity) {
        return enemyTeam.contains(entity, true);
    }

    public ObjectMap<Integer, Array<Entity>> getPlayerByCol() {
        return playerByCol;
    }

    public ObjectMap<Integer, Array<Entity>> getEnemyByCol() {
        return enemyByCol;
    }

    public void reset() {
        playerByCol.clear();
        enemyByCol.clear();
    }
}
