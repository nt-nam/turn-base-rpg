package com.game.utils.json;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Info {
    public String name;
    public int level;
    public String characterSelect;
    public String area;
    public Vector2 pos;
    public int sizeTeam;
    public int exp;
    public int numberOfTeammatesRecruited;
    public int numberOfEnemies;
    public List<String> unlocked;

    public Info() {}

    public Info(String name, int level, String characterSelect, String area, Vector2 pos, int sizeTeam, int exp,
                int numberOfTeammatesRecruited, int numberOfEnemies, List<String> unlocked) {
        this.name = name;
        this.level = level;
        this.characterSelect = characterSelect;
        this.area = area;
        this.pos = pos;
        this.sizeTeam = sizeTeam;
        this.exp = exp;
        this.numberOfTeammatesRecruited = numberOfTeammatesRecruited;
        this.numberOfEnemies = numberOfEnemies;
        this.unlocked = unlocked;
    }
}
