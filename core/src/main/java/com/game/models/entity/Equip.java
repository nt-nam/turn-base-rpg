package com.game.models.entity;

import com.game.managers.GameSessionManager;

public class Equip {
public String id;
public String nameRegion;
public int level;
public String target;

    public Equip() {
    }

    public Equip(String nameRegion) {
        id = "equip"+GameSessionManager.getInstance().profile.equipment++;
        this.nameRegion = nameRegion;
        level = 1;
        target = "empty";
    }
}
