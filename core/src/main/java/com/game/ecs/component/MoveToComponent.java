package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class MoveToComponent implements Component {
    public float startX, startY;
    public float endX, endY;
    public float duration=0;   // tổng thời gian đi từ start -> end (giây)
    public float elapsed=0;    // thời gian đã trôi qua
    public boolean reached = false; // đã tới đích chưa
    public MoveToComponent(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
    public MoveToComponent(float startX, float startY, float endX, float endY, float duration) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.duration = duration;
        this.elapsed = 0;
    }
}

