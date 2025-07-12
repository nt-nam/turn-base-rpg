package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UITable;

public class RolePP {
    private static JsonValue useFor = null;
    private static int page = 0;

    public static Group pp(float w, float h) {
        UIGroup popup = new UIGroup().name("role").size(w, h);

        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(popup)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.4f, h * 0.05f, w * 0.6f, h * 0.9f);


        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnMission = new UIButton("Nhệm vụ", gray, green)
            .bounds((int) (w * 0.6f), (int) (h * 0.79f), w * 0.16f, h * 0.12f)
            .name("btnMission")
            .check(() -> {
                ((UIButton) popup.findActor("btnAchievement")).setChecked(false);
                ((UIButton) popup.findActor("btnMission")).setChecked(true);
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnAchievement = new UIButton("Thành tích", gray, green)
            .bounds((int) (w * 0.45f), (int) (h * 0.79f), w * 0.15f, h * 0.12f)
            .name("btnAchievement")
            .check(() -> {
                ((UIButton) popup.findActor("btnAchievement")).setChecked(true);
                ((UIButton) popup.findActor("btnMission")).setChecked(false);
            })
            .check(false)
            .fontScale(1.2f).parent(popup);

        float size = w * 0.1f;
        float margin = size * 0.2f;


        UITable table = new UITable().name("table").size(size * 5, size * 3).pos(w * 0.43f, h * 0.12f);

        for (int i = 0; i < 15; i++) {
            UIGroup uiGroup = new UIGroup().name("empty").child(
                new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                    .size(size, size));

            table.add(uiGroup).size(size, size);
            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }
        popup.addActor(table);

        return popup;
    }
}
