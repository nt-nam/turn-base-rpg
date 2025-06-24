package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class GridComponent implements Component {
    public int row;
    public int col;

    public GridComponent(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
