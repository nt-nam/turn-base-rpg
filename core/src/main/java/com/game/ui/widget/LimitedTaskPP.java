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

        return popup;
    }
}
