package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    int currentHP, maxHP;

    public HealthComponent(int maxHP) {
        this.maxHP = maxHP;
        this.currentHP = maxHP;
    }

    public void update(int damage) {
        currentHP -= damage;
        if (currentHP <= 0) {
            currentHP = 0;
        }
    }

    public boolean isDead() {
        return currentHP <= 0;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

}
