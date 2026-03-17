package com.game.combat;

import com.badlogic.ashley.core.Entity;
import java.util.HashMap;
import java.util.Map;

public class TurnResult {
    public String actorId;
    public String targetId;
    public String skillUsed;
    public int damage;
    public Map<String, Float> state; // Buffs/debuffs applied
    public boolean isCritical;
    public boolean isMiss;
    public boolean targetDead;
    public Entity actorEntity;
    public Entity targetEntity;
    public String effectDescription; // Human-readable description

    public TurnResult() {
        actorId = "";
        targetId = "";
        skillUsed = "";
        damage = 0;
        isCritical = false;
        isMiss = false;
        targetDead = false;
        actorEntity = null;
        targetEntity = null;
        state = new HashMap<>();
        effectDescription = "";
    }

    public void reset() {
        actorId = "";
        targetId = "";
        skillUsed = "";
        damage = 0;
        isCritical = false;
        isMiss = false;
        targetDead = false;
        actorEntity = null;
        targetEntity = null;
        state.clear();
        effectDescription = "";
    }

    @Override
    public String toString() {
        return "TurnResult{" +
            "actorId='" + actorId + '\'' +
            ", targetId='" + targetId + '\'' +
            ", skillUsed='" + skillUsed + '\'' +
            ", damage=" + damage +
            ", isCritical=" + isCritical +
            ", isMiss=" + isMiss +
            ", targetDead=" + targetDead +
            ", state=" + state +
            ", effectDescription='" + effectDescription + '\'' +
            ", actorEntity=" + (actorEntity != null ? "visible" : "null") +
            ", targetEntity=" + (targetEntity != null ? "visible" : "null") +
            '}';
    }
}
