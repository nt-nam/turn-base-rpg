package com.game.ui.widget;

import static com.game.utils.Constants.DAILY_REWARD_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.ui.hud.NotificationPP;
import com.game.utils.GameSession;
import com.game.utils.JsonHelper;
import com.game.utils.JsonSaver;
import com.game.utils.json.DailyReward;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class DailyPP {
    public static UIGroup popup;
    private static List<DailyReward> dailyRewardList;

    public static void show(boolean b) {
        dailyRewardList = JsonHelper.loadDailyRewardList(true);
        popup.setVisible(b);
    }

    public static Group pp(float w, float h) {
        popup = new UIGroup().name("checkin").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.15f, h * 0.05f, w * 0.7f, h * 0.9f);
        dailyRewardList = JsonHelper.loadDailyRewardList(true);

//        JsonValue dailyRewards = JsonHelper.getJsonValue(DAILY_REWARD_JSON).get("daily_rewards");
//        List<DailyReward> dailyRewardList = JsonHelper.loadDailyRewards(DAILY_REWARD_JSON);

        float size = h/6.2f;
        float margin = size * 0.3f;

        UITable table = new UITable().name("table").size(size * 7, size * 5).pos(w * 0.2f, h * 0.1f);

        for (int i = 0; i < 30; i++) {
            UIGroup uiGroup;
            if (dailyRewardList != null && i < dailyRewardList.size()) {
                DailyReward item = dailyRewardList.get(i);
                uiGroup = new UIGroup().name(item.id+"").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, item.typereward))
                        .name("icon")
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin*0.7f),
                    new UILabel(item.number+"")
                        .pos(margin * 0.4f, margin * 0.2f)
                );
                uiGroup.onClick(() -> {
                    if(GameSession.profile.getDailyCheck() == LocalDate.now())
                    popup.addActor(NotificationPP.ppr(w,h,"You get "+item.number+" "+item.typereward));
                    item.confirm = true;
                    update();
                });
                if (item.confirm) {
                    uiGroup.findActor("icon").setColor(Color.GRAY);
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
    }
}
