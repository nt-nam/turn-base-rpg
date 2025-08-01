package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.game.MainGame;
import com.game.ui.OverlayUI;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.json.EquipBase;


public class EquipDetailPP {
    private static UIGroup popup;
    private static EquipBase equipBase;
    private static int page = 0;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        float size = h * 0.1f;
        popup = new UIGroup().name("equipDetail").size(w, h);
        OverlayUI.overlay(popup);
        NinePatch n9p = MainGame.getAsM().get9p();

        new UIImage(n9p)
            .name("card")
            .parent(popup)
            .bounds(w * 0.08f, h * 0.08f, w * 0.84f, h * 0.84f);

        new UIGroup().name("coin").pos(w * 0.6f, h * 0.75f).size(w * 0.15f, h * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20)).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "coin")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.1f),
            new UILabel("100", BMF).pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f)
        ).parent(popup);

        new UIGroup().name("gem").pos(w * 0.75f, h * 0.75f).size(w * 0.15f, h * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20)).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.10f),
            new UILabel("100", BMF).pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f)
        ).parent(popup);

        return popup;
    }


    private static void showItemDetail() {

    }


    public static void update(EquipBase equip) {
    }
}
