package com.game.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

public class BattleSimulationResultSaver {

    // Lưu BattleSimulationResult vào file JSON sử dụng FileHandle của LibGDX
    public static void saveBattleSimulationResult(BattleSimulationResult result, String filePath) {
        // Tạo đối tượng Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty printing để JSON dễ đọc

        JsonObject jsonResult = new JsonObject();

        // Lưu các lượt đấu
        ArrayList<ArrayList<JsonObject>> roundsJson = new ArrayList<>();
        for (Array<TurnResult> round : result.rounds) {
            ArrayList<JsonObject> turnResultsJson = new ArrayList<>();
            for (TurnResult turn : round) {
                JsonObject turnJson = new JsonObject();
                turnJson.addProperty("actorId", turn.actorId);
                turnJson.addProperty("targetId", turn.targetId);
                turnJson.addProperty("skillUsed", turn.skillUsed);
                turnJson.addProperty("damage", turn.damage);
                turnJson.addProperty("isCritical", turn.isCritical);
                turnJson.addProperty("isMiss", turn.isMiss);
                turnJson.addProperty("targetDead", turn.targetDead);
                turnJson.add("state", convertStateToJson(turn.state));
                turnJson.add("actorEntity", saveEntityToJson(turn.actorEntity));
                turnJson.add("targetEntity", saveEntityToJson(turn.targetEntity));

                turnResultsJson.add(turnJson);
            }
            roundsJson.add(turnResultsJson);
        }

        jsonResult.add("rounds", gson.toJsonTree(roundsJson));
        jsonResult.addProperty("winner", result.winner);

        // Sử dụng FileHandle để lưu vào file
        FileHandle file = new FileHandle(filePath);
        file.writeString(gson.toJson(jsonResult), false);  // false để ghi đè lên file cũ

        System.out.println("Saved BattleSimulationResult to " + filePath);
    }

    // Chuyển Map stats thành JSON
    private static JsonObject convertStatsToJson(Map<String, Integer> stats) {
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            json.addProperty(entry.getKey(), entry.getValue());
        }
        return json;
    }

    // Chuyển Map state thành JSON
    private static JsonObject convertStateToJson(Map<String, Float> state) {
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Float> entry : state.entrySet()) {
            json.addProperty(entry.getKey(), entry.getValue());
        }
        return json;
    }

    // Lưu thông tin của Entity vào JSON
    private static JsonObject saveEntityToJson(Entity entity) {
        JsonObject json = new JsonObject();

        // Lấy các component của Entity và chuyển thành JSON
        StatComponent stat = entity.getComponent(StatComponent.class);
        if (stat != null) {
            JsonObject statJson = new JsonObject();
            statJson.addProperty("hp", stat.hp);
            statJson.addProperty("mp", stat.mp);
            statJson.addProperty("atk", stat.atk);
            statJson.addProperty("def", stat.def);
            json.add("stat", statJson);
        }

        CharacterComponent character = entity.getComponent(CharacterComponent.class);
        if (character != null) {
            JsonObject characterJson = new JsonObject();
            characterJson.addProperty("characterId", character.nameRegion);
            characterJson.addProperty("name", character.name);
            json.add("character", characterJson);
        }

        // Tương tự cho các component khác mà bạn cần lưu trữ (ví dụ: GridComponent, PlayerComponent, etc.)

        return json;
    }
}
