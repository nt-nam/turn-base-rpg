package com.game.utils.json.skill;

import java.util.List;

public class SkillBase {
    public String name;
    public Skill skill1;
    public Skill skill2;
    public Skill skill3;

    @Override
    public String toString() {
        return "\nSkillBase: "+name+" \n    skill1: "+skill1.name+"- " + skill1.description + " - " + skill1.effectSkill.name + " " + skill1.effectSkill.value
            +" \n    skill2: "+skill2.name+" - " + skill2.description + " - " + skill2.effectSkill.name + " " + skill2.effectSkill.value
            +" \n    skill3: "+skill3.name+" - " + skill3.description + " - " + skill3.effectSkill.name + " " + skill3.effectSkill.value;
    }
}
