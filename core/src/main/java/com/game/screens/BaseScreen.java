package com.game.screens;

import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.utils.ResourceUtils;

public abstract class BaseScreen implements Screen {
    protected Stage stage;
    protected static Engine engine;
    protected float screenWidth;
    protected float screenHeight;
    protected Group rootGroup;
    protected Label title;

    private ShapeRenderer shapeRenderer;

    public BaseScreen() {
        stage = MainGame.getStage();
        engine = MainGame.getEngine();
        screenWidth = stage.getWidth();
        screenHeight = stage.getHeight();
        rootGroup = new Group();
        rootGroup.setName("rootGroup");
        rootGroup.setSize(stage.getWidth(), stage.getHeight());
        shapeRenderer = new ShapeRenderer();
    }

    protected void createScreen() {
    }

    @Override
    public void show() {
        stage.clear();
        stage.addActor(rootGroup);
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

    protected void createCloseButton(ScreenType prevScreen) {
        UIButton btnClose = new UIButton(
            MainGame.getAsM().getRegion(UI_WOOD, "x_up_037"),
            MainGame.getAsM().getRegion(UI_WOOD, "x_down_038"))
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.8f, screenHeight * 0.8f)
            .onClick(() -> {
                MainGame.getScM().showScreen(prevScreen);
            }).debug(false);
        rootGroup.addActor(btnClose);
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

    protected void debugGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (int i = 0; i < 10; i++) {
            float y = Gdx.graphics.getHeight() * 0.1f * i;
            shapeRenderer.line(0, y, Gdx.graphics.getWidth(), y);
        }

        for (int i = 0; i < 10; i++) {
            float x = Gdx.graphics.getWidth() * 0.1f * i;
            shapeRenderer.line(x, 0, x, Gdx.graphics.getHeight());
        }
        shapeRenderer.end();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateLogic(delta);

        engine.update(delta);
        stage.act(delta);
        stage.draw();

        updateUI(delta);
        debugGrid();
    }
}
