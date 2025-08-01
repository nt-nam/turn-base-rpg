package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UITable;
import com.game.utils.DataHelper;
import com.game.utils.json.Equip;
import com.game.utils.json.Item;
import com.game.utils.json.Lineup;

import java.util.ArrayList;
import java.util.List;

public class BagPP {
    private static UIGroup popup;
    private static UITable table;
    private static UIGroup compareEquip;
    private static List<Item> itemList;
    private static List<Equip> equipList;
    private static List<Lineup> lineupList;
    private static int page = 0;
    private static float sizeTile;
    private static float marginTile;
    private static float width;
    private static float height;
    private static boolean useE;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        width = w;
        height = h;
        popup = new UIGroup().name("bag").size(BagPP.width, height);
        lineupList = new ArrayList<>();
        equipList = DataHelper.loadEquipList(true);
        itemList = DataHelper.loadItemList(true);
        useE = true;
        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(popup)
            .bounds(width * 0.01f, height * 0.05f, width * 0.38f, height * 0.9f);

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
                useE = true;
                page = 0;

                update();
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnItem = new UIButton("Vật phẩm", gray, green)
            .bounds((int) (width * 0.6f), (int) (height * 0.79f), width * 0.12f, height * 0.12f)
            .name("btnItem")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(false);
                ((UIButton) popup.findActor("btnItem")).setChecked(true);
                useE = false;
                page = 0;
                update();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);


        sizeTile = height * 0.2f;
        marginTile = sizeTile * 0.2f;

        table = new UITable().name("table").size(sizeTile * 5, sizeTile * 3).pos(width * 0.43f, height * 0.12f);

        btnRedirect(popup, w, h, h * 0.1f);
        update();

        popup.addActor(table);
        popup.run(() -> {

        });
//        createGridLineup();
        return popup;
    }

    private static void btnRedirect(UIGroup popup, float w, float h, float size) {
        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_up"))
            .size(size, size)
            .pos(w * 0.93f, h * 0.63f)
            .onClick(() -> {
                page = page != 0 ? page - 1 : 0;
                update();
            })
            .parent(popup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_down"))
            .size(size, size)
            .pos(w * 0.93f, h * 0.1f)
            .onClick(() -> {
                if (useE) {
                    if ((page + 1) * 15 < equipList.size()) {
                        page++;
                        update();
                    }
                } else {
                    if ((page + 1) * 15 < itemList.size()) {
                        page++;
                        update();
                    }
                }
            })
            .parent(popup);
    }

//    private static void createGridLineup() {
//        gridLineup = new UIGroup().name("grid").size(width * 0.4f, height);
//        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
//        new UIImage(profile).nine(profile, 30, 30, 30, 30)
//            .name("origin")
//            .parent(gridLineup)
//            .bounds(width * 0.01f, height * 0.05f, width * 0.38f, height * 0.9f);
//        new UILabel("Team", BMF).pos(width * 0.05f, height * 0.8f).fontScale(2.5f).parent(gridLineup);
//        updateGrid(width, height);
//        popup.addActor(gridLineup);
//    }
//
//    private static void updateGrid(float width, float height) {
//        float posOrgX = width * 0.05f;
//        float posOrgY = height * 0.15f;
//        List<Lineup> lineup = JsonHelper.loadLineupList(LINEUP_ATTACK, true);
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                UIGroup heroGrid = null;
//                if (gridLineup.findActor(i + "," + j) == null) {
//                    heroGrid = new UIGroup()
//                        .name(i + "," + j)
//                        .pos(posOrgX + sizeTile * i, posOrgY + sizeTile * j)
//                        .size(sizeTile, sizeTile).child(
//                            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
//                                .size(sizeTile, sizeTile),
//                            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity0", 20))
//                                .name("select")
//                                .size(sizeTile, sizeTile)
//                                .visible(false),
//                            new UIImage((TextureRegion) null)
//                                .name("frame")
//                                .size(sizeTile - marginTile, sizeTile - marginTile)
//                                .pos(marginTile * 0.5f, marginTile * 0.5f).visible(false)
//                        ).parent(gridLineup);
//                    if (JsonHelper.get(lineup, "grid", i + "," + j) != null) {
//                        Lineup l = JsonHelper.get(lineup, "grid", i + "," + j);
//                        ((UIImage) heroGrid.findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(Constants.CHARACTER_ATLAS + l.nameRegion + ".atlas", "idle")));
//                        heroGrid.findActor("frame").setVisible(true);
//                    }
//                } else {
//                    heroGrid = gridLineup.findActor(i + "," + j);
//                }
//
////                heroGrid.onClick(() -> {
////                    compareEquipPP();
////                });
//
//            }
//        }
//    }

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

    public static void update() {
        table.clear();
        if (useE) {
            for (int i = 0; i < 15; i++) {
                UIGroup uiGroup;
                int id = page * 15 + i;
                if (equipList != null && id < equipList.size()) {
                    Equip item = equipList.get(id + 15 * page);
                    uiGroup = new UIGroup().name(item.id).child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(sizeTile, sizeTile),
                        new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "select", 20))
                            .name("imageSelect")
                            .size(sizeTile, sizeTile).visible(false),
                        new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.nameRegion))
                            .size(sizeTile - marginTile, sizeTile - marginTile)
                            .pos(marginTile * 0.5f, marginTile * 0.5f)
                    );
                    uiGroup.onClick(() -> {
                        uiGroup.findActor("imageSelect").setVisible(true);
                    });
                } else {
                    uiGroup = new UIGroup().name("empty").child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(sizeTile, sizeTile));
                }


                table.add(uiGroup).size(sizeTile, sizeTile);

                if ((i + 1) % 5 == 0) {
                    table.row();
                }
            }
        } else {
            for (int i = 0; i < 15; i++) {
                UIGroup uiGroup;
                int id = page * 15 + i;
                if (itemList != null && id < itemList.size()) {
                    Item item = itemList.get(id);
                    uiGroup = new UIGroup().name(item.nameRegion).child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(sizeTile, sizeTile),
                        new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "select", 20))
                            .name("imageSelect")
                            .size(sizeTile, sizeTile).visible(false),
                        new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.nameRegion))
                            .size(sizeTile - marginTile, sizeTile - marginTile)
                            .pos(marginTile * 0.5f, marginTile * 0.5f)
                    );
                    uiGroup.onClick(() -> {
                        uiGroup.findActor("imageSelect").setVisible(true);
                    });
                } else {
                    uiGroup = new UIGroup().name("empty").child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(sizeTile, sizeTile));
                }


                table.add(uiGroup).size(sizeTile, sizeTile);

                if ((i + 1) % 5 == 0) {
                    table.row();
                }
            }
        }
    }
}
