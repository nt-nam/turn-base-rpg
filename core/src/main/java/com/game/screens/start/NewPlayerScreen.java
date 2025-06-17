package com.game.screens.start;

import static com.game.utils.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.managers.event.ui.ClickButtonEvent;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITextField;
import com.game.utils.data.KnightBaseData;
import com.game.utils.data.KnightDataManager;

import java.util.function.Consumer;

public class NewPlayerScreen extends BaseScreen {
    private int currentKnightIndex = 0;
    private String playerName = "";
    private final Array<String> knightIds = new Array<>();
    private KnightDataManager knightDataManager;
    private Image knightAnimImage; // actor animation
    private UILabel knightStatsLabel; // Label mô tả/thông số
    private TextField playerNameField;
    private Consumer<ClickButtonEvent> clickButtonEventConsumer;
    private final float leftWidth = screenWidth * 0.5f;
    private final float rightWidth = screenWidth * 0.5f;
    private final float characterSize = screenHeight * 0.5f;


    public NewPlayerScreen() {
        super();
        Gdx.app.log("NewPlayerScreen", "create() called");
        loadData();
        createScreen();
        createEvent();
    }

    private void createEvent() {
        clickButtonEventConsumer = event -> {
            Gdx.app.log("Event", "ClickButtonEvent received");
            if (playerNameField != null && playerNameField.getStage() != null) {
                playerNameField.getStage().setKeyboardFocus(null);
                Gdx.input.setOnscreenKeyboardVisible(false); // Nếu cần ẩn bàn phím luôn trên mobile
            }
        };
    }

    private void addEvent() {
        MainGame.getEvM().subscribe(ClickButtonEvent.class, clickButtonEventConsumer);
    }

    private void loadData() {
        knightDataManager = new KnightDataManager();
    }

    public static void loadingAsset() {
        for (int i = 1; i <= 10; i++) { // tùy số lượng knight
            String knightId = (i == 10 ? i : "0" + i) + "Knight";
            MainGame.getAsM().load(CHARACTER + knightId + ".atlas", TextureAtlas.class);
        }
        MainGame.getAsM().load(SKILL_SKILL, TextureAtlas.class);
        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
    }

    @Override
    protected void createScreen() {
        createBackground();
        createTitle("New Player Screen", rootGroup);

        knightIds.clear();
        for (int i = 1; i <= 10; i++) knightIds.add(String.format("%02dKnight", i));

// Nhân vật bên trái
        knightAnimImage = createCharacterImage(getCurrentKnightData().knightId, "idle");
        knightAnimImage.setSize(characterSize, characterSize);
        knightAnimImage.setPosition(leftWidth - characterSize, screenHeight * 0.6f - characterSize / 2);
        rootGroup.addActor(knightAnimImage);

// TextField nhập tên Player (bên phải, trên cùng)
        TextureRegion bgRegion = MainGame.getAsM().getRegion(UI_WOOD, "bar_out_096");
        playerNameField = new UITextField("", bgRegion)
            .name("playerNameField")
            .size(rightWidth * 0.5f, 90)
            .pos(leftWidth, screenHeight * 0.7f)
            .align(Align.center)
            .fontScale(1.5f)
            .onEnter(()-> {
                String input = playerNameField.getText().trim();
                if (input.isEmpty()) {
                    playerNameField.setMessageText("Please enter your name!");
                }
                playerNameField.getStage().setKeyboardFocus(null);
            })
            .message("Enter your name...")
            .maxLength(15)
            .parent(rootGroup);



// Thông số (bên phải, dưới tên)
        knightStatsLabel = new UILabel(getCurrentKnightStats());
        knightStatsLabel.setSize(rightWidth * 0.5f, screenHeight * 0.5f);
        knightStatsLabel.setWrap(true);
        knightStatsLabel.setFontScale(1.5f);
        knightStatsLabel.setAlignment(Align.topLeft);
        knightStatsLabel.setPosition(leftWidth, screenHeight * 0.2f);
        rootGroup.addActor(knightStatsLabel);

        // Nút chọn knight (bên phải, cuối cùng)
        TextureRegion upSelect = MainGame.getAsM().getRegion(UI_WOOD, "btn_up");
        TextureRegion downSelect = MainGame.getAsM().getRegion(UI_WOOD, "btn_down");
        UIButton btnSelect = new UIButton("Select", upSelect,downSelect)
            .size(screenWidth * 0.2f, 70)
            .fontScale(2)
            .pos(screenWidth * 0.4f, screenHeight * 0.13f)
            .onClick(() -> {
                String nameInput = playerNameField.getText().trim();
                if (nameInput.isEmpty()) {
                    playerNameField.setText("");
                    playerNameField.setMessageText("Please enter your name!");
                    return;
                }
                playerName = nameInput; // cập nhật biến nếu cần dùng sau
                KnightBaseData selectedKnight = getCurrentKnightData();
                Gdx.app.log("KnightSelected", "Player: " + playerName + ", Knight: " + selectedKnight.knightId + " - " + selectedKnight.name);
                // TODO: Lưu playerName và selectedKnight cho player, tạo entity, chuyển scene...
            });
        rootGroup.addActor(btnSelect);


        // Lấy region gốc
        TextureRegion upRegion = MainGame.getAsM().getRegion(UI_WOOD, "nextb_up_009");
        TextureRegion downRegion = MainGame.getAsM().getRegion(UI_WOOD, "nextb_down_010");

        // Nút Next (bên phải dưới cùng)
        UIButton btnNext = new UIButton(upRegion, downRegion)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.8f, screenHeight * 0.1f)
            .onClick(this::showNextKnight);
        rootGroup.addActor(btnNext);

// Clone để tránh ảnh hưởng tới atlas gốc
        TextureRegion upFlip = new TextureRegion(upRegion);
        TextureRegion downFlip = new TextureRegion(downRegion);

// Lật ngang (true: lật ngang, false: không lật dọc)
        upFlip.flip(true, false);
        downFlip.flip(true, false);

// Nút Prev (bên trái dưới cùng)
        UIButton btnPrev = new UIButton(upFlip, downFlip)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.1f, screenHeight * 0.1f)
            .onClick(this::showPrevKnight).debug(false);
        rootGroup.addActor(btnPrev);

// Nút Close (bên trái trên cùng)
        createCloseButton(ScreenType.MENU_GAME);
    }

    private KnightBaseData getCurrentKnightData() {
        return knightDataManager.knightList.get(currentKnightIndex);
    }

    private String getCurrentKnightStats() {
        KnightBaseData data = getCurrentKnightData();
        return "Name:        " + data.name +
            "\nHP:              " + data.hp +
            "\nMP:              " + data.mp +
            "\nAttack:        " + data.atk +
            "\nDefense:     " + data.def +
            "\nAgility:         " + data.agi +
            "\nCrit:              " + data.crit +
            /*"\nSkills: " + data.skills.toString() +*/
            "\n      " + data.desc;
    }

    private Image createCharacterImage(String knightId, String animationName) {
        TextureAtlas atlas = MainGame.getAsM().get(CHARACTER + knightId + ".atlas", TextureAtlas.class);
        Array<TextureAtlas.AtlasRegion> idleFrames = atlas.findRegions(animationName);
        Animation<TextureRegion> anim = new Animation<>(0.1f, idleFrames, Animation.PlayMode.LOOP);
        return new Image(anim.getKeyFrame(0)) {
            float stateTime = 0f;

            @Override
            public void act(float delta) {
                super.act(delta);
                stateTime += delta;
                setDrawable(new TextureRegionDrawable(anim.getKeyFrame(stateTime, true)));
            }
        };
    }

    private void showNextKnight() {
        currentKnightIndex = (currentKnightIndex + 1) % knightIds.size;
        updateKnightDisplay();
    }

    private void showPrevKnight() {
        currentKnightIndex = (currentKnightIndex - 1 + knightIds.size) % knightIds.size;
        updateKnightDisplay();
    }

    private void updateKnightDisplay() {
        // Xoá nhân vật cũ
        knightAnimImage.remove();
        knightAnimImage = createCharacterImage(getCurrentKnightData().knightId, "idle");
        knightAnimImage.setSize(characterSize, characterSize);
        knightAnimImage.setPosition(leftWidth - characterSize, screenHeight * 0.5f - characterSize / 2);
        rootGroup.addActor(knightAnimImage);

// Update info
        knightStatsLabel.setText(getCurrentKnightStats());

    }

    private void createBackground() {
        String namePopup = "popup_000";
        TextureRegion skill = MainGame.getAsM().getRegion(UI_WOOD, "popup_000");
        UIImage popup = new UIImage(skill).name(namePopup).parent(rootGroup).bounds(0, 0, screenWidth, screenHeight).debug(false);
    }

    private void createCharacter(String nameAtlas, String animation, int i) {
        float posX = 100;
        float spacing = 200;
        TextureAtlas atlas = MainGame.getAsM().get(nameAtlas, TextureAtlas.class);

        Array<TextureAtlas.AtlasRegion> idleFrames = atlas.findRegions(animation);

        if (idleFrames.size == 0) return;

        Animation<TextureRegion> anim = new Animation<>(0.1f, idleFrames, Animation.PlayMode.LOOP);

        Image animImage = createAnimatedImage(anim);
        animImage.setPosition(posX + (i - 1) * spacing, 100);
        animImage.setName(nameAtlas + animation);
        animImage.setSize(screenHeight * 0.8f, screenHeight * 0.8f);
        animImage.debug();

        animImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedKnight = event.getListenerActor().getName();
                Gdx.app.log("KnightSelected", selectedKnight);
                // Lưu lựa chọn knight
                MainGame.getScM().showScreen(ScreenType.NEW_PLAYER);
            }
        });

        rootGroup.addActor(animImage);
    }


    private Image createAnimatedImage(Animation<TextureRegion> animation) {
        return new Image(animation.getKeyFrame(0)) {
            float stateTime = 0f;

            @Override
            public void act(float delta) {
                super.act(delta);
                stateTime += delta;
                setDrawable(new TextureRegionDrawable(animation.getKeyFrame(stateTime, true)));
            }
        };
    }

    @Override
    public void show() {
        Gdx.app.log("NewPlayerScreen", "show() called");
        super.show();
        addEvent();
    }

    @Override
    protected void updateLogic(float delta) {
//        rootGroup.debugAll();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        updateLogic(delta);
        updateUI(delta);
    }

    @Override
    protected void updateUI(float delta) {
    }

    @Override
    public void dispose() {
        title.remove();
    }
}
