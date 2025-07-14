package com.game.screens.start;

import static com.game.utils.Constants.BMF;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.MainGame;
import com.game.screens.BaseScreen;
import com.game.ui.base.UILabel;

public class LoadingScreen extends BaseScreen {

    private final UILabel progressLabel;
    private final Table table;
    private boolean loadingStarted = false;

    public LoadingScreen() {
        table = new Table();
        table.setFillParent(true);
        progressLabel = new UILabel("Đang tải: 0%", BMF);
        table.add(progressLabel).center();
    }

    @Override
    public void show() {
        super.show();
        Gdx.app.log("LoadingScreen", "show() called");
        stage.addActor(table);
        progressLabel.setText("Đang tải: 0%");
        progressLabel.setFontScale(3f);
        loadingStarted = true;

    }


    @Override
    protected void updateLogic(float delta) {
        if (loadingStarted) {
            boolean done = MainGame.getAsM().update();

            int progress = (int)(MainGame.getAsM().getProgress() * 100);
            progressLabel.setText("Đang tải: " + progress + "%");

            if (done) {
                MainGame.getScM().showPendingScreen();
            }
        }
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.getRoot().removeActor(table);
    }
}
