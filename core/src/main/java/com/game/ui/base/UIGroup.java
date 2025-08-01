package com.game.ui.base;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class UIGroup extends Group {
    private Runnable runnable = null;
    private boolean isActionRunning = false;
    private boolean blockClick = false;

    public UIGroup() {
        super();
    }

    public UIGroup name(String name) {
        this.setName(name);
        return this;
    }

    public UIGroup pos(float x, float y) {
        this.setPosition(x, y);
        return this;
    }

    public UIGroup size(float w, float h) {
        this.setSize(w, h);
        return this;
    }

    public UIGroup scale(float scale) {
        this.setScale(scale);
        return this;
    }

    public UIGroup origin(int alignment) {
        this.setOrigin(alignment);
        return this;
    }

    public UIGroup run() {
        if (runnable != null) {
            runnable.run();
        }
        return this;
    }

    public UIGroup run(Runnable run) {
        this.runnable = run;
        return this;
    }

    public UIGroup blockClick(boolean b) {
        blockClick = b;
        return this;
    }

    public UIGroup originCenter() {
        this.setOrigin(Align.center);
        return this;
    }

    public UIGroup add(Actor actor) {
        this.addActor(actor);
        return this;
    }

    public UIGroup index(int index) {
        this.setZIndex(index);
        return this;
    }

    public Actor query(String name) {
        return this.findActor(name);
    }

    public <T extends Actor> T query(String name, Class<T> type) {
        Actor actor = this.findActor(name);
        if (actor == null) return null;

        if (type.isInstance(actor)) {
            return type.cast(actor);
        } else {
            throw new ClassCastException("Actor with name '" + name + "' is not of type " + type.getSimpleName());
        }
    }

    public UIGroup parent(Group parent) {
        parent.addActor(this);
        return this;
    }

    public UIGroup debug(boolean b) {
        setDebug(b);
        return this;
    }

    public UIGroup onClick(Runnable runnable) {
        this.runnable = runnable;
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isActionRunning || blockClick) {
                    return;
                }
                runnable.run();
            }
        });
        return this;
    }

    public UIGroup action(Action action) {
        this.addAction(Actions.sequence(
            Actions.run(() -> {
                isActionRunning = true;
                setTouchable(Touchable.disabled);
            }),
            action,
            Actions.run(() -> {
                isActionRunning = false;
                setTouchable(Touchable.enabled);
            })
        ));
        return this;
    }


    public UIGroup onClickAction(Runnable runnable) {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (isActionRunning) {
                    return;
                }
                isActionRunning = true;
                setTouchable(Touchable.disabled);
                runnable.run();
            }
        });
        return this;
    }

    public void finishAction() {
        isActionRunning = false;
        setTouchable(Touchable.enabled);
    }

    public UIGroup child(Actor... actors) {
        for (Actor actor : actors) {
            this.addActor(actor);
        }
        return this;
    }

}
