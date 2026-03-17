package com.game.ui.widget;

import com.game.utils.Constants;

import static com.game.utils.Constants.BMF;

import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.managers.GameSessionManager;
import com.game.utils.JsonSaver;
import com.game.models.entity.Hero;

public class PotentialPP {
    private static float width, height, sizeTile;
    private static UIGroup popup;
    private static UIGroup group;
    private static UITable table;
    private static Hero heroMerge;
    private static Hero heroDelete;


    public static UIGroup pp(float w, float h) {
        width = w;
        height = h;
        sizeTile = width * 0.09f;
        popup = new UIGroup().name("potential");
        heroMerge = null;
        UIImage origin = new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.2f, height * 0.2f, width * 0.6f, height * 0.6f);
        popup.addActor(origin);

        group = new UIGroup();
        table = new UITable().name("table");
        group.child(
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "board", 100)).bounds(width * 0.1f, height * 0.1f, width * 0.8f, height * 0.8f),
            new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "close2")).name("closeBtn2").bounds(width * 0.82f, height * 0.78f, height * 0.1f, height * 0.1f).onClick(() -> {
                group.remove();
                popup.findActor("closeBtn").setVisible(true);
            })
        );

        UILabel wr = new UILabel("Tiến hóa cần 2 nhân vật cùng cấp sao với nhau", BMF).bounds(width * 0.25f, height * 0.4f, width * 0.5f, height * 0.1f).align(Align.center).warp(false);
        UIGroup originM = new UIGroup().name("originM").child(new UIImage(MainGame.getAsM().get9p()).name("bg").size(sizeTile, sizeTile), new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_plus")).name("frame").size(sizeTile, sizeTile).origin(Align.center).scale(0.8f)).pos(width * 0.45f, height * 0.5f).size(sizeTile, sizeTile).origin(Align.center);
        UIGroup origin1 = new UIGroup().name("origin1").child(new UIImage(MainGame.getAsM().get9p()).name("bg").size(sizeTile, sizeTile), new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_plus")).name("frame").size(sizeTile, sizeTile).origin(Align.center).scale(0.8f)).pos(width * 0.35f, height * 0.5f).onClick(() -> showListHero("origin1"));
        UIGroup origin2 = new UIGroup().name("origin2").child(new UIImage(MainGame.getAsM().get9p()).name("bg").size(sizeTile, sizeTile), new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_plus")).name("frame").size(sizeTile, sizeTile).origin(Align.center).scale(0.8f)).pos(width * 0.55f, height * 0.5f).onClick(() -> showListHero("origin2"));
        originM.onClick(() -> {
            popup.setVisible(true);
            originM.remove();
        });
        UILabel m = new UILabel("<<", BMF).size(sizeTile, sizeTile).pos(width * 0.45f, height * 0.5f).align(Align.center).fontScale(3f);

        UIButton button = new UIButton("Tiến hóa", MainGame.getAsM().getRegion(UI_POPUP, "btn_green")).bounds(width * 0.4f, height * 0.28f, width * 0.2f, height * 0.1f)
            .onClick(() -> {
                if ((heroMerge != null) && (heroDelete != null)) {
                    int star = heroMerge.star++;
                    GameSessionManager.getInstance().heroList.remove(heroDelete);
                    JsonSaver.saveObject(Constants.playerPath("hero_full.json"), GameSessionManager.getInstance().heroList);
                    ((UIImage) originM.findActor("bg")).setDrawable(new NinePatchDrawable(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" + star, 20)));
                    ((UIImage) originM.findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(heroMerge.nameRegion, "idle")));
                    popup.getParent().addActor(originM);
                    popup.setVisible(false);
                    originM.clearActions();
                    originM.addAction(Actions.sequence(
                        Actions.moveBy(-200, 0),
                        Actions.parallel(
                            Actions.moveBy(200, 0, 1),
                            Actions.scaleTo(2, 2, 1)
                        ),
                        Actions.run(() -> {
                            ((UIImage) originM.findActor("bg")).setDrawable(new NinePatchDrawable(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" + (star + 1), 20)));
                            popup.findActor("closeBtn").setVisible(true);
                        }),
                        Actions.forever(Actions.parallel(
                            Actions.sequence(
                                Actions.moveBy(20, 0, 0.3f, Interpolation.smooth),
                                Actions.moveBy(-20, 0, 0.3f, Interpolation.smooth)
                            ),
                            Actions.sequence(
                                Actions.rotateTo(5, 0.1f),
                                Actions.rotateTo(-10, 0.2f),
                                Actions.rotateTo(10, 0.2f),
                                Actions.rotateTo(-5, 0.1f)
                            )
                        ))
                    ));
                    heroDelete = null;
                    heroMerge = null;
                    updateDrawables("origin1", null);
                    updateDrawables("origin2", null);

                }
            });

        popup.child(
            origin1, origin2, m, button, wr
        );

        return popup;
    }

    private static void showListHero(String name) {
        table.clearChild();
        int index = 0;

        for (Hero hero : GameSessionManager.getInstance().heroList) {
            if (hero.star > 4) continue;
            if (heroMerge != null) {
                if (!heroMerge.nameRegion.equals(hero.nameRegion)) continue;
                if (hero.star != heroMerge.star) continue;

            }

            index++;
            UIGroup item = createItem(hero);
            if (heroMerge != null && heroMerge.characterId.equals(hero.characterId)
                || heroDelete != null && heroDelete.characterId.equals(hero.characterId)) {
                item.addActor(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "tile_select"))
                        .name("select")
                        .size(sizeTile, sizeTile)
                        .origin(Align.center)
                        .scale(1f)
                );
            }

            if (name.equals("origin2")) {
                if (!hero.grid.equals("empty")) {
                    item.addActor(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_lock"))
                            .name("lock")
                            .size(sizeTile, sizeTile)
                            .origin(Align.center)
                            .scale(0.4f)
                    );
                }
            }

            item.onClick(() -> {
                if (item.findActor("select") != null) {
                    heroMerge = null;
                    heroDelete = null;
                    updateDrawables("origin1", null);
                    updateDrawables("origin2", null);
                    group.remove();
                    return;
                }

                if (name.equals("origin1")) {
                    heroMerge = hero;
                } else {
                    heroDelete = hero;
                }

                updateDrawables(name, hero);
                group.remove();
            });


            table.add(item).pad(10);
            if (index % 6 == 0) {
                table.row();
            }

        }
        popup.findActor("closeBtn").setVisible(false);


        if (index == 0) {
            UILabel label = new UILabel("Không có nhân vật cùng loại", BMF).bounds(width * 0.1f, height * 0.1f, width * 0.8f, height * 0.7f).align(Align.center).warp(true).fontScale(2).debug(true);
            group.addActor(label);
            popup.addActor(group);
            return;
        }
        table.top().left();
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setSize(width * 0.64f, height * 0.62f);
        scrollPane.setPosition(width * 0.18f, height * 0.13f);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVisible(true);

        group.addActor(scrollPane);
        popup.addActor(group);
    }

    private static void updateDrawables(String name, Hero hero) {
        if (hero != null) {
            ((UIImage) ((UIGroup) popup.findActor(name)).findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle")));
            ((UIImage) ((UIGroup) popup.findActor(name)).findActor("bg")).setDrawable(new NinePatchDrawable(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" + hero.star, 20)));
        } else {
            ((UIImage) ((UIGroup) popup.findActor(name)).findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, "shadow_plus")));
            ((UIImage) ((UIGroup) popup.findActor(name)).findActor("bg")).setDrawable(new NinePatchDrawable(MainGame.getAsM().get9p()));
        }
        popup.findActor("closeBtn").setVisible(true);
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


}
