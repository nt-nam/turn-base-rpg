package com.game.utils.data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class AnimationCache {
    private static final Map<String, Animation<TextureRegion>> animMap = new HashMap<>();
    // key: characterId + "_" + animationName

    // Chỉ gọi lúc load game hoặc load asset
    public static void put(String characterId, String animName, Animation<TextureRegion> anim) {
        animMap.put(characterId + "_" + animName, anim);
    }

    public static Animation<TextureRegion> get(String characterId, String animName) {
        return animMap.get(characterId + "_" + animName);
    }
}
