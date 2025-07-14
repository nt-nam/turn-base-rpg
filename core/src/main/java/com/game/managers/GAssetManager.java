package com.game.managers;

import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.utils.Constants;

public class GAssetManager {
    private final AssetManager am;
    private
    String currentMapName = null;

    public GAssetManager() {
        am = new AssetManager();
        am.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
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

    public boolean isLoaded(String path) {
        return am.isLoaded(path);
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
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        return atlas;
    }

    public TextureRegion getRegion(String atlasPath, String regionName) {
        TextureAtlas atlas = getAtlas(atlasPath);
        if (atlas == null) {
            Gdx.app.error("AssetManager", "Atlas not found: " + atlasPath);
        }
        if (atlas.findRegion(regionName)==null) {
            Gdx.app.error("AssetManager", "Region not found: " + regionName+"---"+atlasPath);
        }
        return atlas.findRegion(regionName);
    }

    public TextureRegion getRegionCharacter(String atlasPath, String regionName) {
        TextureAtlas atlas = getAtlas(Constants.CHARACTER_ATLAS+atlasPath+".atlas");
        if (atlas == null) {
            Gdx.app.error("AssetManager", "Atlas not found: " + atlasPath);
        }
        if (atlas.findRegion(regionName)==null) {
            Gdx.app.error("AssetManager", "Region not found: " + regionName+"---"+atlasPath);
        }
        return atlas.findRegion(regionName);
    }

    public NinePatch getRegion9patch(String atlasPath, String regionName, int padding) {
        TextureAtlas atlas = getAtlas(atlasPath);
        return new NinePatch(atlas.findRegion(regionName),padding,padding,padding,padding);
    }

    public NinePatch get9p() {
        return getRegion9patch(UI_POPUP, "origin", 20);
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

    public void loadTiledMap(String mapName) {
        if (currentMapName != null) {
            am.unload("tilemap/map/" + currentMapName + ".tmx");
        }
        currentMapName = mapName;
        am.load("tilemap/map/" + mapName + ".tmx", TiledMap.class);
    }

    public void loadCharacterTiledMap(String tileName) {
        am.load("tilemap/character/" + tileName + ".tmx", TiledMap.class);
    }

    public TiledMap getTiledMap(String mapName) {
        return get("tilemap/map/" + mapName + ".tmx", TiledMap.class);
    }

    public TiledMap getCharacterTiledMap(String tileName) {
        return get("tilemap/map/character" + tileName + ".tmx", TiledMap.class);
    }

    public void loadJson(String path) {
        am.load(path, String.class);
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

    public void unload(String s) {
        am.unload(s);
    }

    public JsonValue getJsonValue(String s) {
        FileHandle fileHandle = get("data/base/character_base.json", FileHandle.class);
        JsonReader reader = new JsonReader();
        return reader.parse(fileHandle.readString());
    }
}
