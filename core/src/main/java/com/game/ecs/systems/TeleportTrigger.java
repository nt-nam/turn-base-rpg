package com.game.ecs.systems;

import com.badlogic.gdx.math.Rectangle;

public class TeleportTrigger {
    public final int index;
    public final String map;
    public final int spawn;
    public final Rectangle rect;

    public TeleportTrigger(int index, String map, int spawn, Rectangle rect) {
        this.index = index;
        this.map = map;
        this.spawn = spawn;
        this.rect = rect;
    }
}
