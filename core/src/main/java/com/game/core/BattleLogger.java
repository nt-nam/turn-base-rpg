package com.game.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;

public class BattleLogger {
    public static void logBattleResult(BattleSimulationResult result, Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        ObjectMap<String, Entity> entityMap = new ObjectMap<>();
        for (Entity e : playerTeam) {
            CharacterComponent base = Mappers.base.get(e);
            if (base != null) {
                entityMap.put(base.characterId, e);
            }
        }
        for (Entity e : enemyTeam) {
            CharacterComponent base = Mappers.base.get(e);
            if (base != null) {
                entityMap.put(base.characterId, e);
            }
        }

        // Log ra từng lượt đánh
        int roundIndex = 0;
        for (Array<TurnResult> round : result.rounds) {
            System.out.println("Round " + (roundIndex + 1) + ":");
            int turnIndex = 0;
            for (TurnResult turn : round) {
                // Lấy StatComponent từ targetEntity
                StatComponent targetStats = turn.targetEntity != null ? Mappers.stat.get(turn.targetEntity) : null;
                int targetHp = targetStats != null ? targetStats.hp : 0;
                int targetMp = targetStats != null ? targetStats.mp : 0;

                // Lấy CharacterComponent của actor và target để kiểm tra counters và weakAgainst
                Entity actorEntity = entityMap.get(turn.actorId);
                Entity targetEntity = turn.targetEntity;
                String counterFlag = "";
                if (actorEntity != null && targetEntity != null) {
                    CharacterComponent actorComp = Mappers.base.get(actorEntity);
                    CharacterComponent targetComp = Mappers.base.get(targetEntity);
                    if (actorComp != null && targetComp != null && targetComp.classType != null) {
                        // Kiểm tra counters
                        if (actorComp.counters != null && actorComp.counters.contains(targetComp.classType, false)) {
                            counterFlag += " [COUNTER]";
                        }
                        // Kiểm tra weakAgainst
                        if (targetComp.weakAgainst != null && targetComp.weakAgainst.contains(actorComp.classType, false)) {
                            counterFlag += " [WEAK]";
                        }
                    }
                }

                System.out.println(
                    "Battle " + (++turnIndex) + ": " + turn.actorId +
                        " used " + (turn.skillUsed.isEmpty() ? "Unknown Skill" : turn.skillUsed) +
                        " on " + turn.targetId +
                        " :: " + turn.damage +
                        " damage :: " + targetHp + " hp :: " + targetMp + " mp" +
                        (turn.isCritical ? " [CRIT]" : "") +
                        counterFlag +
                        (turn.targetDead ? " -> Doi phuong chet!" : "")
                );
            }
            roundIndex++;
        }

        // Log ra đội thắng
        System.out.println("Doi thang: " + result.winner);

        // Giải phóng TurnResult về pool
        for (Array<TurnResult> round : result.rounds) {
            for (TurnResult turn : round) {
                TurnResultPool.getInstance().free(turn);
            }
        }
    }
}
