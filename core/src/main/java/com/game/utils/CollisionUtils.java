package com.game.utils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.game.ecs.component.BoundComponent;

public class CollisionUtils {
    public static boolean check(BoundComponent a, BoundComponent b) {
        // RECT vs RECT
        if (a.type == BoundComponent.BoundType.RECT && b.type == BoundComponent.BoundType.RECT) {
            return a.rect.overlaps(b.rect);
        }
        // RECT vs POLYGON
        if (a.type == BoundComponent.BoundType.RECT && b.type == BoundComponent.BoundType.POLYGON) {
            return Intersector.overlapConvexPolygons(rectToPolygon(a.rect), b.polygon);
        }
        if (a.type == BoundComponent.BoundType.POLYGON && b.type == BoundComponent.BoundType.RECT) {
            return Intersector.overlapConvexPolygons(a.polygon, rectToPolygon(b.rect));
        }
        // POLYGON vs POLYGON
        if (a.type == BoundComponent.BoundType.POLYGON && b.type == BoundComponent.BoundType.POLYGON) {
            return Intersector.overlapConvexPolygons(a.polygon, b.polygon);
        }
        // POLYLINE các kiểu: đơn giản dùng Intersector.isPointInPolygon với từng điểm, hoặc kiểm tra đoạn thẳng cắt đoạn thẳng (nâng cao)
        // ... (bổ sung nếu game bạn cần)

        // Nếu chưa hỗ trợ trường hợp nào đó, trả về false
        return false;
    }

    public static Polygon rectToPolygon(Rectangle rect) {
        float[] vertices = new float[] {
            rect.x, rect.y,
            rect.x + rect.width, rect.y,
            rect.x + rect.width, rect.y + rect.height,
            rect.x, rect.y + rect.height
        };
        return new Polygon(vertices);
    }
}
