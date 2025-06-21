package com.game.utils.data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class AnimationCache {
    private static final Map<String, Animation<TextureRegion>> animMap = new HashMap<>();

    public static void put(String characterId, String animName, Animation<TextureRegion> anim) {
        animMap.put(characterId + "_" + animName, anim);
    }

    public static void put(String animName, Animation<TextureRegion> anim) {
        animMap.put(animName, anim);
    }

    public static Animation<TextureRegion> get(String characterId, String animName) {
        return animMap.get(characterId + "_" + animName);
    }

    public static void clear() {
        animMap.clear();
    }

    public static void remove(String characterId, String animName) {
        animMap.remove(characterId + "_" + animName);
    }
}
