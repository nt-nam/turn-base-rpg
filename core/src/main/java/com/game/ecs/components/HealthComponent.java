package com.game.ecs.components;

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

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public void setHealth(int currentHP, int maxHP) {
        this.currentHP = currentHP;
        this.maxHP = maxHP;
    }
}
