package com.game.ui.hud;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.game.ui.base.UIGroup;

public class NotificationPP {
    public static UIGroup pp(float w, float h, String text) {
        UIGroup popup = TagPP.pp(w * 0.1f, h,w * 0.8f, h * 0.2f,text);
        if(!text.isEmpty()) {
            popup.addAction(Actions.sequence(
                Actions.moveBy(0,-h * 0.2f, 0.5f),
                Actions.delay(3f),
                Actions.moveBy(0,h * 0.2f, 0.5f)
            ));
        }
        return popup;
    }
    public static UIGroup ppr(float w, float h, String text) {
        UIGroup popup = TagPP.pp(w * 0.1f, h,w * 0.8f, h * 0.2f,text);
        if(!text.isEmpty()) {
            popup.addAction(Actions.sequence(
                Actions.moveBy(0,-h * 0.2f, 0.5f),
                Actions.delay(3f),
                Actions.moveBy(0,h * 0.2f, 0.5f),
                Actions.removeActor()
            ));
        }
        return popup;
    }
}
