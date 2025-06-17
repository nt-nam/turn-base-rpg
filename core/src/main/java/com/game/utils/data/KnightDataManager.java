package com.game.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Array;

public class KnightDataManager {
    public Array<KnightBaseData> knightList;

    public KnightDataManager() {
        Json json = new Json();
        knightList = json.fromJson(Array.class, KnightBaseData.class, Gdx.files.internal("data/knight_base.json"));
    }

    public KnightBaseData getKnightById(String knightId) {
        for (KnightBaseData k : knightList) {
            if (k.knightId.equals(knightId)) return k;
        }
        return null;
    }
}
