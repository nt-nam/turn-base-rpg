package com.game.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.managers.GameSessionManager;
import com.game.models.entity.MapBattle;
import com.game.models.entity.Reward;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader cho MapBattle (doi hinh enemy + reward cua man battle).
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class BattleMapLoader {

    private BattleMapLoader() {
    }

    public static MapBattle loadMapBattle(String filePath) {
        GameSessionManager.getInstance().mapBattle = new MapBattle();
        GameSessionManager.getInstance().mapBattle.heroEnemyList = HeroLoader.loadHeroList(filePath, true);
        GameSessionManager.getInstance().mapBattle.rewardList = loadRewardBattle(filePath);
        return GameSessionManager.getInstance().mapBattle;
    }

    private static List<Reward> loadRewardBattle(String filePath) {
        FileHandle fileHandle = Gdx.files.internal(filePath);
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(fileHandle);
        if (root.get("reward") != null) {
            root = root.get("reward");
        }
        List<Reward> rewards = new ArrayList<>();
        for (JsonValue hero : root) {
            Reward reward = new Reward();
            reward.nameRegion = hero.getString("id", "idBaseDefault");
            reward.type = hero.getString("type", "typeDefault");
            reward.quantity = hero.getInt("quantity", 0);
            rewards.add(reward);
        }
        return rewards;
    }
}
