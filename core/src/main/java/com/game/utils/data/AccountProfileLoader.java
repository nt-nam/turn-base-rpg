package com.game.utils.data;

import static com.game.utils.Constants.MAININFO_JSON_LOCAL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Account;
import com.game.models.entity.Profile;
import com.game.utils.Constants;

import java.util.List;

/**
 * Loader cho Account + Profile nguoi choi.
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class AccountProfileLoader {

    private AccountProfileLoader() {
    }

    public static List<Account> loadAccountList(boolean b) {
        if (b || GameSessionManager.getInstance().accountList.isEmpty()) {
            GameSessionManager.getInstance().accountList.clear();
            FileHandle fileHandle = Gdx.files.local(MAININFO_JSON_LOCAL);
            if (!fileHandle.exists()) {
                return null;
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);
            if (root == null) {
                return null;
            }
            for (JsonValue item : root) {
                Account newItem = new Account();
                newItem.id = item.getString("id");
                newItem.level = item.getInt("level");
                newItem.characterSelect = item.getString("characterSelect", null);

                GameSessionManager.getInstance().accountList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().accountList;
    }

    public static Profile loadProfile(boolean b) {
        if (b || GameSessionManager.getInstance().profile != null) {
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("info.json"));
            if (!fileHandle.exists()) {
                return null;
            }
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            Profile newItem = new Profile(root.getString("name"), root.getString("characterSelect"));
            newItem.level = root.getInt("level");
            newItem.area = root.getString("area");
            newItem.pos = new Vector2(root.get("pos").getInt("x"), root.get("pos").getInt("y"));
            newItem.dailyCheck = root.getString("dailyCheck");
            newItem.sizeTeam = root.getInt("sizeTeam");
            newItem.exp = root.getInt("exp");
            newItem.coin = root.getInt("coin");
            newItem.gem = root.getInt("gem");
            newItem.energy = root.getInt("energy", 0);
            newItem.energyTime = root.getString("energy", "empty");
            newItem.numberOfTeammatesRecruited = root.getInt("numberOfTeammatesRecruited");
            newItem.equipment = root.getInt("equipment");
            newItem.numberOfEnemies = root.getInt("numberOfEnemies");
            newItem.playMusic = root.getBoolean("playMusic", true);
            newItem.playSound = root.getBoolean("playSound", true);
            GameSessionManager.getInstance().profile = newItem;
        }
        return GameSessionManager.getInstance().profile;
    }

    public static void clearDataProfile() {
        GameSessionManager.getInstance().profile = null;
        GameSessionManager.getInstance().profile = new Profile();
        GameSessionManager.getInstance().achievementList.clear();
        GameSessionManager.getInstance().dailyRewardList.clear();
        GameSessionManager.getInstance().equipList.clear();
        GameSessionManager.getInstance().heroList.clear();
        GameSessionManager.getInstance().itemList.clear();
        GameSessionManager.getInstance().lineupList.clear();
        GameSessionManager.getInstance().missionList.clear();
        GameSessionManager.getInstance().checkMapList.clear();
    }
}
