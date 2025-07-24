package com.game.utils.json;

import com.game.utils.GameSession;

public class Equip {
public String id;
public String nameRegion;
public int level;
public String target;

    public Equip() {
    }

    public Equip(String nameRegion) {
        id = "equip"+GameSession.profile.equipment++;
        this.nameRegion = nameRegion;
        level = 1;
        target = "empty";
    }
}
