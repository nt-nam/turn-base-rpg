package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.LINEUP_ATTACK;
import static com.game.utils.Constants.HERO_FULL;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.GameSession.equipList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.OverlayUI;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.Constants;
import com.game.utils.DataHelper;
import com.game.utils.GameSession;
import com.game.utils.JsonSaver;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.Equip;
import com.game.utils.json.EquipBase;
import com.game.utils.json.Hero;
import com.game.utils.json.Lineup;
import com.game.utils.json.Stat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HerosPP {
    private static List<EquipBase> equipBaseList = null;

    private static Hero heroSelect;
    private static UIGroup groupSelect;

    private static UIGroup popup;
    private static UITable table;
    private static UIGroup gridLineup;
    private static UIGroup detail;
    private static UIGroup equipDetail;
    private static float size;
    private static float margin;
    private static float width;
    private static float height;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        width = w;
        height = h;
        size = height * 0.2f;
        margin = size * 0.2f;
        popup = new UIGroup().name("hero").size(HerosPP.width, HerosPP.height);

        heroSelect = null;


        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(width * 0.4f, height * 0.05f, width * 0.6f, height * 0.9f);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnLineup = new UIButton("Đội hình", gray, green)
            .bounds((int) (width * 0.45f), (int) (height * 0.79f), width * 0.12f, height * 0.12f)
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
            .bounds((int) (width * 0.6f), (int) (height * 0.79f), width * 0.12f, height * 0.12f)
            .name("btnDetail")
            .check(() -> {
                ((UIButton) popup.findActor("btnDetail")).setChecked(true);
                ((UIButton) popup.findActor("btnLineup")).setChecked(false);
                gridLineup.setVisible(false);
                detail.setVisible(heroSelect != null);
                updateDetail();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);


        createTable();
        createGrid();
        createDetail();

        return popup;
    }

    private static void createTable() {
        if (table != null) table.clearChild();
        table = new UITable().name("table");
        equipBaseList = DataHelper.loadEquipBaseList(true);

        table.top().left();
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setName("tableHero");
        scrollPane.setSize(width * 0.55f, height * 0.64f);
        scrollPane.setPosition(width * 0.43f, height * 0.1f);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVisible(true);

        updateTable();
        popup.addActor(scrollPane);
    }

    private static void updateTable() {
        if (GameSession.heroList.size() > 1) sortHero(DataHelper.loadHeroList(HERO_FULL, false));
        if (table != null) table.clearChild();
        int index = 0;
        for (Hero hero : GameSession.heroList) {
            UIGroup uiGroup = new UIGroup().name(hero.characterId).child(
                new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" + hero.star, 20))
                    .name("bg")
                    .size(size, size),
                new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "select", 20))
                    .name("imageSelect")
                    .size(size, size).visible(false).color(Color.YELLOW)
                    .action(Actions.forever(Actions.sequence(Actions.fadeOut(0.5f), Actions.fadeIn(0.5f)))),
                new UIImage(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle"))
                    .name("frame")
                    .size(size - margin, size - margin)
                    .pos(margin * 0.5f, margin * 0.8f),
                new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_battle"))
                    .name("lineup")
                    .size(size * 0.25f, size * 0.25f)
                    .pos(size * 0.7f, size * 0.7f).visible(!hero.grid.equals("empty")),
                new UILabel(hero.level + "", BMF)
                    .name("textLevel")
                    .pos(size * 0.2f, size * 0.2f).align(Align.center).fontScale(1f)
            );
            int a = index;
            uiGroup.onClick(() -> {
                if (groupSelect != null) {
                    groupSelect.findActor("imageSelect").setVisible(false);
                    if (heroSelect != null && !heroSelect.grid.equals("empty"))
                        ((UIGroup) gridLineup.findActor(heroSelect.grid)).findActor("select").setVisible(false);
                }
                if (heroSelect == GameSession.heroList.get(a)) {
                    heroSelect = null;
                    detail.setVisible(false);
                    return;
                }
                groupSelect = uiGroup;
                groupSelect.findActor("imageSelect").setVisible(true);
                heroSelect = GameSession.heroList.get(a);
                if (heroSelect != null && !heroSelect.grid.equals("empty"))
                    ((UIGroup) gridLineup.findActor(heroSelect.grid)).findActor("select").setVisible(true);
                boolean flag = gridLineup.isVisible();
                if (flag) {
                    System.out.println("updateGridDrawable");
                } else {
                    detail.setVisible(true);
                    updateDetail();
                }
            });
            table.add(uiGroup).size(size, size).pad(5);
            index++;
            if (index % 5 == 0) {
                table.row();
            }
        }
    }

    private static void createGrid() {
        if (gridLineup != null) gridLineup.clearChildren();
        gridLineup = new UIGroup().name("grid").size(width * 0.4f, height);
        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("origin")
            .parent(gridLineup)
            .bounds(width * 0.01f, height * 0.05f, width * 0.38f, height * 0.9f);
        new UILabel("Đội hình", BMF).pos(width * 0.05f, height * 0.8f).fontScale(2.5f).parent(gridLineup);

        float posOrgX = width * 0.05f;
        float posOrgY = height * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIImage frame = new UIImage((TextureRegion) null)
                    .name("frame")
                    .size(size - margin, size - margin)
                    .pos(margin * 0.5f, margin * 0.7f)
                    .visible(false);
                UIGroup item = new UIGroup().name(i + "," + j)
                    .pos(posOrgX + size * i, posOrgY + size * j)
                    .size(size, size).child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(size, size),
                        new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "select", 20))
                            .name("select")
                            .size(size, size).visible(false).color(Color.YELLOW)
                            .action(Actions.forever(Actions.sequence(Actions.fadeOut(0.5f), Actions.fadeIn(0.5f)))),
                        frame
                    ).parent(gridLineup);

                final int m = i;
                final int n = j;
                item.onClick(() -> {
                    if (heroSelect == null) {
                        return;
                    }
                    Lineup lineup = DataHelper.get(GameSession.lineupList, "grid", m + "," + n);
                    if (lineup == null) {
                        if (!heroSelect.grid.equals("empty")) {
                            ((UIGroup) gridLineup.findActor(heroSelect.grid)).findActor("frame").setVisible(false);
                            ((UIGroup) gridLineup.findActor(heroSelect.grid)).findActor("select").setVisible(false);
                        }
                        if (GameSession.profile.sizeTeam > GameSession.lineupList.size()) {
                            heroSelect.grid = m + "," + n;
                            lineup = new Lineup();
                            lineup.characterId = heroSelect.characterId;
                            lineup.grid = heroSelect.grid;
                            lineup.nameRegion = heroSelect.nameRegion;
                            GameSession.lineupList.add(lineup);
                            groupSelect.findActor("lineup").setVisible(true);
                            frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(lineup.nameRegion, "idle")));
                            frame.setVisible(true);
                            item.findActor("select").setVisible(true);
                            saveDataLineup();
                        }
                        return;
                    }
                    if (heroSelect.grid.equals(m + "," + n)) {
                        //xoa thoi
                        item.findActor("select").setVisible(false);
                        heroSelect.grid = "empty";
                        groupSelect.findActor("lineup").setVisible(false);
                        GameSession.lineupList.remove(lineup);
                        frame.setVisible(false);
                    } else {
                        //xoa va them
                        //heroSelect con o grid
                        if (!heroSelect.grid.equals("empty")) {
                            ((UIGroup) gridLineup.findActor(heroSelect.grid)).findActor("frame").setVisible(false);
                            ((UIGroup) gridLineup.findActor(heroSelect.grid)).findActor("select").setVisible(false);
                        }

                        //lineup con o grid
                        if (table.findActor(lineup.characterId) != null) {
                            UIGroup tileTable = table.findActor(lineup.characterId);
                            tileTable.findActor("imageSelect").setVisible(false);
                            tileTable.findActor("lineup").setVisible(false);
                            setEmptyGrid(lineup.grid);
                        }

                        GameSession.lineupList.remove(lineup);
                        item.findActor("select").setVisible(true);
                        heroSelect.grid = m + "," + n;
                        groupSelect.findActor("lineup").setVisible(true);
                        frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(heroSelect.nameRegion, "idle")));
                        frame.setVisible(true);
                    }
                    saveDataLineup();

                });
            }
        }
        updateDrawableGrid();
        popup.addActor(gridLineup);
    }

    private static void setEmptyGrid(String gridId) {
        for (Hero hero : GameSession.heroList) {
            if (hero.grid.equals(gridId)) {
                hero.grid = "empty";
            }
        }
    }

    private static void saveDataLineup() {
        JsonSaver.saveObject(LINEUP_ATTACK, GameSession.lineupList);
        JsonSaver.saveObject(HERO_FULL, GameSession.heroList);
    }

    private static void updateDrawableGrid() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIGroup item = gridLineup.findActor(i + "," + j);
                Lineup lineup = DataHelper.get(GameSession.lineupList, "grid", i + "," + j);
                UIImage frame = item.findActor("frame");
                UIImage select = item.findActor("select");

                if (heroSelect == null) {
                    select.setVisible(false);
                } else if (heroSelect.grid.equals(i + "," + j)) {
                    select.setVisible(true);
                }

                if (lineup == null) {
                    frame.setVisible(false);
                    continue;
                }
                frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(lineup.nameRegion, "idle")));
                frame.setVisible(true);
            }
        }
    }

    private static void createDetail() {
        if (detail != null) detail.clearChildren();
        detail = new UIGroup().name("detail").size(width * 0.4f, height);
        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(detail)
            .bounds(width * 0.01f, height * 0.05f, width * 0.38f, height * 0.9f);
        new UIImage((TextureRegion) null).name("frame").bounds(width * 0.05f, height * 0.5f, height * 0.2f, height * 0.2f).parent(detail).visible(false);

        new UILabel("Nhân vật", BMF).name("nameLb").bounds(width * 0.05f, height * 0.84f, width * 0.3f, height * 0.1f).align(Align.center).fontScale(1.5f).parent(detail);
        new UILabel("Cấp độ _/_", BMF).name("levelLb").pos(width * 0.04f, height * 0.74f).color(Color.SKY).fontScale(1.5f).parent(detail);
        new UILabel("Trang bị", BMF).pos(width * 0.04f, height * 0.43f).color(Color.SKY).fontScale(1.5f).parent(detail);
        new UILabel("Chỉ số", BMF).pos(width * 0.24f, height * 0.47f).color(Color.SKY).fontScale(1.5f).parent(detail);

        new UILabel("Lực chiến:", BMF).pos(width * 0.2f, height * 0.73f).size(width * 0.2f, height * 0.1f).color(Color.CORAL).fontScale(1.2f).parent(detail).warp(true).debug(false).align(Align.center);
        new UILabel("N/A", BMF).name("battleScoreLb").pos(width * 0.2f, height * 0.67f).size(width * 0.2f, height * 0.1f).color(Color.CORAL).fontScale(1.2f).parent(detail).warp(true).debug(false).align(Align.center);

        new UILabel("Kinh nghiệm", BMF).pos(width * 0.24f, height * 0.62f).color(Color.SKY).fontScale(1.2f).parent(detail);
        new UILabel("N/A", BMF).name("expLb").pos(width * 0.24f, height * 0.55f).fontScale(1f).parent(detail);

        new UILabel("N/A", BMF).name("hpLb").pos(width * 0.27f, height * 0.38f).fontScale(1.2f).parent(detail);
        new UILabel("N/A", BMF).name("attackLb").pos(width * 0.27f, height * 0.31f).fontScale(1.2f).parent(detail);
        new UILabel("N/A", BMF).name("defLb").pos(width * 0.27f, height * 0.24f).fontScale(1.2f).parent(detail);
        new UILabel("N/A", BMF).name("criticalLb").pos(width * 0.27f, height * 0.17f).fontScale(1.2f).parent(detail);
        new UILabel("N/A", BMF).name("agilityLb").pos(width * 0.27f, height * 0.1f).fontScale(1.2f).parent(detail);

        float sizeIcon = height * 0.05f;
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_star")).name("star5").bounds(width * 0.14f, height * 0.62f, sizeIcon, sizeIcon).visible(false).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_star")).name("star4").bounds(width * 0.14f, height * 0.59f, sizeIcon, sizeIcon).visible(false).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_star")).name("star3").bounds(width * 0.14f, height * 0.56f, sizeIcon, sizeIcon).visible(false).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_star")).name("star2").bounds(width * 0.14f, height * 0.53f, sizeIcon, sizeIcon).visible(false).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_star")).name("star1").bounds(width * 0.14f, height * 0.50f, sizeIcon, sizeIcon).visible(false).parent(detail);

        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_hp")).bounds(width * 0.23f, height * 0.38f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_dame")).bounds(width * 0.23f, height * 0.31f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_def")).bounds(width * 0.23f, height * 0.24f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_crit")).bounds(width * 0.23f, height * 0.17f, sizeIcon, sizeIcon).parent(detail);
        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_agility")).bounds(width * 0.23f, height * 0.1f, sizeIcon, sizeIcon).parent(detail);


        float pos = height * 0.1f;
        float tile = height * 0.15f;

        new UIGroup().name("shield").child(
            new UIImage(MainGame.getAsM().get9p()).size(tile, tile),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_shield")).name("icon").size(tile, tile).origin(Align.center).scale(0.6f)
        ).pos(pos, pos).parent(detail).onClick(() -> {
            System.out.println("Click equip: shield");
        });
        new UIGroup().name("necklet").child(
            new UIImage(MainGame.getAsM().get9p()).size(tile, tile),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_necklet")).name("icon").size(tile, tile).origin(Align.center).scale(0.6f)
        ).pos(pos + tile, pos).parent(detail).onClick(() -> {
            System.out.println("Click equip: necklet");
        });
        new UIGroup().name("sword").child(
            new UIImage(MainGame.getAsM().get9p()).size(tile, tile),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_sword")).name("icon").size(tile, tile).origin(Align.center).scale(0.6f)
        ).pos(pos, pos + tile).parent(detail).onClick(() -> {
            System.out.println("Click equip: sword");
        });
        new UIGroup().name("armor").child(
            new UIImage(MainGame.getAsM().get9p()).size(tile, tile),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "shadow_armor")).name("icon").size(tile, tile).origin(Align.center).scale(0.6f)
        ).pos(pos + tile, pos + tile).parent(detail).onClick(() -> {
            System.out.println("Click equip: armor");
        });

        detail.setVisible(false);
        updateDetail();
        popup.addActor(detail);
    }

    private static void updateDetail() {
        if (heroSelect == null) {
            detail.setVisible(false);
            return;
        }
        detail.setVisible(true);
        CharacterBase herobase = DataHelper.get(DataHelper.loadCharacterBaseList(), "nameRegion", heroSelect.nameRegion);
        int star = heroSelect.star;
        ((UIImage) detail.findActor("star5")).visible(5 <= star);
        ((UIImage) detail.findActor("star4")).visible(4 <= star);
        ((UIImage) detail.findActor("star3")).visible(3 <= star);
        ((UIImage) detail.findActor("star2")).visible(2 <= star);
        ((UIImage) detail.findActor("star1")).visible(1 <= star);

        Stat stat = new Stat(heroSelect, herobase, equipBaseList);
        ((UIImage) detail.findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(Constants.CHARACTER_ATLAS + herobase.nameRegion + ".atlas", "idle")));
        ((UIImage) detail.findActor("frame")).visible(true);
        ((UILabel) detail.findActor("nameLb")).setText(herobase.name);
        ((UILabel) detail.findActor("levelLb")).setText("Cấp " + heroSelect.level);
        ((UILabel) detail.findActor("expLb")).setText(heroSelect.exp + "/" + (heroSelect.level * 100));
        ((UILabel) detail.findActor("battleScoreLb")).setText("" + heroSelect.getBattleScore());
        ((UILabel) detail.findActor("hpLb")).setText(stat.hp);
        ((UILabel) detail.findActor("attackLb")).setText(stat.atk);
        ((UILabel) detail.findActor("defLb")).setText(stat.def);
        ((UILabel) detail.findActor("criticalLb")).setText(stat.crit);
        ((UILabel) detail.findActor("agilityLb")).setText(stat.agi);

        if (!heroSelect.equip.weapon.equals("empty"))
            ((UIImage) ((Group) detail.findActor("sword")).findActor("icon")).setDrawable(getRegionEquip(heroSelect.equip.weapon));
        else
            ((UIImage) ((Group) detail.findActor("sword")).findActor("icon")).setDrawable(getRegionEquipEmpty("shadow_sword"));

        if (!heroSelect.equip.armor.equals("empty"))
            ((UIImage) ((Group) detail.findActor("armor")).findActor("icon")).setDrawable((getRegionEquip(heroSelect.equip.armor)));
        else
            ((UIImage) ((Group) detail.findActor("armor")).findActor("icon")).setDrawable(getRegionEquipEmpty("shadow_armor"));

        if (!heroSelect.equip.support.equals("empty"))
            ((UIImage) ((Group) detail.findActor("shield")).findActor("icon")).setDrawable(getRegionEquip(heroSelect.equip.support));
        else
            ((UIImage) ((Group) detail.findActor("shield")).findActor("icon")).setDrawable(getRegionEquipEmpty("shadow_shield"));

        if (!heroSelect.equip.jewelry.equals("empty"))
            ((UIImage) ((Group) detail.findActor("necklet")).findActor("icon")).setDrawable((getRegionEquip(heroSelect.equip.jewelry)));
        else
            ((UIImage) ((Group) detail.findActor("necklet")).findActor("icon")).setDrawable(getRegionEquipEmpty("shadow_necklet"));

    }

    private static TextureRegionDrawable getRegionEquip(String idEquip) {
        System.out.println(idEquip);
        Equip equip = DataHelper.get(equipList, "id", idEquip);
        return new TextureRegionDrawable(MainGame.getAsM().getRegion(ATLAS_ITEM, equip.nameRegion));
    }

    private static TextureRegionDrawable getRegionEquipEmpty(String idEquip) {
        return new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, idEquip));
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

    public static void createNotification(String tile, String message) {
//        float width = popup.getWidth();
//        float height = popup.getHeight();
        UIGroup a;
        if (popup.findActor("notification") != null) {
            a = popup.findActor("notification");
            a.setVisible(true);
        } else {
            a = new UIGroup().name("popupDelete").size(width, height).parent(popup);
        }
        UIButton btnYes = new UIButton("Xác nhận", MainGame.getAsM().getRegion(UI_POPUP, "btn_green"));
        UIButton btnNo = new UIButton("Hủy", MainGame.getAsM().getRegion(UI_POPUP, "btn_red"));
        UIButton btnClose = new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_close2"));

        btnYes.check(() -> {
        });
        btnNo.check(() -> {
            Group parent = popup.getParent();
            parent.findActor("closeBtn").setVisible(true);
            a.setVisible(false);
        });


        a.child(
            OverlayUI.overlay(a),
            new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.2f, height * 0.2f, width * 0.6f, height * 0.6f),
            new UILabel("Cảnh báo xóa!!!", BMF).pos(width * 0.28f, height * 0.65f).fontScale(2),
            new UILabel("Bạn sẽ mất toàn bộ dữ liệu từ tài khoản hiện tại và không thể khôi phục lại.", BMF).bounds(width * 0.3f, height * 0.4f, width * 0.4f, height * 0.2f).warp(true),
            btnYes.bounds(width * 0.32f, height * 0.25f, width * 0.15f, height * 0.12f),
            btnNo.bounds(width * 0.53f, height * 0.25f, width * 0.15f, height * 0.12f)
        );
    }


    public static boolean updateGrid(int i, int j) {
        for (Hero hero : GameSession.heroList) {
            if (hero.grid.equals(i + "," + j)) {
                hero.grid = "empty";
                return true;
            }
        }
        return false;
    }

    public static void update() {

    }
}
