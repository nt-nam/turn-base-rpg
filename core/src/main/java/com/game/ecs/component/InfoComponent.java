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
        public EquipComponent weapon;
        public EquipComponent armor;
        public EquipComponent jewelry;
        public EquipComponent support;

        public Equipment() {
            this.weapon = new EquipComponent();
            this.armor = new EquipComponent();
            this.jewelry = new EquipComponent();
            this.support = new EquipComponent();
        }

        public Equipment(JsonValue json) {
            this.weapon = new EquipComponent(json.get("weapon"));
            this.armor = new EquipComponent(json.get("armor"));
            this.jewelry = new EquipComponent(json.get("jewelry"));
            this.support = new EquipComponent(json.get("support"));
        }
    }

}
