package com.game;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.managers.AudioManager;
import com.game.managers.GAssetManager;
import com.game.managers.SoundManager;
import com.game.screens.ScreenManager;
import com.game.managers.event.EventManager;
import com.game.screens.ScreenType;
import com.game.ui.OverlayUI;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.GameSession;

public class MainGame extends Game {
    private static AudioManager audioManager;
    private static SoundManager soundManager;
    private static GAssetManager assetManager;
    private static EventManager eventManager;
    private static ScreenManager scm;

    private static GameSession session;

    private static SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private static Stage stage;
    private static Engine engine;

    InputMultiplexer multiplexer;

    public static AudioManager getAum() {
        if (audioManager == null) {
            audioManager = new AudioManager();
        }
        return audioManager;
    }

    public static SoundManager getSoundManager(){
        if (soundManager == null) {
            soundManager = new SoundManager();
        }
        return soundManager;
    }

    public static GAssetManager getAsM() {
        if (assetManager == null) {
            assetManager = new GAssetManager();
        }
        return assetManager;
    }

    public static EventManager getEvM() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }


    public static ScreenManager getScM() {
        if (scm == null) scm = new ScreenManager();
        return scm;
    }

    public static GameSession getSession() {
        if (session == null) {
            session = new GameSession();
        }
        return session;
    }

    public static SpriteBatch batch() {
        return batch;
    }

    public static Stage getStage() {
        return stage;
    }

    public static Engine getEngine() {
        if (engine == null) {
            engine = new Engine();
        }
        return engine;
    }

    public static MainGame getInstance() {
        return (MainGame) Gdx.app.getApplicationListener();
    }

    @Override
    public void create() {
        assetManager = new GAssetManager();
        batch = new SpriteBatch();
        multiplexer = new InputMultiplexer();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float worldHeight = 720;
        float worldWidth = worldHeight * screenWidth / screenHeight;

        camera = new OrthographicCamera(worldWidth, worldHeight);
        camera.setToOrtho(false);

        viewport = new FitViewport(worldWidth, worldHeight, camera);
        viewport.apply();

        stage = new Stage(viewport);
        scm = new ScreenManager();

        assetManager.loadSkin("ui/uiskin.json");
        assetManager.loadFont("ui/default.fnt");
        assetManager.loadFont(BMF);
        assetManager.loadAudioAssets();
        assetManager.finishLoading();

        multiplexer.addProcessor(stage); // nếu có stage
        Gdx.input.setInputProcessor(multiplexer);
        getScM().showScreen(ScreenType.MENU_GAME);
    }

    @Override
    public void render() {
        super.render();
    }




    @Override
    public void dispose() {
        batch.dispose();
        if (screen != null) {
            screen.dispose();
        }
        getEvM().clear();
        stage.dispose();
        assetManager.dispose();
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
    }
}
