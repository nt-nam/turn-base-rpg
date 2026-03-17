package com.game.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Polygon;
import com.game.ecs.component.BoundComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho CollisionUtils.
 * LibGDX Rectangle và Polygon không cần Gdx.app,
 * nên test này chạy thuần không cần headless backend.
 */
class CollisionUtilsTest {

    // ===================== RECT vs RECT =====================

    @Test
    @DisplayName("RECT vs RECT: hai hình chồng lên nhau → true")
    void rectRect_overlapping_returnsTrue() {
        BoundComponent a = new BoundComponent(new Rectangle(0, 0, 10, 10));
        BoundComponent b = new BoundComponent(new Rectangle(5, 5, 10, 10));
        assertTrue(CollisionUtils.check(a, b));
    }

    @Test
    @DisplayName("RECT vs RECT: hai hình tách biệt → false")
    void rectRect_noOverlap_returnsFalse() {
        BoundComponent a = new BoundComponent(new Rectangle(0, 0, 5, 5));
        BoundComponent b = new BoundComponent(new Rectangle(10, 10, 5, 5));
        assertFalse(CollisionUtils.check(a, b));
    }

    @Test
    @DisplayName("RECT vs RECT: hai hình chạm cạnh nhau → false (overlaps không tính tiếp xúc)")
    void rectRect_touching_returnsFalse() {
        // LibGDX Rectangle.overlaps trả về false khi chỉ chạm cạnh
        BoundComponent a = new BoundComponent(new Rectangle(0, 0, 5, 5));
        BoundComponent b = new BoundComponent(new Rectangle(5, 0, 5, 5));
        assertFalse(CollisionUtils.check(a, b));
    }

    @Test
    @DisplayName("RECT vs RECT: hình B nằm hoàn toàn trong hình A → true")
    void rectRect_contained_returnsTrue() {
        BoundComponent a = new BoundComponent(new Rectangle(0, 0, 20, 20));
        BoundComponent b = new BoundComponent(new Rectangle(5, 5, 5, 5));
        assertTrue(CollisionUtils.check(a, b));
    }

    // ===================== RECT vs POLYGON =====================

    @Test
    @DisplayName("RECT vs POLYGON: polygon nằm trong rect → true")
    void rectPoly_overlapping_returnsTrue() {
        BoundComponent a = new BoundComponent(new Rectangle(0, 0, 100, 100));
        float[] vertices = {10f, 10f, 20f, 10f, 20f, 20f, 10f, 20f};
        BoundComponent b = new BoundComponent(new Polygon(vertices));
        assertTrue(CollisionUtils.check(a, b));
    }

    @Test
    @DisplayName("RECT vs POLYGON: tách biệt hoàn toàn → false")
    void rectPoly_noOverlap_returnsFalse() {
        BoundComponent a = new BoundComponent(new Rectangle(0, 0, 5, 5));
        float[] vertices = {50f, 50f, 60f, 50f, 60f, 60f, 50f, 60f};
        BoundComponent b = new BoundComponent(new Polygon(vertices));
        assertFalse(CollisionUtils.check(a, b));
    }

    // ===================== POLYGON vs RECT =====================

    @Test
    @DisplayName("POLYGON vs RECT: polygon chồng lên rect → true")
    void polyRect_overlapping_returnsTrue() {
        float[] vertices = {0f, 0f, 10f, 0f, 10f, 10f, 0f, 10f};
        BoundComponent a = new BoundComponent(new Polygon(vertices));
        BoundComponent b = new BoundComponent(new Rectangle(5, 5, 10, 10));
        assertTrue(CollisionUtils.check(a, b));
    }

    // ===================== POLYGON vs POLYGON =====================

    @Test
    @DisplayName("POLYGON vs POLYGON: hai polygon chồng nhau → true")
    void polyPoly_overlapping_returnsTrue() {
        float[] va = {0f, 0f, 10f, 0f, 10f, 10f, 0f, 10f};
        float[] vb = {5f, 5f, 15f, 5f, 15f, 15f, 5f, 15f};
        BoundComponent a = new BoundComponent(new Polygon(va));
        BoundComponent b = new BoundComponent(new Polygon(vb));
        assertTrue(CollisionUtils.check(a, b));
    }

    @Test
    @DisplayName("POLYGON vs POLYGON: hai polygon tách biệt → false")
    void polyPoly_noOverlap_returnsFalse() {
        float[] va = {0f, 0f, 5f, 0f, 5f, 5f, 0f, 5f};
        float[] vb = {50f, 50f, 60f, 50f, 60f, 60f, 50f, 60f};
        BoundComponent a = new BoundComponent(new Polygon(va));
        BoundComponent b = new BoundComponent(new Polygon(vb));
        assertFalse(CollisionUtils.check(a, b));
    }

    // ===================== rectToPolygon =====================

    @Test
    @DisplayName("rectToPolygon phải tạo polygon 4 đỉnh từ rectangle")
    void rectToPolygon_hasCorrectVertices() {
        Rectangle rect = new Rectangle(10, 20, 30, 40);
        Polygon poly = CollisionUtils.rectToPolygon(rect);
        float[] verts = poly.getVertices();
        assertEquals(8, verts.length, "Polygon hình chữ nhật phải có 8 float (4 điểm)");
    }
}
