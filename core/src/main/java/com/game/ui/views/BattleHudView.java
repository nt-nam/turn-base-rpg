package com.game.ui.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.models.entity.Reward;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.Constants;

import java.util.List;

public class BattleHudView {
    private Group rootGroup;
    private float screenWidth;
    private float screenHeight;
    private Runnable onResumeClicked;

    public BattleHudView(Group rootGroup, float screenWidth, float screenHeight, Runnable onResumeClicked) {
        this.rootGroup = rootGroup;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.onResumeClicked = onResumeClicked;
    }

    public void createPopup(Runnable onHomeClicked) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);

        new UIButton(
            MainGame.getAsM().getRegion(Constants.UI_WOOD, "resume_up_007"),
            MainGame.getAsM().getRegion(Constants.UI_WOOD, "resume_down_008"))
            .name("resume")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.9f, screenHeight * 0.8f)
            .onClick(onResumeClicked)
            .parent(rootGroup);

        TextureRegion origin = MainGame.getAsM().getRegion(Constants.UI_POPUP, "tile_origin");
        new UIImage(origin).nine(origin, 30, 30, 30, 30)
            .name("board")
            .parent(rootGroup)
            .bounds(screenWidth * 0.2f, screenHeight * 0.1f, screenWidth * 0.6f, screenHeight * 0.8f);

        new UIButton(MainGame.getAsM().getRegion(Constants.UI_POPUP, "menu"))
            .name("menu")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.3f, screenHeight * 0.18f)
            .onClick(() -> {

            })
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(Constants.UI_POPUP, "home"))
            .name("home")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.45f, screenHeight * 0.18f)
            .onClick(onHomeClicked)
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(Constants.UI_POPUP, "setting"))
            .name("setting")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.6f, screenHeight * 0.18f)
            .onClick(() -> {

            })
            .parent(rootGroup);
        hidePopupPause();
    }

    public void showPopupPause() {
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("board").setVisible(true);
        rootGroup.findActor("home").setVisible(true);
        rootGroup.findActor("menu").setVisible(true);
        rootGroup.findActor("setting").setVisible(true);
    }

    public void hidePopupPause() {
        rootGroup.findActor("overlay").setVisible(false);
        rootGroup.findActor("board").setVisible(false);
        rootGroup.findActor("home").setVisible(false);
        rootGroup.findActor("menu").setVisible(false);
        rootGroup.findActor("setting").setVisible(false);
    }

    public void showPopupWin(List<Reward> rewardList) {
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("board").setVisible(true);
        rootGroup.findActor("home").setVisible(true);
        rootGroup.findActor("resume").setVisible(false);

        rootGroup.addActor(new UIGroup().child(
            new UILabel("Chiến thắng", Constants.BMF).pos(screenWidth * 0.2f, screenHeight * 0.6f).size(screenWidth * 0.6f, screenHeight * 0.3f).fontScale(4).align(Align.center)
        ));

        int i = 0;
        float sizeTile = screenHeight * 0.2f;
        float sizeItem = sizeTile * 0.6f;
        float posH = screenHeight * 0.4f;
        float posStart = screenWidth * 0.5f - sizeTile * 0.5f * (rewardList != null ? rewardList.size() : 0);
        
        if (rewardList != null) {
            for (Reward reward : rewardList) {
                rootGroup.addActor(
                    new UIGroup().child(
                        new UIImage(Constants.UI_POPUP, "tile_rarity0").size(sizeTile, sizeTile),
                        new UIImage(Constants.UI_POPUP, reward.nameRegion).bounds(sizeTile * (0.2f), sizeTile * 0.2f, sizeItem, sizeItem),
                        new UILabel(reward.quantity + "", Constants.BMF).pos(sizeTile * (0.2f), sizeTile * 0.2f).color(Color.SKY).fontScale(1.2f)
                    ).pos(posStart + sizeTile * i, posH)
                );
                i++;
            }
        }
    }

    public void showPopupFail() {
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("board").setVisible(true);
        rootGroup.findActor("home").setVisible(true);

        rootGroup.addActor(new UIGroup().child(
            new UILabel("Thất bại", Constants.BMF).pos(screenWidth * 0.2f, screenHeight * 0.6f).size(screenWidth * 0.6f, screenHeight * 0.3f).fontScale(4).align(Align.center)
        ));
    }
}
