package com.game.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Tien ich truy cap du lieu chung: tim phan tu theo field, doc JsonValue tho.
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class JsonAccess {

    private JsonAccess() {
    }

    public static <T> T get(List<T> list, String key, Object value) {
        for (T item : list) {
            try {
                Field field = item.getClass().getDeclaredField(key);
                field.setAccessible(true);
                Object fieldValue = field.get(item);

                if (fieldValue != null && fieldValue.equals(value)) {
                    return item;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Gdx.app.error("JsonAccess", "get() loi field '" + key + "': " + e.getMessage(), e);
            }
        }
        return null;
    }

    public static JsonValue getJsonValue(String filePath) {
        JsonReader reader = new JsonReader();

        FileHandle fileHandle = Gdx.files.internal(filePath);

        if (!fileHandle.exists()) {
            Gdx.app.error("JsonAccess", "File not found: " + filePath);
            return null;
        }

        return reader.parse(fileHandle);
    }
}
