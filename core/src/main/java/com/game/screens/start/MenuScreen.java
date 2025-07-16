package com.game.screens.start;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.CHARACTER_BASE_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UITable;
import com.game.utils.JsonHelper;
import com.game.utils.json.CharacterBase;

import java.util.List;

public class MenuScreen extends BaseScreen {


    public MenuScreen() {
        super();
        createScreen();
    }

    @Override
    public void createScreen() {
        List<CharacterBase> characterBaseList = JsonHelper.loadCharacterBaseList(CHARACTER_BASE_JSON, true);
//        List<ItemBase> items = JsonHelper.loadItems(ITEM_JSON, true);
//        List<EquipBase> equips = JsonHelper.loadEquips(EQUIP_JSON, true);
//        List<SkillBase> skillBase = JsonHelper.loadSkillBase(SKILL_JSON,true);
//        System.out.println(skillBase.toString());

//        List<Lineup> lineups = JsonHelper.loadLineups(LINEUP_ATTACK, true);
//        List<Hero> heroes = JsonHelper.loadFullHero(PARTY_FULL, true);

        new UIImage(MainGame.getAsM().getTexture("texture/default.png")).size(screenWidth*0.5f, screenHeight).parent(rootGroup);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");

        UITable table1 = new UITable()
            .name("tbMenu")
            .size(screenWidth*0.3f, screenHeight)
            .pos(screenWidth*0.55f, 0)
            .al(Align.center)
            .child(
                new UIButton("Chơi mới",green).fontScale(1.5f).onClick(() -> MainGame.getScM().showScreen(ScreenType.NEW_PLAYER)),
                new UIButton("Chơi tiếp",green).fontScale(1.5f).onClick(() -> MainGame.getScM().showScreen(ScreenType.SELECT_PLAYER)),
                new UIButton("Thoát game",green).fontScale(1.5f).onClick(() -> Gdx.app.exit())
            ).padChildren(20)
            .sizeChildren(screenWidth*0.3f,screenHeight*0.2f);

        rootGroup.addActor(table1);
    }



    @Override
    public void show() {
        super.show();
        Gdx.app.log("MenuScreen", "show() called");
        stage.addActor(rootGroup);
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
        MainGame.getAsM().loadAtlas(UI_POPUP);
        MainGame.getAsM().load("texture/default.png", Texture.class);
        MainGame.getAsM().load("texture/default2.png", Texture.class);
        MainGame.getAsM().load("music/music_demo.mp3", Music.class);
        MainGame.getAsM().loadFont(BMF);
    }

}
