package com.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.systems.BattleStateSystem;
import com.game.ecs.systems.TargetSelectionSystem;
import com.game.ecs.systems.TurnExecutionSystem;
import com.game.ecs.systems.TurnOrderSystem;

public class BattleSimulator {
    private TurnOrderSystem turnOrderSystem;
    private TargetSelectionSystem targetSelectionSystem;
    private TurnExecutionSystem turnExecutionSystem;
    private BattleStateSystem battleStateSystem;

    public BattleSimulator() {
        turnOrderSystem = new TurnOrderSystem();
        targetSelectionSystem = new TargetSelectionSystem();
        turnExecutionSystem = new TurnExecutionSystem();
        battleStateSystem = new BattleStateSystem();
    }

    public BattleSimulationResult run(Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        if (playerTeam == null || enemyTeam == null || playerTeam.isEmpty() || enemyTeam.isEmpty()) {
            Gdx.app.error("BattleSimulator", "Invalid teams: player=" + (playerTeam == null ? "null" : playerTeam.size) +
                ", enemy=" + (enemyTeam == null ? "null" : enemyTeam.size));
            throw new BattleSimulationException("Teams cannot be null or empty");
        }

        BattleSimulationResult result = new BattleSimulationResult();
        result.rounds.add(new Array<>());

        // Initialize engine
        Engine engine = new Engine();
        engine.addSystem(turnOrderSystem);
        engine.addSystem(targetSelectionSystem);
        engine.addSystem(turnExecutionSystem);
        engine.addSystem(battleStateSystem);

        // Add entities
        Array<Entity> all = new Array<>();
        all.addAll(playerTeam);
        all.addAll(enemyTeam);
        for (Entity e : all) {
            engine.addEntity(e);
            CharacterComponent c = Mappers.base.get(e);
            Gdx.app.log("BattleSimulator", "Added entity: " + (c != null ? c.characterId : "null"));
        }

        int roundCount = 0;
        while (!battleStateSystem.isBattleOver(playerTeam, enemyTeam)) {
            if (roundCount >= BattleConfig.getMaxRounds()) {
                Gdx.app.error("BattleSimulator", "Max rounds reached: " + BattleConfig.getMaxRounds());
                result.winner = "draw max rounds";
                break;
            }

            // Update grid state
            targetSelectionSystem.updateGridState(playerTeam, enemyTeam);

            Array<Entity> order = turnOrderSystem.getTurnOrder();
            Gdx.app.log("BattleSimulator", "Round " + (roundCount + 1) + ": Processing " + order.size + " actors");
            Array<TurnResult> currentRound = result.rounds.get(result.rounds.size - 1);
            boolean anyAction = false;

            for (Entity actor : order) {
                CharacterComponent actorComp = Mappers.base.get(actor);
                String actorId = actorComp != null ? actorComp.characterId : "unknown";
                if (battleStateSystem.isDead(actor)) {
                    Gdx.app.log("BattleSimulator", "Skipping actor " + actorId + ": dead");
                    continue;
                }

                Entity target = targetSelectionSystem.selectTarget(actor, playerTeam, enemyTeam);
                if (target == null || battleStateSystem.isDead(target)) {
                    Gdx.app.log("BattleSimulator", "No valid target for actor " + actorId);
                    continue;
                }

                CharacterComponent targetComp = Mappers.base.get(target);
                Gdx.app.log("BattleSimulator", "Actor " + actorId + " targeting " +
                    (targetComp != null ? targetComp.characterId : "unknown"));

                TurnResult turn = turnExecutionSystem.executeTurn(actor, target, playerTeam, enemyTeam);
                if (turn.damage > 0 || turn.targetDead || !turn.skillUsed.isEmpty()) {
                    currentRound.add(turn);
                    anyAction = true;
                    Gdx.app.log("BattleSimulator", "Action performed: " + turn.actorId + " -> " + turn.targetId +
                        ", skill=" + turn.skillUsed + ", damage=" + turn.damage);
                } else {
                    Gdx.app.log("BattleSimulator", "No valid action for actor " + actorId + ": empty turn result");
                }

                if (battleStateSystem.isBattleOver(playerTeam, enemyTeam)) {
                    Gdx.app.log("BattleSimulator", "Battle over after action by " + actorId);
                    break;
                }
            }

            if (!anyAction) {
                Gdx.app.error("BattleSimulator", "No actions performed in round " + (roundCount + 1));
                result.winner = "draw anyAction";
                break;
            }

            if (!battleStateSystem.isBattleOver(playerTeam, enemyTeam)) {
                result.rounds.add(new Array<>());
            }

            roundCount++;
            turnOrderSystem.reset();
        }

        if (result.winner == null) {
            result.winner = battleStateSystem.checkWinner(playerTeam, enemyTeam);
        }

        Gdx.app.log("BattleSimulator", "Battle ended. Winner: " + result.winner);
        return result;
    }
}
