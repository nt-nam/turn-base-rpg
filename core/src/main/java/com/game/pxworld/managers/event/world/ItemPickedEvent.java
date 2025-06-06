package com.game.pxworld.managers.event.world;

import com.game.pxworld.managers.event.GameEvent;

public class ItemPickedEvent implements GameEvent {
    public final String itemName;
    public ItemPickedEvent(String itemName) {
        this.itemName = itemName;
    }
}
