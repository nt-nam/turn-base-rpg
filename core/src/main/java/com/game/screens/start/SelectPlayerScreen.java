package com.game.screens.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.MainGame;
import com.game.screens.BaseScreen;

public class SelectPlayerScreen extends BaseScreen {

    public SelectPlayerScreen() {
        super();
        createScreen();
    }

    public static void loadingAsset() {
    }

    @Override
    protected void createScreen() {
        super.createScreen();
    }

    @Override
    public void show() {
        super.show();
        Gdx.app.log("SelectPlayerScreen", "show() called");
        createTitle("Select Player Screen", rootGroup);
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
