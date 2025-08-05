package com.game.ui.widget;

import static com.game.utils.Constants.ACHIEVEMENT_JSON;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.DAILY_REWARD_JSON;
import static com.game.utils.Constants.EQUIPS_JSON;
import static com.game.utils.Constants.HERO_FULL;
import static com.game.utils.Constants.INFO_JSON;
import static com.game.utils.Constants.ITEMS_JSON;
import static com.game.utils.Constants.LINEUP_ATTACK;
import static com.game.utils.Constants.MAININFO_JSON_LOCAL;
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
import com.game.utils.DataHelper;
import com.game.utils.GameSession;
import com.game.utils.JsonSaver;
import com.game.utils.json.Account;

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
            .check(GameSession.profile.playMusic)
            .fontScale(0.75f)
            .parent(popup);

        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_sound"))
            .pos(w * 0.41f, h * 0.7f)
            .scale(1.8f)
            .parent(popup);
        new UIButton("", off, on, true)
            .bounds(w * 0.47f, h * 0.7f, w * 0.12f, h * 0.09f)
            .name("btnSound")
            .check(GameSession.profile.playSound)
            .fontScale(0.75f)
            .parent(popup);

        return popup;
    }

    private static void saveData() {
        JsonSaver.saveObject(INFO_JSON, GameSession.profile);
        JsonSaver.saveObject(ACHIEVEMENT_JSON, GameSession.achievementList);
        JsonSaver.saveObject(DAILY_REWARD_JSON, GameSession.dailyRewardList);
        JsonSaver.saveObject(EQUIPS_JSON, GameSession.equipList);
        JsonSaver.saveObject(HERO_FULL, GameSession.heroList);
        JsonSaver.saveObject(ITEMS_JSON, GameSession.itemList);
        JsonSaver.saveObject(LINEUP_ATTACK, GameSession.lineupList);
        JsonSaver.saveObject(MISSION_JSON, GameSession.missionList);
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
            for (Account account : GameSession.accountList) {
                if (account.id.equals( GameSession.profile.name)) {
                    remove = account;
                }
            }
            GameSession.accountList.remove(remove);
            JsonSaver.saveObject(MAININFO_JSON_LOCAL, GameSession.accountList);
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
