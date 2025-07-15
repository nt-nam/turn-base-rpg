package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.JsonValue;
import com.game.utils.json.skill.Skill;

/// được truyền vào từ data/base/skill_base.json
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
    public SkillComponent(int id,Skill skill) {
        this.id = id;
        this.name = skill.name;
        this.description = skill.description;
        this.effect = new JsonValue(JsonValue.ValueType.object);
        this.effect.addChild(skill.effectSkill.name,new JsonValue(skill.effectSkill.value));
    }
}
