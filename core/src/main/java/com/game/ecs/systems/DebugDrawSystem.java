package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.game.ecs.component.BoundComponent;

public class DebugDrawSystem extends EntitySystem {
    private final TiledMap map;
    private final OrthographicCamera camera;
    private float SCALE = 1;
    private ShapeRenderer shapeRenderer;
    private final ComponentMapper<BoundComponent> bm = ComponentMapper.getFor(BoundComponent.class);
    ImmutableArray<Entity> entities;
    public DebugDrawSystem(TiledMap map, OrthographicCamera camera, float scale) {
        this.map = map;
        this.camera = camera;
        this.SCALE = scale;
        this.shapeRenderer = new ShapeRenderer();

    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = getEngine().getEntitiesFor(Family.all(BoundComponent.class).get());

    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entities) {
            BoundComponent bound = bm.get(entity);
            if (bound.type == BoundComponent.BoundType.RECT && bound.rect != null) {
                shapeRenderer.rect(bound.rect.x, bound.rect.y, bound.rect.width, bound.rect.height);
            } else if (bound.type == BoundComponent.BoundType.POLYGON && bound.polygon != null) {
                shapeRenderer.polygon(bound.polygon.getTransformedVertices());
            }
        }

        // Vẽ line
        MapLayer lineLayer = map.getLayers().get("line");
        if (lineLayer != null) {
            shapeRenderer.setColor(0, 0, 1, 1); // xanh lá
            for (MapObject obj : lineLayer.getObjects()) {
                if (obj instanceof PolygonMapObject) {
                    PolygonMapObject poly = (PolygonMapObject) obj;
                    float[] vertices = poly.getPolygon().getTransformedVertices();
                    float[] scaled = new float[vertices.length];
                    for (int i = 0; i < vertices.length; i++) {
                        scaled[i] = vertices[i] * SCALE;
                    }
                    shapeRenderer.polygon(scaled);
                }
            }
        }

        MapLayer wallLayer = map.getLayers().get("wall");
        if (wallLayer != null) {
            for (MapObject obj : wallLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    float x = (float)obj.getProperties().get("x")*SCALE;
                    float y = (float)obj.getProperties().get("y")*SCALE;
                    float w = (float)obj.getProperties().get("width")*SCALE;
                    float h = (float)obj.getProperties().get("height")*SCALE;
                    shapeRenderer.rect(x, y, w, h);
                }else{
                    System.out.println("Không phải RectangleMapObject: " + obj);
                }
            }
        }


        MapLayer teleportTriggerLayer = map.getLayers().get("teleport");
        if (teleportTriggerLayer != null) {
            shapeRenderer.setColor(1, 1, 1, 1);
            for (MapObject obj : teleportTriggerLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    float x = (float)obj.getProperties().get("x")*SCALE;
                    float y = (float)obj.getProperties().get("y")*SCALE;
                    float w = (float)obj.getProperties().get("width")*SCALE;
                    float h = (float)obj.getProperties().get("height")*SCALE;
                    shapeRenderer.rect(x, y, w, h);
                }else{
                    System.out.println("Không phải RectangleMapObject: " + obj);
                }
            }
        }
        MapLayer enemiesTriggerLayer = map.getLayers().get("enemies");
        if (enemiesTriggerLayer != null) {
            shapeRenderer.setColor(1, 0, 0, 1);
            for (MapObject obj : enemiesTriggerLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    float x = (float)obj.getProperties().get("x")*SCALE;
                    float y = (float)obj.getProperties().get("y")*SCALE;
                    float w = (float)obj.getProperties().get("width")*SCALE;
                    float h = (float)obj.getProperties().get("height")*SCALE;
                    shapeRenderer.rect(x, y, w, h);
                }else{
                    System.out.println("Không phải RectangleMapObject: " + obj);
                }
            }
        }
        shapeRenderer.end();

    }
}
