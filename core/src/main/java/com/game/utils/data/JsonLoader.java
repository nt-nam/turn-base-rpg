package com.game.utils.data;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

import com.game.ecs.component.CharacterBaseDataComponent;

public class JsonLoader {
    private static final Json json = new Json();
    private static final JsonReader jsonReader = new JsonReader();
    private static final ObjectMap<String, Array<?>> dataMap = new ObjectMap<>();

    private static synchronized <T> Array<T> loadArrayFromFile(String filePath, Class<T> type, boolean forceReload) {
        if (forceReload || !dataMap.containsKey(filePath)) {
            Array<T> result = json.fromJson(Array.class, type, Gdx.files.internal(filePath));
            dataMap.put(filePath, result);
            return result;
        }
        return (Array<T>) dataMap.get(filePath);
    }

    public static <T> Array<T> loadArray(String filePath, Class<T> type, boolean forceReload) {
        return loadArrayFromFile(filePath, type, forceReload);
    }

    public static <T> Array<T> loadArrayFromJsonValue(JsonValue jsonValue, Class<T> type) {
        Array<T> result = new Array<>();

        for (JsonValue value = jsonValue.child(); value != null; value = value.next()) {
            T obj = json.readValue(type, value);
            result.add(obj);
        }
        return result;
    }

    public static <T> Array<T> getArray(String filePath) {
        if (dataMap.containsKey(filePath)) {
            return (Array<T>) dataMap.get(filePath);
        } else {
            throw new IllegalArgumentException("No data found for filePath: " + filePath);
        }
    }

    public static <T> T getValue(String filePath, String key, String name, Class<T> type) {
        Array<T> dataArray = JsonLoader.loadArray(filePath, type, false);

        for (T a : dataArray) {
            try {
                // Truy cập vào trường "key" của đối tượng
                Object fieldValue = a.getClass().getDeclaredField(key).get(a);

                // Kiểm tra nếu trường đó bằng với "name"
                if (fieldValue != null && fieldValue.equals(name)) {
                    return a;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Log lỗi nếu không thể truy cập trường hoặc không tồn tại
                Gdx.app.error("JsonLoader", "Field '" + key + "' not found or inaccessible in class " + type.getName(), e);
            }
        }

        // Nếu không tìm thấy đối tượng, trả về null
        return null;
    }


    public static void clearData(String filePath) {
        dataMap.remove(filePath);
    }

    public static void clearAllData() {
        dataMap.clear();
    }

    // Hàm load tất cả các character vào ECS engine từ JSON
    public static void loadAllCharacters(Engine engine, boolean forceReload) {
        Array<CharacterBaseData> characterList = loadArray("data/base/character_base.json", CharacterBaseData.class, forceReload);
        for (CharacterBaseData c : characterList) {
            Entity e = engine.createEntity();
            e.add(CharacterBaseDataComponent.from(c));
            engine.addEntity(e);
        }
    }

    // Lấy data theo id
    public static CharacterBaseData getCharacterBaseData(String characterId, boolean forceReload) {
        Array<CharacterBaseData> characterList = loadArray("data/base/character_base.json", CharacterBaseData.class, forceReload);
        for (CharacterBaseData c : characterList) {
            if (c.characterId.equals(characterId)) {
                return c;
            }
        }
        return null;
    }

    // Lấy danh sách các character theo type
    public static Array<CharacterBaseData> getCharactersByType(String type, boolean forceReload) {
        Array<CharacterBaseData> result = new Array<>();
        Array<CharacterBaseData> characterList = loadArray("data/base/character_base.json", CharacterBaseData.class, forceReload);
        for (CharacterBaseData c : characterList) {
            if (type.equals(c.classType)) {
                result.add(c);
            }
        }
        return result;
    }
}
