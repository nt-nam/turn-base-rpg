package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.game.combat.EffectType;

public class BuffComponent implements Component {
    public static class Buff {
        public EffectType type;
        public float value;
        public int duration;

        public Buff(EffectType type, float value, int duration) {
            this.type = type;
            this.value = value;
            this.duration = duration;
        }
    }

    public Array<Buff> buffs = new Array<>(false, 8);

    public void addBuff(EffectType type, float value, int duration, StatComponent stats) {
        // Apply the stat change
        applyBuffStat(type, value, stats);
        // Add to active buffs list
        buffs.add(new Buff(type, value, duration));
    }

    public void applyBuffStat(EffectType type, float value, StatComponent stats) {
        if (stats == null) return;
        if (type == EffectType.ARMOR) stats.def += value;
        else if (type == EffectType.DODGE_CHANCE) stats.agi += value;
        else if (type == EffectType.CRIT_CHANCE) stats.critRate += value;
    }

    public void removeBuffStat(EffectType type, float value, StatComponent stats) {
        if (stats == null) return;
        if (type == EffectType.ARMOR) stats.def -= value;
        else if (type == EffectType.DODGE_CHANCE) stats.agi -= value;
        else if (type == EffectType.CRIT_CHANCE) stats.critRate -= value;
    }
}
