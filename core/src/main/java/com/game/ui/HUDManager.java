package com.game.ui;

import com.game.MainGame;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class HUDManager {
//    private ProgressBar healthBar;
//
//    public HUDManager() {
//        setupListeners();
//    }
//
//    private void setupListeners() {
//        Main.getEvM().subscribe(PlayerHealthChangedEvent.class, event -> {
//            healthBar.setValue(event.newHealth);
//        });
//
//        Main.getEvM().subscribe(PlayerDiedEvent.class, event -> {
//            showGameOverPopup();
//        });
//
//        Main.getEvM().subscribe(ItemPickedEvent.class, event -> {
//            showNotification("Picked up: " + event.itemName);
//        });
//    }
//
//    private void showGameOverPopup() {
//        // Implement UI logic to show game over dialog
//    }
//
//    private void showNotification(String message) {
//        // Implement UI logic to show toast/notification
//    }

    private Group hudGroup;
    private Label hpLabel;
    private Label manaLabel;

    public HUDManager(Group hudGroup) {
        this.hudGroup = hudGroup;

        hpLabel = new Label("HP: 100", MainGame.getAsM().getSkin());
        hpLabel.setPosition(20, 680);
        hudGroup.addActor(hpLabel);

        manaLabel = new Label("Mana: 50", MainGame.getAsM().getSkin());
        manaLabel.setPosition(20, 650);
        hudGroup.addActor(manaLabel);
    }

    public void updateHP(int hp) {
        hpLabel.setText("HP: " + hp);
    }

    public void updateMana(int mana) {
        manaLabel.setText("Mana: " + mana);
    }
}
