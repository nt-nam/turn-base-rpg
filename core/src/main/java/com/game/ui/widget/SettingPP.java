package com.game.ui.widget;

import static com.game.utils.Constants.ACHIEVEMENT_JSON;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.DAILY_REWARD_JSON;
import static com.game.utils.Constants.EQUIPS_JSON;
import static com.game.utils.Constants.HERO_FULL;
import static com.game.utils.Constants.INFO_JSON;
import static com.game.utils.Constants.ITEMS_JSON;
import static com.game.utils.Constants.LINEUP_ATTACK;
import static com.game.utils.Constants.MISSION_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.screens.ScreenType;
import com.game.ui.OverlayUI;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.GameSession;
import com.game.utils.JsonSaver;

public class SettingPP {
    private static UIGroup popup;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        popup = new UIGroup().name("setting").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
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
                MainGame.getScM().clearScreenCache();
                MainGame.getScM().showScreen(ScreenType.MENU_GAME);
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnEquip = new UIButton("Thoát game", red)
            .bounds((int) (w * 0.4f), (int) (h * 0.3f), w * 0.2f, h * 0.15f)
            .name("btnExitGame")
            .check(() -> {
                JsonSaver.saveObject(INFO_JSON, GameSession.profile);
                JsonSaver.saveObject(ACHIEVEMENT_JSON, GameSession.achievementList);
                JsonSaver.saveObject(DAILY_REWARD_JSON, GameSession.dailyRewardList);
                JsonSaver.saveObject(EQUIPS_JSON, GameSession.equipList);
                JsonSaver.saveObject(HERO_FULL, GameSession.heroList);
                JsonSaver.saveObject(ITEMS_JSON, GameSession.itemList);
                JsonSaver.saveObject(LINEUP_ATTACK, GameSession.lineupList);
                JsonSaver.saveObject(MISSION_JSON, GameSession.missionList);
                Gdx.app.exit();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);

        UIButton btnDelete = new UIButton("Xóa tài khoản", gray)
            .bounds((int) (w * 0.32f), (int) (h * 0.75f), w * 0.12f, h * 0.09f)
            .name("btnDeleteAcc")
            .check(() -> {
                Group parent = popup.getParent();
                parent.findActor("closeBtn").setVisible(false);
                createPopupDelete();
            })
            .check(true)
            .fontScale(0.75f)
            .parent(popup);
        btnDelete.setColor(Color.RED);

        return popup;
    }

    private static void createPopupDelete() {
        float width = popup.getWidth();
        float height = popup.getHeight();
        if(popup.findActor("popupDelete") != null){
            popup.findActor("popupDelete").setVisible(true);
            return;
        }
        UIGroup a = new UIGroup().name("popupDelete").size(width, height).parent(popup);
        UIButton btnYes = new UIButton("Xác nhận", MainGame.getAsM().getRegion(UI_POPUP, "btn_green"));
        UIButton btnNo = new UIButton("Hủy", MainGame.getAsM().getRegion(UI_POPUP, "btn_red"));

        btnYes.check(() -> {
//                MainGame.getScM().clearScreenCache();
//                MainGame.getScM().showScreen(ScreenType.MENU_GAME);
        });
        btnNo.check(() -> {
            Group parent = popup.getParent();
            parent.findActor("closeBtn").setVisible(true);
            a.setVisible(false);
        });


        a.child(
            OverlayUI.overlay(a),
            new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.2f, height * 0.2f, width * 0.6f, height * 0.6f),
            new UILabel("Cảnh báo xóa!!!", BMF).pos(width * 0.28f, height * 0.65f).fontScale(2),
            new UILabel("Bạn sẽ mất toàn bộ dữ liệu từ tài khoản hiện tại và không thể khôi phục lại.", BMF).bounds(width * 0.3f, height * 0.4f, width * 0.4f, height * 0.2f).warp(true),
            btnYes.bounds(width * 0.32f, height * 0.25f, width * 0.15f, height * 0.12f),
            btnNo.bounds(width * 0.53f, height * 0.25f, width * 0.15f, height * 0.12f)
        );
    }
}
