package com.game.screens.start;

import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.MainGame;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIImage;

public class SelectPlayerScreen extends BaseScreen {

    public SelectPlayerScreen() {
        super();
    }

    public static void loadingAsset() {
        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
    }

    @Override
    protected void createScreen() {
        createBackground();
        createCloseButton(ScreenType.MENU_GAME);
    }
    private void createBackground() {
        String namePopup = "popup_000";
        TextureRegion skill = MainGame.getAsM().getRegion(UI_WOOD, "popup_000");
        UIImage popup = new UIImage(skill).name(namePopup).parent(rootGroup).bounds(0, 0, screenWidth, screenHeight).debug(false);
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
