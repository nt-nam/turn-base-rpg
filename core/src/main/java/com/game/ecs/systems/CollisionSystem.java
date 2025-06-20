package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.utils.CollisionUtils;

public class CollisionSystem extends EntitySystem {
    private final TiledMap map;
    private ImmutableArray<Entity> players;
    private final float SCALE;

    public CollisionSystem(Engine engine, TiledMap map, float scale) {
        this.map = map;
        this.SCALE = scale;
    }

    @Override
    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());
    }

    public static Array<BoundComponent> extractMapCollisions(MapLayer layer, float scale) {
        Array<BoundComponent> list = new Array<>();
        if (layer == null) return list;
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                list.add(new BoundComponent(new Rectangle(r.x * scale, r.y * scale, r.width * scale, r.height * scale)));
            }
            if (obj instanceof PolygonMapObject) {
                Polygon poly = ((PolygonMapObject) obj).getPolygon();
                float[] verts = poly.getTransformedVertices();
                float[] scaled = new float[verts.length];
                for (int i = 0; i < verts.length; i++) {
                    scaled[i] = verts[i] * scale;
                }
                list.add(new BoundComponent(new Polygon(scaled)));
            }
        }
        return list;
    }

    @Override
    public void update(float deltaTime) {
        MapLayer wallLayer = map.getLayers().get("wall");
        Array<BoundComponent> mapColliders = extractMapCollisions(wallLayer, SCALE);
        MapLayer lineLayer = map.getLayers().get("line");
        mapColliders.addAll(extractMapCollisions(lineLayer, SCALE));

        for (Entity player : players) {
            PositionComponent pos = player.getComponent(PositionComponent.class);
            BoundComponent col = player.getComponent(BoundComponent.class);

            // Update bounding box vị trí hiện tại
            if (col.type == BoundComponent.BoundType.RECT) {
                col.rect.setPosition(pos.x, pos.y);
            } else if (col.type == BoundComponent.BoundType.POLYGON) {
                col.polygon.setPosition(pos.x, pos.y);
            }

            // Kiểm tra va chạm với map
            boolean collided = false;
            for (BoundComponent mapCol : mapColliders) {
                if (CollisionUtils.check(col, mapCol)) {
                    collided = true;
                    break;
                }
            }

            if (collided) {
                pos.x = pos.prevX;
                pos.y = pos.prevY;
            } else {
                pos.prevX = pos.x;
                pos.prevY = pos.y;
            }
        }

    }

}
