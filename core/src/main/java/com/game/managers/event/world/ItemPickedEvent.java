package com.game.managers.event.world;

import com.game.managers.event.GameEvent;

public class ItemPickedEvent implements GameEvent {
    public final String itemName;
    public ItemPickedEvent(String itemName) {
        this.itemName = itemName;
    }
}
