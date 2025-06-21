package com.game.screens.start;

import static com.game.utils.Constants.*;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.ecs.component.PlayerSelectedComponent;
import com.game.ecs.factory.CharacterLoader;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITextField;
import com.game.utils.data.GameSession;

public class NewPlayerScreen extends BaseScreen {
    private int currentKnightIndex = 0;
    private ImmutableArray<Entity> knightEntities;
    private static final ComponentMapper<CharacterBaseDataComponent> baseDataMapper = ComponentMapper.getFor(CharacterBaseDataComponent.class);

    private Image knightAnimImage;
    private UILabel knightStatsLabel;
    private UITextField playerNameField;

    private final float leftWidth = screenWidth * 0.5f;
    private final float rightWidth = screenWidth * 0.5f;
    private final float characterSize = screenHeight * 0.5f;

    public NewPlayerScreen() {
        super();
        Gdx.app.log("NewPlayerScreen", "create() called");
        Gdx.app.log("NewPlayerScreen", "create() called, baseDataMapper=" + baseDataMapper);
        createScreen();
    }

    public static void loadingAsset() {
        CharacterLoader.loadAllCharacters(engine);
        for (int i = 1; i <= 5; i++) { // tùy số lượng knight
            String knightId = (i == 10 ? i : "0" + i) + "Knight";
            MainGame.getAsM().load(CHARACTER + knightId + ".atlas", TextureAtlas.class);
        }
        MainGame.getAsM().load(SKILL_SKILL, TextureAtlas.class);
        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
        MainGame.getAsM().load(UI_POPUP, TextureAtlas.class);
    }

    @Override
    protected void createScreen() {
        createBackground();
//        createTitle("New Player Screen", rootGroup);

        // Lấy danh sách knight entity (chỉ loadAllKnights một lần duy nhất ở chỗ khởi tạo game)
        knightEntities = MainGame.getEngine().getEntitiesFor(Family.all(CharacterBaseDataComponent.class).get());
        if (knightEntities.size() == 0) throw new RuntimeException("No knights found!");

        // Tạo nhân vật bên trái
        knightAnimImage = createCharacterImage(getCurrentKnightId(), "idle");
        knightAnimImage.setSize(characterSize, characterSize);
        knightAnimImage.setPosition(leftWidth - characterSize, screenHeight * 0.6f - characterSize / 2);
        rootGroup.addActor(knightAnimImage);

        // TextField nhập tên Player
        TextureRegion bgRegion = MainGame.getAsM().getRegion(UI_WOOD, "bar_out_096");
        playerNameField = new UITextField("", bgRegion)
            .name("playerNameField")
            .size(rightWidth * 0.5f, 90)
            .pos(leftWidth, screenHeight * 0.7f)
            .align(Align.center)
            .fontScale(1.5f)
            .onEnter(() -> {
                String input = playerNameField.getText().trim();
                if (input.isEmpty()) {
                    playerNameField.setMessageText("Please enter your name!");
                    return;
                }
                playerNameField.getStage().setKeyboardFocus(null);
            })
            .message("Enter your name...")
            .maxLength(15)
            .parent(rootGroup);

        // Thông số knight
        knightStatsLabel = new UILabel(getCurrentKnightStats());
        knightStatsLabel.setSize(rightWidth * 0.5f, screenHeight * 0.5f);
        knightStatsLabel.setWrap(true);
        knightStatsLabel.setFontScale(1.5f);
        knightStatsLabel.setAlignment(Align.topLeft);
        knightStatsLabel.setPosition(leftWidth, screenHeight * 0.2f);
        rootGroup.addActor(knightStatsLabel);

        // Nút chọn knight
        NinePatch upSelect = new NinePatch(MainGame.getAsM().getRegion(UI_WOOD, "btn_up"),6,6,0,0);
        NinePatch downSelect = new NinePatch(MainGame.getAsM().getRegion(UI_WOOD, "btn_down"),6,6,0,0);
        UIButton btnSelect = new UIButton("Select", upSelect, downSelect)
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
                // Tạo entity sự kiện chọn knight (chuẩn ECS)
                Entity eventEntity = MainGame.getEngine().createEntity();
                PlayerSelectedComponent comp = MainGame.getEngine().createComponent(PlayerSelectedComponent.class);
                comp.playerName = nameInput;
                comp.knightId = getCurrentKnightId();
                eventEntity.add(comp);
                MainGame.getEngine().addEntity(eventEntity);

                playerNameField.getStage().setKeyboardFocus(null);
                GameSession.playerName = nameInput;
                GameSession.selectedCharacterId = getCurrentKnightId();
                MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
            });
        rootGroup.addActor(btnSelect);

        // Nút Next và Prev
        TextureRegion upRegion = MainGame.getAsM().getRegion(UI_WOOD, "nextb_up_009");
        TextureRegion downRegion = MainGame.getAsM().getRegion(UI_WOOD, "nextb_down_010");
        UIButton btnNext = new UIButton(upRegion, downRegion)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.8f, screenHeight * 0.1f)
            .onClick(this::showNextKnight);
        rootGroup.addActor(btnNext);

        TextureRegion upFlip = new TextureRegion(upRegion);
        TextureRegion downFlip = new TextureRegion(downRegion);
        upFlip.flip(true, false);
        downFlip.flip(true, false);
        UIButton btnPrev = new UIButton(upFlip, downFlip)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.1f, screenHeight * 0.1f)
            .onClick(this::showPrevKnight).debug(false);
        rootGroup.addActor(btnPrev);

        // Nút Close
        createCloseButton(ScreenType.MENU_GAME);
    }

    /**
     * Helper lấy entity hiện tại
     */
    private Entity getCurrentKnightEntity() {
        return knightEntities.get(currentKnightIndex);
    }

    private CharacterBaseDataComponent getCurrentKnightData() {
        Gdx.app.log("NewPlayerScreen", "getCurrentKnightData, baseDataMapper=" + baseDataMapper);
        return baseDataMapper.get(getCurrentKnightEntity());
    }

    private String getCurrentKnightId() {
        return getCurrentKnightData().characterId;
    }

    private String getCurrentKnightStats() {
        CharacterBaseDataComponent data = getCurrentKnightData();


        return "Name:        " + data.name +
            "\nHP:              " + data.hp +
            "\nMP:              " + data.mp +
            "\nAttack:        " + data.atk +
            "\nDefense:     " + data.def +
            "\nAgility:         " + data.agi +
            "\nCrit:              " + data.crit +
            "\n      " + data.desc;
    }

    private Image createCharacterImage(String CharacterId, String animationName) {
        TextureAtlas atlas = MainGame.getAsM().get(CHARACTER + CharacterId + ".atlas", TextureAtlas.class);
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
        currentKnightIndex = (currentKnightIndex + 1) % knightEntities.size();
        updateKnightDisplay();
    }

    private void showPrevKnight() {
        currentKnightIndex = (currentKnightIndex - 1 + knightEntities.size()) % knightEntities.size();
        updateKnightDisplay();
    }

    private void updateKnightDisplay() {
        knightAnimImage.remove();
        knightAnimImage = createCharacterImage(getCurrentKnightId(), "idle");
        knightAnimImage.setSize(characterSize, characterSize);
        knightAnimImage.setPosition(leftWidth - characterSize, screenHeight * 0.6f - characterSize / 2);
        rootGroup.addActor(knightAnimImage);

        knightStatsLabel.setText(getCurrentKnightStats());
    }

    private void createBackground() {
        String namePopup = "popup_000";
//        TextureRegion origin = MainGame.getAsM().getRegion(UI_WOOD, "popup_000");
        TextureRegion origin = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        UIImage popup = new UIImage(origin).nine(origin,30,30,30,30)
            .name(namePopup)
            .parent(rootGroup)
            .bounds(screenWidth*0.2f, screenHeight*0.1f, screenWidth*0.6f, screenHeight*0.8f);
    }

    @Override
    public void show() {
        Gdx.app.log("NewPlayerScreen", "show() called");
        super.show();
    }

    @Override
    protected void updateLogic(float delta) {
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
        for (int i = 1; i <= 5; i++) { // tùy số lượng knight
            String knightId = (i == 10 ? i : "0" + i) + "Knight";
//            if(!knightId.equals(getCurrentKnightId()))
            MainGame.getAsM().unload(CHARACTER + knightId + ".atlas");
        }
        MainGame.getAsM().unload(CHARACTER + getCurrentKnightId() + ".atlas");
        ;
        title.remove();
    }
}
