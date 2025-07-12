package com.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;

public class ClickLoggerInputProcessor extends InputAdapter {
    private final OrthographicCamera camera;

    public ClickLoggerInputProcessor(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Vị trí click trên màn hình (pixel)
        Gdx.app.log("CLICK", "Screen: (" + screenX + ", " + screenY + ")");

        // Chuyển sang tọa độ world/map
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        Gdx.app.log("CLICK", "Map: (" + worldCoords.x + ", " + worldCoords.y + ")");

        // Nếu cần lấy tileX, tileY:
        int tileX = (int)(worldCoords.x / 32);  // Ví dụ TILE_WIDTH = 32
        int tileY = (int)(worldCoords.y / 32); // TILE_HEIGHT = 32
        Gdx.app.log("CLICK", "Tile: (" + tileX + ", " + tileY + ")");

        return false; // hoặc true nếu muốn chặn sự kiện tiếp theo
    }
}
