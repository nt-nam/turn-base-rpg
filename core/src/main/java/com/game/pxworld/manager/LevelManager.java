package com.game.pxworld.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class LevelManager {

    private TiledMap currentLevel;
    private TextureAtlas spriteAtlas;

    public LevelManager() {
        // Khởi tạo các đối tượng cần thiết nếu có
    }

    // Load level từ file TMX
    public TiledMap loadLevel(String levelFile) {
        if (currentLevel != null) {
            currentLevel.dispose();  // Giải phóng level cũ trước khi tải level mới
        }
        currentLevel = new TmxMapLoader().load(levelFile);  // Tải file TMX
        return currentLevel;
    }

    // Load sprite từ TextureAtlas
    public TextureAtlas loadSprite(String atlasFile) {
        if (spriteAtlas != null) {
            spriteAtlas.dispose();  // Giải phóng sprite cũ
        }
        spriteAtlas = new TextureAtlas(Gdx.files.internal(atlasFile)); // Tải Atlas
        return spriteAtlas;
    }

    public TiledMap getCurrentLevel() {
        return currentLevel;
    }

    public TextureAtlas getSpriteAtlas() {
        return spriteAtlas;
    }
}
