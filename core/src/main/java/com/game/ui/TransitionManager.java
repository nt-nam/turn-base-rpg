package com.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.MainGame;

public class TransitionManager {
    private Group effectGroup;

    public TransitionManager(Group effectGroup) {
        this.effectGroup = effectGroup;
    }

    public void fadeIn(float duration, Runnable onComplete) {
        Image fadeImage = createFadeImage();
        fadeImage.getColor().a = 1f;
        fadeImage.addAction(Actions.sequence(
            Actions.fadeOut(duration),
            Actions.run(() -> {
                effectGroup.clearChildren();
                if (onComplete != null) onComplete.run();
            })
        ));
        effectGroup.addActor(fadeImage);
    }

    public void fadeOut(float duration, Runnable onComplete) {
        Image fadeImage = createFadeImage();
        fadeImage.getColor().a = 0f;
        fadeImage.addAction(Actions.sequence(
            Actions.fadeIn(duration),
            Actions.run(() -> {
                effectGroup.clearChildren();
                if (onComplete != null) onComplete.run();
            })
        ));
        effectGroup.addActor(fadeImage);
    }

    private Image createFadeImage() {
        Texture texture = new Texture(MainGame.getAsM().resolve("default.png"));
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setSize(MainGame.getStage().getWidth(), MainGame.getStage().getHeight());
        image.setColor(Color.BLACK);
        return image;
    }
}
