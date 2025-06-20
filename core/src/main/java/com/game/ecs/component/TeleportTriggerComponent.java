package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class TeleportTriggerComponent implements Component {
    public String nextMap;
    public int nextSpawn;
    public String name;
    public TeleportTriggerComponent( String nextMap, int nextSpawn, String name) {
        this.nextMap = nextMap;
        this.nextSpawn = nextSpawn;
        this.name = name;
    }
}
