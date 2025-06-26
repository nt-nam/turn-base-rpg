package com.game.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class JsonLoader {
    private static final Json json = new Json();
    private static final ObjectMap<String, Array<?>> dataMap = new ObjectMap<>();

    // Tải JSON và lưu trữ JsonValue
    private static synchronized JsonValue loadJsonValue(String filePath, boolean forceReload) {
        String cacheKey = "json:" + filePath;
        if (!forceReload && dataMap.containsKey(cacheKey)) {
            return ((Array<JsonValue>) dataMap.get(cacheKey)).first();
        }
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonValue = jsonReader.parse(Gdx.files.internal(filePath));
        if (jsonValue == null) {
            Gdx.app.error("JsonLoader", "Failed to load JSON from " + filePath);
            return null;
        }
        dataMap.put(cacheKey, new Array<>(new JsonValue[]{jsonValue}));
        return jsonValue;
    }

    private static synchronized <T> Array<T> loadArrayFromFile(String filePath, Class<T> type, boolean forceReload) {
        String cacheKey = "array:" + filePath + ":" + type.getName();
        if (!forceReload && dataMap.containsKey(cacheKey)) {
            return (Array<T>) dataMap.get(cacheKey);
        }
        Array<T> result = json.fromJson(Array.class, type, Gdx.files.internal(filePath));
        dataMap.put(cacheKey, result);
        return result;
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
        String cacheKey = "array:" + filePath;
        if (dataMap.containsKey(cacheKey)) {
            return (Array<T>) dataMap.get(cacheKey);
        } else {
            throw new IllegalArgumentException("No data found for filePath: " + filePath);
        }
    }

    public static <T> T getValue(String filePath, String key, String name, Class<T> type) {
        Array<T> dataArray = loadArray(filePath, type, false);
        for (T a : dataArray) {
            try {
                Object fieldValue = a.getClass().getDeclaredField(key).get(a);
                if (fieldValue != null && fieldValue.equals(name)) {
                    return a;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Gdx.app.error("JsonLoader", "Field '" + key + "' not found or inaccessible in class " + type.getName(), e);
            }
        }
        return null;
    }

    public static <T> T getObject(String filePath, String key, Class<T> type, boolean forceReload) {
        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
        if (jsonValue == null) {
            return null;
        }

        JsonValue targetValue = jsonValue.get(key);
        if (targetValue == null) {
            Gdx.app.error("JsonLoader", "Key '" + key + "' not found in " + filePath);
            return null;
        }

        try {
            return json.readValue(type, targetValue);
        } catch (Exception e) {
            Gdx.app.error("JsonLoader", "Failed to deserialize key '" + key + "' to type " + type.getName(), e);
            return null;
        }
    }

    public static <T> T getNestedValue(String filePath, String path, Class<T> type, boolean forceReload) {
        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
        if (jsonValue == null) {
            return null;
        }

        String[] keys = path.split("\\.");
        JsonValue current = jsonValue;
        for (int i = 0; i < keys.length; i++) {
            if (current == null) {
                Gdx.app.error("JsonLoader", "Invalid path: " + path + " at key: " + keys[i]);
                return null;
            }

            if (keys[i].startsWith("id=") && current.isArray()) {
                String idValue = keys[i].substring(3);
                for (JsonValue child : current) {
                    String childId = child.getString("id", null);
                    if (childId != null && childId.equals(idValue)) {
                        current = child;
                        break;
                    }
                }
                if (current == jsonValue) {
                    Gdx.app.error("JsonLoader", "No array element with id '" + idValue + "' found in " + filePath);
                    return null;
                }
            } else if (current.isArray() && keys[i].matches("\\d+")) {
                try {
                    int index = Integer.parseInt(keys[i]);
                    current = current.get(index);
                } catch (NumberFormatException e) {
                    Gdx.app.error("JsonLoader", "Invalid array index in path: " + keys[i]);
                    return null;
                }
            } else {
                current = current.get(keys[i]);
            }
        }

        if (current == null) {
            Gdx.app.error("JsonLoader", "Path '" + path + "' not found in " + filePath);
            return null;
        }

        try {
            return json.readValue(type, current);
        } catch (Exception e) {
            Gdx.app.error("JsonLoader", "Failed to deserialize path '" + path + "' to type " + type.getName(), e);
            return null;
        }
    }

    public static JsonValue getJsonValue(String filePath, boolean forceReload) {
        return loadJsonValue(filePath, forceReload);
    }
    public static JsonValue getJsonValue(JsonValue jsonValue, String path) {
        String[] keys = path.split("\\.");
        JsonValue result = jsonValue;
        for (String k:keys) {
            result = result.get(k);
        }
        return result;
    }

    // Hàm mới: Lấy danh sách các key của JsonValue từ file JSON
    public static Array<String> getJsonKeys(String filePath, boolean forceReload) {
        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
        if (jsonValue == null) {
            return null;
        }

        if (!jsonValue.isObject()) {
            Gdx.app.error("JsonLoader", "JSON is not an object in " + filePath);
            return null;
        }

        Array<String> keys = new Array<>();
        for (JsonValue child = jsonValue.child(); child != null; child = child.next()) {
            keys.add(child.name);
        }
        return keys;
    }

    // Hàm mới: Lấy danh sách các key của JsonValue
    public static Array<String> getJsonKeys(JsonValue jsonValue) {
        if (jsonValue == null) {
            return null;
        }

        if (!jsonValue.isObject()) {
            Gdx.app.error("JsonLoader", "JSON is not an object in " + jsonValue.name);
            return null;
        }

        Array<String> keys = new Array<>();
        for (JsonValue child = jsonValue.child(); child != null; child = child.next()) {
            keys.add(child.name);
        }
        return keys;
    }

    public static Array<JsonValue> getJsonArray(String filePath, boolean forceReload) {
        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
        if (jsonValue == null) {
            return null;
        }

        Array<JsonValue> result = new Array<>();
        if (jsonValue.isArray()) {
            for (JsonValue child : jsonValue) {
                result.add(child);
            }
        } else if (jsonValue.isObject()) {
            for (JsonValue child = jsonValue.child(); child != null; child = child.next()) {
                result.add(child);
            }
        } else {
            Gdx.app.error("JsonLoader", "JSON is neither array nor object in " + filePath);
            return null;
        }
        return result;
    }

    public static void clearData(String filePath) {
        dataMap.remove(filePath);
        dataMap.remove("json:" + filePath);
        removeIfKeyStartsWith("array:" + filePath);
    }

    public static void clearAllData() {
        dataMap.clear();
    }

    // Phương pháp trợ giúp để xóa các khóa bắt đầu bằng tiền tố
    private static void removeIfKeyStartsWith(String prefix) {
        Array<String> keysToRemove = new Array<>();
        for (String key : dataMap.keys()) {
            if (key.startsWith(prefix)) {
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            dataMap.remove(key);
        }
    }
}

///    -----------------------------------------
// Lấy JsonValue (cache sau lần đầu)
///    JsonValue json = JsonLoader.getJsonValue("skills.json", false);
///    String name = json.get("warrior").get("1").get("name").asString(); // In: Basic Attack
// Lấy object "warrior"
///    public class SkillSet {
///        public Map<String, Skill> skills;
///    }
///    public class Skill {
///        public String name;
///        public String description;
///        public Map<String, Integer> effect;
///    }
///    SkillSet warrior = JsonLoader.getObject("skills.json", "warrior", SkillSet.class, false);
///    System.out.println(warrior.skills.get("1").name); // In: Basic Attack
///
// Lấy nested value
///    String skillName = JsonLoader.getNestedValue("skills.json", "warrior.1.name", String.class, false);
///    System.out.println(skillName); // In: Basic Attack
///
// Lấy array các giá trị con
///    Array<JsonValue> array = JsonLoader.getJsonArray("skills.json", false);
///    JsonValue warriorJson = array.find(v -> v.name.equals("warrior"));
///    System.out.println(warriorJson.get("1").get("name").asString()); // In: Basic Attack
