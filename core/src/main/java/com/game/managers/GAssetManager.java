package com.game.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class GAssetManager {
    private final AssetManager am;

    public GAssetManager() {
        am = new AssetManager();
    }

    // ==== API chung ====
    public <T> void load(String path, Class<T> type) {
        am.load(path, type);
    }

    public <T> T get(String path, Class<T> type) {
        return am.get(path, type);
    }

    public boolean update() {
        return am.update();
    }

    public float getProgress() {
        return am.getProgress();
    }

    public void finishLoading() {
        am.finishLoading();
    }

    public void dispose() {
        am.dispose();
    }

    // ==== API riêng từng loại ====

    public void loadSkin(String path) {
        load(path, Skin.class);
    }

    public Skin getSkin() {
        return get("ui/uiskin.json", Skin.class);
    }

    public void loadAtlas(String path) {
        load(path, TextureAtlas.class);
    }

    public TextureAtlas getAtlas(String path) {
        TextureAtlas atlas = get(path, TextureAtlas.class);
        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return atlas;
    }

    public TextureRegion getRegion(String atlasPath, String regionName) {
        TextureAtlas atlas = getAtlas(atlasPath);
        return atlas.findRegion(regionName);
    }

    public void loadTexture(String path) {
        load(path, Texture.class);
    }

    public Texture getTexture(String path) {
        Texture texture = get(path, Texture.class);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public void loadFont(String path) {
        load(path, BitmapFont.class);
    }

    public BitmapFont getFont(String path) {
        return get(path, BitmapFont.class);
    }

    public void loadMusic(String path) {
        load(path, Music.class);
    }

    public Music getMusic(String path) {
        return get(path, Music.class);
    }

    public void loadSound(String path) {
        load(path, Sound.class);
    }

    public Sound getSound(String path) {
        return get(path, Sound.class);
    }

    public void loadTiledMap(String path) {
        load(path, TiledMap.class);
    }

    public TiledMap getTiledMap(String path) {
        return get(path, TiledMap.class);
    }

    public JsonValue loadJson(String path) {
        JsonReader reader = new JsonReader();
        return reader.parse(new InternalFileHandleResolver().resolve(path));
    }

    // ==== API theo nhóm ====

    public void loadAllUIAssets() {
        loadAtlas("ui/uiskin.atlas");
        loadFont("ui/default.fnt");
        load("ui/uiskin.json", Skin.class);
    }

    public void loadMapAssets() {
        loadTiledMap("maps/world.tmx");
        loadAtlas("maps/tiles.atlas");
    }

    public void loadDemoAudioAssets() {
        loadMusic("music/music_demo.mp3");
        loadSound("sound/sound_demo.mp3");
    }


    public String resolve(String image) {
        return "textures/" + image;
    }
}
