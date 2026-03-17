package com.game.utils;

import static com.game.utils.Constants.ACHIEVEMENT_BASE_JSON;

import static com.game.utils.Constants.DAILY_BASE_JSON;



import static com.game.utils.Constants.MISSION_BASE_JSON;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.utils.json.Profile;
import com.google.gson.Gson;


public class JsonSaver {

    public static boolean saveObject(String filePath, Object object) {
        Gson json = new Gson();
        String jsonString = json.toJson(object);

        System.out.println("Serialized JSON: " + jsonString);
        FileHandle file = Gdx.files.local(filePath);

        try {
            file.writeString(jsonString, false);
            System.out.println("Data saved to " + file.path());
            return true;
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveInfo(Profile profile) {
        Gson json = new Gson();

        String dirPath = "data/select/" + profile.name;
        FileHandle dir = Gdx.files.local(dirPath);

        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created directory: " + dirPath);
        }
        String s = json.toJson(profile);
        FileHandle file = Gdx.files.local(dirPath + "/info.json");
        try {
            file.writeString(s, false);  // Ghi đè nếu tệp đã tồn tại
            System.out.println("Data saved to " + file.path());
            return true;
        } catch (GdxRuntimeException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveString(String filePath, String value) {
        Gson json = new Gson();

        FileHandle file = Gdx.files.local(filePath);

        try {
            file.writeString(value, false);
            System.out.println("Data saved to " + file.path());
            return true;
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveStringJsonValue(String filePath, JsonValue value) {
        Gson json = new Gson();

        FileHandle file = Gdx.files.local(filePath);
        String jsonString = json.toJson(value);

        try {
            file.writeString(jsonString, false);
            System.out.println("Data saved to " + file.path());
            return true;
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return false;
        }
    }

    public static boolean createAccount() {
        saveString(Constants.playerPath("items.json"), "[]");
        saveString(Constants.playerPath("equips.json"), "[]");
        copyFileToLocal(ACHIEVEMENT_BASE_JSON, Constants.playerPath("achievement.json"));
        copyFileToLocal(MISSION_BASE_JSON, Constants.playerPath("mission.json"));
        copyFileToLocal(DAILY_BASE_JSON, Constants.playerPath("daily_rewards.json"));
        return true;
    }

    public static void copyFileToLocal(String assetPath, String localPath) {
        FileHandle sourceFile = Gdx.files.internal(assetPath);

        if (!sourceFile.exists()) {
            System.err.println("Source file does not exist: " + assetPath);
            return;
        }

        FileHandle destFile = Gdx.files.local(localPath);
        destFile.parent().mkdirs();

        sourceFile.copyTo(destFile);
        System.out.println("File copied from " + assetPath + " to " + localPath);
    }


    public static JsonValue load(String path) {
        JsonReader reader = new JsonReader();
        JsonValue jsonValue = null;

        try {
            // Lấy tệp từ thư mục internal (assets)
            FileHandle file = Gdx.files.internal(path);

            if (!file.exists()) {
                System.err.println("File not found in assets: " + path);
                return null;  // Nếu tệp không tồn tại, trả về null
            }

            // Đọc dữ liệu từ tệp JSON trong thư mục internal (assets)
            jsonValue = reader.parse(file);

            if (jsonValue == null) {
                System.err.println("Failed to parse JSON from file: " + path);
                return null;  // Nếu không thể parse JSON, trả về null
            }

            System.out.println("Successfully loaded JSON from: " + path);
        } catch (Exception e) {
            // Xử lý lỗi khi đọc hoặc phân tích JSON
            System.err.println("An error occurred while loading the JSON file: " + path);
            e.printStackTrace();
        }

        return jsonValue;  // Trả về JsonValue sau khi đã tải
    }

    public static void SaveFullDataAccount(){
//        saveObject();
    }

    public static void removeAccount(){
        FileHandle file = Gdx.files.local("data/select/"+GameSession.profile.name);
        deleteDirectory(file);

    }

    public static void deleteDirectory(FileHandle dir) {
        if (dir.isDirectory()) {
            FileHandle[] files = dir.list();
            if (files != null) {
                for (FileHandle file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();
    }


}
