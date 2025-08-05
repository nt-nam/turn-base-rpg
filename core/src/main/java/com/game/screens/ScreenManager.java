package com.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Disposable;
import com.game.MainGame;
import com.game.screens.battle.BattleResultScreen;
import com.game.screens.battle.BattleScreen;
import com.game.screens.main.WorldMapScreen;
import com.game.screens.service.CharacterScreen;
import com.game.screens.service.InventoryScreen;
import com.game.screens.service.MapScreen;
import com.game.screens.service.PauseScreen;
import com.game.screens.service.QuestScreen;
import com.game.screens.start.LoadingScreen;
import com.game.screens.start.MenuScreen;
import com.game.screens.start.NewPlayerScreen;
import com.game.screens.start.SelectPlayerScreen;
import com.game.screens.start.SplashScreen;

import java.util.EnumMap;

public class ScreenManager implements Disposable {

    private final EnumMap<ScreenType, Screen> screenCache;
    private ScreenType pendingScreen;

    public ScreenManager() {
        screenCache = new EnumMap<>(ScreenType.class);
    }

    public void showScreen(ScreenType type) {
        if (needLoadingFor(type)) {
            this.pendingScreen = type;
            showInternal(ScreenType.LOADING);
        } else {
            showInternal(type);
        }
    }

    private void showInternal(ScreenType type) {
        Screen screenToShow = screenCache.get(type);

        if (screenToShow == null) {
            screenToShow = createScreen(type);
            screenCache.put(type, screenToShow);
        }

        if (screenToShow != null) {
            MainGame.getInstance().setScreen(screenToShow);
        }
    }

    private Screen createScreen(ScreenType type) {
        switch (type) {
            case SPLASH:
                return new SplashScreen();
            case LOADING:
                return new LoadingScreen();
            case MENU_GAME:
                return new MenuScreen();
            case CHECK_ATLAS:
                return new CheckRegionScreen();
            case NEW_PLAYER:
                return new NewPlayerScreen();
            case SELECT_PLAYER:
                return new SelectPlayerScreen();
            case WORLD_MAP:
                return new WorldMapScreen();
            case BATTLE:
                return new BattleScreen();
            case BATTLE_RESULT:
                return new BattleResultScreen();
            case INVENTORY:
                return new InventoryScreen();
            case CHARACTER_SELECT:
                return new CharacterScreen();
            case QUEST:
                return new QuestScreen();
            case MINI_MAP:
                return new MapScreen();
            case PAUSE:
                return new PauseScreen();
            default:
                return null;
        }
    }

    public void removeScreen(ScreenType type) {
        Screen screenToRemove = screenCache.get(type);
        if (screenToRemove != null) {
            screenToRemove.dispose();
            screenCache.remove(type);
        } else {
            System.out.println("Lỗi: Màn hình loại " + type + " không tồn tại trong bộ nhớ đệm.");
        }
    }

    public void showPendingScreen() {
        if (pendingScreen != null) {
            showInternal(pendingScreen);
            pendingScreen = null;
        }
    }

    private boolean needLoadingFor(ScreenType targetScreen) {
        switch (targetScreen) {
            case MAIN_GAME:
                return true;
            case CHECK_ATLAS:
                CheckRegionScreen.loadingAsset();
                return true;
            case MENU_GAME:
                MenuScreen.loadingAsset();
                return true;
            case NEW_PLAYER:
                NewPlayerScreen.loadingAsset();
                return true;
            case WORLD_MAP:
                WorldMapScreen.loadingAsset();
                return true;
            case SELECT_PLAYER:
                SelectPlayerScreen.loadingAsset();
                return true;
            case BATTLE:
                BattleScreen.loadingAsset();
                return true;
            default:
                return false;
        }
    }

    public void clearScreenCache() {
        for (ScreenType type : screenCache.keySet().toArray(new ScreenType[0])) {
            if (type != ScreenType.WORLD_MAP) {
                Screen screen = screenCache.remove(type);
                if (screen != null) screen.dispose();
            }
        }
    }

    @Override
    public void dispose() {
        System.out.println("Disposing ScreenManager and all cached screens...");
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();
    }
}
