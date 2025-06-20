package com.game.ui.base;

import static com.game.utils.Constants.*;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.utils.Constants;

public class UIProgressBar extends ProgressBar {

    public UIProgressBar(float min, float max, float stepSize, boolean vertical, Skin skin) {
        super(min, max, stepSize, vertical, skin);
        setValue(min);
    }

    public UIProgressBar(float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
        super(min, max, stepSize, vertical, style);
        setValue(min);
    }

    public UIProgressBar(float min, float max, float stepSize, boolean vertical, String type) {
        super(min, max, stepSize, vertical, MainGame.getAsM().getSkin());
        TextureRegion barOut = MainGame.getAsM().getRegion(UI_POPUP,type);
        TextureRegion barIn = MainGame.getAsM().getRegion(UI_POPUP, "line_empty");

        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(barOut);
        TextureRegionDrawable fillDrawable = new TextureRegionDrawable(barIn);

        ProgressBarStyle style = new ProgressBarStyle();
        style.background = bgDrawable;
        style.knobBefore = fillDrawable;

        setStyle(style);
        setValue(max);
    }

    public UIProgressBar min(float min) {
        setRange(min, getMaxValue());
        return this;
    }

    public UIProgressBar max(float max) {
        setRange(getMinValue(), max);
        return this;
    }

    public UIProgressBar value(float value) {
        setValue(value);
        return this;
    }

    public UIProgressBar animateTo(float value, float duration) {
        setAnimateDuration(duration);
        setValue(value);
        return this;
    }

    public UIProgressBar size(float w, float h) {
        setSize(w, h);
        return this;
    }

    public UIProgressBar pos(float x, float y) {
        setPosition(x, y);
        return this;
    }

    public UIProgressBar name(String name) {
        setName(name);
        return this;
    }

    public UIProgressBar origin(int alignment) {
        setOrigin(alignment);
        return this;
    }

    public UIProgressBar originCenter() {
        setOrigin(Align.center);
        return this;
    }

    public UIProgressBar parent(Group rootGroup) {
        rootGroup.addActor(this);
        return this;
    }

    public UIProgressBar debug(boolean b) {
        setDebug(b);
        return this;
    }
}
