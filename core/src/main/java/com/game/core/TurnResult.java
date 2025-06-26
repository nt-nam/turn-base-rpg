package com.game.core;

import com.badlogic.ashley.core.Entity;

public class TurnResult {
    public String actorId;
    public String targetId;
    public String skillUsed;
    public int damage;
    public boolean isCritical;
    public boolean targetDead;
    public Entity targetEntity; // Thêm để lưu Entity mục tiêu

    public TurnResult() {
        actorId = "";
        targetId = "";
        skillUsed = "";
        damage = 0;
        isCritical = false;
        targetDead = false;
        targetEntity = null;
    }

    // Reset để tái sử dụng trong pool
    public void reset() {
        actorId = "";
        targetId = "";
        skillUsed = "";
        damage = 0;
        isCritical = false;
        targetDead = false;
        targetEntity = null;
    }
}
