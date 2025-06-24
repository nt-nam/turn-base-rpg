package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class BattleCharacterComponent implements Component {
    public boolean isPlayer;
    public Array<String> skills;
    public BattleState state = BattleState.IDLE;

    public enum BattleState {
        IDLE,      // Đợi lượt
        ACTING,    // Đang ra đòn
        DEAD,      // Đã chết
        STUNNED,   // Bị choáng
        WINNER,    // Thắng trận
        LOSER      // Thua trận
    }

    public BattleCharacterComponent(boolean isPlayer, Array<String> skills) {
        this.isPlayer = isPlayer;
        this.skills = skills;
    }
}
