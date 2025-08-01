package com.game.ui.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;

public class UIImage extends Image {

    public UIImage(String path) {
        super(MainGame.getAsM().getTexture(path));
    }

    public UIImage(TextureRegion region) {
        super(region);
    }

    public UIImage(NinePatch ninePatch) {
        super(ninePatch);
    }

    public UIImage(Texture texture) {
        super(texture);
    }

    public UIImage(String atlasPath, String regionName) {
        super(MainGame.getAsM().getRegion(atlasPath, regionName));
    }

    public UIImage name(String name) {
        this.setName(name);
        return this;
    }

    public UIImage bounds(float x, float y, float w, float h) {
        this.setBounds(x, y, w, h);
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


    public UIImage align(int align) {
        this.setAlign(align);
        return this;
    }


    public UIImage parent(Group rootGroup) {
        rootGroup.addActor(this);
        return this;
    }

    public UIImage debug(boolean b) {
        this.setDebug(b);
        return this;
    }

    public UIImage nine(TextureRegion region, int i, int i1, int i2, int i3) {
        this.setDrawable(new NinePatchDrawable(new NinePatch(region, i, i1, i2, i3)));
        return this;
    }

    public UIImage pos(float x, float y, int align) {
        this.origin(align);
        this.setPosition(x, y);
        return this;
    }

    public UIImage visible(boolean b) {
        this.setVisible(b);
        return this;
    }

    public UIImage color(Color color) {
        this.setColor(color);
        return this;
    }

    public UIImage color(String color) {
        this.setColor(Color.valueOf(color));
        return this;
    }

    public UIImage origin(int alignment) {
        this.setOrigin(alignment);
        return this;
    }

    public UIImage action(Action action) {
        this.addAction(action);
        return this;
    }

}
