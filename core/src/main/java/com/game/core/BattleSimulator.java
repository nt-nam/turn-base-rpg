package com.game.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.ecs.component.StatComponent;

public class BattleSimulator {
    public static BattleSimulationResult run(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        BattleSimulationResult result = new BattleSimulationResult();

        Array<Entity> all = new Array<>();
        all.addAll(playerTeam);
        all.addAll(enemyTeam);

        ObjectMap<String, TempStat> stats = new ObjectMap<>();
        for (Entity e : all) {
            StatComponent s = Mappers.stat.get(e);
            CharacterBaseDataComponent c = Mappers.base.get(e);
            stats.put(c.characterId, new TempStat(s.hp, s.mp, s.atk, s.def, s.agi, s.crit));
        }

        Array<Entity> order = new Array<>(all);
        order.sort((a, b) -> Integer.compare(
            stats.get(b.getComponent(CharacterBaseDataComponent.class).characterId).agi,
            stats.get(a.getComponent(CharacterBaseDataComponent.class).characterId).agi
        ));

        while (!isOver(playerTeam, enemyTeam, stats)) {
            for (Entity actor : order) {
                if (isDead(actor, stats)) continue;
                Entity target = selectTarget(actor, playerTeam, enemyTeam, stats);
                if (target == null || isDead(target, stats)) continue;
                TurnResult turn = simulateTurn(actor, target, stats);
                result.turns.add(turn);
            }
        }

        result.winner = checkWinner(playerTeam, enemyTeam, stats);
        return result;
    }

    private static boolean isDead(Entity e, ObjectMap<String, TempStat> stats) {
        return stats.get(e.getComponent(CharacterBaseDataComponent.class).characterId).hp <= 0;
    }

    private static Entity selectTarget(Entity actor, Array<Entity> team1, Array<Entity> team2, ObjectMap<String, TempStat> stats) {
        boolean isEnemy = team2.contains(actor, true);
        Array<Entity> opposingTeam = isEnemy ? team1 : team2;
        for (Entity e : opposingTeam) {
            if (!isDead(e, stats)) return e;
        }
        return null;
    }

    private static TurnResult simulateTurn(Entity actor, Entity target, ObjectMap<String, TempStat> stats) {
        TempStat a = stats.get(actor.getComponent(CharacterBaseDataComponent.class).characterId);
        TempStat t = stats.get(target.getComponent(CharacterBaseDataComponent.class).characterId);
        float multiplier = 1f;

        boolean isCritical = MathUtils.random() < t.crit * 0.01f;
        float avoidChance = (float) t.agi / (t.agi + a.agi) * 0.5f;
        boolean isAvoid = MathUtils.random() < avoidChance;


        CharacterBaseDataComponent ca = Mappers.base.get(actor);
        CharacterBaseDataComponent ct = Mappers.base.get(target);
        if (ca.counters.contains(ct.classType, false)) multiplier += 0.25f;

        int damage = (int) ((a.atk - t.def / 2f) * multiplier);
        damage = Math.max(damage, 1);
        damage = (isCritical ? (int) (damage * MathUtils.random(1.5f, 2f)) : damage);
        damage = (isAvoid && !isCritical ? 0 : damage);
        t.hp -= damage;

        TurnResult r = new TurnResult();
        r.actorId = actor.getComponent(CharacterBaseDataComponent.class).characterId;
        r.targetId = target.getComponent(CharacterBaseDataComponent.class).characterId;
        r.isCritical = isCritical;
        r.damage = damage;
        r.targetDead = t.hp <= 0;
        r.skillUsed = "Basic Attack";
        r.tempStat = new TempStat(t.hp, t.mp, t.atk, t.def, t.agi, t.crit);
        return r;
    }

    private static boolean isOver(Array<Entity> team1, Array<Entity> team2, ObjectMap<String, TempStat> stats) {
        return isTeamDead(team1, stats) || isTeamDead(team2, stats);
    }

    private static boolean isTeamDead(Array<Entity> team, ObjectMap<String, TempStat> stats) {
        for (Entity e : team) {
            if (!isDead(e, stats)) return false;
        }
        return true;
    }

    private static String checkWinner(Array<Entity> team1, Array<Entity> team2, ObjectMap<String, TempStat> stats) {
        if (isTeamDead(team2, stats)) return "player";
        if (isTeamDead(team1, stats)) return "enemy";
        return "draw";
    }
}
