package com.game.ecs.systems;

import com.badlogic.ashley.core.Component;

public class TriggerComponent implements Component {
    public String id;
    public int index;
    public String name;

    public TriggerComponent(String id, int index) {
        this.id = id;
        this.index = index;
    }

    public TriggerComponent(String id, int index, String name) {
        this.id = id;
        this.index = index;
        this.name = name;
    }
}
