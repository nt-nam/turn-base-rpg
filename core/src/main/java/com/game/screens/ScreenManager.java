package com.game.screens;

import com.badlogic.gdx.Gdx;
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
import java.util.function.Supplier;

public class ScreenManager implements Disposable {

    /**
     * Bang dang ky khai bao cho tung ScreenType:
     * - factory: cach tao Screen.
     * - assetLoader: (tuy chon) tac vu preload asset; neu != null thi Screen can di qua LoadingScreen.
     * Them Screen moi = them 1 dong o day, KHONG phai sua switch trong nhieu method.
     */
    private static final class ScreenSpec {
        final Supplier<Screen> factory;
        final Runnable assetLoader; // null neu khong can loading

        ScreenSpec(Supplier<Screen> factory, Runnable assetLoader) {
            this.factory = factory;
            this.assetLoader = assetLoader;
        }
    }

    private final EnumMap<ScreenType, ScreenSpec> registry;
    private final EnumMap<ScreenType, Screen> screenCache;
    private ScreenType pendingScreen;

    public ScreenManager() {
        screenCache = new EnumMap<>(ScreenType.class);
        registry = new EnumMap<>(ScreenType.class);

        // factory-only (khong can loading)
        registry.put(ScreenType.SPLASH, new ScreenSpec(SplashScreen::new, null));
        registry.put(ScreenType.LOADING, new ScreenSpec(LoadingScreen::new, null));
        registry.put(ScreenType.BATTLE_RESULT, new ScreenSpec(BattleResultScreen::new, null));
        registry.put(ScreenType.INVENTORY, new ScreenSpec(InventoryScreen::new, null));
        registry.put(ScreenType.CHARACTER_SELECT, new ScreenSpec(CharacterScreen::new, null));
        registry.put(ScreenType.QUEST, new ScreenSpec(QuestScreen::new, null));
        registry.put(ScreenType.MINI_MAP, new ScreenSpec(MapScreen::new, null));
        registry.put(ScreenType.PAUSE, new ScreenSpec(PauseScreen::new, null));

        // can preload asset -> di qua LoadingScreen
        registry.put(ScreenType.CHECK_ATLAS, new ScreenSpec(CheckRegionScreen::new, CheckRegionScreen::loadingAsset));
        registry.put(ScreenType.MENU_GAME, new ScreenSpec(MenuScreen::new, MenuScreen::loadingAsset));
        registry.put(ScreenType.NEW_PLAYER, new ScreenSpec(NewPlayerScreen::new, NewPlayerScreen::loadingAsset));
        registry.put(ScreenType.WORLD_MAP, new ScreenSpec(WorldMapScreen::new, WorldMapScreen::loadingAsset));
        registry.put(ScreenType.SELECT_PLAYER, new ScreenSpec(SelectPlayerScreen::new, SelectPlayerScreen::loadingAsset));
        registry.put(ScreenType.BATTLE, new ScreenSpec(BattleScreen::new, BattleScreen::loadingAsset));
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
        ScreenSpec spec = registry.get(type);
        return spec != null ? spec.factory.get() : null;
    }

    public void removeScreen(ScreenType type) {
        Screen screenToRemove = screenCache.get(type);
        if (screenToRemove != null) {
            screenToRemove.dispose();
            screenCache.remove(type);
        } else {
            Gdx.app.log("ScreenManager", "Man hinh loai " + type + " khong ton tai trong bo nho dem.");
        }
    }

    public void showPendingScreen() {
        if (pendingScreen != null) {
            showInternal(pendingScreen);
            pendingScreen = null;
        }
    }

    private boolean needLoadingFor(ScreenType targetScreen) {
        ScreenSpec spec = registry.get(targetScreen);
        if (spec != null && spec.assetLoader != null) {
            spec.assetLoader.run();
            return true;
        }
        return false;
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
        Gdx.app.log("ScreenManager", "Disposing ScreenManager and all cached screens...");
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();
    }
}
