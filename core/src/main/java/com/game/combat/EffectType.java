package com.game.combat;

public enum EffectType {
    DAMAGE("damage"),
    ARMOR("armor"),
    DODGE_CHANCE("dodgeChance"),
    CRIT_CHANCE("critChance"),
    MANA_REGEN("manaRegen"),
    HEAL("heal"),
    DAMAGE_REFLECTION("damageReflection"),
    ATTACK_TIMES("attackTimes"),
    TARGETS("targets"),
    UNKNOWN("");

    private final String key;

    EffectType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static EffectType fromString(String key) {
        for (EffectType type : values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
