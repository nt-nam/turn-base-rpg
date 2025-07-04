package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.EQUIP_JSON;
import static com.game.utils.Constants.ITEM_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UITable;
import com.game.utils.data.JsonLoader;

public class ShopPP {
    public static JsonValue useFor = null;
    private static int page = 0;

    public static Group pp(float w, float h) {
        //data
        JsonValue items = JsonLoader.getJsonValue(ITEM_JSON, false);
        JsonValue equips = JsonLoader.getJsonValue(EQUIP_JSON, false).get("items");
        useFor = items;
        float size = h * 0.1f;

        UIGroup popup = new UIGroup().name("shop").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.08f, h * 0.08f, w * 0.84f, h * 0.84f);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP,"btn_up"))
            .size(size,size)
            .pos(w*0.84f,h*0.62f)
            .onClick(()->{
                page = page != 0? page-1:0;
                updateGrid(popup, w, h);
            })
            .parent(popup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP,"btn_down"))
            .size(size,size)
            .pos(w*0.84f,h*0.12f)
            .onClick(()->{
                if(useFor.get((page+1)*21)!= null){
                    page++;
                    updateGrid(popup, w, h);
                }
            })
            .parent(popup);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnItem = new UIButton("Item", gray, green)
            .bounds((int) (w * 0.1f), (int) (h * 0.75f), w * 0.12f, h * 0.12f)
            .name("btnItem")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(false);
                ((UIButton) popup.findActor("btnItem")).setChecked(true);
                useFor = items;
                page = 0;
                updateGrid(popup, w, h);
            })
            .check(true)
            .fontScale(2).parent(popup);

        UIButton btnEquip = new UIButton("Equip", gray, green)
            .bounds((int) (w * 0.25f), (int) (h * 0.75f), w * 0.12f, h * 0.12f)
            .name("btnEquip")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(true);
                ((UIButton) popup.findActor("btnItem")).setChecked(false);
                useFor = equips;
                page = 0;
                updateGrid(popup, w, h);
            })
            .check(false)
            .fontScale(2).parent(popup);

        updateGrid(popup, w, h);

        return popup;
    }

    private static void updateGrid(UIGroup popup, float w, float h) {
        float size = w * 0.1f;
        float margin = size * 0.2f;

        UITable table = new UITable().name("table").size(size * 7, size * 3).pos(w * 0.12f, h * 0.12f);

        for (int i = 0; i < 21; i++) {
            int id = page * 21 + i;
            UIGroup uiGroup;
            if (id < useFor.size) {
                JsonValue item = useFor.get(id);
                uiGroup = new UIGroup().name(item.get("id").asString()).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.get("id").asString()))
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

            if ((i + 1) % 7 == 0) {
                table.row();
            }
        }
        popup.addActor(table);
    }


}
