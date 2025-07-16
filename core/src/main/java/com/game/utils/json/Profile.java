package com.game.utils.json;

import com.badlogic.gdx.math.Vector2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Profile {
    public String name;
    public int level;
    public String characterSelect;
    public String area;
    public Vector2 pos;
    public String dailyCheck;
    public int sizeTeam;
    public int exp;
    public int numberOfTeammatesRecruited;
    public int numberOfEnemies;
    public List<String> unlocked;

    public Profile() {
    }

    public Profile(String name, String characterSelect) {
        this.name = name;
        this.level = 1;
        this.characterSelect = characterSelect;
        this.area = "village_0";
        this.pos = new Vector2(0, 0);
        this.dailyCheck = LocalDate.now().minusDays(1).toString();
        this.sizeTeam = 1;
        this.exp = -1;
        this.numberOfTeammatesRecruited = -1;
        this.numberOfEnemies = -1;
        this.unlocked = new ArrayList<>();
    }
    public LocalDate getDailyCheck() {
        return dailyCheck != null ? LocalDate.parse(dailyCheck) : LocalDate.now();
    }
}
