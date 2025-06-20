package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class SkillStateComponent implements Component {
    public enum State {
        IDLE, WALK, RUN, ATTACK, JUMP, HURT, DIE
    }

    public State current = State.IDLE;
    public State requested = null;
}
