package com.game.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho CalculateHelper.
 * Class này hoàn toàn thuần Java (không phụ thuộc LibGDX),
 * không cần khởi tạo Gdx.app.
 */
class CalculateHelperTest {

    // --- xp(level) ---

    @Test
    @DisplayName("xp(1) phải trả về giá trị dương")
    void xp_level1_isPositive() {
        int result = CalculateHelper.xp(1);
        assertTrue(result > 0, "EXP ở cấp 1 phải > 0");
    }

    @Test
    @DisplayName("xp(level) phải tăng dần theo level")
    void xp_increasesWithLevel() {
        for (int level = 1; level < 50; level++) {
            int xpCurrent = CalculateHelper.xp(level);
            int xpNext    = CalculateHelper.xp(level + 1);
            assertTrue(xpNext > xpCurrent,
                "xp(level+1) phải lớn hơn xp(level) tại level=" + level);
        }
    }

    @ParameterizedTest(name = "xp({0}) = {1}")
    @CsvSource({
        "1,  50",     // (int)((1 * 0.5) * 100) = 50
        "2, 200",     // (int)((4 * 0.5) * 100) = 200
        "10, 50000",  // (int)((100 * 0.5) * 100) = 5000... kiểm tra formula
    })
    @DisplayName("Giá trị xp phải đúng công thức level*(level*0.5)*100")
    void xp_specificValues(int level, int expected) {
        // Công thức thực tế: (int)((level * (level * 0.5f)) * 100)
        int actual = CalculateHelper.xp(level);
        assertEquals(expected, actual,
            "xp(" + level + ") không khớp, actual=" + actual);
    }

    // --- statLevel(statRoot, level) ---

    @Test
    @DisplayName("statLevel với level=0 phải trả về statRoot (không đổi)")
    void statLevel_level0_equalsRoot() {
        int root = 100;
        int result = CalculateHelper.statLevel(root, 0);
        assertEquals(root, result);
    }

    @Test
    @DisplayName("statLevel phải tăng theo level")
    void statLevel_increasesWithLevel() {
        int root = 100;
        for (int lv = 0; lv < 10; lv++) {
            assertTrue(CalculateHelper.statLevel(root, lv + 1) >= CalculateHelper.statLevel(root, lv),
                "statLevel không tăng tại level=" + lv);
        }
    }

    @Test
    @DisplayName("statLevel(100, 1) phải bằng khoảng 110 (x1.1^1)")
    void statLevel_level1() {
        int result = CalculateHelper.statLevel(100, 1);
        assertEquals(110, result, "statLevel(100,1) phải là 110");
    }

    // --- statStar(statRoot, level) ---

    @Test
    @DisplayName("statStar với level=0 phải trả về statRoot")
    void statStar_level0_equalsRoot() {
        int root = 50;
        assertEquals(root, CalculateHelper.statStar(root, 0));
    }

    @Test
    @DisplayName("statStar(100, 1) phải bằng 150 (x1.5^1)")
    void statStar_level1() {
        int result = CalculateHelper.statStar(100, 1);
        assertEquals(150, result, "statStar(100,1) phải là 150");
    }

    @Test
    @DisplayName("statStar phải tăng mạnh hơn statLevel tại cùng level")
    void statStar_growsFasterThanStatLevel() {
        int root = 100;
        int level = 3;
        assertTrue(CalculateHelper.statStar(root, level) > CalculateHelper.statLevel(root, level),
            "statStar phải tăng mạnh hơn statLevel (1.5^n > 1.1^n)");
    }
}
