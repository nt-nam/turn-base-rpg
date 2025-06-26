package com.game.core;

import com.badlogic.gdx.utils.Array;

public class BattleSimulationResult {
    public Array<Array<TurnResult>> rounds;
    public String winner;

    public BattleSimulationResult() {
        rounds = new Array<>();
        winner = null;
    }
}
