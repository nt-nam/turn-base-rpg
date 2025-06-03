package com.game.pxworld.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component{
    int currentHP, maxHP;
    public HealthComponent(int maxHP) {
        this.maxHP = maxHP;
        this.currentHP = maxHP;
    }
}
