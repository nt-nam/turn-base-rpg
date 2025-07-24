package com.game.screens.start;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ecs.component.PlayerSelectedComponent;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.GameSession;
import com.game.utils.DataHelper;
import com.game.utils.json.Account;

import java.util.List;

public class SelectPlayerScreen extends BaseScreen {
    private ScrollPane scrollPane;
    private Table table;
    private static List<Account> accounts;

    public SelectPlayerScreen() {
        super();
        createBackground();
    }

    public static void loadingAsset() {
        accounts = DataHelper.loadAccountList(true);
        if (accounts != null) {
            for (Account element : accounts) {
                MainGame.getAsM().load("atlas/characters/" + element.characterSelect + ".atlas", TextureAtlas.class);
            }
        }

        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);

    }

    private void createBackground() {
        new UIImage(new Texture(Gdx.files.internal("texture/battle/summer.png"))).size(screenWidth, screenHeight).parent(rootGroup);
        table = new Table();
        table.top();
        scrollPane = new ScrollPane(table);
        rootGroup.addActor(scrollPane);
        createCloseButton(ScreenType.MENU_GAME);
    }

    private void updateScrollAction() {
        if (table == null) return;
        for (Actor actor : table.getChildren()) {
            if (actor instanceof UIGroup) {
                UIGroup uiGroup = (UIGroup) actor;

                Vector2 v = new Vector2();
                uiGroup.localToStageCoordinates(v);

                // Tính khoảng cách từ phần tử đến trung tâm màn hình
                float distanceFromCenter = Math.abs(v.x - (screenWidth * 0.4f));  // Sử dụng screenWidth * 0.5f để tính khoảng cách từ trung tâm

                // Khoảng cách biên, điều chỉnh nếu cần thiết
                float margin = screenWidth * 0.2f; // Sử dụng một tỷ lệ tùy chỉnh (có thể điều chỉnh giá trị này)
                float distanceFromMargin = Math.max(0, distanceFromCenter - margin);  // Tính khoảng cách từ biên

                // Tính toán alpha và scale dựa trên khoảng cách từ biên
                // Khi gần trung tâm, alpha = 1, scale = 1
                float alpha = Math.max(0.0f, 1.0f - distanceFromMargin / margin);  // Alpha giảm dần khi ra biên
                float scale = Math.max(0.8f, 1.0f - distanceFromMargin / margin);  // Scale giảm dần khi ra biên

                // Cập nhật alpha và scale liên tục
                uiGroup.addAction(Actions.parallel(
                    Actions.alpha(alpha),  // Thay đổi alpha
                    Actions.scaleTo(scale, scale)  // Thay đổi scale
                ));
            }
        }
    }

    @Override
    public void show() {
        Gdx.app.log("SelectPlayerScreen", "show() called");
        super.show();
        accounts = DataHelper.loadAccountList(true);

        showScrollPane();

    }

    private void showScrollPane() {
        table.clear();
        UIGroup uiGroup;
        if (accounts != null) {
            NinePatch ninePatch = MainGame.getAsM().getRegion9patch(UI_POPUP, "origin", 20);
            int i = 0;
            for (Account element : accounts) {
                uiGroup = new UIGroup().size(screenWidth * 0.2f, screenHeight * 0.6f)
                    .pos(screenWidth * 0.2f, screenHeight * 0.1f * i)
                    .child(
                        new UIImage(ninePatch).size(screenWidth * 0.2f, screenHeight * 0.6f),
                        new UIImage(MainGame.getAsM().getRegion("atlas/characters/" + element.characterSelect + ".atlas", "idle")).pos(0, screenHeight * 0.11f).size(screenWidth * 0.2f, screenHeight * 0.4f),
                        new UILabel(element.id, BMF).size(screenWidth * 0.2f, screenHeight * 0.15f).pos(0, screenHeight * 0.43f).fontScale(1.5f).align(Align.center).warp(true).debug(false),
                        new UILabel("Cấp độ: " + element.level, BMF).pos(80, screenHeight * 0.05f).fontScale(1.5f).align(Align.center).debug(false)
//                        new UILabel(acc.getString("characterSelect"), BMF).pos(50, screenHeight*0.2f).fontScale(1f)
                    )
                    .onClick(() -> {
                        Entity eventEntity = engine.createEntity();
                        PlayerSelectedComponent comp = engine.createComponent(PlayerSelectedComponent.class);
                        comp.playerName = element.id;
                        comp.knightId = element.characterSelect;
                        eventEntity.add(comp);
                        engine.addEntity(eventEntity);

                        GameSession.playerName = element.id;
                        GameSession.selectedCharacterId = element.characterSelect;
                        GameSession.profile = DataHelper.loadProfile(true);
                        MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
                    }).parent(rootGroup);
                uiGroup.setOrigin(uiGroup.getWidth() / 2, uiGroup.getHeight() / 2);
                table.add(uiGroup).pad(screenWidth * 0.05f);
                table.padLeft(screenWidth * 0.35f);
                table.padRight(screenWidth * 0.35f);
                i++;
            }

            scrollPane.setSize(screenWidth * 0.9f, screenHeight * 0.8f);
            scrollPane.setPosition(screenWidth * 0.05f, screenHeight * 0.1f);
            scrollPane.setScrollingDisabled(false, true);
        } else {
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "origin", 20)).size(screenWidth * 0.5f, screenHeight * 0.5f).pos(screenWidth * 0.25f, screenHeight * 0.25f).parent(rootGroup);
            new UILabel("Chưa có nhân vật có sẵn  nào, Vui lòng tạo mới!!!", BMF).size(screenWidth * 0.5f, screenHeight * 0.5f).pos(screenWidth * 0.25f, screenHeight * 0.25f).align(Align.center).parent(rootGroup);
        }
    }


    @Override
    public void render(float delta) {
        super.render(delta);
        updateScrollAction();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
