package com.game.ui.widget;

import static com.game.utils.Constants.DAILY_REWARD_JSON;
import static com.game.utils.Constants.INFO_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.ui.hud.NotificationPP;
import com.game.utils.GameSession;
import com.game.utils.DataHelper;
import com.game.utils.JsonSaver;
import com.game.utils.json.DailyReward;

import java.time.LocalDate;
import java.util.List;

public class DailyPP {
    public static UIGroup popup;
    private static List<DailyReward> dailyRewardList;

    public static void show(boolean b) {
        dailyRewardList = DataHelper.loadDailyRewardList(true);
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        popup = new UIGroup().name("checkin").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.15f, h * 0.05f, w * 0.7f, h * 0.9f);
        dailyRewardList = DataHelper.loadDailyRewardList(true);

        float size = h / 6.2f;
        float margin = size * 0.3f;

        UITable table = new UITable().name("table").size(size * 7, size * 5).pos(w * 0.2f, h * 0.1f);
        boolean flag = false;
        for (int i = 0; i < 30; i++) {
            UIGroup uiGroup;
            if (dailyRewardList != null && i < dailyRewardList.size()) {
                DailyReward item = dailyRewardList.get(i);
                uiGroup = new UIGroup().name(item.id + "").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, item.typereward))
                        .name("icon")
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.7f),
                    new UILabel(item.number + "").name("label")
                        .pos(margin * 0.4f, margin * 0.2f)
                );
                if (!item.confirm && !flag) {
                    flag = true;
                    uiGroup.onClick(() -> {
                        if (GameSession.profile.getDailyCheck().equals(LocalDate.now())) {
                            popup.addActor(NotificationPP.ppr(w, h, "Bạn đã điểm danh cho ngày hôm nay"));
                        } else {
                            popup.addActor(NotificationPP.ppr(w, h, "Điểm danh thành công"));
                            item.confirm = true;
                            uiGroup.findActor("icon").setColor(Color.DARK_GRAY);
                            uiGroup.findActor("label").setColor(Color.DARK_GRAY);
                            GameSession.profile.dailyCheck = LocalDate.now().toString();
                            update();
                        }
                    });
                }

                if (item.confirm) {
                    uiGroup.findActor("icon").setColor(Color.DARK_GRAY);
                    uiGroup.findActor("label").setColor(Color.DARK_GRAY);
                }
            } else {
                uiGroup = new UIGroup().name("empty").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size));
            }


            table.add(uiGroup).size(size, size);

            if ((i + 1) % 7 == 0) {
                table.row();
            }
        }
        popup.addActor(table);
        return popup;
    }

    public static void update() {
        JsonSaver.saveObject(DAILY_REWARD_JSON, dailyRewardList);
        JsonSaver.saveObject(INFO_JSON, GameSession.profile);
    }
}
