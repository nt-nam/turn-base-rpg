package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.DataHelper;
import com.game.utils.GameSession;
import com.game.utils.json.Hero;
import com.game.utils.json.MapBattle;

public class BattleDetailPP {
    private static UIGroup popup;
    private static MapBattle mapBattle;
    private static float sizeTile;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float width, float height) {
        popup = new UIGroup().name("battleDetail").size(width, width);
        String mapPath = "data/enemy/" + GameSession.profile.area + "_" + GameSession.enemyMapId + ".json";
        mapBattle = DataHelper.loadMapBattle(mapPath);
        sizeTile = height * 0.16f;
        long battleScore = 0;

        Table table = new UITable().pos(width * 0.3f, height * 0.2f).size(width * 0.3f, height * 0.6f).debugAll();

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                Hero hero = DataHelper.get(mapBattle.heroEnemyList, "grid", i + "," + (2-j));
                if (hero != null) battleScore += hero.getBattleScore();
                table.add(createItem(hero)).pad(10);
            }
            table.row();
        }

        UIButton button = new UIButton("Tấn công", MainGame.getAsM().getRegion(UI_POPUP, "btn_green")).bounds(width * 0.6f, height * 0.2f, width * 0.1f, height * 0.1f)
            .onClick(() -> {
                popup.getParent().findActor("overlay").setVisible(false);
                popup.remove();
                MainGame.getScM().showScreen(ScreenType.BATTLE);
            });
        UIButton back = new UIButton("bỏ quả", MainGame.getAsM().getRegion(UI_POPUP, "btn_gray")).bounds(width * 0.6f, height * 0.4f, width * 0.1f, height * 0.1f)
            .onClick(() -> {
                popup.getParent().findActor("overlay").setVisible(false);
                popup.remove();
            });

        UILabel lbBattleScore = new UILabel("Lực chiến địch: " + battleScore, BMF).bounds(width * 0.4f, height * 0.8f, width * 0.1f, height * 0.05f).color(Color.CORAL).fontScale(1.5f).align(Align.center);

        popup.child(
            new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.25f, height * 0.1f, width * 0.5f, height * 0.8f),
            table,
            lbBattleScore,
            button, back
        );
        return popup;
    }

    private static UIGroup createItem(Hero hero) {
        UIGroup uiGroup = new UIGroup().name("").child(
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" + (hero != null ? hero.star : 0), 20))
                .name("bg")
                .size(sizeTile, sizeTile),
            hero != null ? new UIImage(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle"))
                .name("frame")
                .size(sizeTile, sizeTile)
                .origin(Align.center)
                .scale(0.7f) : new Actor(),
            new UILabel("" + (hero != null ? hero.level : ""), BMF)
                .name("textLevel")
                .pos(sizeTile * 0.1f, sizeTile * 0.1f).align(Align.center).fontScale(1f)
        ).size(sizeTile, sizeTile);

        return uiGroup;
    }

}
