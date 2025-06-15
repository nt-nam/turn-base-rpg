package com.game.screens.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UITable;

public class MenuScreen extends BaseScreen {


    public MenuScreen() {
        super();
        createScreen();
    }

    @Override
    public void createScreen() {
        Gdx.app.log("MainMenuScreen", "show() called");

        Image bg = new Image(MainGame.getAsM().getTexture("texture/default.png"));
        bg.setSize(250, 250);
        rootGroup.addActor(bg);

        createTitle("Menu Screen", rootGroup);

        UITable table1 = new UITable()
            .name("tbMenu")
            .fillParent()
            .al(Align.center)
            .child(
                new UIButton("New Game").onClick(() -> MainGame.getScM().showScreen(ScreenType.NEW_PLAYER)),
                new UIButton("Load Game").onClick(() -> MainGame.getScM().showScreen(ScreenType.SELECT_PLAYER)),
                new UIButton("Setting").onClick(() -> MainGame.getScM().showScreen(ScreenType.MAIN_SETTING))
            ).padChildren(20)
            .sizeChildren(150, 50);

        rootGroup.addActor(table1);





    }



    @Override
    public void show() {
        super.show();
        Gdx.app.log("MenuScreen", "show() called");
        stage.addActor(rootGroup);
//        engine.addEntity();
//        engine.addSystem();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("MainMenuScreen", "resize(" + width + ", " + height + ") called");
        super.resize(width, height);
    }

    @Override
    public void pause() {
        Gdx.app.log("MainMenuScreen", "pause() called");
    }

    @Override
    public void resume() {
        Gdx.app.log("MainMenuScreen", "resume() called");
    }

    @Override
    public void hide() {
        Gdx.app.log("MainMenuScreen", "hide() called");
    }

    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen", "dispose() called");
        super.dispose();
    }

    public static void loadingAsset() {
        MainGame.getAsM().load("texture/default.png", Texture.class);
        MainGame.getAsM().load("music/music_demo.mp3", Music.class);


    }

}
