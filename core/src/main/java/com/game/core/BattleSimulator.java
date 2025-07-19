package com.game.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.StatComponent;

public class BattleSimulator {
    private TurnExecution turnExecution;
    private BattleState battleState;

    public static Array<Entity> playerTeam;
    public static Array<Entity> enemyTeam;
    public BattleSimulator() {
        turnExecution = new TurnExecution();
        battleState = new BattleState();
    }

    public BattleSimulationResult run(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        BattleSimulator.playerTeam = playerTeam;
        BattleSimulator.enemyTeam = enemyTeam;
        if (playerTeam == null || enemyTeam == null || playerTeam.isEmpty() || enemyTeam.isEmpty()) {
            Gdx.app.error("BattleSimulator", "Invalid teams: player=" + (playerTeam == null ? "null" : playerTeam.size) +
                ", enemy=" + (enemyTeam == null ? "null" : enemyTeam.size));
            throw new BattleSimulationException("Teams cannot be null or empty");
        }

        BattleSimulationResult result = new BattleSimulationResult();
        result.rounds.add(new Array<>());

        Array<Entity> all = new Array<>();
        all.addAll(playerTeam);
        all.addAll(enemyTeam);
        all.sort((a, b) -> {
            StatComponent sa = a.getComponent(StatComponent.class);
            StatComponent sb = b.getComponent(StatComponent.class);
            return Integer.compare(sb.agi, sa.agi);
        });

        int roundCount = 0;
        while (!battleState.isBattleOver(playerTeam, enemyTeam)) {
            if (roundCount >= BattleConfig.getMaxRounds()) {
                Gdx.app.error("BattleSimulator", "Max rounds reached: " + BattleConfig.getMaxRounds());
                result.winner = "draw max rounds";
                break;
            }

            Array<Entity> order = all;
            Array<TurnResult> currentRound = result.rounds.get(result.rounds.size - 1);
            boolean anyAction = false;

            for (Entity actor : order) {
                boolean isActor = actor.getComponent(PlayerComponent.class) != null;

                if (battleState.isDead(actor)) {
                    continue;
                }

                Entity target;
                if (isActor) {
                    target = getTarget(actor, enemyTeam);
                } else {
                    target = getTarget(actor, playerTeam);
                }

                if (target == null) {
                    Gdx.app.error("BattleSimulator", "null target");
                }

                if (target == null || battleState.isDead(target)) {
                    continue;
                }

                TurnResult turn = turnExecution.executeTurn(actor, target);
                currentRound.add(turn);
                anyAction = true;

                if (battleState.isBattleOver(playerTeam, enemyTeam)) {
                    break;
                }
            }

            if (!anyAction) {
                Gdx.app.error("BattleSimulator", "No actions performed in round " + (roundCount + 1));
                result.winner = "draw anyAction";
                break;
            }

            if (!battleState.isBattleOver(playerTeam, enemyTeam)) {
                result.rounds.add(new Array<>());
            }

            roundCount++;
        }

        if (result.winner == null) {
            result.winner = battleState.checkWinner(playerTeam, enemyTeam);
        }

        Gdx.app.log("BattleSimulator", "Battle ended. Winner: " + result.winner);
        return result;
    }

    private Entity getTarget(Entity actor, Array<Entity> targetTeam) {
        GridComponent actorGrid = actor.getComponent(GridComponent.class);
        int hp = actor.getComponent(StatComponent.class).hp;
        CharacterComponent ca = actor.getComponent(CharacterComponent.class);

        if (ca == null || actorGrid == null || hp <= 0) {
            Gdx.app.log("TargetSelection", "Invalid actor: " + (ca != null ? ca.nameRegion : "null"));
            return null;
        }

        boolean iPlayer = actor.getComponent(PlayerComponent.class) != null;

        // Lọc các mục tiêu hợp lệ từ targetTeam (chỉ lấy các entity còn sống)
        Array<Entity> validTargets = new Array<>();
        for (Entity e : targetTeam) {
            if (isValidTarget(e)) {
                validTargets.add(e);
            }
        }

        // Nếu không có mục tiêu hợp lệ, trả về null
        if (validTargets.isEmpty()) {
            Gdx.app.error("TargetSelection", "No valid target found for actor: " + ca.nameRegion);
            return validTargets.first();
        }

        // Tìm mục tiêu hợp lệ trong cùng cột (col) nếu actor là iPlayer
        if (iPlayer) {
            // Sắp xếp các mục tiêu trong cùng cột của actor (theo thứ tự trước)
            validTargets.sort((a, b) -> {
                GridComponent ga = a.getComponent(GridComponent.class);
                GridComponent gb = b.getComponent(GridComponent.class);
                return Integer.compare(ga.row, gb.row);  // Sắp xếp theo hàng (row), chọn mục tiêu đầu tiên
            });

            // Kiểm tra mục tiêu trong cùng cột (với iPlayer, chọn entity có thứ tự đầu tiên trong cột)
            for (Entity target : validTargets) {
                GridComponent targetGrid = target.getComponent(GridComponent.class);
                if (targetGrid.col == actorGrid.col) {
                    return target;
                }
            }
        } else {
            // Nếu không phải iPlayer, lấy entity cuối cùng trong row
            validTargets.sort((a, b) -> {
                GridComponent ga = a.getComponent(GridComponent.class);
                GridComponent gb = b.getComponent(GridComponent.class);
                return Integer.compare(gb.row, ga.row);  // Sắp xếp theo hàng (row), chọn mục tiêu cuối cùng
            });

            // Kiểm tra mục tiêu trong cùng cột, nếu không phải iPlayer thì chọn mục tiêu cuối cùng trong row
            for (Entity target : validTargets) {
                GridComponent targetGrid = target.getComponent(GridComponent.class);
                if (targetGrid.col == actorGrid.col) {
                    return target;
                }
            }
        }

        return validTargets.first();
    }

    // Kiểm tra xem mục tiêu có hợp lệ không (còn sống)
    private boolean isValidTarget(Entity e) {
        StatComponent stat = e.getComponent(StatComponent.class);
        return stat != null && stat.hp > 0;
    }

    public static Array<Entity> getPlayerTeam() {
        return playerTeam;
    }

    public static Array<Entity> getEnemyTeam() {
        return enemyTeam;
    }
}
