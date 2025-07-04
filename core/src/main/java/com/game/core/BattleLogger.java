package com.game.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;

public class BattleLogger {
    public static void logBattleResult(BattleSimulationResult result, Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        // Create a map to quickly access entities by their characterId
        ObjectMap<String, Entity> entityMap = new ObjectMap<>();
        for (Entity e : playerTeam) {
            CharacterComponent base = e.getComponent(CharacterComponent.class);
            if (base != null) {
                e.add(new StatComponent().fromCharacter(base));
                entityMap.put(base.characterBaseId, e);
            }
        }
        for (Entity e : enemyTeam) {
            CharacterComponent base = e.getComponent(CharacterComponent.class);
            if (base != null) {
                e.add(new StatComponent().fromCharacter(base));
                entityMap.put(base.characterBaseId, e);
            }
        }

        // Log initial team states
        System.out.println("=== Battle Start ===");
        System.out.println("Player Team Initial State:");
        logTeamState(playerTeam);
        System.out.println("Enemy Team Initial State:");
        logTeamState(enemyTeam);
        System.out.println("===================");

        // Log each round
        int roundIndex = 0;
        for (Array<TurnResult> round : result.rounds) {
            System.out.println("Round " + (roundIndex + 1) + ":");
            if (round.isEmpty()) {
                System.out.println("  No actions performed in this round.");
            } else {
                int turnIndex = 0;
                for (TurnResult turn : round) {
                    // Get StatComponent from targetEntity
                    StatComponent targetStats = turn.targetEntity != null ? Mappers.stat.get(turn.targetEntity) : null;
                    int targetHp = targetStats != null ? targetStats.hp -= turn.damage : 0;
                    int targetMp = targetStats != null ? targetStats.mp /*+=turn.state.get("mp")*/: 0;

                    // Get CharacterComponent of actor and target to check counters and weakAgainst
                    Entity actorEntity = entityMap.get(turn.actorId);
                    Entity targetEntity = turn.targetEntity;
                    String counterFlag = "";
                    if (actorEntity != null && targetEntity != null) {
                        CharacterComponent actorComp = Mappers.base.get(actorEntity);
                        CharacterComponent targetComp = Mappers.base.get(targetEntity);
                        if (actorComp != null && targetComp != null && targetComp.classType != null) {
                            // Check counters
                            if (actorComp.counters != null && actorComp.counters.contains(targetComp.classType, false)) {
                                counterFlag += " [COUNTER]";
                            }
                            // Check weakAgainst
                            if (targetComp.weakAgainst != null && targetComp.weakAgainst.contains(actorComp.classType, false)) {
                                counterFlag += " [WEAK]";
                            }
                        }
                    }

                    // Log turn details
                    System.out.println(
                        "  Turn " + (++turnIndex) + ": " + turn.actorId +
                            " used " + (turn.skillUsed.isEmpty() ? "Unknown Skill" : turn.skillUsed) +
                            " on " + turn.targetId +
                            " :: Damage: " + turn.damage +
                            " :: Target HP: " + targetHp +
                            " :: Target MP: " + targetMp +
                            (turn.isCritical ? " [CRIT]" : "") +
                            counterFlag +
                            (turn.targetDead ? " -> Target defeated!" : "")
                    );
                }
            }
            roundIndex++;
        }

        // Log final team states
        System.out.println("=== Battle End ===");
        System.out.println("Player Team Final State:");
        logTeamState(playerTeam);
        System.out.println("Enemy Team Final State:");
        logTeamState(enemyTeam);
        System.out.println("Winner: " + result.winner);
        System.out.println("===================");

    }

    private static void logTeamState(Array<Entity> team) {
        if (team.isEmpty()) {
            System.out.println("  Team is empty.");
            return;
        }
        for (Entity e : team) {
            CharacterComponent base = Mappers.base.get(e);
            StatComponent stats = Mappers.stat.get(e);
            String characterId = base != null ? base.characterBaseId : "Unknown";
            int hp = stats != null ? stats.hp : 0;
            int mp = stats != null ? stats.mp : 0;
            String status = (stats != null && stats.hp > 0) ? "Alive" : "Dead";
            System.out.println(
                "  " + characterId +
                    " :: HP: " + hp +
                    " :: MP: " + mp +
                    " :: Status: " + status
            );
        }
    }
}
