package com.game.ui.base;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;

public class UIGroup extends Group {
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

    public UIGroup originCenter() {
        this.setOrigin(Align.center);
        return this;
    }

    public UIGroup add(Actor actor) {
        this.addActor(actor);
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

    public UIGroup child(Actor... actors) {
        for (Actor actor : actors) {
            this.addActor(actor);
        }
        return this;
    }

}
