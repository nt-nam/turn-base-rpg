package com.game.ecs.component;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.ashley.core.Component;

// Component để lưu trữ dữ liệu nhân vật
public class InfoComponent implements Component {
    public String characterId;
    public String characterBaseId;
    public int star;
    public int level;
    public Equipment equip;

    // Constructor mặc định
    public InfoComponent() {
        this.equip = new Equipment();
    }

    // Constructor từ JsonValue
    public InfoComponent(JsonValue json) {
        this.characterId = json.getString("characterId", "unknown");
        this.characterBaseId = json.getString("characterBaseId", "unknown");
        this.star = json.getInt("star", 0);
        this.level = json.getInt("level", 1);
        this.equip = new Equipment(json.get("equip"));
    }

    // Lớp lồng để lưu trữ dữ liệu trang bị
    public static class Equipment {
        public String weapon;
        public String armor;
        public String jewelry;
        public String support;

        public Equipment() {
            this.weapon = "empty";
            this.armor = "empty";
            this.jewelry = "empty";
            this.support = "empty";
        }

        public Equipment(JsonValue json) {
            this.weapon = json.getString("weapon", "empty");
            this.armor = json.getString("armor", "empty");
            this.jewelry = json.getString("jewelry", "empty");
            this.support = json.getString("support", "empty");
        }
    }
}
