package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.game.core.BattleSimulationResult;

public class BattlePlaybackComponent implements Component {
    public BattleSimulationResult result;
    public int currentTurn = 0;
    public float turnTimer = 0f;
    public float delayPerTurn = 1f;
}
