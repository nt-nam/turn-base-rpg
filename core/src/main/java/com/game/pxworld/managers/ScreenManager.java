package com.game.pxworld.managers;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Disposable;
import com.game.pxworld.Main;
import com.game.pxworld.screens.BattleScreen;
import com.game.pxworld.screens.GameScreen;
import com.game.pxworld.screens.ScreenType;

import java.util.EnumMap;

public class ScreenManager implements Disposable {

    private EnumMap<ScreenType,Screen> screenCache;

    public ScreenManager() {
        screenCache = new EnumMap<>(ScreenType.class);
    }

    public Screen getScreen(ScreenType type) {
        return screenCache.get(type);
    }

    public void showScreen(ScreenType type) {
        Screen screen = null;
        switch (type) {
            case GAME:
                if (screenCache.get(ScreenType.GAME) == null) {
                    screenCache.put(ScreenType.GAME, new GameScreen());
                }
                screen = screenCache.get(ScreenType.GAME);
                Main.getInstance().setScreen(new GameScreen());
                break;
            case BATTLE:
                Main.getInstance().setScreen(new BattleScreen());
                break;
        }
        if (screen != null) {
            Main.getInstance().setScreen(screen);
        }


    }

    private Screen createScreen(ScreenType type) {
        switch (type) {
            case GAME:
                return new GameScreen();
            case BATTLE:
                return new BattleScreen();
            default:
                return null;
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

