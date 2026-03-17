package com.game.screens.start;

import com.game.utils.Constants;

import static com.game.utils.Constants.*;

import com.badlogic.ashley.core.Entity;
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
import com.game.ecs.component.PlayerSelectedComponent;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITextField;
import com.game.ui.hud.NotificationPP;
import com.game.utils.DataHelper;
import com.game.utils.JsonSaver;
import com.game.models.entity.Account;
import com.game.models.entity.CharacterBase;
//import com.game.utils.JsonValueHelper;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Lineup;
import com.game.models.entity.Profile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class NewPlayerScreen extends BaseScreen {
    private int currentKnightIndex = 0;
    private List<CharacterBase> characterBaseDataList;

    private Image knightAnimImage;
    private UILabel knightStatsLabel;
    private UITextField playerNameField;

    private final float leftWidth = screenWidth * 0.5f;
    private final float rightWidth = screenWidth * 0.5f;
    private final float characterSize = screenHeight * 0.5f;

    public NewPlayerScreen() {
        super();
        Gdx.app.log("NewPlayerScreen", "create() called");
        createScreen();
    }

    public static void loadingAsset() {
        List<CharacterBase> characterBaseList = DataHelper.loadCharacterBaseList();
        for (CharacterBase baseData : characterBaseList) {
            MainGame.getAsM().load(CHARACTER_ATLAS + baseData.nameRegion + ".atlas", TextureAtlas.class);
        }
        MainGame.getAsM().load(SKILL_SKILL, TextureAtlas.class);
        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
        MainGame.getAsM().load(UI_POPUP, TextureAtlas.class);
    }

    @Override
    protected void createScreen() {
        createBackground();
        characterBaseDataList = GameSessionManager.getInstance().characterBaseList;
        List<Account> account = DataHelper.loadAccountList(true);
        if (characterBaseDataList.isEmpty()) {
            throw new RuntimeException("No knights found!");
        }

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
                    playerNameField.setMessageText("'............'");
                    return;
                }
                playerNameField.getStage().setKeyboardFocus(null);
            })
            .message("'............'")
            .maxLength(15)
            .parent(rootGroup);

        // Thông số knight
        knightStatsLabel = new UILabel(getCurrentKnightStats(), MainGame.getAsM().getFont(BMF));
        knightStatsLabel.setSize(rightWidth * 0.5f, screenHeight * 0.5f);
        knightStatsLabel.setWrap(true);
        knightStatsLabel.setFontScale(1f);
        knightStatsLabel.setAlignment(Align.topLeft);
        knightStatsLabel.setPosition(leftWidth, screenHeight * 0.2f);
        rootGroup.addActor(knightStatsLabel);

        // Nút chọn knight
        NinePatch upSelect = new NinePatch(MainGame.getAsM().getRegion(UI_WOOD, "btn_up"), 6, 6, 0, 0);
        NinePatch downSelect = new NinePatch(MainGame.getAsM().getRegion(UI_WOOD, "btn_down"), 6, 6, 0, 0);
        UIButton btnSelect = new UIButton("Tạo", upSelect, downSelect)
            .size(screenWidth * 0.2f, 70)
            .fontScale(2)
            .pos(screenWidth * 0.27f, screenHeight * 0.13f)
            .onClick(() -> {
                String nameInput = playerNameField.getText().trim();
                if (nameInput.isEmpty()) {
                    playerNameField.setText("");
                    playerNameField.setMessageText("'............'");
                    rootGroup.addActor(NotificationPP.ppr(screenWidth, screenHeight, "Vui lòng nhập tên!"));
                    return;
                }
                if (account != null) {
                    if (DataHelper.get(account, "id", nameInput) != null) {
                        playerNameField.getStage().setKeyboardFocus(null);
                        playerNameField.setMessageText("'............'");
                        rootGroup.addActor(NotificationPP.ppr(screenWidth, screenHeight, "Người chơi này đã tồn tại, vui lòng nhập tên khác!!"));
                        return;
                    }
                }



                // Tạo entity sự kiện chọn knight (chuẩn ECS)
                Entity eventEntity = engine.createEntity();
                PlayerSelectedComponent comp = engine.createComponent(PlayerSelectedComponent.class);
                comp.playerName = nameInput;
                comp.knightId = getCurrentKnightId();
                eventEntity.add(comp);
                engine.addEntity(eventEntity);

                playerNameField.getStage().setKeyboardFocus(null);
                GameSessionManager.getInstance().playerName = nameInput;
                GameSessionManager.getInstance().selectedCharacterId = getCurrentKnightId();

                Profile profile = new Profile(nameInput, getCurrentKnightId());
                GameSessionManager.getInstance().profile = profile;

                List<Lineup> lineups = new ArrayList<>();
                Lineup g = new Lineup();
                g.grid = "1,1";
                g.characterId = "character0";
                g.nameRegion = getCurrentKnightId();
                lineups.add(g);
                JsonSaver.saveObject(Constants.playerPath("lineup.json"), lineups);

                createFullPatty();

                List<Account> accounts = DataHelper.loadAccountList(true);
                if (accounts == null) {
                    accounts = new ArrayList<>();
                }
                Account a = new Account();
                a.id = nameInput;
                a.level = 1;
                a.characterSelect = getCurrentKnightId();
                accounts.add(a);

                JsonSaver.saveObject("data/select/" + nameInput + "/info.json", GameSessionManager.getInstance().profile);
                JsonSaver.saveObject(MAININFO_JSON_LOCAL, accounts);
                JsonSaver.createAccount();

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

    private void createFullPatty() {
        List<JsonObject> jsonObjectList = new ArrayList<>();
        // Tạo đối tượng JsonObject để xây dựng cấu trúc JSON
        JsonObject equip = new JsonObject();
        equip.addProperty("weapon", "empty");
        equip.addProperty("armor", "empty");
        equip.addProperty("jewelry", "empty");
        equip.addProperty("support", "empty");

        JsonObject character = new JsonObject();
        character.addProperty("characterId", "character0");
        character.addProperty("nameRegion", getCurrentKnightId());
        character.addProperty("grid", "1,1");
        character.addProperty("star", 0);
        character.addProperty("level", 1);
        character.add("equip", equip);

        jsonObjectList.add(character);

        // Chuyển đối tượng Java thành JSON string
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObjectList);

        JsonSaver.saveString(Constants.playerPath("hero_full.json"), jsonString);
        // Lưu vào file trong bộ nhớ trong của ứng dụng
//            FileHandle file = Gdx.files.local("data/character.json");
//            file.writeString(jsonString, false); // false để ghi đè nếu tệp đã tồn tại
//            System.out.println("Character JSON saved to " + file.path());

    }

    private String getCurrentKnightId() {
        return characterBaseDataList.get(currentKnightIndex).nameRegion;
    }

    private String getCurrentKnightStats() {
        CharacterBase data = characterBaseDataList.get(currentKnightIndex);
        return "Tên:               " + data.name +
            "\nHP:                          " + data.hp +
            "\nMP:                          " + data.mp +
            "\nCông:                     " + data.atk +
            "\nThủ:                         " + data.def +
            "\nNhanh nhẹn:         " + data.agi +
            "\nChí mạng:              " + data.crit +
            "\n      " + data.desc;
    }

    private Image createCharacterImage(String characterId, String animationName) {
        TextureAtlas atlas = MainGame.getAsM().get(CHARACTER_ATLAS + characterId + ".atlas", TextureAtlas.class);
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
        currentKnightIndex = (currentKnightIndex + 1) % characterBaseDataList.size();
        updateKnightDisplay();
    }

    private void showPrevKnight() {
        currentKnightIndex = (currentKnightIndex - 1 + characterBaseDataList.size()) % characterBaseDataList.size();
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
        TextureRegion origin = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
        UIImage popup = new UIImage(origin).nine(origin, 30, 30, 30, 30)
            .name(namePopup)
            .parent(rootGroup)
            .bounds(screenWidth * 0.2f, screenHeight * 0.1f, screenWidth * 0.6f, screenHeight * 0.8f);
    }

    @Override
    public void hide() {
        super.hide();
        engine.removeAllEntities();
        engine.removeAllSystems();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
