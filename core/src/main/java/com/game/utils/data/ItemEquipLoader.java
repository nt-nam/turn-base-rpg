package com.game.utils.data;

import static com.game.utils.Constants.EQUIP_JSON;
import static com.game.utils.Constants.ITEM_JSON;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Equip;
import com.game.models.entity.EquipBase;
import com.game.models.entity.Item;
import com.game.models.entity.ItemBase;
import com.game.utils.Constants;

import java.util.HashMap;
import java.util.List;

/**
 * Loader cho Item + Equip (base & instance nguoi choi).
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class ItemEquipLoader {

    private ItemEquipLoader() {
    }

    public static List<EquipBase> loadEquipBaseList(boolean b) {
        if (b || GameSessionManager.getInstance().equipBaseList.isEmpty()) {
            GameSessionManager.getInstance().equipBaseList.clear();
            FileHandle fileHandle = Gdx.files.internal(EQUIP_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                EquipBase newEquip = new EquipBase();
                newEquip.nameRegion = equip.getString("nameRegion");
                newEquip.name = equip.getString("name");
                newEquip.show = equip.getBoolean("show");
                newEquip.category = equip.getString("category", "default");
                newEquip.currency = equip.getString("currency", "default");
                newEquip.price = equip.getInt("price", -1);

                JsonValue stats = equip.get("stats");
                newEquip.stats = new HashMap<>();
                if (stats != null) {
                    for (JsonValue stat : stats) {
                        newEquip.stats.put(stat.name(), stat.asInt());
                    }
                } else {
                    Gdx.app.debug("ItemEquipLoader", "stats null");
                }

                GameSessionManager.getInstance().equipBaseList.add(newEquip);
            }
        }
        return GameSessionManager.getInstance().equipBaseList;
    }

    public static List<Equip> loadEquipList(boolean b) {
        if (b || GameSessionManager.getInstance().equipList.isEmpty()) {
            GameSessionManager.getInstance().equipList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("equips.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue equip : root) {
                Equip newEquip = new Equip();
                newEquip.id = equip.getString("id");
                newEquip.nameRegion = equip.getString("nameRegion");
                newEquip.level = equip.getInt("level");
                newEquip.target = equip.getString("target", "default");

                GameSessionManager.getInstance().equipList.add(newEquip);
            }
        }
        return GameSessionManager.getInstance().equipList;
    }

    public static List<ItemBase> loadItemBaseList(boolean b) {
        if (b || GameSessionManager.getInstance().itemBaseList.isEmpty()) {
            GameSessionManager.getInstance().itemBaseList.clear();
            FileHandle fileHandle = Gdx.files.internal(ITEM_JSON);
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                ItemBase newItem = new ItemBase();
                newItem.nameRegion = item.getString("nameRegion", "empty");
                newItem.name = item.getString("name");
                newItem.detail = item.getString("detail", "-- || --");
                newItem.tier = item.getInt("tier");
                newItem.show = item.getBoolean("show");
                newItem.currency = item.getString("currency");
                newItem.price = item.getInt("price");
                GameSessionManager.getInstance().itemBaseList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().itemBaseList;
    }

    public static List<Item> loadItemList(boolean b) {
        if (b || GameSessionManager.getInstance().itemList.isEmpty()) {
            GameSessionManager.getInstance().itemList.clear();
            FileHandle fileHandle = Gdx.files.local(Constants.playerPath("items.json"));
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            for (JsonValue item : root) {
                Item newItem = new Item();
                newItem.nameRegion = item.getString("nameRegion", "empty");
                newItem.quantity = item.getInt("quantity", item.getInt("index", 1));
                GameSessionManager.getInstance().itemList.add(newItem);
            }
        }
        return GameSessionManager.getInstance().itemList;
    }
}
