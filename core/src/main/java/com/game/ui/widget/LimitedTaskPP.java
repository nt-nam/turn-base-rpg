package com.game.ui.widget;

import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;

public class LimitedTaskPP {
    public  static UIGroup popup;
    public static void show(boolean b) {
        popup.setVisible(b);
    }
    public static Group pp(float w, float h){
        UIGroup popup = new UIGroup().name("limitedtask").size(w,h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.08f, h * 0.08f, w * 0.84f, h * 0.84f);

//        JsonValue items = JsonLoader.getJsonValue(ITEM_JSON, false);
//        JsonValue equip = JsonLoader.getJsonValue(EQUIP_JSON, false).get("items");
//
//        JsonValue useFor = items;
//
//        float size = w * 0.1f;
//        float margin = size * 0.2f;
//
//        UITable table = new UITable().name("table").size(size * 5, size * 3).pos(w * 0.43f, h * 0.12f);
//
//        for (int i = 0; i < 15; i++) {
//            UIGroup uiGroup;
//            if (i < items.size) {
//                JsonValue item = items.get(i);
//                uiGroup = new UIGroup().name(item.get("id").asString()).child(
//                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
//                        .size(size, size),
//                    new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.get("id").asString()))
//                        .size(size - margin, size - margin)
//                        .pos(margin * 0.5f, margin * 0.5f)
//                ).onClick(() -> {
//                    // Logic xử lý khi click vào item
//                });
//            } else {
//                uiGroup = new UIGroup().name("empty").child(
//                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
//                        .size(size, size));
//            }
//
//
//            table.add(uiGroup).size(size, size);
//
//            if ((i + 1) % 5 == 0) {
//                table.row();
//            }
//        }
//        popup.addActor(table);

        return popup;
    }
}
