package com.game.ui.widget;

import com.game.utils.Constants;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;


import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.ui.hud.NotificationPP;
import com.game.utils.GameSession;
import com.game.utils.DataHelper;
import com.game.utils.JsonSaver;
import com.game.utils.json.Equip;
import com.game.utils.json.EquipBase;
import com.game.utils.json.Item;
import com.game.utils.json.ItemBase;

import java.util.List;
import java.util.Map;

public class ShopPP {
    private static UIGroup popup;
    private static List<?> useFor = null;
    private static float width, height;
    private static UITable listView;
    private static UITable table;

    public static void show(boolean b) {
        popup.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        width = w;
        height = h;
        useFor = DataHelper.loadItemBaseList(true);
        popup = new UIGroup().name("shop").size(w, h);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.1f, h * 0.1f, w * 0.8f, h * 0.8f);


        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnItem = new UIButton("Vật phẩm", gray, green)
            .bounds((int) (w * 0.12f), (int) (h * 0.75f), w * 0.12f, h * 0.12f)
            .name("btnItem")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(false);
                ((UIButton) popup.findActor("btnItem")).setChecked(true);
                useFor = DataHelper.loadItemBaseList(true);
                updateGrid();
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnEquip = new UIButton("Trang bị", gray, green)
            .bounds((int) (w * 0.25f), (int) (h * 0.75f), w * 0.12f, h * 0.12f)
            .name("btnEquip")
            .check(() -> {
                ((UIButton) popup.findActor("btnEquip")).setChecked(true);
                ((UIButton) popup.findActor("btnItem")).setChecked(false);
                useFor = DataHelper.loadEquipBaseList(true);
                updateGrid();
            })
            .check(false)
            .fontScale(1.2f).parent(popup);


        new UIGroup().name("coin").pos(w * 0.55f, h * 0.75f).size(w * 0.12f, h * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20)).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "coin")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.1f),
            new UILabel(GameSession.profile.coin + "", BMF).name("label").pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f)
        ).parent(popup);

        new UIGroup().name("gem").pos(w * 0.70f, h * 0.75f).size(w * 0.12f, h * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20)).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.10f),
            new UILabel(GameSession.profile.gem + "", BMF).name("label").pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f)
        ).parent(popup);

        createGrid(popup, w, h);

        return popup;
    }


    private static void createGrid(UIGroup popup, float w, float h) {
        float size = h * 0.2f;
        table = new UITable().name("table").size(size * 7, size * 3).pos(w * 0.12f, h * 0.12f);
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setName("scroll");
        scrollPane.setSize(w * 0.8f, h * 0.58f);
        scrollPane.setPosition(w * 0.1f, h * 0.14f);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVisible(true);
        updateGrid();
        popup.addActor(scrollPane);
    }

    private static void updateGrid() {
        float size = height * 0.16f;
        float margin = size * 0.1f;

        table.clearChild();
        int index = 1;

        for (Object obj : useFor) {
            UIGroup uiGroup;

            String idString = "";
            if (obj instanceof ItemBase) {
                if (!((ItemBase) obj).show) continue;
                idString = ((ItemBase) obj).nameRegion;
            } else if (obj instanceof EquipBase) {
                if (!((EquipBase) obj).show) continue;
                idString = ((EquipBase) obj).nameRegion;
            }

            uiGroup = new UIGroup().name(idString).child(
                new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                    .size(size, size),
                new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "select")).visible(false).name("select")
                    .size(size, size),
                new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, idString))
                    .size(size - margin, size - margin)
                    .pos(margin * 0.5f, margin * 0.5f)
            );

            uiGroup.onClick(() -> {
                showItemDetail(obj);
            });

            table.add(uiGroup).size(size, size).pad(margin);

            if ((index % 6 == 0)) {
                table.row();
            }
            index++;
        }
    }

    private static void showItemDetail(Object object) {
        popup.findActor("closeBtn").setVisible(false);
        UIGroup detailItem = new UIGroup().name("detail").size(width, height);

        UIImage background = new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.1f, height * 0.1f, width * 0.8f, height * 0.8f);
        UIButton btnClose = new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "close2")).name("closeBtn2").bounds(width * 0.82f, height * 0.78f, height * 0.1f, height * 0.1f).onClick(() -> {
            detailItem.remove();
            popup.findActor("closeBtn").setVisible(true);
        });

        UIImage tile = new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.18f, height * 0.25f, height * 0.5f, height * 0.5f);
        UIImage frame = new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.18f, height * 0.25f, height * 0.5f, height * 0.5f).origin(Align.center).scale(0.8f);
        UILabel name = new UILabel("Name", BMF).name("name").pos(width * 0.47f, height * 0.75f).fontScale(3f);
        UILabel lbCategory = new UILabel("Loại: Vật phẩm tiêu hao", BMF).name("category").pos(width * 0.55f, height * 0.65f).fontScale(1f).align(Align.left).color(Color.SKY);
        UILabel lbDetail = new UILabel("-- || --", BMF).name("lbDetail").bounds(width * 0.5f, height * 0.3f, width * 0.3f, height * 0.3f).align(Align.center).fontScale(1.5f).warp(true);
        UIImage iconCurrency = new UIImage(MainGame.getAsM().get9p()).bounds(width * 0.55f, height * 0.2f, height * 0.1f, height * 0.1f).scale(0.7f).align(Align.center);
        UILabel lbPrice = new UILabel("Price", BMF).name("name").bounds(width * 0.6f, height * 0.2f, width * 0.1f, height * 0.1f).align(Align.center).fontScale(1.5f);

        UIButton btnBuy = new UIButton("Mua", MainGame.getAsM().getRegion(UI_POPUP, "btn_green")).bounds(width * 0.7f, height * 0.2f, width * 0.1f, height * 0.1f);

        if (object instanceof ItemBase) {
            ItemBase item = (ItemBase) object;
            frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(ATLAS_ITEM, item.nameRegion)));
            name.setText(item.name);
            lbDetail.setText(item.detail);
            iconCurrency.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, item.currency)));
            lbPrice.setText(item.price + "");
            btnBuy.onClick(() -> {
                if (item.currency.equals("coin")) {
                    if (GameSession.profile.useCoin(item.price)) {
                        popup.add(NotificationPP.ppr(width, height, "Mua thành công"));
                        GameSession.itemList.add(new Item(item.nameRegion, 1));
                    } else {
                        popup.add(NotificationPP.ppr(width, height, "Không đủ tiền"));
                    }
                } else if (item.currency.equals("gem_pink")) {
                    if (GameSession.profile.useGem(item.price)) {
                        popup.add(NotificationPP.ppr(width, height, "Mua thành công"));
                        GameSession.itemList.add(new Item(item.nameRegion, 1));
                    } else {
                        popup.add(NotificationPP.ppr(width, height, "Không đủ gem"));
                    }
                }
                JsonSaver.saveObject(Constants.playerPath("items.json"), GameSession.itemList);
                updateLabel();
            });
        } else if (object instanceof EquipBase) {
            EquipBase item = (EquipBase) object;
            frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(ATLAS_ITEM, item.nameRegion)));
            name.setText(item.name);
//            lbCategory.setText(getTextDetail(item));
            lbDetail.setText(getTextDetail(item));
            lbDetail.debug();
            iconCurrency.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, item.currency)));
            lbPrice.setText(item.price + "");
            btnBuy.onClick(() -> {
                if (item.currency.equals("coin")) {
                    if (GameSession.profile.useCoin(item.price)) {
                        popup.add(NotificationPP.ppr(width, height, "Mua thành công"));
                        GameSession.equipList.add(new Equip(item.nameRegion));
                    } else {
                        popup.add(NotificationPP.ppr(width, height, "Không đủ tiền"));
                    }
                } else if (item.currency.equals("gem_pink")) {
                    if (GameSession.profile.useGem(item.price)) {
                        popup.add(NotificationPP.ppr(width, height, "Mua thành công"));
                        GameSession.equipList.add(new Equip(item.nameRegion));
                    } else {
                        popup.add(NotificationPP.ppr(width, height, "Không đủ gem"));
                    }
                }
                JsonSaver.saveObject(Constants.playerPath("equips.json"), GameSession.equipList);
                updateLabel();

            });
        }
        detailItem.child(
            background, btnClose, tile, frame, name, lbCategory, lbDetail, iconCurrency, lbPrice, btnBuy
        ).origin(Align.center);

        detailItem.action(Actions.sequence(
            Actions.scaleTo(0.5f, 0.5f),
            Actions.scaleTo(1, 1, 0.5f, Interpolation.swingOut)
        ));
        popup.addActor(detailItem);
    }

    private static String getTextDetail(EquipBase equipBase) {
        String text = "";
        Map<String, Integer> stats = equipBase.stats;
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            text += optionText(entry.getKey()) + ":  " + entry.getValue() + "   \n";
        }
        return text;
    }

    private static String optionText(String op) {
        String text = "";

        if (op.equals("atk")) {
            text += "Tấn công";
        } else if (op.equals("crit")) {
            text += "Chí mạng";
        } else if (op.equals("agi")) {
            text += "Tốc độ";
        } else if (op.equals("hp")) {
            text += "Máu";
        } else if (op.equals("def")) {
            text += "Thủ";
        }
        return text;
    }

    private static String textDetail(String category) {
        String text = "";
        switch (category) {
            case "weapon":
                text = "Vũ khí";
                break;
            case "armor":
                text = "Áp giáp";
                break;
            case "jewelry":
                text = "Trang sức";
                break;
            case "support":
                text = "Hỗ trợ";
                break;
        }

        return text;
    }

    private static void updateLabel() {
        ((UILabel) ((UIGroup) popup.findActor("coin")).findActor("label")).setText(GameSession.profile.coin + "");
        ((UILabel) ((UIGroup) popup.findActor("gem")).findActor("label")).setText(GameSession.profile.gem + "");
    }
}
