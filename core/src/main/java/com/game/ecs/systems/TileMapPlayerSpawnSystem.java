package com.game.ecs.systems;

import static com.game.utils.Constants.CHARACTER_BASE;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SizeComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.factory.CharacterLoader;
import com.game.screens.main.WorldMapScreen;
import com.game.utils.data.CharacterBaseData;
import com.game.utils.data.GameSession;
import com.game.utils.data.JsonLoader;

public class TileMapPlayerSpawnSystem extends EntitySystem {
    private final Engine engine;
    private final TiledMap map;
    private final int spawnIndex;
    private final OrthographicCamera camera;
    private boolean spawned = false;
    private static final float SCALE = WorldMapScreen.SCALE;

    public TileMapPlayerSpawnSystem(Engine engine, TiledMap map, int spawnIndex, OrthographicCamera camera) {
        this.engine = engine;
        this.map = map;
        this.spawnIndex = spawnIndex;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        if (spawned) return;
        MapLayer playerLayer = map.getLayers().get("player");
        if (playerLayer == null) return;
        for (MapObject obj : playerLayer.getObjects()) {
            if ("spawn".equals(obj.getName())) {
                int index = obj.getProperties().get("index", 0, Integer.class);
                if (index == spawnIndex) {
                    float x = obj.getProperties().get("x", 0f, Float.class) * SCALE;
                    float y = obj.getProperties().get("y", 0f, Float.class) * SCALE;

                    // Đặt camera đúng tâm spawn
                    camera.position.set(x, y, 0);
                    camera.update();

                    // Tạo entity player
                    String characterId = GameSession.selectedCharacterId;
//                    CharacterBaseData charData = CharacterLoader.getCharacterBaseData(characterId);
                    CharacterBaseData charData = JsonLoader.getValue(CHARACTER_BASE, "characterId", characterId, CharacterBaseData.class);

                    CharacterBaseDataComponent data = CharacterBaseDataComponent.from(charData);

                    Entity player = engine.createEntity();
                    player.add(new PlayerComponent());
                    player.add(new PositionComponent(x, y));
                    player.add(data);
                    player.add(new SpriteComponent(characterId, "idle"));
                    player.add(new SizeComponent(16*SCALE, 16*SCALE));
                    player.add(new AnimationStateComponent());
                    player.add(new BoundComponent(new Rectangle(30, 10, 16*SCALE, 16*SCALE)));

                    engine.addEntity(player);
                    spawned = true;
                    break;
                }
            }
        }
    }
}
