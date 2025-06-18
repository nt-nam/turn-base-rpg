package com.game;

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
import com.game.managers.CharacterTilemapManager;
import com.game.managers.GAssetManager;
import com.game.ui.HUDManager;
import com.game.managers.InputManager;
import com.game.managers.LevelManager;
import com.game.managers.MapManager;
import com.game.managers.SaveManager;
import com.game.screens.ScreenManager;
import com.game.ui.UIManager;
import com.game.managers.event.EventManager;
import com.game.screens.ScreenType;
import com.game.utils.ClickLoggerInputProcessor;
import com.game.utils.data.GameSession;

public class MainGame extends Game {
    private static AudioManager aum;
    private static CharacterTilemapManager ctm;
    private static GAssetManager asm;
    private static EventManager evm;
    private static HUDManager hudm;
    private static InputManager im;
    private static LevelManager lvm;
    private static MapManager mpm;
    private static SaveManager pm;
    private static ScreenManager scm;
    private static UIManager uim;

    private static GameSession session;

    private static SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private static Stage stage;
    private static Engine engine;

    InputMultiplexer multiplexer;

    public static AudioManager getAum() {
        if (aum == null) {
            aum = new AudioManager();
        }
        return aum;
    }

    public static CharacterTilemapManager getCtm(){
        if (ctm == null) {
            ctm = new CharacterTilemapManager();
        }
        return ctm;
    }

    public static GAssetManager getAsM() {
        if (asm == null) { asm = new GAssetManager();}
        return asm;
    }

    public static EventManager getEvM() {
        if (evm == null) {
            evm = new EventManager();
        }
        return evm;
    }

    public static HUDManager getHudm() {
        if (hudm == null) {
            hudm = new HUDManager(stage.getRoot());
        }
        return hudm;
    }

    public static InputManager getIm() {
        if (im == null) {
            im = new InputManager();
        }
        return im;
    }

    public static LevelManager getLvm() {
        if (lvm == null) {
            lvm = new LevelManager();
        }
        return lvm;
    }

    public static MapManager getMpm() {
        if (mpm == null) {
            mpm = new MapManager();
        }
        return mpm;
    }

    public static SaveManager getPm() {
        if (pm == null) {
            pm = new SaveManager();
        }
        return pm;
    }

    public static ScreenManager getScM() {
        if(scm == null) scm = new ScreenManager();
        return scm;
    }

    public static UIManager getUim() {
        if (uim == null) {
            uim = new UIManager();
        }
        return uim;
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

    public static MainGame getInstance(){
        return (MainGame) Gdx.app.getApplicationListener();
    }

    @Override
    public void create() {
        asm = new GAssetManager();
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

        asm.loadSkin("ui/uiskin.json");
        asm.loadFont("ui/default.fnt");
        asm.finishLoading();
//        Gdx.input.setInputProcessor(stage);
        multiplexer.addProcessor(stage); // nếu có stage
//        multiplexer.addProcessor(new ClickLoggerInputProcessor(camera));
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
        asm.dispose();
        super.dispose();
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height,true);
    }
}
