package com.game.screens;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;

public class CheckRegionScreen extends BaseScreen {
    private static String name = UI_WOOD;


    private int currentFrameIndex = 0;
    private Array<TextureAtlas.AtlasRegion> debugFrames;
    private Image debugImage;
    private BitmapFont font;
    private SpriteBatch batch;

    public CheckRegionScreen() {
        super();
        createScreen();
    }

    public static void loadingAsset() {
//        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
        MainGame.getAsM().load(name, TextureAtlas.class);
        MainGame.getAsM().loadFont(BMF);
    }

    @Override
    protected void createScreen() {
        Gdx.app.log("CheckRegionScreen", "show() called");
        createTitle("Check Region Screen", rootGroup);

        font = MainGame.getAsM().getFont(BMF);
        batch = new SpriteBatch();

        TextureAtlas atlas = MainGame.getAsM().get(name, TextureAtlas.class);
        debugFrames = atlas.getRegions();

        if (debugFrames.size == 0) {
            Gdx.app.log("CheckRegionScreen", "❌ Không tìm thấy region nào trong atlas!");
            return;
        }

        debugImage = new Image(new TextureRegionDrawable(debugFrames.get(currentFrameIndex)));
        debugImage.setSize(stage.getHeight()*0.8f, stage.getHeight()*0.8f);
//        debugImage.setPosition((screenWidth - debugImage.getWidth()) / 2f,
//            (screenHeight - debugImage.getHeight()) / 2f + 40);

        debugImage.setPosition(100,100);
        debugImage.debug();
        rootGroup.addActor(debugImage);
    }

    @Override
    protected void updateLogic(float delta) {
        super.updateLogic(delta);
        if (debugFrames != null && debugImage != null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                currentFrameIndex = (currentFrameIndex + 1) % debugFrames.size;
                debugImage.setDrawable(new TextureRegionDrawable(debugFrames.get(currentFrameIndex)));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                currentFrameIndex = (currentFrameIndex - 1 + debugFrames.size) % debugFrames.size;
                debugImage.setDrawable(new TextureRegionDrawable(debugFrames.get(currentFrameIndex)));
            }
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    protected void updateUI(float delta) {
        super.updateUI(delta);

        batch.begin();
        if (debugFrames != null && debugFrames.size > 0) {
            String regionName = debugFrames.get(currentFrameIndex).name;
            font.draw(batch, regionName, 50, 50);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        title.remove();
        if (font != null) font.dispose();
        if (batch != null) batch.dispose();
    }
}
