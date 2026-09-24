package com.game.screens.service;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Mission;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;

import java.util.List;

/**
 * Quest Log: hien danh sach nhiem vu tu {@link GameSessionManager#missionList}.
 * Moi mission hien title, mo ta, tien do (progress/target) va trang thai.
 */
public class QuestScreen extends BaseScreen {

    public QuestScreen() {
        super();
        createScreen();
    }

    @Override
    protected void createScreen() {
        // Bang nen
        NinePatch board = new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20);
        new UIImage(board).name("board")
            .bounds(screenWidth * 0.15f, screenHeight * 0.1f, screenWidth * 0.7f, screenHeight * 0.8f)
            .parent(rootGroup);

        // Tieu de
        new UILabel("NHAT KY NHIEM VU", BMF)
            .size(screenWidth * 0.7f, screenHeight * 0.1f)
            .pos(screenWidth * 0.15f, screenHeight * 0.8f)
            .align(Align.center)
            .fontScale(1.8f)
            .parent(rootGroup);

        List<Mission> missions = GameSessionManager.getInstance().missionList;

        if (missions == null || missions.isEmpty()) {
            new UILabel("Chua co nhiem vu nao.", BMF)
                .size(screenWidth * 0.7f, screenHeight * 0.1f)
                .pos(screenWidth * 0.15f, screenHeight * 0.45f)
                .align(Align.center)
                .fontScale(1.2f)
                .parent(rootGroup);
        } else {
            float y = screenHeight * 0.72f;
            float rowH = screenHeight * 0.13f;
            for (Mission m : missions) {
                boolean done = m.targetAmount > 0 && m.progress >= m.targetAmount;

                new UILabel(m.title, BMF)
                    .size(screenWidth * 0.5f, rowH * 0.4f)
                    .pos(screenWidth * 0.2f, y)
                    .fontScale(1.2f)
                    .color(done ? Color.valueOf("43A047") : Color.WHITE)
                    .parent(rootGroup);

                new UILabel(m.description, BMF)
                    .size(screenWidth * 0.5f, rowH * 0.3f)
                    .pos(screenWidth * 0.2f, y - rowH * 0.35f)
                    .fontScale(0.9f)
                    .color(Color.valueOf("cccccc"))
                    .parent(rootGroup);

                String status = done ? "HOAN THANH" : (m.progress + "/" + m.targetAmount);
                new UILabel(status, BMF)
                    .size(screenWidth * 0.15f, rowH * 0.4f)
                    .pos(screenWidth * 0.65f, y)
                    .align(Align.right)
                    .fontScale(1.1f)
                    .color(done ? Color.valueOf("43A047") : Color.valueOf("e0a44a"))
                    .parent(rootGroup);

                y -= rowH;
                if (y < screenHeight * 0.15f) break; // trong pham vi bang
            }
        }

        // Nut dong -> WorldMap
        createCloseButton(ScreenType.WORLD_MAP);
    }
}
