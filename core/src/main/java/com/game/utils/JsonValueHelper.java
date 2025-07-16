//package com.game.utils;
//
//import static com.game.utils.Constants.CHARACTER_BASE_JSON;
//import static com.game.utils.Constants.EQUIP_JSON;
//import static com.game.utils.Constants.ITEM_JSON;
//import static com.game.utils.Constants.SKILL_JSON;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.JsonReader;
//import com.badlogic.gdx.utils.JsonValue;
//import com.badlogic.gdx.utils.ObjectMap;
//import com.game.utils.json.CharacterBase;
//import com.game.utils.json.DailyReward;
//import com.game.utils.json.GridData;
//import com.game.utils.json.Mission;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class JsonValueHelper {
//    private static final Json json = new Json();
//    private static final ObjectMap<String, Array<?>> dataMap = new ObjectMap<>();
//
//    public static List<GridData> listGrid = new ArrayList<>();//mainInfo
//
//    public static List<DailyReward> daily_rewards = new ArrayList<>();//daily_rewards
//    public static List<GridData> info = new ArrayList<>();//info
//
//    public static List<Mission> missions = new ArrayList<>();//mission_base
//    public static List<CharacterBase> character_base = new ArrayList<>();//character_base
//
//    public static List<Mission> loadMissions(String filePath,boolean b) {
//        if(b || missions.isEmpty()){
//            FileHandle fileHandle = new FileHandle(filePath);
//            missions = json.fromJson(ArrayList.class, Mission.class, fileHandle);
//        }
//        return missions;
//    }
//
//    public static List<CharacterBase> loadCharacterBase(String filePath,boolean b) {
//        if(b || character_base.isEmpty()){
//            FileHandle fileHandle = new FileHandle(filePath);
//            character_base = json.fromJson(ArrayList.class, CharacterBase.class, fileHandle);
//        }
//        return character_base;
//    }
//
//    public static List<DailyReward> loadDailyRewards(String filePath,boolean b) {
//        if(b || daily_rewards.isEmpty()){
//            FileHandle fileHandle = new FileHandle(filePath);
//            daily_rewards = json.fromJson(ArrayList.class, DailyReward.class, fileHandle);
//        }
//        return daily_rewards;
//    }
//
////    public static DailyRewardsData loadDailyRewards(String filePath) {
////        FileHandle fileHandle = new FileHandle(filePath);
////
////        // Chuyển đổi dữ liệu JSON thành đối tượng DailyRewardsData
////        DailyRewardsData dailyRewardsData = json.fromJson(DailyRewardsData.class, fileHandle);
////        return dailyRewardsData;
////    }
//
//
//    // Tải JSON và lưu trữ JsonValue
//    private static synchronized JsonValue loadJsonValue(String filePath, boolean forceReload) {
//        String cacheKey = "json:" + filePath;
//        if (!forceReload && dataMap.containsKey(cacheKey)) {
//            return ((Array<JsonValue>) dataMap.get(cacheKey)).first();
//        }
//        JsonReader jsonReader = new JsonReader();
//        JsonValue jsonValue = jsonReader.parse(Gdx.files.internal(filePath));
//        if (jsonValue == null) {
//            Gdx.app.error("JsonLoader", "Failed to load JSON from " + filePath);
//            return null;
//        }
//        dataMap.put(cacheKey, new Array<>(new JsonValue[]{jsonValue}));
//        return jsonValue;
//    }
//
//    private static synchronized <T> Array<T> loadArrayFromFile(String filePath, Class<T> type, boolean forceReload) {
//        String cacheKey = "array:" + filePath + ":" + type.getName();
//        if (!forceReload && dataMap.containsKey(cacheKey)) {
//            return (Array<T>) dataMap.get(cacheKey);
//        }
//        Array<T> result = json.fromJson(Array.class, type, Gdx.files.internal(filePath));
//        dataMap.put(cacheKey, result);
//        return result;
//    }
//
//    public static <T> Array<T> loadArray(String filePath, Class<T> type, boolean forceReload) {
//        return loadArrayFromFile(filePath, type, forceReload);
//    }
//
//    public static <T> Array<T> loadArrayFromJsonValue(JsonValue jsonValue, Class<T> type) {
//        Array<T> result = new Array<>();
//        for (JsonValue value = jsonValue.child(); value != null; value = value.next()) {
//            T obj = json.readValue(type, value);
//            result.add(obj);
//        }
//        return result;
//    }
//
//    public static <T> Array<T> getArray(String filePath) {
//        String cacheKey = "array:" + filePath;
//        if (dataMap.containsKey(cacheKey)) {
//            return (Array<T>) dataMap.get(cacheKey);
//        } else {
//            throw new IllegalArgumentException("No data found for filePath: " + filePath);
//        }
//    }
//
//    public static <T> T getValueClassByKey(String filePath, String key, String name, Class<T> type) {
//        Array<T> dataArray = loadArray(filePath, type, false);
//        for (T a : dataArray) {
//            try {
//                Object fieldValue = a.getClass().getDeclaredField(key).get(a);
//                if (fieldValue != null && fieldValue.equals(name)) {
//                    return a;
//                }
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                Gdx.app.error("JsonLoader", "Field '" + key + "' not found or inaccessible in class " + type.getName(), e);
//            }
//        }
//        return null;
//    }
//
//    public static JsonValue getJsonValueByKey(String filePath, String key, String name) {
//        String jsonString = Gdx.files.internal(filePath).readString();
//        JsonReader jsonReader = new JsonReader();
//        JsonValue jsonData = jsonReader.parse(jsonString);
//
//        // Mảng JSON (tất cả các phần tử đều chứa thông tin cần tìm)
//        if (jsonData != null && jsonData.isArray()) {
//            // Duyệt qua từng phần tử trong mảng JSON
//            for (JsonValue item : jsonData) {
//                // Kiểm tra nếu trường `key` có trong đối tượng này
//                if (item.has(key) && item.getString(key).equals(name)) {
//                    return item;  // Trả về phần tử khớp
//                }
//            }
//        }
//        return null;  // Nếu không tìm thấy phần tử nào khớp
//    }
//
//    public static JsonValue getJsonValueByKey(JsonValue jsonData, String key, String name) {
//        if (jsonData != null && jsonData.isArray()) {
//            for (JsonValue item : jsonData) {
//                // Kiểm tra nếu trường `key` có trong đối tượng này
//                if (item.has(key) && item.getString(key).equals(name)) {
//                    return item;
//                }
//            }
//        }
//        return null;
//    }
//
//    /// Quantity of each type <--> Số lượng từng loại
//    public static int checkQuantity(JsonValue jsonData, String key, String name) {
//        int number = 0;
//        if (jsonData != null && jsonData.isArray()) {
//            for (JsonValue item : jsonData) {
//                if (item.has(key) && item.getString(key).equals(name)) {
//                    number++;
//                }
//            }
//        }
//        return number;
//    }
//
//    public static boolean removeChild(JsonValue jsonData, String key, String value) {
//        JsonValue previous = null;
//        for (JsonValue currentItem = jsonData.child; currentItem != null; currentItem = currentItem.next) {
//            if (value.equals(currentItem.getString(key, ""))) {
//                if (previous == null) {
//                    jsonData.child = currentItem.next;
//                } else {
//                    previous.next = currentItem.next;
//                }
//                return true;
//            }
//            previous = currentItem;
//        }
//        return false;
//    }
//
//    public static boolean removeChilds(JsonValue jsonData, String key, String value) {
//        boolean removed = false;
//        JsonValue previous = null;
//
//        for (JsonValue currentItem = jsonData.child; currentItem != null; currentItem = currentItem.next) {
//            if (value.equals(currentItem.getString(key, ""))) {
//                if (previous == null) {
//                    jsonData.child = currentItem.next;
//                } else {
//                    previous.next = currentItem.next;
//                }
//                removed = true;
//            } else {
//                previous = currentItem;
//            }
//        }
//        return removed;
//    }
//
//
//
//    public static JsonValue sortJsonByKey(JsonValue jsonData,String key) {
//        //sử dụng để sort level
//        List<JsonValue> jsonList = new ArrayList<>();
//
//        for (JsonValue value : jsonData) {
//            jsonList.add(value);
//        }
//
//        // Sắp xếp danh sách theo trường 'level' giảm dần
//        Collections.sort(jsonList, new Comparator<JsonValue>() {
//            @Override
//            public int compare(JsonValue json1, JsonValue json2) {
//                int level1 = json1.getInt(key);
//                int level2 = json2.getInt(key);
//                return Integer.compare(level2, level1); // Giảm dần
//            }
//        });
//
//        JsonValue sortedJsonData = new JsonValue(JsonValue.ValueType.array);
//        for (JsonValue value : jsonList) {
//            sortedJsonData.addChild(value);
//        }
//
//        return sortedJsonData;
//    }
//
//    // Hàm sắp xếp JsonValue (chứa một mảng JSON) theo grid
//    public static JsonValue sortJsonByGrid(JsonValue jsonData,String key, String empty) {
//        List<JsonValue> jsonList = new ArrayList<>();
//
//        for (JsonValue value : jsonData) {
//            jsonList.add(value);
//        }
//
//        Collections.sort(jsonList, new Comparator<JsonValue>() {
//            @Override
//            public int compare(JsonValue json1, JsonValue json2) {
//                String grid1 = json1.getString(key, empty);
//                String grid2 = json2.getString(key, empty);
//
//                if (!grid1.equals(empty) && grid2.equals(empty)) {
//                    return -1; // json1 lên trước
//                } else if (grid1.equals(empty) && !grid2.equals(empty)) {
//                    return 1; // json2 lên trước
//                }
//
//                return 0;
//            }
//        });
//
//        JsonValue sortedJsonData = new JsonValue(JsonValue.ValueType.array);
//        for (JsonValue value : jsonList) {
//            sortedJsonData.addChild(value);
//        }
//
//        return sortedJsonData;
//    }
//
//
//    public static <T> T getObject(String filePath, String key, Class<T> type, boolean forceReload) {
//        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
//        if (jsonValue == null) {
//            return null;
//        }
//
//        JsonValue targetValue = jsonValue.get(key);
//        if (targetValue == null) {
//            Gdx.app.error("JsonLoader", "Key '" + key + "' not found in " + filePath);
//            return null;
//        }
//
//        try {
//            return json.readValue(type, targetValue);
//        } catch (Exception e) {
//            Gdx.app.error("JsonLoader", "Failed to deserialize key '" + key + "' to type " + type.getName(), e);
//            return null;
//        }
//    }
//
//    public static <T> T getNestedValue(String filePath, String path, Class<T> type, boolean forceReload) {
//        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
//        if (jsonValue == null) {
//            return null;
//        }
//
//        String[] keys = path.split("\\.");
//        JsonValue current = jsonValue;
//        for (int i = 0; i < keys.length; i++) {
//            if (current == null) {
//                Gdx.app.error("JsonLoader", "Invalid path: " + path + " at key: " + keys[i]);
//                return null;
//            }
//
//            if (keys[i].startsWith("id=") && current.isArray()) {
//                String idValue = keys[i].substring(3);
//                for (JsonValue child : current) {
//                    String childId = child.getString("id", null);
//                    if (childId != null && childId.equals(idValue)) {
//                        current = child;
//                        break;
//                    }
//                }
//                if (current == jsonValue) {
//                    Gdx.app.error("JsonLoader", "No array element with id '" + idValue + "' found in " + filePath);
//                    return null;
//                }
//            } else if (current.isArray() && keys[i].matches("\\d+")) {
//                try {
//                    int index = Integer.parseInt(keys[i]);
//                    current = current.get(index);
//                } catch (NumberFormatException e) {
//                    Gdx.app.error("JsonLoader", "Invalid array index in path: " + keys[i]);
//                    return null;
//                }
//            } else {
//                current = current.get(keys[i]);
//            }
//        }
//
//        if (current == null) {
//            Gdx.app.error("JsonLoader", "Path '" + path + "' not found in " + filePath);
//            return null;
//        }
//
//        try {
//            return json.readValue(type, current);
//        } catch (Exception e) {
//            Gdx.app.error("JsonLoader", "Failed to deserialize path '" + path + "' to type " + type.getName(), e);
//            return null;
//        }
//    }
//
//    public static JsonValue getJsonValue(String filePath, boolean forceReload) {
//        return loadJsonValue(filePath, forceReload);
//    }
//
//    // Phương thức mới: Ghi JsonValue vào tệp JSON
//    public static void saveJsonValue(String filePath, JsonValue jsonValue) {
//        try {
//            // Chuyển JsonValue thành chuỗi JSON
//            String jsonString = json.toJson(jsonValue);
//
//            // Ghi lại chuỗi JSON vào tệp
//            Gdx.files.local(filePath).writeString(jsonString, false); // Ghi đè vào tệp (false)
//            Gdx.app.log("JsonLoader", "JSON saved to " + filePath);
//        } catch (Exception e) {
//            Gdx.app.error("JsonLoader", "Failed to save JSON to " + filePath, e);
//        }
//    }
//
//    public static JsonValue getJsonValue(JsonValue jsonValue, String path) {
//        String[] keys = path.split("\\.");
//        JsonValue result = jsonValue;
//        for (String k : keys) {
//            result = result.get(k);
//        }
//        return result;
//    }
//
//    // Hàm mới: Lấy danh sách các key của JsonValue từ file JSON
//    public static Array<String> getJsonKeys(String filePath, boolean forceReload) {
//        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
//        if (jsonValue == null) {
//            return null;
//        }
//
//        if (!jsonValue.isObject()) {
//            Gdx.app.error("JsonLoader", "JSON is not an object in " + filePath);
//            return null;
//        }
//
//        Array<String> keys = new Array<>();
//        for (JsonValue child = jsonValue.child(); child != null; child = child.next()) {
//            keys.add(child.name);
//        }
//        return keys;
//    }
//
//    // Hàm mới: Lấy danh sách các key của JsonValue
//    public static Array<String> getJsonKeys(JsonValue jsonValue) {
//        if (jsonValue == null) {
//            return null;
//        }
//
//        if (!jsonValue.isObject()) {
//            Gdx.app.error("JsonLoader", "JSON is not an object in " + jsonValue.name);
//            return null;
//        }
//
//        Array<String> keys = new Array<>();
//        for (JsonValue child = jsonValue.child(); child != null; child = child.next()) {
//            keys.add(child.name);
//        }
//        return keys;
//    }
//
//    public static Array<JsonValue> getJsonArray(String filePath, boolean forceReload) {
//        JsonValue jsonValue = loadJsonValue(filePath, forceReload);
//        if (jsonValue == null) {
//            return null;
//        }
//
//        Array<JsonValue> result = new Array<>();
//        if (jsonValue.isArray()) {
//            for (JsonValue child : jsonValue) {
//                result.add(child);
//            }
//        } else if (jsonValue.isObject()) {
//            for (JsonValue child = jsonValue.child(); child != null; child = child.next()) {
//                result.add(child);
//            }
//        } else {
//            Gdx.app.error("JsonLoader", "JSON is neither array nor object in " + filePath);
//            return null;
//        }
//        return result;
//    }
//
//    public static void clearData(String filePath) {
//        dataMap.remove(filePath);
//        dataMap.remove("json:" + filePath);
//        removeIfKeyStartsWith("array:" + filePath);
//    }
//
//    public static void clearAllData() {
//        dataMap.clear();
//    }
//
//    // Phương pháp trợ giúp để xóa các khóa bắt đầu bằng tiền tố
//    private static void removeIfKeyStartsWith(String prefix) {
//        Array<String> keysToRemove = new Array<>();
//        for (String key : dataMap.keys()) {
//            if (key.startsWith(prefix)) {
//                keysToRemove.add(key);
//            }
//        }
//        for (String key : keysToRemove) {
//            dataMap.remove(key);
//        }
//    }
//
//    public static void loadFullDataAccount() {
//        loadJsonValue(EQUIP_JSON, true);
//        loadJsonValue(ITEM_JSON, true);
//        loadJsonValue(CHARACTER_BASE_JSON, true);
//        loadJsonValue(SKILL_JSON, true);
//    }
//}
//
/////    -----------------------------------------
//// Lấy JsonValue (cache sau lần đầu)
/////    JsonValue json = JsonLoader.getJsonValue("skills.json", false);
/////    String name = json.get("warrior").get("1").get("name").asString(); // In: Basic Attack
//// Lấy object "warrior"
/////    public class SkillSet {
/////        public Map<String, Skill> skills;
/////    }
/////    public class Skill {
/////        public String name;
/////        public String description;
/////        public Map<String, Integer> effect;
/////    }
/////    SkillSet warrior = JsonLoader.getObject("skills.json", "warrior", SkillSet.class, false);
/////    System.out.println(warrior.skills.get("1").name); // In: Basic Attack
//// Lấy nested value
/////    String skillName = JsonLoader.getNestedValue("skills.json", "warrior.1.name", String.class, false);
/////    System.out.println(skillName); // In: Basic Attack
/////
//// Lấy array các giá trị con
/////    Array<JsonValue> array = JsonLoader.getJsonArray("skills.json", false);
/////    JsonValue warriorJson = array.find(v -> v.name.equals("warrior"));
/////    System.out.println(warriorJson.get("1").get("name").asString()); // In: Basic Attack
