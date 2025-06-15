package com.game.screens.main;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.game.MainGame;
import com.game.managers.event.battle.encounter.BattleStartRequestedEvent;
import com.game.managers.event.world.PlayerEncounteredEnemyEvent;

public class GameScreen implements Screen {

    public static void loadingAsset(){
        MainGame.getAsM().load("texture/default.png", Texture.class);
    }

    @Override
    public void show() {
        System.out.println("GameScreen.show");
        MainGame.getStage().clear();
        MainGame.getStage().addActor(new Image(new NinePatch(new Texture("texture/default.png"))));


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                MainGame.getEvM().dispatch(new PlayerEncounteredEnemyEvent());
            }
        }, 3); // Trễ 2 giây

        MainGame.getEvM().subscribe(PlayerEncounteredEnemyEvent.class, event -> {
            System.out.println("Encountered enemy! Switching to battle.");
            MainGame.getEvM().dispatch(new BattleStartRequestedEvent());
        });
    }

    @Override
    public void render(float delta) {
        MainGame.getStage().act(delta);
        MainGame.getStage().draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
