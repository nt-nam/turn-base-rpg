package com.game.screens.start;

import static com.game.utils.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;

public class NewPlayerScreen extends BaseScreen {

    private int currentKnightIndex = 0;
    private final Array<String> knightIds = new Array<>();
    private Image knightAnimImage; // actor animation
    private String[] knightNames = { "Knight A", "Knight B", "Knight C", "Knight D", "Knight E", "Knight F", "Knight G", "Knight H", "Knight I", "Knight J" }; // tuỳ bạn
    private UILabel knightNameLabel; // Label tên knight
    private UILabel knightStatsLabel; // Label mô tả/thông số

    public NewPlayerScreen() {
        super();
        createScreen();
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
        Gdx.app.log("NewPlayerScreen", "show() called");
        createBackground();
        createTitle("New Player Screen", rootGroup);

        // Khởi tạo danh sách knightId
        for (int i = 1; i <= 10; i++) {
            knightIds.add(String.format("%02dKnight", i));
        }

        // Khởi tạo image nhân vật (animation)
        knightAnimImage = createCharacterImage(knightIds.get(currentKnightIndex), "idle");
        knightAnimImage.setSize(screenHeight * 0.7f, screenHeight * 0.7f);
        knightAnimImage.setPosition((screenWidth - knightAnimImage.getWidth()) / 2f, screenHeight * 0.3f);
        rootGroup.addActor(knightAnimImage);

        // Label tên nhân vật
        knightNameLabel = new UILabel(knightNames[currentKnightIndex]);
        knightNameLabel.setPosition(screenWidth / 2f, screenHeight * 0.22f, Align.center);
        rootGroup.addActor(knightNameLabel);

        // Label stats/mô tả (giả lập, thay bằng hàm lấy stats thực)
        knightStatsLabel = new UILabel(getKnightStats(knightIds.get(currentKnightIndex)));
        knightStatsLabel.setPosition(screenWidth / 2f, screenHeight * 0.16f, Align.center);
        rootGroup.addActor(knightStatsLabel);

        // Nút NEXT
        UIButton btnNext = new UIButton(MainGame.getAsM().getRegion(UI_WOOD, "next_up_005"),
            MainGame.getAsM().getRegion(UI_WOOD, "next_down_006"))
            .size(screenHeight * 0.18f, screenHeight * 0.18f)
            .pos(screenWidth * 0.80f, screenHeight * 0.42f)
            .onClick(this::showNextKnight);
        rootGroup.addActor(btnNext);

        // Nút PREV
        UIButton btnPrev = new UIButton(MainGame.getAsM().getRegion(UI_WOOD, "next_up_005"),
            MainGame.getAsM().getRegion(UI_WOOD, "next_down_006"))
            .size(screenHeight * 0.18f, screenHeight * 0.18f)
            .pos(screenWidth * 0.02f, screenHeight * 0.42f)
            .onClick(this::showPrevKnight);
        rootGroup.addActor(btnPrev);

        // Nút chọn knight
        UIButton btnSelect = new UIButton("Chọn", "default")
            .size(220, 70)
            .pos(screenWidth/2f - 110, screenHeight * 0.07f)
            .onClick(() -> {
                String selectedKnight = knightIds.get(currentKnightIndex);
                Gdx.app.log("KnightSelected", selectedKnight);
                // Xử lý lưu chọn nhân vật tại đây
            });
        rootGroup.addActor(btnSelect);

//        TextureRegion skill = MainGame.getAsM().getRegion(SKILL_SKILL, "blue1");
//        Image skillImage = new Image(skill);
//        skillImage.setPosition(100, 100);
//        rootGroup.addActor(skillImage);
//
//
//        for (int i = 1; i <= 2; i++) {
//            String knightId = String.format("%02dKnight", i);
//            createCharacter(CHARACTER + knightId + ".atlas","deal",i);
//        }
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
    private String getKnightStats(String knightId) {
        // Lấy dữ liệu thực từ JSON/class của bạn, ở đây demo tạm
        if (knightId.equals("01Knight")) return "Máu: 120\nTấn công: 20\nThủ: 10";
        if (knightId.equals("02Knight")) return "Máu: 100\nTấn công: 25\nThủ: 8";
        // ... tiếp tục các knight khác
        return "Máu: 110\nTấn công: 22\nThủ: 9";
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
        // Xoá actor animation cũ
        knightAnimImage.remove();
        knightAnimImage = createCharacterImage(knightIds.get(currentKnightIndex), "idle");
        knightAnimImage.setSize(screenHeight * 0.7f, screenHeight * 0.7f);
        knightAnimImage.setPosition((screenWidth - knightAnimImage.getWidth()) / 2f, screenHeight * 0.3f);
        knightAnimImage.debug();
        rootGroup.addActor(knightAnimImage);

        // Update label tên
        knightNameLabel.setText(knightNames[currentKnightIndex]);
        // Update label stats/mô tả
        knightStatsLabel.setText(getKnightStats(knightIds.get(currentKnightIndex)));
    }
    private void createBackground() {
        String namePopup = "popup_000";
        TextureRegion skill = MainGame.getAsM().getRegion(UI_WOOD, "popup_000");
        UIImage popup = new UIImage(skill).name(namePopup).parent(rootGroup).bounds(0,0,screenWidth,screenHeight).debug(true);

//        UIButton button = new UIButton(MainGame.getAsM().getRegion(UI_WOOD, "next_up_005"),MainGame.getAsM().getRegion(UI_WOOD, "next_down_006"))
//            .parent(rootGroup).size(screenHeight*0.2f,screenHeight*0.2f).pos(screenWidth*0.9f,screenHeight*0.4f)
//            .onClick(()->{})
//            .debug(true);

    }

    private void createCharacter(String nameAtlas,String animation,int i) {
        float posX = 100;
        float spacing = 200;
        TextureAtlas atlas = MainGame.getAsM().get(nameAtlas, TextureAtlas.class);

        Array<TextureAtlas.AtlasRegion> idleFrames = atlas.findRegions(animation);

        if (idleFrames.size == 0) return;

        Animation<TextureRegion> anim = new Animation<>(0.1f, idleFrames, Animation.PlayMode.LOOP);

        Image animImage = createAnimatedImage(anim);
        animImage.setPosition(posX + (i - 1) * spacing, 100);
        animImage.setName(nameAtlas+animation);
        animImage.setSize(screenHeight*0.8f, screenHeight*0.8f);
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
        title.remove();
    }
}
