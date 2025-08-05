package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.Color;
import com.game.utils.Constants;
import com.game.utils.DataHelper;
import com.game.utils.GameSession;
import com.game.utils.json.Achievement;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.Hero;
import com.game.utils.json.Mission;
import com.game.utils.json.Stat;

import java.util.List;

public class RolePP {
    private static int page = 0;
    private static UIGroup popup;
    private static UIGroup detail;
    private static float sizeTile;

    public static void show(boolean b) {
        popup.setVisible(b);
        update();
    }

    public static UIGroup pp(float w, float h) {
        popup = new UIGroup().name("role").size(w, h);

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
                ((ScrollPane) popup.findActor("achievement")).setVisible(false);
                ((ScrollPane) popup.findActor("mission")).setVisible(true);
            })
            .check(false)
            .fontScale(1.2f).parent(popup);

        UIButton btnAchievement = new UIButton("Thành tích", gray, green)
            .bounds((int) (w * 0.45f), (int) (h * 0.79f), w * 0.15f, h * 0.12f)
            .name("btnAchievement")
            .check(() -> {
                ((UIButton) popup.findActor("btnAchievement")).setChecked(true);
                ((UIButton) popup.findActor("btnMission")).setChecked(false);
                ((ScrollPane) popup.findActor("achievement")).setVisible(true);
                ((ScrollPane) popup.findActor("mission")).setVisible(false);
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        float size = h * 0.2f;
        float margin = size * 0.2f;

        List<Achievement> achievements = DataHelper.loadAchievementList(false);
        List<Mission> missions = DataHelper.loadMissionList(false);

        createAchievement(popup, achievements, size, w, h);
        createMission(popup, missions, size, w, h);
        createDetail(w, h);
        return popup;
    }

    private static void createDetail(float w, float h) {
        detail = new UIGroup().name("detail").size(w * 0.4f, h);
        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(detail)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);
        new UIImage((TextureRegion) null).name("frame").bounds(w * 0.05f, h * 0.5f, h * 0.2f, h * 0.2f).parent(detail).visible(false);

        new UILabel("|   " + GameSession.profile.name + "   |", BMF).name("nameLb").bounds(w * 0.05f, h * 0.84f, w * 0.3f, h * 0.1f).align(Align.center).fontScale(1.5f).parent(detail);
        new UILabel("Cấp: " + GameSession.profile.level, BMF).name("levelLb").pos(w * 0.04f, h * 0.74f).color(com.badlogic.gdx.graphics.Color.SKY).fontScale(1.5f).parent(detail);

        new UILabel("Kinh nghiệm:", BMF).pos(w * 0.04f, h * 0.65f).color(com.badlogic.gdx.graphics.Color.SKY).fontScale(1.5f).parent(detail);
        new UILabel(GameSession.profile.exp + "/" + (GameSession.profile.level * 100), BMF).name("expLb").pos(w * 0.05f, h * 0.58f).fontScale(1f).parent(detail);

        new UILabel("Lực chiến đội", BMF).pos(w * 0.04f, h * 0.43f).color(com.badlogic.gdx.graphics.Color.SKY).fontScale(1.5f).parent(detail);
        new UILabel(GameSession.profile.getBattleScore() + "", BMF).name("expLb").pos(w * 0.05f, h * 0.36f).fontScale(1f).parent(detail);

        float pos = h * 0.1f;
        float tile = h * 0.14f;
        sizeTile = h * 0.14f;

        int index=0;
        for (Hero hero : GameSession.heroList) {
            if(!hero.grid.equals("empty")){
                detail.addActor(createItem(hero).pos(w * 0.28f, pos + tile * index));
                index++;
            }
        }
        popup.addActor(detail);
    }

    private static void updateDetail() {
        int index=1;
        for (Hero hero : GameSession.heroList) {
            if(!hero.grid.equals("empty")){
                ((UIImage) detail.findActor("cha" + index)).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle")));
                index++;
            }
        }
    }

    private static void createAchievement(UIGroup popup, List<Achievement> achievements, float size, float w, float h) {
        UITable table = new UITable();
        if (achievements != null) {
            for (Achievement child : achievements) {
                table.add(
                    new UIGroup().name(child.idBase).size(size * 5, size).child(
                        new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20)).size(size * 5, size),
                        new UILabel(child.name, BMF).fontScale(1.5f).pos(size * 0.1f, size * 0.65f).color(Color.paleYellow),
                        new UILabel(child.dec, BMF).fontScale(1f).pos(size * 0.5f, size * 0.1f).size(size * 4f, size * 0.6f).warp(true),
                        new UILabel(child.number + "", BMF).fontScale(1.5f).pos(size * 4f, size * 0.65f).color(Color.lightRed)
                    )
                );
                table.row().pad(5, 0, 5, 0);
            }
        }
        table.top().left();
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setName("achievement");
        scrollPane.setSize(size * 5, size * 3.1f);
        scrollPane.setPosition(w * 0.43f, h * 0.11f);
        scrollPane.setScrollingDisabled(true, false);

        popup.addActor(scrollPane);
    }

    private static void createMission(UIGroup popup, List<Mission> missions, float size, float w, float h) {
        UITable table = new UITable();
        if (missions != null) {
            for (Mission child : missions) {
                table.add(new UIGroup().size(size * 5, size).child(
                    new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20)).size(size * 5, size),
                    new UILabel(child.title, BMF).fontScale(1.5f).pos(size * 0.1f, size * 0.65f).color(Color.paleYellow),
                    new UILabel(child.description, BMF).fontScale(1f).pos(size * 0.5f, size * 0.1f).size(size * 4f, size * 0.6f).warp(true),
                    new UILabel(child.progress + "/" + child.targetAmount, BMF).fontScale(1.5f).pos(size * 3.8f, size * 0.65f).color(Color.lightRed)
                ));
                table.row().pad(5, 0, 5, 0);
            }
        }
        table.top().left();
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setName("mission");
        scrollPane.setSize(size * 5, size * 3);
        scrollPane.setPosition(w * 0.43f, h * 0.12f);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVisible(false);
        popup.addActor(scrollPane);
    }

    private static UIGroup createItem(Hero hero) {
        UIGroup uiGroup = new UIGroup().name(hero.characterId).child(
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" + hero.star, 20))
                .name("bg")
                .size(sizeTile, sizeTile),
            new UIImage(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle"))
                .name("frame")
                .size(sizeTile, sizeTile)
                .origin(Align.center)
                .scale(0.7f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_battle"))
                .name("lineup")
                .size(sizeTile * 0.25f, sizeTile * 0.25f)
                .pos(sizeTile * 0.7f, sizeTile * 0.7f).visible(hero.grid.equals("empty") ? false : true),
            new UILabel("" + hero.level, BMF)
                .name("textLevel")
                .pos(sizeTile * 0.1f, sizeTile * 0.1f).align(Align.center).fontScale(1f)
        ).size(sizeTile, sizeTile);

        return uiGroup;
    }

    public static void update() {

    }
}
