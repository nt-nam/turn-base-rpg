package com.game.ui.widget;

import static com.game.utils.Constants.DAILY_REWARD_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.ui.hud.NotificationPP;
import com.game.utils.JsonHelper;
//import com.game.utils.JsonValueHelper;

import java.util.Objects;

public class DailyPP {
    public static Group pp(float w, float h) {
        UIGroup popup = new UIGroup().name("checkin").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.15f, h * 0.05f, w * 0.7f, h * 0.9f);

        JsonValue dailyRewards = JsonHelper.getJsonValue(DAILY_REWARD_JSON).get("daily_rewards");

        float size = h/6.2f;
        float margin = size * 0.3f;

        UITable table = new UITable().name("table").size(size * 7, size * 5).pos(w * 0.2f, h * 0.1f);

        for (int i = 0; i < 30; i++) {
            UIGroup uiGroup;
            if (dailyRewards != null && i < dailyRewards.size) {
                JsonValue item = dailyRewards.get(i);
                uiGroup = new UIGroup().name(item.get("id").asString()).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, item.get("typereward").asString()))
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin*0.7f),
                    new UILabel(item.get("number").asString())
                        .pos(margin * 0.4f, margin * 0.2f)
                ).onClick(() -> {
                    popup.addActor(NotificationPP.ppr(w,h,"You get "+item.get("number").asString()+" "+item.get("typereward").asString()));
                    item.get("confirm").set(true);
                });
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
}
