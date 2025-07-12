package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;

public class RecruitPP {
    private static UIGroup hiddenCard;
    public static UIGroup pp(float w, float h) {

        NinePatch ninePatch = MainGame.getAsM().getRegion9patch(UI_POPUP, "origin", 20);
        hiddenCard = new UIGroup().name("recruit").pos(w*0.4f,h*0.2f).size(w * 0.2f, h * 0.6f);
        new UIImage(ninePatch).size(w * 0.2f, h * 0.6f).parent(hiddenCard);
        new UILabel("?",BMF).pos(w * 0.03f,h * 0.3f).fontScale(12).parent(hiddenCard);

        return hiddenCard;
    }
}
