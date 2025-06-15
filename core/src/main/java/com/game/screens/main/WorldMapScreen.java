package com.game.screens.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.game.screens.BaseScreen;

public class WorldMapScreen extends BaseScreen {
    private Stage mapStage;
    public WorldMapScreen() {
        super();
        mapStage = new Stage(stage.getViewport());
        createScreen();
    }

    @Override
    protected void createScreen() {
        super.createScreen();
    }

    public static void loadingAsset(){

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateLogic(delta);

        mapStage.act(delta);
        mapStage.draw();

        stage.act(delta);
        stage.draw();

        updateUI(delta);
    }
    @Override
    public void dispose() {
        super.dispose();
    }
}
