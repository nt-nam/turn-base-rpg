package com.game.ecs.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public class EB extends Entity {
    Map<Class<? extends Component>, Component> components = new HashMap<>();

    void addComponent(Component c) {
        components.put(c.getClass(), c);
    }

}
