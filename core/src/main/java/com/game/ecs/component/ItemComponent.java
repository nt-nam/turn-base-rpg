package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class ItemComponent implements Component {
    public String id; // ID của vật phẩm
    public String name; // Tên của vật phẩm
    public int level; // Cấp độ của vật phẩm
}
