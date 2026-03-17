package com.game.models.entity;

public class Lineup {
    public String grid;
    public String characterId;
    public String nameRegion;

    public Lineup() {
    }

    public Lineup(Hero hero) {
        this.grid = hero.grid;
        this.characterId = hero.characterId;
        this.nameRegion = nameRegion;
    }
}
