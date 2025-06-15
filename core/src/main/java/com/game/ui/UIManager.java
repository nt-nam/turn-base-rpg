package com.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.game.MainGame;

import com.badlogic.gdx.scenes.scene2d.Group;

public class UIManager {
//    private Stage stage;
//    private Skin skin;
//
//    public UIManager() {
//        stage = Main.getStage();
//        skin = new Skin(Gdx.files.internal("default/uiskin.json")); // hoặc tự tạo Skin
//    }
//
//    public void addUIElement(Actor actor) {
//        stage.addActor(actor);
//    }
//
//    public void removeUIElement(Actor actor) {
//        actor.remove();
//    }
//
//    public void loadHUD(){};
//    public void loadMainMenu(){};
//    public void showPopup(String message){};
//
//    public void showDialog(String title, String content){};
//    public void showNotification(String message){};
//
//    public void enableInput() {
//        Gdx.input.setInputProcessor(stage);
//    }
//
//    public void update(float delta) {
//        stage.act(delta);
//    }
//
//    public void render() {
//        stage.draw();
//    }
//
//    public void setSkin(Skin newSkin) {
//        this.skin = newSkin;
//    }
//
//    public void onEvent(GameEvent event) {
//        if (event instanceof PlayerHealthChangedEvent) {
//            updateHealthBar(((PlayerHealthChangedEvent) event).newHealth);
//        }
//    }
//
//    private void updateHealthBar(int newHealth) {
//        // Cập nhật UI cho thanh máu
//    }

    private Group hudGroup;
    private Group popupGroup;
    private Group effectGroup;

    private HUDManager hudManager;
    private PopupManager popupManager;
    private TransitionManager transitionManager;

    public UIManager() {
        hudGroup = new Group();
        hudGroup.setName("HUDGroup");

        popupGroup = new Group();
        popupGroup.setName("PopupGroup");

        effectGroup = new Group();
        effectGroup.setName("EffectGroup");

        Stage stage = MainGame.getStage();
        stage.addActor(hudGroup);
        stage.addActor(popupGroup);
        stage.addActor(effectGroup);

        hudManager = new HUDManager(hudGroup);
        popupManager = new PopupManager(popupGroup);
        transitionManager = new TransitionManager(effectGroup);
    }

    public HUDManager getHudManager() {
        return hudManager;
    }

    public PopupManager getPopupManager() {
        return popupManager;
    }

    public TransitionManager getTransitionManager() {
        return transitionManager;
    }

    public void addToPopup(Actor actor) {
        popupGroup.addActor(actor);
    }

    public void clearPopups() {
        popupGroup.clearChildren();
    }
}
