package com.game.utils.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class Equip {
    public String id;
    public String name;
    public String category;
    public Map<String, Integer> stats;

    public Equip() {
        stats = new HashMap<>();
    }

    // Hàm này để ánh xạ dữ liệu từ JSON vào đối tượng Equip
    public static Equip fromJson(JsonValue json) {
        Equip equip = new Equip();
        equip.id = json.getString("id");
        equip.name = json.getString("name");
        equip.category = json.getString("category");

        // Ánh xạ stats
        JsonValue statsJson = json.get("stats");
        if (statsJson != null) {
            for (JsonValue stat : statsJson) {
                equip.stats.put(stat.name(), stat.asInt()); // Chuyển đổi giá trị từ JSON thành số nguyên
            }
        }

        return equip;
    }
}
