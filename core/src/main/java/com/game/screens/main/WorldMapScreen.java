package com.game.screens.main;

import static com.game.utils.Constants.CHARACTER;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.game.MainGame;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.TileMapComponent;
import com.game.ecs.systems.SpriteRenderSystem;
import com.game.ecs.systems.TileMapPlayerSpawnSystem;
import com.game.ecs.systems.TileMapRenderSystem;
import com.game.screens.BaseScreen;
import com.game.utils.data.AnimationCache;
import com.game.utils.data.GameSession;

public class WorldMapScreen extends BaseScreen {
    public static TiledMap map;
    private int playerSpawnIndex = 2;
    public WorldMapScreen() {
        super();
        createScreen();
    }
    @Override
    protected void createScreen() {
        // 1. Khởi tạo camera
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        // 2. Load tiled map
        map = MainGame.getAsM().getTiledMap("map_village_0");
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, 4.5f);

        String characterId = GameSession.selectedKnightId;
        TextureAtlas atlas = MainGame.getAsM().get(CHARACTER + characterId + ".atlas", TextureAtlas.class);
        Animation<TextureRegion> idleAnim = new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP);
        AnimationCache.put(characterId, "idle", idleAnim);

        // 3. Tạo entity và add TileMapComponent
        Entity mapEntity = engine.createEntity();
        mapEntity.add(new TileMapComponent(map, renderer));
        engine.addEntity(mapEntity);

        Entity player = engine.createEntity();
        player.add(new PositionComponent(200, 150));
        player.add(new SpriteComponent("01Knight", "idle"));
        engine.addEntity(player);

        // 4. Add TileMapRenderSystem vào engine
        engine.addSystem(new TileMapRenderSystem(camera));
        engine.addSystem(new SpriteRenderSystem(engine, camera));
        engine.addSystem(new TileMapPlayerSpawnSystem(engine, map, playerSpawnIndex,camera));
    }

    public static void loadingAsset() {
        MainGame.getAsM().loadTiledMap("map_village_0");
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
