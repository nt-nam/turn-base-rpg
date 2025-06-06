package com.game.pxworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.pxworld.managers.AudioManager;
import com.game.pxworld.managers.GAssetManager;
import com.game.pxworld.managers.HUDManager;
import com.game.pxworld.managers.InputManager;
import com.game.pxworld.managers.LevelManager;
import com.game.pxworld.managers.MapManager;
import com.game.pxworld.managers.PreferencesManager;
import com.game.pxworld.managers.ScreenManager;
import com.game.pxworld.managers.UIManager;
import com.game.pxworld.managers.event.EventManager;
import com.game.pxworld.screens.ScreenType;

public class Main extends Game {
    private static AudioManager aum;
    private static GAssetManager asm;
    private static EventManager evm;
    private static HUDManager hudm;
    private static InputManager im;
    private static LevelManager lvm;
    private static MapManager mpm;
    private static PreferencesManager pm;
    private static ScreenManager scm;
    private static UIManager uim;

    private static SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private static Stage stage;

    public static AudioManager getAum() {
        if (aum == null) {
            aum = new AudioManager();
        }
        return aum;
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
            hudm = new HUDManager();
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

    public static PreferencesManager getPm() {
        if (pm == null) {
            pm = new PreferencesManager();
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


    public static SpriteBatch batch() {
        return batch;
    }

    public static Stage getStage() {
        return stage;
    }

    public static Main getInstance(){
        return (Main) Gdx.app.getApplicationListener();
    }

    @Override
    public void create() {
        asm = new GAssetManager();
        batch = new SpriteBatch();

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

        getScM().showScreen(ScreenType.GAME);
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
}
