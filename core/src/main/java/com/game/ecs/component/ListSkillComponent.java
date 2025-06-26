package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class ListSkillComponent implements Component {
    public Array<SkillComponent> skills;
    public ListSkillComponent() {
        skills = new Array<>();
    }
}
