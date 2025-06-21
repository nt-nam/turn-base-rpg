package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class GridComponent implements Component {
    public int x;
    public int y;

    GridComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
