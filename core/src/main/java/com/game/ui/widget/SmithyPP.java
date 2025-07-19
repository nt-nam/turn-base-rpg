package com.game.ui.widget;

import com.game.ui.base.UIGroup;

public class SmithyPP {
    private static UIGroup popup;
    public static void show(boolean b) {
        popup.setVisible(b);
    }
    public static UIGroup pp(float w, float h) {
        popup = new UIGroup().name("smithy").size(w, h);


        return popup;
    }
}
