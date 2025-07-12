package com.game.ui.widget;

import static com.game.utils.Constants.EQUIP_JSON;
import static com.game.utils.Constants.ITEM_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.utils.JsonHelper;

public class SettingPP {
    public static Group pp(float w, float h){
        UIGroup popup = new UIGroup().name("setting").size(w,h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.3f, h * 0.1f, w * 0.4f, h * 0.8f);


        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        TextureRegion red = MainGame.getAsM().getRegion(UI_POPUP, "btn_red");
        UIButton btnItem = new UIButton("Đổi tài khoản", gray)
            .bounds((int) (w * 0.4f), (int) (h * 0.55f), w * 0.2f, h * 0.15f)
            .name("btnChangeAcc")
            .check(() -> {
                //SaveAll();
                MainGame.getScM().showScreen(ScreenType.MENU_GAME);
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnEquip = new UIButton("Thoát game", red)
            .bounds((int) (w * 0.4f), (int) (h * 0.3f), w * 0.2f, h * 0.15f)
            .name("btnExitGame")
            .check(() -> {
                Gdx.app.exit();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);



        return popup;
    }
}
