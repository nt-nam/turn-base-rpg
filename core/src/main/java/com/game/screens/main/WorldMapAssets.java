package com.game.screens.main;

import static com.game.utils.Constants.ATLAS_ICON;
import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.CHARACTER_ATLAS;
import static com.game.utils.Constants.CHARACTER_BASE_JSON;

import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.managers.GameSessionManager;
import com.game.utils.Constants;
import com.game.utils.DataHelper;

/**
 * Tap trung logic preload asset + du lieu cho WorldMapScreen.
 * Tach khoi WorldMapScreen (tang View) de Screen chi lo hien thi/vong doi.
 * Logic giu nguyen hanh vi goc.
 */
public final class WorldMapAssets {

    private WorldMapAssets() {
    }

    public static void load() {
        JsonValue characterBase = DataHelper.getJsonValue(CHARACTER_BASE_JSON);
        for (JsonValue character : characterBase) {
            String nameRegion = character.getString("nameRegion");
            MainGame.getAsM().loadAtlas(CHARACTER_ATLAS + nameRegion + ".atlas");
        }
        MainGame.getAsM()
                .loadTiledMap((GameSessionManager.getInstance().pendingTeleport != null
                        ? GameSessionManager.getInstance().pendingTeleport.nextMap
                        : GameSessionManager.getInstance().profile.area));
        MainGame.getAsM().loadAtlas(ATLAS_ITEM);
        MainGame.getAsM().loadAtlas(ATLAS_ICON);
        DataHelper.loadHeroList(Constants.playerPath("hero_full.json"), true);
        DataHelper.loadLineupList(true);
        DataHelper.loadAchievementList(true);
        DataHelper.loadMissionList(true);
        DataHelper.loadEquipBaseList(true);
        DataHelper.loadItemBaseList(true);
        DataHelper.loadEquipList(true);
        DataHelper.loadItemList(true);
    }
}
