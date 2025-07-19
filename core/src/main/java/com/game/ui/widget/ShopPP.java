package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.JsonHelper;
import com.game.utils.json.EquipBase;
import com.game.utils.json.ItemBase;

import java.util.List;

public class ShopPP {
    private static UIGroup popup;
    private static List<?> useFor = null;
    private static int page = 0;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static Group pp(float w, float h) {
        float size = h * 0.1f;
        useFor = JsonHelper.loadItemBaseList(true);
        popup = new UIGroup().name("shop").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.08f, h * 0.08f, w * 0.84f, h * 0.84f);

        btnRedirect(popup, w, h, size);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnItem = new UIButton("Vật phẩm", gray, green)
            .bounds((int) (w * 0.1f), (int) (h * 0.75f), w * 0.12f, h * 0.12f)
            .name("btnItem")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(false);
                ((UIButton) popup.findActor("btnItem")).setChecked(true);
                useFor = JsonHelper.loadItemBaseList(true);
                page = 0;
                updateGrid(popup, w, h);
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnEquip = new UIButton("Trang bị", gray, green)
            .bounds((int) (w * 0.25f), (int) (h * 0.75f), w * 0.12f, h * 0.12f)
            .name("btnEquip")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(true);
                ((UIButton) popup.findActor("btnItem")).setChecked(false);
                useFor = JsonHelper.loadEquipBaseList(true);
                page = 0;
                updateGrid(popup, w, h);
            })
            .check(false)
            .fontScale(1.2f).parent(popup);

        new UIGroup().name("coin").pos(w * 0.6f, h * 0.75f).size(w * 0.15f, h * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "origin"), 20, 20, 20, 20)).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "coin")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.1f),
            new UILabel("100", BMF).pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f)
        ).parent(popup);

        new UIGroup().name("gem").pos(w * 0.75f, h * 0.75f).size(w * 0.15f, h * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "origin"), 20, 20, 20, 20)).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.10f),
            new UILabel("100", BMF).pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f)
        ).parent(popup);

        updateGrid(popup, w, h);

        return popup;
    }

    private static void btnRedirect(UIGroup popup, float w, float h, float size) {
        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_up"))
            .size(size, size)
            .pos(w * 0.84f, h * 0.62f)
            .onClick(() -> {
                page = page != 0 ? page - 1 : 0;
                updateGrid(popup, w, h);
            })
            .parent(popup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_down"))
            .size(size, size)
            .pos(w * 0.84f, h * 0.12f)
            .onClick(() -> {
                if ((page + 1) * 21 < useFor.size()) {
                    page++;
                    updateGrid(popup, w, h);
                }
            })
            .parent(popup);
    }

    private static void updateGrid(UIGroup popup, float w, float h) {
        float size = h * 0.2f;
        float margin = size * 0.2f;

        UITable table = new UITable().name("table").size(size * 7, size * 3).pos(w * 0.12f, h * 0.12f);

        for (int i = 0; i < 21; i++) {
            int id = page * 21 + i;
            UIGroup uiGroup;
            if (id < useFor.size()) {

                Object obj = useFor.get(id);
                String idString = "";
                if (obj instanceof ItemBase) {
                    idString = ((ItemBase) obj).nameRegion;
                } else if (obj instanceof EquipBase) {
                    idString = ((EquipBase) obj).nameRegion;
                }


                uiGroup = new UIGroup().name(idString).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "select")).visible(false).name("select")
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, idString))
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.5f)
                );

                uiGroup.onClick(() -> {
                    uiGroup.findActor("select").setVisible(!uiGroup.findActor("select").isVisible());
                    if (uiGroup.findActor("select").isVisible()) {

                    }
                    showItemDetail();
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

    private static void showItemDetail() {

    }


    public static void update() {
    }
}
