package com.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.MainGame;

public class PopupManager {
    private Group popupGroup;

    public PopupManager(Group popupGroup) {
        this.popupGroup = popupGroup;
    }

    public void showPopup(String text) {
        Window popup = new Window("Popup", MainGame.getAsM().getSkin());
        popup.add(new Label(text, MainGame.getAsM().getSkin())).pad(10);
        popup.pack();
        popup.setPosition(
            (MainGame.getStage().getWidth() - popup.getWidth()) / 2,
            (MainGame.getStage().getHeight() - popup.getHeight()) / 2
        );
        popupGroup.addActor(popup);
    }

    public void clearPopups() {
        popupGroup.clearChildren();
    }
}
