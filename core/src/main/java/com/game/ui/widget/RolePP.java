package com.game.ui.widget;

import static com.game.utils.Constants.ACHIEVEMENT_JSON;
import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.MISSION_JSON;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.Color;
import com.game.utils.JsonHelper;
import com.game.utils.json.Achievement;
import com.game.utils.json.Mission;

import java.util.List;

public class RolePP {
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

        List<Achievement> achievements = JsonHelper.loadAchievements(ACHIEVEMENT_JSON, false);
        List<Mission> missions = JsonHelper.loadMissions(MISSION_JSON, false);

        createAchievement(popup, achievements, size, w,h);
        createMission(popup, missions, size, w,h);
        return popup;
    }

    private static void createAchievement(UIGroup popup, List<Achievement> achievements, float size, float w,float h) {
        UITable table = new UITable();
        if(achievements != null){
            for (Achievement child:achievements) {
                table.add(new UIGroup().name(child.name).size(size*5, size).child(
                    new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "origin"),20,20,20,20)).size(size*5, size),
                    new UILabel(child.name,BMF).fontScale(1.5f).pos(size*0.1f,size*0.65f).color(Color.paleYellow),
                    new UILabel(child.dec,BMF).fontScale(1f).pos(size*0.5f,size*0.1f).size(size*4f,size*0.6f).warp(true),
                    new UILabel(child.number+"",BMF).fontScale(1.5f).pos(size*4f,size*0.65f).color(Color.lightRed)
                ));
                table.row().pad(5, 0,5, 0);
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
    private static void createMission(UIGroup popup, List<Mission> missions, float size, float w,float h) {
        UITable table = new UITable();
        if(missions != null){
            for (Mission child:missions) {
                table.add(new UIGroup().size(size*5, size).child(
                    new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "origin"),20,20,20,20)).size(size*5, size),
                    new UILabel(child.title,BMF).fontScale(1.5f).pos(size*0.1f,size*0.65f).color(Color.paleYellow),
                    new UILabel(child.description,BMF).fontScale(1f).pos(size*0.5f,size*0.1f).size(size*4f,size*0.6f).warp(true),
                    new UILabel(child.progress+"/"+child.targetAmount,BMF).fontScale(1.5f).pos(size*3.8f,size*0.65f).color(Color.lightRed)
                ));
                table.row().pad(5, 0,5, 0);
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
}
