package com.game.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.game.MainGame;

public class CharacterTilemapManager {

    private TiledMap characterMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private GAssetManager assetManager;

    private String currentLayerName;

    public CharacterTilemapManager() {
        this.assetManager = MainGame.getAsM();
    }

    public void loadCharacterTilemap(String name) {
        assetManager.loadTiledMap(name);
        assetManager.finishLoading();

        characterMap = assetManager.getCharacterTiledMap(name);
        mapRenderer = new OrthogonalTiledMapRenderer(characterMap, 1f);

        currentLayerName = null;
    }

    public void setActiveLayer(String layerName) {
        this.currentLayerName = layerName;
    }

    public void render(OrthographicCamera camera) {
        if (currentLayerName == null) return;

        int targetLayerIndex = getLayerIndexByName(currentLayerName);
        if (targetLayerIndex < 0) return;

        mapRenderer.setView(camera);
        mapRenderer.render(new int[] { targetLayerIndex });
    }

    private int getLayerIndexByName(String layerName) {
        int count = characterMap.getLayers().getCount();
        for (int i = 0; i < count; i++) {
            if (characterMap.getLayers().get(i).getName().equals(layerName)) {
                return i;
            }
        }
        return -1; // không tìm thấy layer
    }

    public void dispose() {
        mapRenderer.dispose();
        // Không cần dispose characterMap vì dùng AssetManager quản lý
    }
}
