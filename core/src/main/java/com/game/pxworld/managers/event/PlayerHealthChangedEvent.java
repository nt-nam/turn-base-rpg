package com.game.pxworld.managers.event;

public class PlayerHealthChangedEvent implements GameEvent{
    public int newHealth;
    public PlayerHealthChangedEvent(int hp) {
        this.newHealth = hp;
    }
}
