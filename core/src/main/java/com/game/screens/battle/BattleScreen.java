package com.game.screens.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.game.MainGame;
import com.game.managers.event.battle.result.BattleEndedEvent;
import com.game.managers.event.battle.result.BattleVictoryEvent;
import com.game.screens.ScreenType;

public class BattleScreen implements Screen {
    @Override
    public void show() {
        System.out.println("BattleScreen.show");

        // Giả lập thắng trận sau 5 giây
        Gdx.app.postRunnable(() -> {
            try { Thread.sleep(5000); } catch (Exception ignored) {}
            MainGame.getEvM().dispatch(new BattleVictoryEvent());
            MainGame.getEvM().dispatch(new BattleEndedEvent());
        });

        MainGame.getEvM().subscribe(BattleEndedEvent.class, event -> {
            System.out.println("Battle ended, switching back to game.");
            MainGame.getScM().showScreen(ScreenType.MAIN_GAME);
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
