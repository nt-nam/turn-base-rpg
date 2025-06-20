package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;

public class BoundComponent implements Component {
    public enum BoundType { RECT, POLYGON, POLYLINE }
    public BoundType type;
    public Rectangle rect;
    public Polygon polygon;
    public Polyline polyline;

    // Thuộc tính bổ sung (tuỳ game)
    public boolean isWall = false;
    public boolean isHazard = false;
    public boolean isSensor = false; // va chạm không cứng

    // Constructors cho từng loại
    public BoundComponent(Rectangle rect) {
        this.type = BoundType.RECT;
        this.rect = rect;
    }
    public BoundComponent(Polygon polygon) {
        this.type = BoundType.POLYGON;
        this.polygon = polygon;
    }
    public BoundComponent(Polyline polyline) {
        this.type = BoundType.POLYLINE;
        this.polyline = polyline;
    }
}
