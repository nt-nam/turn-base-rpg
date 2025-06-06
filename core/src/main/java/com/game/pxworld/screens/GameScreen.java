package com.game.pxworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.game.pxworld.Main;
import com.game.pxworld.managers.event.battle.encounter.BattleStartRequestedEvent;
import com.game.pxworld.managers.event.world.PlayerEncounteredEnemyEvent;

public class GameScreen implements Screen {
    @Override
    public void show() {
        System.out.println("GameScreen.show");

        // Giả lập gặp enemy sau 3 giây (test)
//        Gdx.app.postRunnable(() -> {
//            try { Thread.sleep(3000); } catch (Exception ignored) {}
//            Main.getEvM().dispatch(new PlayerEncounteredEnemyEvent());
//        });

        // Test: simulate encounter enemy after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (Exception ignored) {}
            Main.getEvM().dispatch(new PlayerEncounteredEnemyEvent());
        }).start();

        Main.getEvM().subscribe(PlayerEncounteredEnemyEvent.class, event -> {
            System.out.println("Encountered enemy! Switching to battle.");
            Main.getEvM().dispatch(new BattleStartRequestedEvent());
        });
    }

    @Override
    public void render(float delta) {
        Main.getStage().act(delta);
        Main.getStage().draw();
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
