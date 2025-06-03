package com.game.pxworld.entities;

import com.game.pxworld.components.Component;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    Map<Class<? extends Component>, Component> components = new HashMap<>();

    // Thêm component vào entity
    void addComponent(Component c) {
        components.put(c.getClass(), c);
    }

    // Lấy component theo kiểu (PositionComponent, HealthComponent...)
    <T extends Component> T getComponent(Class<T> c) {
        return (T) components.get(c);
    }
}
