package com.game.pxworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.game.pxworld.Main;
import com.game.pxworld.managers.ScreenManager;
import com.game.pxworld.managers.event.battle.result.BattleEndedEvent;
import com.game.pxworld.managers.event.battle.result.BattleVictoryEvent;

public class BattleScreen implements Screen {
    @Override
    public void show() {
        System.out.println("BattleScreen.show");

        // Giả lập thắng trận sau 5 giây
        Gdx.app.postRunnable(() -> {
            try { Thread.sleep(5000); } catch (Exception ignored) {}
            Main.getEvM().dispatch(new BattleVictoryEvent());
            Main.getEvM().dispatch(new BattleEndedEvent());
        });

        Main.getEvM().subscribe(BattleEndedEvent.class, event -> {
            System.out.println("Battle ended, switching back to game.");
            Main.getScM().showScreen(ScreenType.GAME);
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
