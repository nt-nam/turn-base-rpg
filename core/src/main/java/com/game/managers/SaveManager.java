package com.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * SaveManager lưu game bằng file JSON (object bất kỳ)
 */
public class SaveManager {
    private final String savePath;
    private final Json json;

    public SaveManager() {
        this("save1.json"); // file mặc định
    }

    public SaveManager(String fileName) {
        this.savePath = fileName;
        this.json = new Json();
        // Có thể set json.setOutputType(JsonWriter.OutputType.json); nếu muốn đẹp
    }

    // Lưu bất kỳ object nào (SaveGameData, PlayerData, v.v.)
    public <T> void save(T data) {
        String jsonStr = json.toJson(data);
        FileHandle file = Gdx.files.local(savePath);
        file.writeString(jsonStr, false);
    }

    // Đọc dữ liệu, trả về object dạng class cần
    public <T> T load(Class<T> clazz) {
        FileHandle file = Gdx.files.local(savePath);
        if (!file.exists()) return null;
        String jsonStr = file.readString();
        return json.fromJson(clazz, jsonStr);
    }

    // Xóa file save
    public void clearSave() {
        FileHandle file = Gdx.files.local(savePath);
        if (file.exists()) file.delete();
    }

    // Đổi tên/backup save (nâng cao, optional)
    public void backup(String backupPath) {
        FileHandle file = Gdx.files.local(savePath);
        if (file.exists()) {
            file.copyTo(Gdx.files.local(backupPath));
        }
    }
}
