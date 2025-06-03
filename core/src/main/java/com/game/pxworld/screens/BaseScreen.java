package com.game.pxworld.screens; // Hoặc package của bạn

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color; // Thêm Color
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;    // Thêm Pixmap
import com.badlogic.gdx.graphics.Texture;  // Thêm Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Thêm BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor; // Thêm Actor
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;    // Thêm Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener; // Thêm ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.pxworld.Main;

public abstract class BaseScreen implements Screen {

    protected final Main game;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected SpriteBatch batch;
    protected Stage stage;
    protected Skin skin; // Skin được quản lý bởi BaseScreen

    public BaseScreen(final Main game) {
        this.game = game;
        this.batch = game.batch;

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, this.batch);

        initializeSkin(); // Gọi hàm khởi tạo skin
    }

    /**
     * Initializes a basic skin. Subclasses can override this to load/create a custom skin.
     */
    protected void initializeSkin() {
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        // Store the default libGDX font under the name "default-font".
        skin.add("default-font", new BitmapFont());

        // Store a slightly larger font for titles (using a default LibGDX asset)
        BitmapFont titleFont = new BitmapFont(Gdx.files.internal("default/default.fnt"));
        // titleFont.getData().setScale(1.5f); // Option to scale if needed
        skin.add("title-font", titleFont);


        // Configure a TextButtonStyle and name it "default".
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLACK);
        textButtonStyle.checked = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default-font");
        skin.add("default", textButtonStyle);

        // Configure another TextButtonStyle, e.g., for "important" buttons
        TextButton.TextButtonStyle importantTextButtonStyle = new TextButton.TextButtonStyle();
        importantTextButtonStyle.up = skin.newDrawable("white", Color.RED);
        importantTextButtonStyle.down = skin.newDrawable("white", Color.FIREBRICK);
        importantTextButtonStyle.over = skin.newDrawable("white", Color.SALMON);
        importantTextButtonStyle.font = skin.getFont("default-font");
        skin.add("important", importantTextButtonStyle);


        // Configure a LabelStyle and name it "default".
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Configure a LabelStyle for titles
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = skin.getFont("title-font"); // Use the larger font
        titleLabelStyle.fontColor = Color.YELLOW; // Example: Yellow title
        skin.add("default-title", titleLabelStyle);
    }

    // --- Utility methods for creating UI elements ---

    /**
     * Creates a TextButton with the default style.
     * @param text The text to display on the button.
     * @param action The action to perform when the button is clicked.
     * @return The created TextButton.
     */
    protected TextButton createTextButton(String text, final Runnable action) {
        return createTextButton(text, "default", action); // Calls the more specific version with "default" style
    }

    /**
     * Creates a TextButton with a specified style.
     * @param text The text to display on the button.
     * @param styleName The name of the TextButtonStyle in the skin.
     * @param action The action to perform when the button is clicked.
     * @return The created TextButton.
     */
    protected TextButton createTextButton(String text, String styleName, final Runnable action) {
        TextButton button = new TextButton(text, skin, styleName);
        if (action != null) {
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    action.run();
                }
            });
        }
        return button;
    }

    /**
     * Creates a Label with the default style.
     * @param text The text to display.
     * @return The created Label.
     */
    protected Label createLabel(String text) {
        return new Label(text, skin, "default");
    }

    /**
     * Creates a Label with a specified style.
     * @param text The text to display.
     * @param styleName The name of the LabelStyle in the skin.
     * @return The created Label.
     */
    protected Label createLabel(String text, String styleName) {
        return new Label(text, skin, styleName);
    }
    /**
     * Các lớp con sẽ override phương thức này để vẽ nội dung game đặc thù của chúng
     * (ví dụ: nhân vật, bản đồ, kẻ địch).
     * Phương thức này được gọi trong `render()` của BaseScreen, TRƯỚC KHI stage được vẽ.
     * @param delta Thời gian từ frame cuối.
     */
    protected void renderScreenContent(float delta) {
        // Mặc định không làm gì cả. Lớp con sẽ triển khai.
    }

    // --- Screen lifecycle methods ---
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderScreenContent(delta);

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        if (skin != null) {
            skin.dispose(); // Dispose the skin managed by BaseScreen
        }
    }

    // Helper getters
    public Main getGame() { return game; }
    public Stage getStage() { return stage; }
    public SpriteBatch getBatch() { return batch; }
    public OrthographicCamera getCamera() { return camera; }
    public Skin getSkin() { return skin; } // Allow subclasses to access the skin if needed
}
