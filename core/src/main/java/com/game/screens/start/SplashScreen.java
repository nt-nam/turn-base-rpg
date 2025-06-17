package com.game.screens.start;

import com.game.screens.BaseScreen;

public class SplashScreen extends BaseScreen {
    public SplashScreen() {
        super();
        createScreen();
    }
    public static void loadingAsset() {
    }

    @Override
    protected void createScreen() {
    }

    @Override
    public void show() {
        super.show();
        createTitle("Splash Screen", rootGroup);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
