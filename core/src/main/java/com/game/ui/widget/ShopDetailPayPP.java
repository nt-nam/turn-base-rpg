package com.game.ui.widget;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.ui.base.UIGroup;

public class ShopDetailPayPP {
    private static UIGroup popup;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static Group pp(float w, float h) {
        popup = new UIGroup();
        popup.name("itemDetailPay").setSize(w, h);

        return popup;
    }
}
