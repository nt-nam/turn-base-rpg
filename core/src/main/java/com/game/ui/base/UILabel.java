package com.game.ui.base;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.MainGame;

public class UILabel extends Label {

    public UILabel(String text) {
        super(text, MainGame.getAsM().getSkin());
    }

    public UILabel(String text, BitmapFont font) {
        super(text, createLabelStyle(font));
    }
    public UILabel(String text,String pathFont) {

        super(text, createLabelStyle(MainGame.getAsM().getFont(pathFont)));
    }

    private static LabelStyle createLabelStyle(BitmapFont font) {
        LabelStyle style = new LabelStyle();
        style.font = font;
        return style;
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

    public UILabel parent(Group parent) {
        parent.addActor(this);
        return this;
    }

    public UILabel debug(boolean b) {
        setDebug(b);
        return this;
    }

    public UILabel font(String path) {
        this.getStyle().font = MainGame.getAsM().getFont(path);
        return this;
    }


    public UILabel bounds(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
        return this;
    }

    public UILabel warp(boolean b) {
        setWrap(b);
        return this;
    }
}
