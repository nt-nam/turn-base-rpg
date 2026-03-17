package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.ui.hud.NotificationPP;
import com.game.utils.DataHelper;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Equip;
import com.game.models.entity.EquipBase;
import com.game.models.entity.Hero;
import com.game.models.entity.Item;

import java.util.Objects;

public class BagPP {
    private static UIGroup popup;
    private static UITable tableEquip;
    private static ScrollPane scrollPaneEquip;
    private static UITable tableItem;
    private static ScrollPane scrollPaneItem;
    private static UITable tableHero;
    private static ScrollPane scrollPaneHero;
    private static UIGroup group;

    private static UIGroup compareEquip;
    private static float sizeTile;
    private static float width;
    private static float height;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        width = w;
        height = h;
        sizeTile = height * 0.18f;

        popup = new UIGroup().name("bag").size(BagPP.width, height);
        tableEquip = new UITable();
        tableItem = new UITable();
        tableHero = new UITable();
        scrollPaneEquip = new ScrollPane(tableEquip);
        scrollPaneEquip.setSize(width * 0.55f, height * 0.6f);
        scrollPaneEquip.setPosition(w * 0.43f, h * 0.11f);
        scrollPaneEquip.setScrollingDisabled(true, false);

        scrollPaneItem = new ScrollPane(tableItem);
        scrollPaneItem.setSize(width * 0.55f, height * 0.6f);
        scrollPaneItem.setPosition(w * 0.43f, h * 0.11f);
        scrollPaneItem.setScrollingDisabled(true, false);
        scrollPaneItem.setVisible(false);

        group = new UIGroup();
        group.child(
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "board", 100)).bounds(width * 0.1f, height * 0.1f, width * 0.8f, height * 0.8f),
            new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "close2")).name("closeBtn2").bounds(width * 0.82f, height * 0.78f, height * 0.1f, height * 0.1f).onClick(() -> {
                group.remove();
                popup.findActor("closeBtn").setVisible(true);
            })
        );


        DataHelper.loadEquipList(true);
        DataHelper.loadItemList(true);
//        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
//        new UIImage(profile).nine(profile, 30, 30, 30, 30)
//            .name("origin")
//            .parent(popup)
//            .bounds(width * 0.01f, height * 0.05f, width * 0.38f, height * 0.9f);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(width * 0.4f, height * 0.05f, width * 0.6f, height * 0.9f);


        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnEquip = new UIButton("Trang bị", gray, green)
            .bounds((int) (width * 0.45f), (int) (height * 0.79f), width * 0.12f, height * 0.12f)
            .name("btnEquip")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(true);
                ((UIButton) popup.findActor("btnItem")).setChecked(false);
                updateEquip();
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnItem = new UIButton("Vật phẩm", gray, green)
            .bounds((int) (width * 0.6f), (int) (height * 0.79f), width * 0.12f, height * 0.12f)
            .name("btnItem")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(false);
                ((UIButton) popup.findActor("btnItem")).setChecked(true);
                updateItem();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);


        updateEquip();
        tableEquip.top().left();
        tableItem.top().left();

        popup.addActor(scrollPaneEquip);
        popup.addActor(scrollPaneItem);
        return popup;
    }

    private static void updateEquip() {
        tableEquip.clear();
        int index = 0;
        for (Equip equip : GameSessionManager.getInstance().equipList) {
            tableEquip.add(tileItem(equip.nameRegion).onClick(() -> {
                createPopup(equip);
            }));
            index++;
            if (index % 6 == 0) {
                tableEquip.row().pad(10, 0, 10, 0);
            }
        }
        scrollPaneEquip.setVisible(true);
        scrollPaneItem.setVisible(false);
    }

    private static void updateItem() {
        tableItem.clear();
        int index = 0;
        for (Item item : GameSessionManager.getInstance().itemList) {
            tableItem.add(tileItem(item.nameRegion).onClick(() -> {
                createPopup(item);
            }));
            index++;
            if (index % 6 == 0) {
                tableItem.row().pad(10, 0, 10, 0);
            }
        }
        scrollPaneEquip.setVisible(false);
        scrollPaneItem.setVisible(true);
    }

    private static void createPopup(Equip equip) {
        tableHero.clearChild();
        int index = 0;

        for (Hero hero : GameSessionManager.getInstance().heroList) {

            index++;
            UIGroup item = createItem(hero);


            item.onClick(() -> {
                String v = category(equip);
                if (v.equals("empty")) return;
                Hero target = DataHelper.get(GameSessionManager.getInstance().heroList, "characterId", equip.target);
                boolean flag = false;
                if (target != null) {
                    flag = true;
                }
                if (v.equals("weapon")) {
                    if (flag) target.equip.weapon = "empty";
                    hero.equip.weapon = equip.id;
                    equip.target = hero.characterId;
                } else if (v.equals("armor")) {
                    if (flag) target.equip.armor = "empty";
                    hero.equip.armor = equip.id;
                    equip.target = hero.characterId;
                } else if (v.equals("jewelry")) {
                    if (flag) target.equip.jewelry = "empty";
                    hero.equip.jewelry = equip.id;
                    equip.target = hero.characterId;
                } else if (v.equals("support")) {
                    if (flag) target.equip.support = "empty";
                    hero.equip.support = equip.id;
                    equip.target = hero.characterId;
                }

                popup.addActor(NotificationPP.ppr(width, height, "Cập nhật thành công"));
                System.out.println("category: capnhat thanh cong");

                group.remove();
                popup.findActor("closeBtn").setVisible(true);
            });


            tableHero.add(item).pad(10);
            if (index % 6 == 0) {
                tableHero.row();
            }

        }
        popup.findActor("closeBtn").setVisible(false);

        tableHero.top().left();
        ScrollPane scrollPane = new ScrollPane(tableHero);
        scrollPane.setSize(width * 0.64f, height * 0.62f);
        scrollPane.setPosition(width * 0.18f, height * 0.13f);
        scrollPane.setScrollingDisabled(true, false);

        group.addActor(scrollPane);
        popup.addActor(group);
    }

    private static String category(Equip e) {
        String eStr = "empty";
        EquipBase equip = DataHelper.get(GameSessionManager.getInstance().equipBaseList, "nameRegion", e.nameRegion);
        if (equip != null) eStr = equip.category;
        return eStr;
    }

    private static void createPopup(Item item) {
        tableHero.clearChild();
        int index = 0;

        for (Hero hero : GameSessionManager.getInstance().heroList) {

            index++;
            UIGroup itemUI = createItem(hero);


            itemUI.onClick(() -> {
                hero.exp += Objects.requireNonNull(DataHelper.get(GameSessionManager.getInstance().itemBaseList, "nameRegion", item.nameRegion)).tier + 100;
                popup.addActor(NotificationPP.ppr(width, height, "Tăng cấp thành công"));
                System.out.println("category: capnhat thanh cong");
hero.checkLevel();
            });


            tableHero.add(itemUI).pad(10);
            if (index % 6 == 0) {
                tableHero.row();
            }

        }
        popup.findActor("closeBtn").setVisible(false);

        tableHero.top().left();
        ScrollPane scrollPane = new ScrollPane(tableHero);
        scrollPane.setSize(width * 0.64f, height * 0.62f);
        scrollPane.setPosition(width * 0.18f, height * 0.13f);
        scrollPane.setScrollingDisabled(true, false);

        group.addActor(scrollPane);
        popup.addActor(group);
    }

    private static UIGroup tileItem(String nameRegion) {
        UIGroup tile = new UIGroup().size(sizeTile, sizeTile);
        new UIImage(MainGame.getAsM().get9p()).size(sizeTile, sizeTile).parent(tile);
        new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, nameRegion)).pos(sizeTile * 0.1f, sizeTile * 0.1f).size(sizeTile, sizeTile).align(Align.center).scale(0.8f).parent(tile);
        new UILabel("", BMF).parent(tile);
        return tile;
    }

    private static void compareEquipPP() {
        compareEquip = new UIGroup();
        compareEquip.name("compareEquip");
        popup.addActor(compareEquip);
        UIGroup detailEquipHero = new UIGroup().name("detailEquipHero").pos(0, 0).size(width * 0.4f, height).parent(compareEquip);
        UIGroup detailEquipSelect = new UIGroup().name("detailEquipSelect").pos(width * 0.6f, 0).size(width * 0.4f, height).parent(compareEquip);

        detailEquipHero.child(
            new UIImage(MainGame.getAsM().get9p()).size(width * 0.4f, height)
        );

        detailEquipSelect.child(
            new UIImage(MainGame.getAsM().get9p()).size(width * 0.4f, height)
        );
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
