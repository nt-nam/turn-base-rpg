package com.game.screens.battle;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.managers.GameSessionManager;
import com.game.models.entity.BattleResult;
import com.game.models.entity.Reward;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;

/**
 * Man hien ket qua tran dau: Victory/Defeat + EXP nhan + danh sach reward.
 * Doc {@link GameSessionManager#lastBattleResult}. Nut "Tiep tuc" -> WorldMap.
 */
public class BattleResultScreen extends BaseScreen {

    public BattleResultScreen() {
        super();
        createScreen();
    }

    @Override
    protected void createScreen() {
        BattleResult result = GameSessionManager.getInstance().lastBattleResult;
        if (result == null) {
            result = new BattleResult();
        }

        // Nen mo toi
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.85f);
        pixmap.fill();
        Texture dim = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(dim).name("dim").bounds(0, 0, screenWidth, screenHeight).parent(rootGroup);

        // Bang ket qua
        NinePatch board = new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20);
        new UIImage(board).name("board")
            .bounds(screenWidth * 0.2f, screenHeight * 0.15f, screenWidth * 0.6f, screenHeight * 0.7f)
            .parent(rootGroup);

        // Tieu de Victory / Defeat
        String titleText = result.won ? "CHIEN THANG!" : "THAT BAI";
        Color titleColor = result.won ? Color.valueOf("43A047") : Color.valueOf("e05a5a");
        new UILabel(titleText, BMF)
            .size(screenWidth * 0.6f, screenHeight * 0.12f)
            .pos(screenWidth * 0.2f, screenHeight * 0.68f)
            .align(Align.center)
            .fontScale(2.2f)
            .color(titleColor)
            .parent(rootGroup);

        // EXP nhan
        new UILabel("EXP nhan duoc: +" + result.expGained, BMF)
            .size(screenWidth * 0.6f, screenHeight * 0.08f)
            .pos(screenWidth * 0.2f, screenHeight * 0.56f)
            .align(Align.center)
            .fontScale(1.3f)
            .parent(rootGroup);

        // Danh sach reward (neu thang)
        float y = screenHeight * 0.48f;
        if (result.won && result.rewards != null && !result.rewards.isEmpty()) {
            new UILabel("Phan thuong:", BMF)
                .size(screenWidth * 0.6f, screenHeight * 0.06f)
                .pos(screenWidth * 0.2f, y)
                .align(Align.center)
                .fontScale(1.1f)
                .parent(rootGroup);
            y -= screenHeight * 0.06f;
            for (Reward r : result.rewards) {
                new UILabel("- " + r.type + " " + r.nameRegion + " x" + r.quantity, BMF)
                    .size(screenWidth * 0.6f, screenHeight * 0.05f)
                    .pos(screenWidth * 0.2f, y)
                    .align(Align.center)
                    .fontScale(1.0f)
                    .parent(rootGroup);
                y -= screenHeight * 0.05f;
            }
        }

        // Nut Tiep tuc -> WorldMap
        new UIButton("Tiep tuc",
            MainGame.getAsM().getRegion(UI_WOOD, "btn_up"),
            MainGame.getAsM().getRegion(UI_WOOD, "btn_down"))
            .size(screenWidth * 0.24f, screenHeight * 0.1f)
            .pos(screenWidth * 0.38f, screenHeight * 0.2f)
            .fontScale(1.4f)
            .onClick(() -> MainGame.getScM().showScreen(ScreenType.WORLD_MAP))
            .parent(rootGroup);
    }
}
