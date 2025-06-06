package com.game.pxworld.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.pxworld.Main;

//Game Asset Manager
public class GAssetManager {
    private final AssetManager am;

    public GAssetManager() {
        am = new AssetManager();
    }

    public <T> void load(String path, Class<T> type ) {
        am.load(path, type);
    }

    public void finishLoading() {
        am.finishLoading();
    }

    public <T> T get(String path, Class<T> type) {
        return am.get(path, type);
    }

    public TextureRegion getRegion(String atlasPath, String regionName) {
        TextureAtlas atlas = get(atlasPath, TextureAtlas.class);
        return atlas.findRegion(regionName);
    }

    public BitmapFont getFont(String fontPath) {
        return get(fontPath, BitmapFont.class);
    }

    public boolean update() {
        return am.update(); // true khi hoàn tất
    }

    public float getProgress() {
        return am.getProgress(); // từ 0.0f → 1.0f
    }
    public void dispose() {
        am.dispose();
    }
}
