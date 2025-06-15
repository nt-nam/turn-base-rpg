package com.game.managers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.game.MainGame;
import com.game.managers.GAssetManager;

import java.util.HashMap;
import java.util.Map;

public class MapManager {

    private GAssetManager assetManager;

    private String currentMapName;
    private TiledMap currentMap;

    private Map<String, TiledMap> cachedMaps;

    public MapManager() {
        this.assetManager = MainGame.getAsM();
        cachedMaps = new HashMap<>();
    }

    public void loadMap(String mapName) {
        // Nếu map đang là current map
        if (mapName.equals(currentMapName)) return;

        // Unload current map nếu cần
        if (currentMap != null && currentMapName != null) {
            unloadCurrentMap();
        }

        // Kiểm tra cache
        if (cachedMaps.containsKey(mapName)) {
            currentMap = cachedMaps.get(mapName);
            currentMapName = mapName;
            return;
        }

        // Load từ GAssetManager
        String path = "tilemap/map/map_" + mapName + ".tmx";

        if (!assetManager.isLoaded(path)) {
            assetManager.loadTiledMap(path);
            assetManager.finishLoading();
        }

        currentMap = assetManager.getTiledMap(path);
        currentMapName = mapName;
        cachedMaps.put(mapName, currentMap);
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public String getCurrentMapName() {
        return currentMapName;
    }

    public boolean isMapLoaded(String mapName) {
        return cachedMaps.containsKey(mapName);
    }

    public void preloadMap(String mapName) {
        // Preload vào cache mà không cần set currentMap
        if (cachedMaps.containsKey(mapName)) return;

        String path = "tilemap/map/map_" + mapName + ".tmx";
        if (!assetManager.isLoaded(path)) {
            assetManager.loadTiledMap(path);
            assetManager.finishLoading();
        }

        TiledMap map = assetManager.getTiledMap(path);
        cachedMaps.put(mapName, map);
    }

    public void unloadCurrentMap() {
        if (currentMap != null && currentMapName != null) {
            // Không unload từ AssetManager để cache xài lại
            currentMap = null;
            currentMapName = null;
        }
    }

    public void clearCache() {
        cachedMaps.clear();
        currentMap = null;
        currentMapName = null;
    }

    // Optional: get meta info
    public MapMetaInfo getCurrentMapMeta() {
        if (currentMap == null) return null;

        int width = currentMap.getProperties().get("width", Integer.class);
        int height = currentMap.getProperties().get("height", Integer.class);
        int tileWidth = currentMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = currentMap.getProperties().get("tileheight", Integer.class);

        return new MapMetaInfo(width, height, tileWidth, tileHeight);
    }

    public static class MapMetaInfo {
        public int width, height, tileWidth, tileHeight;

        public MapMetaInfo(int w, int h, int tw, int th) {
            this.width = w;
            this.height = h;
            this.tileWidth = tw;
            this.tileHeight = th;
        }
    }
}
