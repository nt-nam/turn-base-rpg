package com.game.screens.start;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.OverlayUI;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.DataHelper;
import com.game.models.entity.Account;
import com.game.models.entity.CharacterBase;

import java.util.List;

public class MenuScreen extends BaseScreen {


    public MenuScreen() {
        super();
        createScreen();
    }

    @Override
    protected void createScreen() {
        DataHelper.loadCharacterBaseList();

        new UIImage(MainGame.getAsM().getTexture("texture/default.png")).size(screenWidth * 0.5f, screenHeight).parent(rootGroup);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");

        UITable table1 = new UITable()
            .name("tbMenu")
            .size(screenWidth * 0.3f, screenHeight)
            .pos(screenWidth * 0.55f, 0)
            .al(Align.center)
            .child(
                new UIButton("Chơi mới", green).fontScale(1.5f).onClick(() -> checkNew()),
                new UIButton("Chơi tiếp", green).fontScale(1.5f).onClick(() -> MainGame.getScM().showScreen(ScreenType.SELECT_PLAYER)),
                new UIButton("Thoát game", green).fontScale(1.5f).onClick(() -> Gdx.app.exit())
            ).padChildren(20)
            .sizeChildren(screenWidth * 0.3f, screenHeight * 0.2f);

        rootGroup.addActor(table1);
    }

    private void checkNew() {
        List<Account> lits = DataHelper.loadAccountList(true);
        if(lits == null ||lits.size() <5){
            MainGame.getScM().showScreen(ScreenType.NEW_PLAYER);
        }else {
            createPopupNotification();
        }
    }

    private void createPopupNotification() {
        UIGroup a = new UIGroup().name("popupDelete").size(screenWidth,screenHeight).parent(rootGroup);
        UIButton btnYes = new UIButton("OK", MainGame.getAsM().getRegion(UI_POPUP, "btn_green"));

        btnYes.check(() -> {
            a.remove();
        });

        a.child(
            OverlayUI.overlay(a),
            new UIImage(MainGame.getAsM().get9p()).bounds(screenWidth * 0.2f, screenHeight * 0.2f, screenWidth * 0.6f, screenHeight * 0.6f),
            new UILabel("Cảnh báo xóa!!!", BMF).pos(screenWidth * 0.28f, screenHeight * 0.65f).fontScale(2),
            new UILabel("   Số lượng tài khoản của bạn đã đạt 5, không thể tạo mới.\n    Bạn có thể xóa tài khoản cũ để tiếp tục.", BMF).bounds(screenWidth * 0.3f, screenHeight * 0.4f, screenWidth * 0.4f, screenHeight * 0.2f).warp(true),
            btnYes.bounds(screenWidth * 0.32f, screenHeight * 0.25f, screenWidth * 0.15f, screenHeight * 0.12f)
        );
    }

    @Override
    public void show() {
        super.show();
        Gdx.app.log("MenuScreen", "show() called");
        stage.addActor(rootGroup);
    }

    @Override
    public void dispose() {
        Gdx.app.log("MenuScreen", "dispose() called");
        super.dispose();
    }

    public static void loadingAsset() {
        MainGame.getAsM().loadAtlas(UI_POPUP);
        MainGame.getAsM().loadFont(BMF);
        MainGame.getAsM().load("texture/default.png", Texture.class);
        MainGame.getAsM().load("texture/default2.png", Texture.class);
    }

}
