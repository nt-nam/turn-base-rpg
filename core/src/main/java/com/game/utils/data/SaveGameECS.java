package com.game.utils.data;

import com.badlogic.gdx.utils.Array;

/**
 * Root object chứa toàn bộ trạng thái động của game ở thời điểm lưu (save).
 * Lưu danh sách entity (player, npc, enemy, item...), map hiện tại, meta, version...
 */
public class SaveGameECS {
    // Phiên bản save, dùng để migrate/convert khi cập nhật game (optional, nên có)
    public String version = "1.0";

    // Thời gian lưu, có thể dùng System.currentTimeMillis() hoặc TimeUtils.millis()
    public long saveTime = 0;

    // Map hiện tại hoặc màn chơi hiện tại
    public String currentMapId;

    // Toàn bộ entity động đang tồn tại trong game
    public Array<EntitySaveData> entities = new Array<>();

    // Nếu cần, có thể lưu thêm một số trạng thái phụ (setting, slot, ...)

    // Constructor trống cho (de)serialization
    public SaveGameECS() {}

    // Constructor nhanh nếu muốn
    public SaveGameECS(String currentMapId, Array<EntitySaveData> entities) {
        this.currentMapId = currentMapId;
        this.entities = entities;
        this.saveTime = System.currentTimeMillis();
    }
}
