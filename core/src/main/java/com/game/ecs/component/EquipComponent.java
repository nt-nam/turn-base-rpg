package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;
import java.util.Map;

public class EquipComponent implements Component {
    public String id;
    public String name;
    public String category;
    public Map<String, Integer> stats;

    public EquipComponent() {
        stats = new HashMap<>();
    }
}
