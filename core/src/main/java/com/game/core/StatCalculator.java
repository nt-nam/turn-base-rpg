package com.game.core;

import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;

/**
 * Tập trung logic tính stat nhân vật dựa trên level và star.
 * Thay thế cho code trùng lặp trong BattleScreen.
 */
public class StatCalculator {

    private static final float LEVEL_GROWTH = 1.1f;
    private static final float STAR_GROWTH  = 1.5f;

    /**
     * Tính StatComponent từ CharacterComponent, nhân theo level và star.
     * @param data CharacterComponent chứa base stat
     * @param level Cấp độ nhân vật
     * @param star  Số sao nhân vật
     * @return StatComponent với stat đã tính
     */
    public static StatComponent calculate(CharacterComponent data, int level, int star) {
        return new StatComponent(
            scale(data.hp,   level, star, 100),
            scale(data.mp,   level, star, 50),
            scale(data.atk,  level, star, 20),
            scale(data.def,  level, star, 10),
            scale(data.agi,  level, star, 30),
            scale(data.crit, level, star, 10)
        );
    }

    /**
     * Tính stat đơn lẻ: base * (LEVEL_GROWTH ^ level) * (STAR_GROWTH ^ star).
     * Nếu base <= 0, trả về giá trị mặc định.
     */
    private static int scale(int base, int level, int star, int defaultVal) {
        if (base <= 0) return defaultVal;
        return (int) (base * Math.pow(LEVEL_GROWTH, level) * Math.pow(STAR_GROWTH, star));
    }
}
