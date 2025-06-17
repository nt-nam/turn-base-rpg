package com.game.ui.base;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;

public class UITextField extends TextField {
    private float textYOffset = 7;

    public UITextField(String text, TextureRegion bgRegion) {
        super(text, createStyle(bgRegion));
    }

    public UITextField(String text, TextureRegion bgRegion, TextureRegion cursorRegion) {
        super(text, createStyle(bgRegion, cursorRegion));
    }

    public UITextField textYOffset(float offset) {
        this.textYOffset = offset;
        return this;
    }

    private static TextFieldStyle createStyle(TextureRegion bgRegion) {
        TextFieldStyle style = new TextFieldStyle();
        style.background = new TextureRegionDrawable(bgRegion);
        style.font = MainGame.getAsM().getSkin().getFont("default-font");
        style.fontColor = MainGame.getAsM().getSkin().getColor("white");
        style.cursor = MainGame.getAsM().getSkin().getDrawable("cursor");
        style.selection = MainGame.getAsM().getSkin().getDrawable("selection");
        return style;
    }

    private static TextFieldStyle createStyle(TextureRegion bgRegion, TextureRegion cursorRegion) {
        TextFieldStyle style = createStyle(bgRegion);
        if(cursorRegion != null) {
            style.cursor = new TextureRegionDrawable(cursorRegion);
        }
        return style;
    }

    @Override
    protected void drawText(Batch batch, BitmapFont font, float x, float y) {
        // Hack: cộng thêm offset vào Y để text được đẩy lên (cursor và background không đổi)
        super.drawText(batch, font, x, y + textYOffset);
    }

    @Override
    protected void drawMessageText(Batch batch, BitmapFont font, float x, float y, float maxWidth) {
        super.drawMessageText(batch, font, x, y+textYOffset, maxWidth);
    }

    public UITextField name(String name) {
        this.setName(name);
        return this;
    }

    public UITextField pos(float x, float y) {
        this.setPosition(x, y);
        return this;
    }

    public UITextField size(float w, float h) {
        this.setSize(w, h);
        return this;
    }

    public UITextField align(int align) {
        this.setAlignment(align);
        return this;
    }

    public UITextField origin(int align) {
        this.setOrigin(align);
        return this;
    }

    public UITextField scale(float scale) {
        this.setScale(scale);
        return this;
    }

    public UITextField fontScale(float scale) {
        this.getStyle().font.getData().setScale(scale);
        return this;
    }

    public UITextField fontScale(float scaleX, float scaleY) {
        this.getStyle().font.getData().setScale(scaleX, scaleY);
        return this;
    }

    public UITextField parent(Group rootGroup) {
        rootGroup.addActor(this);
        return this;
    }

    public UITextField bounds(float x, float y, float width, float height) {
        this.setBounds(x, y, width, height);
        return this;
    }

    public UITextField debug(boolean b) {
        this.setDebug(b);
        return this;
    }

    public UITextField message(String hint) {
        this.setMessageText(hint);
        return this;
    }

    public UITextField maxLength(int len) {
        this.setMaxLength(len);
        return this;
    }
    public UITextField filter(TextFieldFilter filter) {
        this.setTextFieldFilter(filter);
        return this;
    }

    public UITextField onEnter(Runnable action) {
        this.setTextFieldListener((textField, c) -> {
            if (c == '\r' || c == '\n') {
                action.run();
            }
        });
        return this;
    }

    public UITextField onClick(Runnable action) {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return this;
    }

    // Hiệu ứng touch như UIButton (optional)
    public UITextField touchEffect(boolean enable) {
        if (!enable) return this;
        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                addAction(Actions.scaleTo(0.97f, 0.97f, 0.1f));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                addAction(Actions.scaleTo(1f, 1f, 0.1f));
                super.touchUp(event, x, y, pointer, button);
            }
        });
        return this;
    }
}
