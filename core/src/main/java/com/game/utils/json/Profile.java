package com.game.utils.json;

import com.badlogic.gdx.math.Vector2;
import com.game.utils.GameSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public int coin;
    public int gem;
    public int energy;
    public String energyTime;
    public int numberOfTeammatesRecruited;
    public int equipment;
    public int numberOfEnemies;
    public List<String> unlocked;
    public boolean playMusic;
    public boolean playSound;

    private int battleScore;

    public Profile() {
    }

    public Profile(String name, String characterSelect) {
        this.name = name;
        this.level = 1;
        this.characterSelect = characterSelect;
        this.area = "village_0";
        this.pos = new Vector2(-1, -1);
        this.dailyCheck = LocalDate.now().minusDays(1).toString();
        this.sizeTeam = 1;
        this.exp = 0;
        this.coin = 0;
        this.gem = 0;
        this.energy = 20;
        this.energyTime = "empty";
        this.numberOfTeammatesRecruited = 0;
        this.equipment = 0;
        this.numberOfEnemies = 0;
        this.unlocked = new ArrayList<>();
        this.playMusic = true;
        this.playSound = true;

        this.battleScore = 0;
    }

    public LocalDate getDailyCheck() {
        return dailyCheck != null ? LocalDate.parse(dailyCheck) : LocalDate.now();
    }

    public int getBattleScore() {
        battleScore = 0;
        for (Hero hero : GameSession.heroList) {
            if (hero.grid.equals("empty")) continue;
            battleScore += hero.getBattleScore();
        }
        return battleScore;
    }

    public boolean useCoin(int value) {
        if (coin >= value) {
            coin -= value;
            return true;
        }
        return false;
    }

    public boolean useGem(int value) {
        if (gem >= value) {
            gem -= value;
            return true;
        }
        return false;
    }

    public void addCoin(int value) {
        coin += value;
    }

    public void addGem(int value) {
        gem += value;
    }

    public int getEnergyMax(){
        return 20+(this.level/5);
    }
}
