package com.game.ui.base;

import static com.game.utils.Constants.*;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;

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
        TextureRegion barOut = MainGame.getAsM().getRegion(UI_POPUP, type);
        TextureRegion barIn = MainGame.getAsM().getRegion(UI_POPUP, "line_empty");

//        NinePatchDrawable bgDrawable = new NinePatchDrawable(MainGame.getAsM().getRegion9patch(UI_POPUP,type,3));
//        NinePatchDrawable fillDrawable = new NinePatchDrawable(MainGame.getAsM().getRegion9patch(UI_POPUP,"line_empty",15));

        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, type));
        TextureRegionDrawable fillDrawable = new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, "line_empty"));

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

    public UIProgressBar update(float value) {
        setValue(getValue() + value);
        return this;
    }

    public UIProgressBar animateTo(float value, float duration) {
        setAnimateDuration(duration);
        setValue(value);
        return this;
    }

    public UIProgressBar visible(boolean visible) {
        setVisible(visible);
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

    public UIProgressBar bounds(float v, float v1, float v2, float v3) {
        setBounds(v, v1, v2, v3);
        return this;
    }
}
