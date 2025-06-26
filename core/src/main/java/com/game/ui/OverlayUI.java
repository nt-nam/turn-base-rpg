package com.game.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.ui.base.UIImage;

public class OverlayUI {
    public static UIImage overlay(Group rootGroup){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        return new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, rootGroup.getWidth(), rootGroup.getHeight());
    }
}
