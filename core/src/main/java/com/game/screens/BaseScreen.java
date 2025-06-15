package com.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.MainGame;
import com.game.utils.ResourceUtils;

public abstract class BaseScreen implements Screen {
    protected Stage stage;
    protected float screenWidth;
    protected float screenHeight;
//    protected Engine engine;
    protected Group rootGroup;
    protected Label title;

    public BaseScreen() {
        stage = MainGame.getStage();
        screenWidth = stage.getWidth();
        screenHeight = stage.getHeight();
//        engine = MainGame.getEngine();
        rootGroup = new Group();
        rootGroup.setSize(stage.getWidth(), stage.getHeight());
    }

    protected void createScreen() {
    }

    @Override
    public void show() {
        stage.clear();
        stage.addActor(rootGroup);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateLogic(delta);

        stage.act(delta);
        stage.draw();
//        engine.update(delta);

        updateUI(delta);
    }

    protected void updateLogic(float delta) {
    }

    protected void updateUI(float delta) {
    }


    protected void createTitle(String text, Group parent) {
        title = new Label(text, MainGame.getAsM().getSkin());
        title.setFontScale(2);
        title.setPosition(150, stage.getHeight() - 50);
        parent.addActor(title);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        this.screenWidth = stage.getWidth();
        this.screenHeight = stage.getHeight();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        ResourceUtils.disposeGroup(rootGroup);
        rootGroup.clear();
        stage.clear();
    }
}
