package com.game.models.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Ket qua cua mot tran dau, dung de man BattleResultScreen hien thi.
 * Duoc BattleController ghi khi win/fail.
 */
public class BattleResult {
    public boolean won;
    public int expGained;
    public List<Reward> rewards = new ArrayList<>();

    public BattleResult() {
    }

    public BattleResult(boolean won, int expGained, List<Reward> rewards) {
        this.won = won;
        this.expGained = expGained;
        if (rewards != null) {
            this.rewards = rewards;
        }
    }
}
