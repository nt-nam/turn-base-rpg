package com.game.screens.start;

import static com.game.utils.Constants.BMF;

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
        Gdx.app.log("MenuScreen", "create() called");

        Image bg = new Image(MainGame.getAsM().getTexture("texture/default.png"));
        bg.setSize(screenWidth*0.4f, screenHeight*0.8f);
        rootGroup.addActor(bg);
        bg.setPosition(screenWidth *0.0f, screenHeight*0.1f);

        Image bg2 = new Image(MainGame.getAsM().getTexture("texture/default2.png"));
        bg2.setSize(screenWidth*0.4f, screenHeight*0.7f);
        rootGroup.addActor(bg2);
        bg2.setPosition(screenWidth *0.6f, screenHeight*0.15f);

//        createTitle("Menu Screen", rootGroup);

        UITable table1 = new UITable()
            .name("tbMenu")
            .fillParent()
            .al(Align.center)
            .child(
                new UIButton("New Game").fontScale(2).onClick(() -> MainGame.getScM().showScreen(ScreenType.NEW_PLAYER)),
                new UIButton("Load Game").fontScale(2).onClick(() -> MainGame.getScM().showScreen(ScreenType.SELECT_PLAYER)),
                new UIButton("Setting").fontScale(2).onClick(() -> MainGame.getScM().showScreen(ScreenType.MAIN_SETTING))
            ).padChildren(20)
            .sizeChildren(screenWidth*0.2f,screenHeight*0.1f);

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
        Gdx.app.log("MenuScreen", "resize(" + width + ", " + height + ") called");
        super.resize(width, height);
    }

    @Override
    public void pause() {
        Gdx.app.log("MenuScreen", "pause() called");
    }

    @Override
    public void resume() {
        Gdx.app.log("MenuScreen", "resume() called");
    }

    @Override
    public void hide() {
        Gdx.app.log("MenuScreen", "hide() called");
    }

    @Override
    public void dispose() {
        Gdx.app.log("MenuScreen", "dispose() called");
        super.dispose();
    }

    public static void loadingAsset() {
        MainGame.getAsM().load("texture/default.png", Texture.class);
        MainGame.getAsM().load("texture/default2.png", Texture.class);
        MainGame.getAsM().load("music/music_demo.mp3", Music.class);
        MainGame.getAsM().loadFont(BMF);
    }

}
