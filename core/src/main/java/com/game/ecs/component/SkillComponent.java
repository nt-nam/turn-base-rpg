package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class SkillComponent implements Component {
    public int id;
    public String name;
    public String description;
    public JsonValue effect;

    public SkillComponent(int id, String name, String description, JsonValue effect) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.effect = effect;
    }
}
