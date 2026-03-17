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
        // TODO: Thêm logo / animation splash ở đây
    }

    @Override
    public void show() {
        super.show();
        createTitle("Splash Screen", rootGroup);
    }
}
