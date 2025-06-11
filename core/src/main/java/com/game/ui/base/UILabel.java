package com.game.ui.base;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.MainGame;

public class UILabel extends Label {

    public UILabel(String text) {
        super(text, MainGame.getAsM().getSkin());
    }

    public UILabel name(String name) {
        this.setName(name);
        return this;
    }

    public UILabel pos(float x, float y) {
        this.setPosition(x, y);
        return this;
    }

    public UILabel fontScale(float scale) {
        this.setFontScale(scale);
        return this;
    }

    public UILabel size(float w, float h) {
        this.setSize(w, h);
        return this;
    }

    public UILabel align(int align) {
        this.setAlignment(align);
        return this;
    }
}
