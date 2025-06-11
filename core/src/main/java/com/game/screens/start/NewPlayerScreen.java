package com.game.screens.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.MainGame;
import com.game.screens.BaseScreen;

public class NewPlayerScreen extends BaseScreen {
    private final Group NPGroup;

    public NewPlayerScreen() {
        super();
        NPGroup = new Group();
        NPGroup.setSize(stage.getWidth(), stage.getHeight());
        createScreen();
    }

    public static void loadingAsset() {

    }

    @Override
    protected void createScreen() {
        Gdx.app.log("NewPlayerScreen", "show() called");
        createTitle("New Player Screen", NPGroup);
    }

    @Override
    public void show() {
        super.show();
        stage.addActor(NPGroup);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        title.remove();
    }
}
