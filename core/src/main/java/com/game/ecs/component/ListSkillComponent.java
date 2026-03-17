package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.game.models.entity.skill.SkillBase;

public class ListSkillComponent implements Component {
    public Array<SkillComponent> skills;
    public ListSkillComponent() {
        skills = new Array<>();
    }
    public ListSkillComponent(SkillBase skillBase){
        skills = new Array<>();
        skills.add(new SkillComponent(1,skillBase.skill1));
        skills.add(new SkillComponent(2,skillBase.skill2));
        skills.add(new SkillComponent(3,skillBase.skill3));
    }
}
