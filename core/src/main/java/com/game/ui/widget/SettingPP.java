package com.game.ui.widget;

import com.game.utils.Constants;


import static com.game.utils.Constants.BMF;






import static com.game.utils.Constants.MAININFO_JSON_LOCAL;

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
import com.game.utils.DataHelper;
import com.game.managers.GameSessionManager;
import com.game.utils.JsonSaver;
import com.game.models.entity.Account;

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
        TextureRegion on = MainGame.getAsM().getRegion(UI_POPUP, "btn_on");
        TextureRegion off = MainGame.getAsM().getRegion(UI_POPUP, "btn_off");
        UIButton btnChangeAcc = new UIButton("Đổi tài khoản", gray)
            .bounds((int) (w * 0.33f), (int) (h * 0.3f), w * 0.15f, h * 0.15f)
            .name("btnChangeAcc")
            .check(() -> {
                saveData();
                MainGame.getScM().clearScreenCache();
                showMenu();
                clearDataSession();
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnExitGame = new UIButton("Thoát game", red)
            .bounds((int) (w * 0.52f), (int) (h * 0.3f), w * 0.15f, h * 0.15f)
            .name("btnExitGame")
            .check(() -> {
                saveData();
                Gdx.app.exit();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);

        UIButton btnDelete = new UIButton("Xóa tài khoản", gray)
            .bounds((int) (w * 0.5f), (int) (h * 0.15f), w * 0.12f, h * 0.09f)
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

        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_music"))
            .pos(w * 0.41f, h * 0.55f)
            .scale(1.8f)
            .parent(popup);
        new UIButton("", off, on, true)
            .bounds(w * 0.47f, h * 0.55f, w * 0.12f, h * 0.09f)
            .name("btnMusic")
            .check(GameSessionManager.getInstance().profile.playMusic)
            .fontScale(0.75f)
            .parent(popup);

        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_sound"))
            .pos(w * 0.41f, h * 0.7f)
            .scale(1.8f)
            .parent(popup);
        new UIButton("", off, on, true)
            .bounds(w * 0.47f, h * 0.7f, w * 0.12f, h * 0.09f)
            .name("btnSound")
            .check(GameSessionManager.getInstance().profile.playSound)
            .fontScale(0.75f)
            .parent(popup);

        return popup;
    }

    private static void saveData() {
        JsonSaver.saveObject(Constants.playerPath("info.json"), GameSessionManager.getInstance().profile);
        JsonSaver.saveObject(Constants.playerPath("achievement.json"), GameSessionManager.getInstance().achievementList);
        JsonSaver.saveObject(Constants.playerPath("daily_rewards.json"), GameSessionManager.getInstance().dailyRewardList);
        JsonSaver.saveObject(Constants.playerPath("equips.json"), GameSessionManager.getInstance().equipList);
        JsonSaver.saveObject(Constants.playerPath("hero_full.json"), GameSessionManager.getInstance().heroList);
        JsonSaver.saveObject(Constants.playerPath("items.json"), GameSessionManager.getInstance().itemList);
        JsonSaver.saveObject(Constants.playerPath("lineup.json"), GameSessionManager.getInstance().lineupList);
        JsonSaver.saveObject(Constants.playerPath("mission.json"), GameSessionManager.getInstance().missionList);
    }

    private static void createPopupDelete() {
        float width = popup.getWidth();
        float height = popup.getHeight();
        if (popup.findActor("popupDelete") != null) {
            popup.findActor("popupDelete").setVisible(true);
            return;
        }
        UIGroup a = new UIGroup().name("popupDelete").size(width, height).parent(popup);
        UIButton btnYes = new UIButton("Xác nhận", MainGame.getAsM().getRegion(UI_POPUP, "btn_green"));
        UIButton btnNo = new UIButton("Hủy", MainGame.getAsM().getRegion(UI_POPUP, "btn_red"));

        btnYes.check(() -> {
            JsonSaver.removeAccount();
            DataHelper.loadAccountList(true);
            Account remove = null;
            for (Account account : GameSessionManager.getInstance().accountList) {
                if (account.id.equals( GameSessionManager.getInstance().profile.name)) {
                    remove = account;
                }
            }
            GameSessionManager.getInstance().accountList.remove(remove);
            JsonSaver.saveObject(MAININFO_JSON_LOCAL, GameSessionManager.getInstance().accountList);
            clearDataSession();
            showMenu();
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

    private static void showMenu() {
        MainGame.getScM().removeScreen(ScreenType.WORLD_MAP);
        MainGame.getScM().showScreen(ScreenType.MENU_GAME);
    }

    private static void clearDataSession() {
        DataHelper.clearDataProfile();
    }
}
