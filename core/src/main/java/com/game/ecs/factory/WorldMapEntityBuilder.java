package com.game.ecs.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.TeleportTriggerComponent;

/**
 * Tach logic dung entity tu du lieu TiledMap ra khoi WorldMapScreen (tang View).
 * Giu nguyen hanh vi goc: doc layer "teleport" / "enemies" va tao trigger entity.
 */
public final class WorldMapEntityBuilder {

    private WorldMapEntityBuilder() {
    }

    /** Doc layer "teleport" tu map va tao cac entity TeleportTrigger. */
    public static void setupTeleportTriggers(Engine engine, TiledMap tiledMap, float scale) {
        MapLayer teleportLayer = tiledMap.getLayers().get("teleport");
        if (teleportLayer == null) {
            return;
        }
        for (MapObject obj : teleportLayer.getObjects()) {
            Object x = obj.getProperties().get("x");
            Object y = obj.getProperties().get("y");
            Object w = obj.getProperties().get("width");
            Object h = obj.getProperties().get("height");
            if (x != null && y != null && w != null && h != null) {
                Rectangle rect = new Rectangle(
                        ((Number) x).floatValue() * scale,
                        ((Number) y).floatValue() * scale,
                        ((Number) w).floatValue() * scale,
                        ((Number) h).floatValue() * scale);
                String nextMap = obj.getProperties().containsKey("map")
                        ? (String) obj.getProperties().get("map")
                        : "";
                int nextSpawn = obj.getProperties().containsKey("spawn")
                        ? ((Number) obj.getProperties().get("spawn")).intValue()
                        : 0;
                String name = obj.getProperties().containsKey("name")
                        ? (String) obj.getProperties().get("name")
                        : "";
                Entity teleportTrigger = new Entity();
                teleportTrigger.add(new TeleportTriggerComponent(nextMap, nextSpawn, name));
                teleportTrigger.add(new BoundComponent(rect));
                engine.addEntity(teleportTrigger);
            }
        }
    }

    /** Doc layer "enemies" tu map va tao cac entity EnemyTrigger. */
    public static void setupEnemies(Engine engine, TiledMap map, float scale) {
        MapLayer enemiesLayer = map.getLayers().get("enemies");
        if (enemiesLayer == null) {
            return;
        }
        for (MapObject obj : enemiesLayer.getObjects()) {
            Object x = obj.getProperties().get("x");
            Object y = obj.getProperties().get("y");
            Object w = obj.getProperties().get("width");
            Object h = obj.getProperties().get("height");
            if (x != null && y != null && w != null && h != null) {
                Rectangle rect = new Rectangle(
                        ((Number) x).floatValue() * scale,
                        ((Number) y).floatValue() * scale,
                        ((Number) w).floatValue() * scale,
                        ((Number) h).floatValue() * scale);
                int id = obj.getProperties().containsKey("id")
                        ? ((Number) obj.getProperties().get("id")).intValue()
                        : 1;
                String name = obj.getProperties().containsKey("name")
                        ? (String) obj.getProperties().get("name")
                        : "";
                int level = obj.getProperties().containsKey("level")
                        ? ((Number) obj.getProperties().get("level")).intValue()
                        : 1;

                Entity enemyTrigger = engine.createEntity();
                enemyTrigger.add(new EnemyTriggerComponent(id, name, level));
                enemyTrigger.add(new BoundComponent(rect));
                engine.addEntity(enemyTrigger);
            }
        }
    }
}
