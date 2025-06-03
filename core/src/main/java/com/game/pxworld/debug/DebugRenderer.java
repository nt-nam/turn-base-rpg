package com.game.pxworld.debug;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Polygon;

public class DebugRenderer {

    private ShapeRenderer shapeRenderer;

    public DebugRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void renderDebugShapes(TiledMap tiledMap) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED); // Màu vẽ cho debug (có thể thay đổi)

        // Vẽ các tường (wall)
        for (MapObject object : tiledMap.getLayers().get("wall").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }

        // Vẽ các đường (line) từ TMX
        for (MapObject object : tiledMap.getLayers().get("line").getObjects()) {
            if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                float[] vertices = polygon.getVertices();

                // Vẽ các đoạn đường nối từ các điểm trong polygon
                for (int i = 0; i < vertices.length; i += 2) {
                    float x = vertices[i];
                    float y = vertices[i + 1];

                    // Vẽ đoạn nối giữa các điểm
                    if (i == 0) {
                        // Vẽ từ điểm đầu tiên đến điểm đầu tiên (nếu là điểm đầu tiên)
                        shapeRenderer.line(x, y, x, y); // Vẽ điểm đầu tiên
                    } else {
                        // Vẽ đoạn nối các điểm liên tiếp
                        float prevX = vertices[i - 2];
                        float prevY = vertices[i - 1];
                        shapeRenderer.line(prevX, prevY, x, y); // Vẽ đường nối từ điểm trước đó đến điểm hiện tại
                    }
                }

                // Vẽ đường nối lại điểm cuối với điểm đầu (để tạo thành một hình kín)
                float lastX = vertices[vertices.length - 2];
                float lastY = vertices[vertices.length - 1];
                float firstX = vertices[0];
                float firstY = vertices[1];
                shapeRenderer.line(lastX, lastY, firstX, firstY); // Vẽ đoạn nối cuối cùng
            }
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose(); // Giải phóng ShapeRenderer khi không cần thiết
    }
}
