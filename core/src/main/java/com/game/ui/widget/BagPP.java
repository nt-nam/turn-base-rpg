package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.WAREHOUSE_JSON;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.ecs.component.WarehouseComponent;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.JsonHelper;
import com.game.utils.JsonValueHelper;
import com.game.utils.json.Bag;

import java.util.List;

public class BagPP {
    public static Group pp(float w, float h){
        UIGroup popup = new UIGroup().name("bag").size(w,h);

        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(popup)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.4f, h * 0.05f, w * 0.6f, h * 0.9f);

        List<Bag> bags = JsonHelper.loadBags(WAREHOUSE_JSON,true);
        float size = h * 0.2f;
        float margin = size * 0.2f;

        UITable table = new UITable().name("table").size(size * 5, size * 3).pos(w * 0.43f, h * 0.12f);

        for (int i = 0; i < 15; i++) {
            UIGroup uiGroup;
            if (bags != null && i < bags.size()) {
                Bag item = bags.get(i);
                uiGroup = new UIGroup().name(item.id).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.id))
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.5f)
                ).onClick(() -> {
                    // Logic xử lý khi click vào item
                });
            } else {
                uiGroup = new UIGroup().name("empty").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size));
            }


            table.add(uiGroup).size(size, size);

            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }
        popup.addActor(table);
        popup.run(()->{

        });
        return popup;
    }
}
