package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class SkillStateComponent implements Component {
    public enum State {
        HIDE,
        ATTACK,
        ATTACK_BIG,
        EXPLODE,
        HEAL,
        ULTIMATE,
    }

    public State current = State.HIDE;
    public State requested = null;
}
