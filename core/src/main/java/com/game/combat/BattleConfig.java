package com.game.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class BattleConfig {
    private static JsonReader jsonReader = new JsonReader();
    private static JsonValue config;

    public static void load() {
        if (config == null) {
            config = jsonReader.parse(Gdx.files.internal("data/config/battle_config.json"));
        }
    }

    public static int getMaxRounds() {
        if (config == null) load();
        return config.getInt("maxRounds", 100);
    }

    public static int getMpBasicAttackGain() {
        if (config == null) load();
        return config.getInt("mpBasicAttackGain", 10);
    }

    public static int getMpTakenDamageLoss() {
        if (config == null) load();
        return config.getInt("mpTakenDamageLoss", 5);
    }

    public static int getMpSkill2Cost() {
        if (config == null) load();
        return config.getInt("mpSkill2Cost", 20);
    }

    public static int getMpSkill3Cost() {
        if (config == null) load();
        return config.getInt("mpSkill3Cost", 35);
    }

    public static float getCounterMultiplier() {
        if (config == null) load();
        return config.getFloat("counterMultiplier", 1.25f);
    }
}
