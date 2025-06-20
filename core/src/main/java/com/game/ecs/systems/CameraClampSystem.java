package com.game.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.TileMapComponent;

public class CameraClampSystem extends EntitySystem {
    private final OrthographicCamera camera;
    private final Engine engine;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> maps;

    private float mapPixelWidth;
    private float mapPixelHeight;

    public CameraClampSystem(Engine engine, OrthographicCamera camera) {
        this.engine = engine;
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());
        maps = engine.getEntitiesFor(Family.all(TileMapComponent.class).get());
        // Assume only 1 tilemap entity
        if (maps.size() > 0) {
            TileMapComponent mapComp = maps.get(0).getComponent(TileMapComponent.class);
            int width = mapComp.map.getProperties().get("width", Integer.class);
            int height = mapComp.map.getProperties().get("height", Integer.class);
            int tileWidth = mapComp.map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = mapComp.map.getProperties().get("tileheight", Integer.class);
            float scale = mapComp.renderer.getUnitScale();
            mapPixelWidth = width * tileWidth * scale;
            mapPixelHeight = height * tileHeight * scale;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (players.size() == 0) return;
        PositionComponent pos = players.get(0).getComponent(PositionComponent.class);
        float camHalfWidth = camera.viewportWidth / 2;
        float camHalfHeight = camera.viewportHeight / 2;

        // Clamp camera to player but don't let it go out of map
        float targetX = Math.max(camHalfWidth, Math.min(pos.x, mapPixelWidth - camHalfWidth));
        float targetY = Math.max(camHalfHeight, Math.min(pos.y, mapPixelHeight - camHalfHeight));
        camera.position.set(targetX, targetY, 0);
        camera.update();
    }
}
