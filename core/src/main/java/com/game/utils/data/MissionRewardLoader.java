package com.game.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Achievement;
import com.game.models.entity.CheckMap;
import com.game.models.entity.DailyReward;
import com.game.models.entity.Mission;
import com.game.models.entity.Reward;
import com.game.utils.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader cho Mission, Achievement, DailyReward, CheckMap.
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class MissionRewardLoader {

    private MissionRewardLoader() {
    }

    public static List<Achievement> loadAchievementList(boolean b) {
        if (b || GameSessionManager.getInstance().achievementList.isEmpty()) {
            GameSessionManager.getInstance().achievementList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("achievement.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue c : root) {
                Achievement newChild = new Achievement();
                newChild.idBase = c.getString("idBase");
                newChild.name = c.getString("name");
                newChild.dec = c.getString("dec", "");
                newChild.number = c.getInt("number", 0);
                GameSessionManager.getInstance().achievementList.add(newChild);
            }
        }
        return GameSessionManager.getInstance().achievementList;
    }

    public static List<CheckMap> loadCheckMapList(boolean b) {
        if (b || GameSessionManager.getInstance().checkMapList.isEmpty()) {
            GameSessionManager.getInstance().checkMapList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("check_enemy_map.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                if (equip.isEmpty()) return null;
                CheckMap child = new CheckMap();
                child.name = equip.getString("name");
                child.sum = equip.getInt("sum");
                child.battleList = new ArrayList<>();
                for (JsonValue a : equip.get("battleList")) {
                    CheckMap.BattleDes battle = new CheckMap.BattleDes();
                    battle.id = a.getString("id");
                    battle.dayCheck = LocalDate.parse(a.getString("dayCheck"));
                    child.battleList.add(battle);
                }
                GameSessionManager.getInstance().checkMapList.add(child);
            }
        }
        return GameSessionManager.getInstance().checkMapList;
    }

    public static List<DailyReward> loadDailyRewardList(boolean b) {
        String filePath = Constants.playerPath("daily_rewards.json");
        if (b || GameSessionManager.getInstance().dailyRewardList.isEmpty()) {
            GameSessionManager.getInstance().dailyRewardList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue child : root) {
                DailyReward newChild = new DailyReward();
                newChild.id = child.getInt("id");
                newChild.typereward = child.getString("typereward");
                newChild.confirm = child.getBoolean("confirm");
                newChild.number = child.getInt("number");
                GameSessionManager.getInstance().dailyRewardList.add(newChild);
            }
        }
        return GameSessionManager.getInstance().dailyRewardList;
    }

    public static List<Mission> loadMissionList(boolean b) {
        String filePath = Constants.playerPath("mission.json");
        if (b || GameSessionManager.getInstance().missionList.isEmpty()) {
            GameSessionManager.getInstance().missionList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue child : root) {
                Mission newChild = new Mission();
                newChild.idBase = child.getString("idBase");
                newChild.title = child.getString("title");
                newChild.description = child.getString("description");
                newChild.progress = child.getInt("progress");
                newChild.targetAmount = child.getInt("targetAmount");
                for (JsonValue a : child.get("rewards")) {
                    Reward reward = new Reward();
                    reward.nameRegion = a.getString("nameRegion");
                    reward.type = a.getString("type");
                    reward.quantity = a.getInt("quantity");
                    newChild.rewards.add(reward);
                }
                GameSessionManager.getInstance().missionList.add(newChild);
            }
        }
        return GameSessionManager.getInstance().missionList;
    }
}
