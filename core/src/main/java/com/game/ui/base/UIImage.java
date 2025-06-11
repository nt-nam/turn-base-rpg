package com.game.ui.base;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;

public class UIImage extends Image {

    public UIImage(String path) {
        super(MainGame.getAsM().getTexture(path));
    }

    public UIImage name(String name) {
        this.setName(name);
        return this;
    }

    public UIImage pos(float x, float y) {
        this.setPosition(x, y);
        return this;
    }

    public UIImage size(float w, float h) {
        this.setSize(w, h);
        return this;
    }

    public UIImage scale(float scale) {
        this.setScale(scale);
        return this;
    }

    public UIImage origin(int alignment) {
        this.setOrigin(alignment);
        return this;
    }
    public UIImage originCenter() {
        this.setOrigin(Align.center);
        return this;
    }

    public UIImage align(int align) {
        this.setAlign(align);
        return this;
    }
}
