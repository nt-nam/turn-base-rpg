package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.JsonValue;

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

    public EquipComponent(JsonValue json) {
        this.id = json.has("id") ? json.getString("id") : null;
        this.name = json.has("name") ? json.getString("name") : null;
        this.category = json.has("category") ? json.getString("category") : null;

        stats = new HashMap<>();
        JsonValue statsJson = json.get("stats");
        if (statsJson != null) {
            // Duyệt qua các trường trong "stats" và thêm vào map stats
            for (JsonValue stat : statsJson) {
                String statName = stat.name();
                int statValue = stat.asInt();
                stats.put(statName, statValue);
            }
        }
    }
}
