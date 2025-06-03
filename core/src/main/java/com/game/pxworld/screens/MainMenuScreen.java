package com.game.pxworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20; // For clearing the screen
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage; // For UI elements
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin; // For UI styling
import com.badlogic.gdx.scenes.scene2d.ui.Table; // For layout
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.game.pxworld.Main;

public class MainMenuScreen extends BaseScreen {

    private final Main game; // Reference to the main Game class
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage; // A stage for holding UI actors (buttons, labels, etc.)
    private Skin skin; // Skin for styling UI elements

    // Constructor
    public MainMenuScreen(final Main game) {
        super(game);
        this.game = game; // Store the reference to the Game class
        this.batch = game.batch; // Use the SpriteBatch from the Game class if you have one, or create new

        // Set up the camera
        camera = new OrthographicCamera();
        // camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Option 1

        // Set up the stage for UI
        stage = new Stage(new ScreenViewport(camera), batch); // Use ScreenViewport for responsive UI
        Gdx.input.setInputProcessor(stage); // Make the stage handle input events

        // Load a skin for UI elements (we'll create a basic one later)
        // For now, let's assume you have a uiskin.json, uiskin.atlas, and font file
        // skin = new Skin(Gdx.files.internal("uiskin.json")); // Placeholder

        // Create a simple skin programmatically for now if you don't have files
        createBasicSkin();


        // Create a table to organize UI elements
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the entire stage
        // table.setDebug(true); // Uncomment to see table debug lines
        stage.addActor(table);

        // Create UI elements
        Label titleLabel = new Label("My RPG Game", skin, "default-title"); // "default-title" style from skin
        TextButton newGameButton = createTextButton("New Game", "default", () -> {
            Gdx.app.log("MainMenuScreen", "New Game button clicked - Transitioning to GameplayScreen");
            game.setScreen(new GameplayScreen(game)); // Chuyển sang GameplayScreen
        });
        TextButton loadGameButton = new TextButton("Load Game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add elements to the table
        table.add(titleLabel).padBottom(50).colspan(2).center();
        table.row(); // New row
        table.add(newGameButton).width(200).height(50).pad(10);
        table.row();
        table.add(loadGameButton).width(200).height(50).pad(10);
        table.row();
        table.add(exitButton).width(200).height(50).pad(10);

        // Add listeners to buttons (actions when clicked)
        newGameButton.addListener(event -> {
            if (event.toString().contains("touchDown")) { // Or use ChangeListener
                Gdx.app.log("MainMenuScreen", "New Game button clicked");
                // game.setScreen(new GameplayScreen(game)); // Example: Switch to GameplayScreen
            }
            return true;
        });

        exitButton.addListener(event -> {
            if (event.toString().contains("touchDown")) {
                Gdx.app.log("MainMenuScreen", "Exit button clicked");
                Gdx.app.exit(); // Exit the game
            }
            return true;
        });
    }

    // This method creates a very basic skin programmatically
    // In a real project, you'd typically load a skin from a JSON file
    private void createBasicSkin() {
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin.
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        pixmap.fill();
        skin.add("white", new com.badlogic.gdx.graphics.Texture(pixmap));
        pixmap.dispose();

        // Store the default libGDX font under the name "default-font".
        skin.add("default-font", new com.badlogic.gdx.graphics.g2d.BitmapFont());

        // Configure a TextButtonStyle and name it "default".
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", com.badlogic.gdx.graphics.Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", com.badlogic.gdx.graphics.Color.BLACK);
        textButtonStyle.checked = skin.newDrawable("white", com.badlogic.gdx.graphics.Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", com.badlogic.gdx.graphics.Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default-font");
        skin.add("default", textButtonStyle);

        // Configure a LabelStyle for the title
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont(Gdx.files.internal("default/default.fnt")); // A default font, or use your own
        // You might need to scale the font or use a larger pre-made font
        // titleLabelStyle.font.getData().setScale(2); // Example: Scale font
        titleLabelStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        skin.add("default-title", titleLabelStyle);

        // Configure a LabelStyle for general labels
        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle();
        defaultLabelStyle.font = skin.getFont("default-font");
        defaultLabelStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        skin.add("default", defaultLabelStyle);
    }


    @Override
    public void show() {
        Gdx.app.log("MainMenuScreen", "show() called");
        Gdx.input.setInputProcessor(stage); // Ensure stage handles input when screen is shown
    }

    @Override
    public void render(float delta) {
        // Clear the screen with a color (e.g., black)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1); // Dark grayish blue
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera
        camera.update();
        batch.setProjectionMatrix(camera.combined); // Not strictly needed if only using Stage for rendering

        // Act and draw the stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update actors in the stage
        stage.draw(); // Draw the stage (and all actors in it)
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("MainMenuScreen", "resize(" + width + ", " + height + ") called");
        stage.getViewport().update(width, height, true); // Update the stage's viewport
        camera.setToOrtho(false, width, height); // Update camera to match new screen size
        camera.update();
    }

    @Override
    public void pause() {
        Gdx.app.log("MainMenuScreen", "pause() called");
    }

    @Override
    public void resume() {
        Gdx.app.log("MainMenuScreen", "resume() called");
    }

    @Override
    public void hide() {
        Gdx.app.log("MainMenuScreen", "hide() called");
        // Optional: Gdx.input.setInputProcessor(null); if you don't want this screen's stage to process input when hidden
    }

    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen", "dispose() called");
        stage.dispose(); // Dispose the stage and its resources
        skin.dispose(); // Dispose the skin
        // Note: 'batch' is managed by the main Game class in this example, so it might not be disposed here.
        // If 'batch' was created uniquely for this screen, dispose it: batch.dispose();
    }
}
