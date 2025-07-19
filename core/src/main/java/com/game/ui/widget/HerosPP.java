package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.LINEUP_ATTACK;
import static com.game.utils.Constants.HERO_FULL;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.Constants;
import com.game.utils.JsonHelper;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.EquipBase;
import com.game.utils.json.Hero;
import com.game.utils.json.Lineup;
import com.game.utils.json.Stat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HerosPP {
    private static List<Hero> heroList = null;
    private static List<Lineup> lineupList = null;
    private static List<EquipBase> equipBaseList = null;

    private static Hero heroSelect;

    private static UIGroup popup;
    private static UITable table;
    private static UIGroup gridLineup;
    private static UIGroup detail;
    private static int page = 0;
    private static float size;
    private static float margin;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static Group pp(float w, float h) {
        size = h * 0.2f;
        margin = size * 0.2f;
        popup = new UIGroup().name("hero").size(w, h);

        heroList = JsonHelper.loadHeroList(HERO_FULL, true);
        lineupList = JsonHelper.loadLineupList(LINEUP_ATTACK, true);
        heroSelect = null;

        if (heroList.size() > 1) sortHero(heroList);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.4f, h * 0.05f, w * 0.6f, h * 0.9f);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnLineup = new UIButton("Đội hình", gray, green)
            .bounds((int) (w * 0.45f), (int) (h * 0.79f), w * 0.12f, h * 0.12f)
            .name("btnLineup")
            .check(() -> {
                ((UIButton) popup.findActor("btnDetail")).setChecked(false);
                ((UIButton) popup.findActor("btnLineup")).setChecked(true);
                gridLineup.setVisible(true);
                detail.setVisible(false);
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnDetail = new UIButton("Chi tiết", gray, green)
            .bounds((int) (w * 0.6f), (int) (h * 0.79f), w * 0.12f, h * 0.12f)
            .name("btnDetail")
            .check(() -> {
                ((UIButton) popup.findActor("btnDetail")).setChecked(true);
                ((UIButton) popup.findActor("btnLineup")).setChecked(false);
                gridLineup.setVisible(false);
                detail.setVisible(true);
            })
            .check(false)
            .fontScale(1.2f).parent(popup);

        btnRedirect(w, h, h * 0.1f);

        createTable(w, h, size, margin);
        createGrid(w, h);
        createDetail(w, h);

        return popup;
    }

    private static void createTable(float w, float h, float size, float margin) {
        table = new UITable().name("table").size(size * 5, size * 3).pos(w * 0.43f, h * 0.12f);
        equipBaseList = JsonHelper.loadEquipBaseList(true);
        updateTable(size, margin);
        popup.addActor(table);
    }

    private static void updateTable(float size, float margin) {
        table.clearChildren();
        for (int i = 0; i < 15; i++) {
            UIGroup uiGroup;
            if (heroList != null && i < heroList.size()) {
                final int index = i + i * page;
                Hero hero = heroList.get(index);
                uiGroup = new UIGroup().name(hero.characterId).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .name("bg")
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "rarity1", 20))
                        .name("lineup")
                        .size(size, size).visible(hero.grid.equals("empty")?false:true),
                    new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "select", 20))
                        .name("imageSelect")
                        .size(size, size).visible(false),
                    new UIImage(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle"))
                        .name("frame")
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.5f),
                    new UILabel(hero.level + "", BMF).pos(size * 0.1f, size * 0.1f).align(Align.center).fontScale(1.5f)
                );

                uiGroup.onClick(() -> {
                    if (heroSelect != null) {
                        ((UIGroup) table.findActor(heroSelect.characterId)).findActor("imageSelect").setVisible(false);
                    }
                    heroSelect = hero;
                    uiGroup.findActor("imageSelect").setVisible(true);
                    updateDetail();
                });
            } else {
                uiGroup = new UIGroup().name("empty").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .name("bg")
                        .size(size, size)
                );
            }

            table.add(uiGroup).size(size, size);
            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }
    }


    private static void createGrid(float w, float h) {
        gridLineup = new UIGroup().name("grid").size(w * 0.4f, h);
        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("origin")
            .parent(gridLineup)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);
        new UILabel("Team", BMF).pos(w * 0.05f, h * 0.8f).fontScale(2.5f).parent(gridLineup);
        updateGrid(w, h);
        popup.addActor(gridLineup);
    }

    private static void updateGrid(float width, float height) {
        float posOrgX = width * 0.05f;
        float posOrgY = height * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIGroup item;
                if (gridLineup.findActor(i + "," + j) == null) {
                    item = new UIGroup()
                        .name(i + "," + j)
                        .pos(posOrgX + size * i, posOrgY + size * j)
                        .size(size, size).child(
                            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                                .size(size, size),
                            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "rarity0", 20))
                                .name("select")
                                .size(size, size)
                                .visible(false),
                            new UIImage((TextureRegion) null)
                                .name("frame")
                                .size(size - margin, size - margin)
                                .pos(margin * 0.5f, margin * 0.5f)
                        ).parent(gridLineup);
                } else {
                    item = gridLineup.findActor(i + "," + j);
                }

                item.onClick(() -> {

                });
            }
        }
        updateGridDrawable();
    }


    private static void updateGridDrawable() {
        JsonHelper.upfateLineupList(true);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIGroup item = gridLineup.findActor(i + "," + j);
                Lineup lineup = JsonHelper.get(lineupList, "grid", i + "," + j);
                UIImage frame = item.findActor("frame");
                if (lineup != null) {
                    System.out.println("nameRegion: "+ lineup.nameRegion);
                    frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion("atlas/characters/" + lineup.nameRegion + ".atlas", "idle")));
                } else {
                    frame.setVisible(false);
                }
            }
        }

    }

    private static void updateJsonValue(List<Lineup> full, String value, Hero hero) {
        for (Lineup item : full) {
            if (value.equals(item.grid)) {
                item.characterId = hero.characterId;
                item.nameRegion = hero.nameRegion;
                return;
            }
        }
    }

    private static void createDetail(float w, float h) {
        detail = new UIGroup().name("detail").size(w * 0.4f, h);
        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(detail)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);
        new UIImage((TextureRegion) null).name("frame").bounds(w*0.05f,h*0.5f,h*0.2f,h*0.2f).parent(detail).visible(false);

        new UILabel("Tên Nhân vật", BMF).name("nameLb").bounds(w * 0.05f, h * 0.84f, w * 0.3f, h * 0.1f).align(Align.center).fontScale(1.5f).parent(detail);
        new UILabel("Cấp độ --", BMF).name("levelLb").pos(w * 0.04f, h * 0.74f).color(Color.SKY).fontScale(1.5f).parent(detail);
        new UILabel("Trang bị", BMF).pos(w * 0.04f, h * 0.43f).color(Color.SKY).fontScale(1.5f).parent(detail);
        new UILabel("Chỉ số", BMF).pos(w * 0.24f, h * 0.54f).color(Color.SKY).fontScale(1.5f).parent(detail);

        new UILabel("Kinh nghiệm", BMF).pos(w * 0.24f, h * 0.7f).fontScale(1.2f).parent(detail);
        new UILabel("000/000", BMF).name("expLb").pos(w * 0.255f, h * 0.63f).fontScale(1.2f).parent(detail);

        new UILabel("000", BMF).name("hpLb").pos(w * 0.27f, h * 0.45f).fontScale(1.2f).parent(detail);
        new UILabel("Sức mạnh", BMF).name("attackLb").pos(w * 0.27f, h * 0.38f).fontScale(1.2f).parent(detail);
        new UILabel("Năng lượng", BMF).name("mpLb").pos(w * 0.27f, h * 0.31f).fontScale(1.2f).parent(detail);
        new UILabel("Thủ", BMF).name("defLb").pos(w * 0.27f, h * 0.24f).fontScale(1.2f).parent(detail);
        new UILabel("Chí mạng", BMF).name("criticalLb").pos(w * 0.27f, h * 0.17f).fontScale(1.2f).parent(detail);
        new UILabel("Nhanh nhẹn", BMF).name("agilityLb").pos(w * 0.27f, h * 0.1f).fontScale(1.2f).parent(detail);

        float sizeIcon = h * 0.05f;
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "iconhp")).bounds(w * 0.23f, h * 0.45f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icondame")).bounds(w * 0.23f, h * 0.38f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "iconn")).bounds(w * 0.23f, h * 0.31f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icondef")).bounds(w * 0.23f, h * 0.24f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "iconcrit")).bounds(w * 0.23f, h * 0.17f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "iconagility")).bounds(w * 0.23f, h * 0.1f, sizeIcon, sizeIcon).parent(detail);


        float pos = h * 0.1f;
        float tile = h * 0.15f;
        new UIImage(MainGame.getAsM().get9p()).bounds(pos, pos, tile, tile).parent(detail);
        new UIImage(MainGame.getAsM().get9p()).bounds(pos + tile, pos, tile, tile).parent(detail);
        new UIImage(MainGame.getAsM().get9p()).bounds(pos, pos + tile, tile, tile).parent(detail);
        new UIImage(MainGame.getAsM().get9p()).bounds(pos + tile, pos + tile, tile, tile).parent(detail);

        new UIImage(MainGame.getAsM().getRegion(UI_POPUP,"shield_empty")).name("shield").bounds(pos, pos, tile, tile).parent(detail).origin(Align.center).scale(0.6f);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP,"necklet_empty")).name("necklet").bounds(pos + tile, pos, tile, tile).parent(detail).origin(Align.center).scale(0.6f);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP,"sword_empty")).name("sword").bounds(pos, pos + tile, tile, tile).parent(detail).origin(Align.center).scale(0.6f);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP,"armor_empty")).name("armor").bounds(pos + tile, pos + tile, tile, tile).parent(detail).origin(Align.center).scale(0.6f);

        detail.setVisible(false);
        popup.addActor(detail);
    }

    private static void updateDetail() {
        CharacterBase herobase = JsonHelper.get(JsonHelper.loadCharacterBaseList(), "nameRegion", heroSelect.nameRegion);
        Stat stat = new Stat( heroSelect,herobase, equipBaseList);

        ((UIImage) detail.findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(Constants.CHARACTER_ATLAS + herobase.nameRegion + ".atlas", "idle")));
        ((UIImage) detail.findActor("frame")).visible(true);
        ((UILabel) detail.findActor("nameLb")).setText(herobase.name);
        ((UILabel) detail.findActor("levelLb")).setText("Cấp "+heroSelect.level);
        ((UILabel) detail.findActor("expLb")).setText(heroSelect.level);
        ((UILabel) detail.findActor("hpLb")).setText(stat.hp);
        ((UILabel) detail.findActor("attackLb")).setText(stat.atk);
        ((UILabel) detail.findActor("mpLb")).setText(stat.mp);
        ((UILabel) detail.findActor("defLb")).setText(stat.def);
        ((UILabel) detail.findActor("criticalLb")).setText(stat.crit);
        ((UILabel) detail.findActor("agilityLb")).setText(stat.agi);
    }

    private static void updateSelectGrid(String grid, boolean b) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String name = i + "," + j;
                if (popup.findActor(name) != null) {
                    if (name.equals(grid)) {
                        getUIImageFromGrid(name).setVisible(b);
                    } else {
                        if (getUIImageFromGrid(name) != null)
                            getUIImageFromGrid(name).setVisible(false);
                    }
                }
            }
        }
    }

    // Hàm trợ giúp để lấy UIImage của select trong grid
    static UIImage getUIImageFromGrid(String grid) {
        UIGroup gridGroup = (UIGroup) popup.findActor(grid);
        if (gridGroup == null) {
            System.out.println("D UIGroup with name " + grid + " not found!");
            return null;
        }
        return gridGroup.findActor("select");
    }

    private static void btnRedirect(float w, float h, float size) {
        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_up"))
            .size(size, size)
            .pos(w * 0.93f, h * 0.63f)
            .onClick(() -> {
                page -= page != 0 ? 1 : 0;
            })
            .parent(popup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_down"))
            .size(size, size)
            .pos(w * 0.93f, h * 0.09f)
            .onClick(() -> {
                if ((page + 1) * 21 < heroList.size()) {
                    page++;
                }
            })
            .parent(popup);
    }

    public static List<Hero> sortHero(List<Hero> heroList) {
        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                return Integer.compare(hero2.level, hero1.level);
            }
        });

        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                String grid1 = hero1.grid;
                String grid2 = hero2.grid;

                if (!hero1.grid.equals("empty") && grid2.equals("empty")) {
                    return -1;
                } else if (grid1.equals("empty") && !grid2.equals("empty")) {
                    return 1;
                }
                return 0;
            }
        });
        return heroList;
    }


    public static void update() {
    }
}
