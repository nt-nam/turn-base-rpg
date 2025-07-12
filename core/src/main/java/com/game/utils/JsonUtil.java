package com.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class JsonUtil {
    public static boolean saveObjectMap(ObjectMap<String, Object> objectMap, String filePath) {
        Json json = new Json();

        FileHandle file = new FileHandle(filePath);

        String jsonString = json.toJson(objectMap);

        try {
            file.writeString(jsonString, false); // false để ghi đè tệp
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObjectMap<String, Object> loadObjectMap(String filePath) {
        Json json = new Json();

        // Tạo FileHandle để truy cập tệp
        FileHandle file = new FileHandle(filePath);

        // Kiểm tra sự tồn tại của tệp
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return null;
        }

        try {
            // Đọc dữ liệu từ file và chuyển thành ObjectMap
            return json.fromJson(ObjectMap.class, file.readString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveArrayObjectMap(Array<ObjectMap<String, Object>> array, String filePath) {
        Json json = new Json();

        // Tạo FileHandle để truy cập tệp
        FileHandle file = new FileHandle(filePath);

        // Chuyển Array<ObjectMap> thành JSON
        String jsonString = json.toJson(array);

        // Ghi dữ liệu vào tệp
        try {
            file.writeString(jsonString, false); // false để ghi đè
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Array<ObjectMap<String, Object>> loadArrayObjectMap(String filePath) {
        Json json = new Json();

        // Tạo FileHandle để truy cập tệp
        FileHandle file = new FileHandle(filePath);

        // Kiểm tra sự tồn tại của tệp
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return null;
        }

        try {
            // Đọc dữ liệu từ file và chuyển thành Array<ObjectMap>
            return json.fromJson(Array.class, file.readString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonValue loadJsonValue(String filePath) {
        JsonReader jsonReader = new JsonReader();
        return jsonReader.parse(Gdx.files.internal(filePath));
    }

    public static boolean saveJsonValue(JsonValue jsonValue, String filePath) {
        Json json = new Json();

        String jsonString = json.toJson(jsonValue);

        try {
            Gdx.files.local(filePath).writeString(jsonString, false); // false: ghi đè tệp nếu đã tồn tại
            return true;
        } catch (Exception e) {
            Gdx.app.error("JsonSaver", "Error saving JSON to file: " + filePath, e);
            return false;
        }
    }
}

/*
*create JsonValue
*/
/// JsonValue jsonValue = new JsonValue(JsonValue.ValueType.object);
//jsonValue.addChild("characterId", new JsonValue(gridData.characterId));
//jsonValue.addChild("grid", new JsonValue(gridData.grid));

///JsonValue jsonValue = new JsonValue(JsonValue.ValueType.array);
//jsonValue.add(new JsonValue("Banana"));
//jsonValue.add(new JsonValue("Cherry"));
//String firstItem = jsonValue.getString(0);  // Lấy giá trị đầu tiên (index 0)
//String secondItem = jsonValue.getString(1);

///JsonValue jsonValue = new JsonValue(JsonValue.ValueType.string);
//jsonValue.set("Hello, World!");
//System.out.println(jsonValue.asString());

///JsonValue jsonValue = new JsonValue(JsonValue.ValueType.number);
//jsonValue.set(100);  // Hoặc có thể là số thực như 10.5
//System.out.println(jsonValue.asInt());

///JsonValue jsonValue = new JsonValue(JsonValue.ValueType.boolean);
//    jsonValue.set(true);
//System.out.println(jsonValue.asBoolean());

///JsonValue jsonValue = new JsonValue(JsonValue.ValueType.nullType);
//System.out.println(jsonValue.isNull());
