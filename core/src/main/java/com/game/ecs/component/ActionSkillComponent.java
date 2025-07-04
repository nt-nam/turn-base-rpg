package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ActionSkillComponent implements Component {
    public Actions actions;
    public ActionSkillComponent(Actions actions) {
        this.actions = actions;
    }
}
