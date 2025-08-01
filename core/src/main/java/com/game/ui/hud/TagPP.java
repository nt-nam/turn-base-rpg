package com.game.ui.hud;

import static com.game.utils.Constants.BMF;

import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.Constants;

public class TagPP {
    public static UIGroup pp(float x, float y, float w, float h, String text) {
        UIGroup popup = new UIGroup().pos(x,y).size(w, h).debug(false);
        new UIImage(MainGame.getAsM().getRegion9patch(Constants.UI_POPUP, "tile_origin", 20)).size(popup.getWidth(), popup.getHeight()).parent(popup);
        new UILabel(text,BMF).name("label").bounds(popup.getWidth() * 0.1f, popup.getHeight() * 0.1f, popup.getWidth() * 0.8f, popup.getHeight() * 0.8f).warp(true).parent(popup);
        return popup;
    }
}
