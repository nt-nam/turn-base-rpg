package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class WarehouseComponent implements Component {
    public enum Type { equip, items }
    public Type type;
    public String id;
    public int index;///index là giá trị chỉ level (EQUIP) or quantity (ITEM)
}
