package com.game.ui.base;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;



public class UITable extends Table {

    public UITable() {
        super(MainGame.getAsM().getSkin());
    }

    // Factory method nếu muốn dùng
    public static UITable New() {
        return new UITable();
    }

    public UITable name(String name) {
        this.setName(name);
        return this;
    }

    public UITable pos(float x, float y) {
        this.setPosition(x, y);
        return this;
    }

    public UITable size(float w, float h) {
        this.setSize(w, h);
        return this;
    }

    public UITable fillParent() {
        this.setFillParent(true);
        return this;
    }

    public UITable al(int alignment) {
        this.align(alignment);
        return this;
    }

    public UITable setOriginCenter() {
        this.setOrigin(Align.center);
        return this;
    }

    public UITable actor(Actor actor) {
        this.add(actor).pad(5).row();
        return this;
    }

    public UITable child(Actor... actors) {
        for (Actor actor : actors) {
            this.add(actor).pad(5).row();
        }
        return this;
    }

    public UITable children(Actor... actors) {
        return child(actors); // alias cho child()
    }

    public UITable sizeChildren(float w, float h) {
        for (Cell cell : this.getCells()) {
            cell.size(w, h);
        }
        return this;
    }

    public UITable scaleChildren(float scale) {
        for (Actor child : this.getChildren()) {
            child.setScale(scale);
        }
        return this;
    }

    public UITable padChildren(float pad) {
        for (Cell cell : this.getCells()) {
            cell.pad(pad);
        }
        return this;
    }

    public UITable padChildren(float padTop, float padLeft, float padBottom, float padRight) {
        for (Cell cell : this.getCells()) {
            cell.padTop(padTop).padLeft(padLeft).padBottom(padBottom).padRight(padRight);
        }
        return this;
    }

    public UITable clearChild() {
        this.clearChildren(true); // remove all children + cells
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

    public UITable debugAll(){
        this.debug();
        this.debugActor();
        return this;
    }

}
