package com.game.ui.base;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.game.MainGame;
import com.game.managers.event.ui.ClickButtonEvent;

public class UIButton extends TextButton {

    public UIButton(String text) {
        super(text, MainGame.getAsM().getSkin(), "toggle");  //thay thế cho styleName default
        tu();
    }

    public UIButton(String text, String styleName) {
        super(text, MainGame.getAsM().getSkin(), styleName);
    }

    public UIButton(String text, TextureRegion up, TextureRegion down) {
        super(text, createStyle(up, down));
        tu();
    }

    public UIButton(String text, NinePatch up, NinePatch down) {
        super(text, createStyle(up, down));
        tu();
    }

    public UIButton(TextureRegion up, TextureRegion down) {
        super("", createStyle(up, down));
        tu();
    }

    public UIButton(TextureRegion region) {
        super("", createStyle(region, region));
        tu();

        this.setTransform(true); // Cho phép scale/rotate actor

        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UIButton.this.setOrigin(getWidth() / 2, getHeight() / 2); // Tâm scale
                UIButton.this.setScale(0.9f); // Nhỏ lại khi nhấn
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                UIButton.this.setScale(1f); // Trả về như cũ
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public UIButton(TextureRegion on,TextureRegion up, TextureRegion down) {
        super("", createStyle(up, down));
        tu();
    }

    private static TextButtonStyle createStyle(TextureRegion up, TextureRegion down) {
        TextButtonStyle style = new TextButtonStyle();
        style.up = new TextureRegionDrawable(up);
        style.down = new TextureRegionDrawable(down);
        style.checked = style.down;
        // Không cần set font nếu không có text, nhưng LibGDX yêu cầu không null, nên cứ để default:
        style.font = MainGame.getAsM().getSkin().getFont("default-font");
        return style;
    }

    private static TextButtonStyle createStyle(NinePatch up, NinePatch down) {
        TextButtonStyle style = new TextButtonStyle();
        style.up = new NinePatchDrawable(up);
        style.down = new NinePatchDrawable(down);
        style.checked = style.down;
        // Không cần set font nếu không có text, nhưng LibGDX yêu cầu không null, nên cứ để default:
        style.font = MainGame.getAsM().getSkin().getFont("default-font");
        return style;
    }

    public UIButton name(String name) {
        this.setName(name);
        return this;
    }

    public UIButton pos(float x, float y) {
        this.setPosition(x, y);
        return this;
    }

    public UIButton size(float w, float h) {
        this.setSize(w, h);
        return this;
    }

    public UIButton scale(float scale) {
        this.setScale(scale);
        return this;
    }

    public UIButton fontScale(float scale) {
        this.getLabel().setFontScale(scale);
        return this;
    }

    public UIButton fontScale(float scaleX, float scaleY) {
        this.getLabel().setFontScale(scaleX, scaleY);
        return this;
    }

    public UIButton onClick(Runnable action) {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainGame.getEvM().dispatch(new ClickButtonEvent());
                action.run();
            }
        });
        return this;
    }

    public UIButton onTouchDown(Runnable action) {
        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                action.run();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        return this;
    }

    public UIButton onTouchUp(Runnable action) {
        this.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                action.run();
                super.touchUp(event, x, y, pointer, button);
            }
        });
        return this;
    }


    public UIButton tu() {
        this.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                setChecked(false);
            }
        });
        return this;
    }

    private void initTouchEffect(boolean enableTouchEffect) {
        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (enableTouchEffect) {
                    addAction(Actions.scaleTo(0.95f, 0.95f, 0.1f));
                }
                super.touchDown(event, x, y, pointer, button);
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (enableTouchEffect) {
                    addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public UIButton parent(Group rootGroup) {
        rootGroup.addActor(this);
        return this;
    }

    public UIButton bounds(int x, int y, float width, float height) {
        this.setBounds(x, y, width, height);
        return this;
    }

    public UIButton debug(boolean b) {
        this.setDebug(b);
        return this;
    }
}
