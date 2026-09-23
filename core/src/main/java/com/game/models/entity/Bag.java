package com.game.models.entity;

import com.game.managers.GameSessionManager;
import com.game.utils.DataHelper;

import java.util.List;

/**
 * Central inventory manager for the player's bag.
 * Provides utility methods to add/remove/find items and equips,
 * and bridges between the runtime lists and the base data.
 * 
 * Usage: Bag.addItem("food_315", 3) → adds 3 food_315 to inventory.
 *        Bag.findEquipBase("sword_000") → returns the EquipBase metadata.
 */
public class Bag {

    // ==================== ITEM OPERATIONS ====================

    /**
     * Add a quantity of an item to the player's inventory.
     * If the item already exists, increment its quantity.
     * If not, create a new Item entry.
     */
    public static void addItem(String nameRegion, int quantity) {
        List<Item> items = GameSessionManager.getInstance().itemList;
        for (Item item : items) {
            if (item.nameRegion.equals(nameRegion)) {
                item.quantity += quantity;
                return;
            }
        }
        items.add(new Item(nameRegion, quantity));
    }

    /**
     * Remove a quantity of an item from the inventory.
     * Returns true if successful, false if not enough quantity.
     */
    public static boolean removeItem(String nameRegion, int quantity) {
        List<Item> items = GameSessionManager.getInstance().itemList;
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.nameRegion.equals(nameRegion)) {
                if (item.quantity < quantity) return false;
                item.quantity -= quantity;
                if (item.quantity <= 0) {
                    items.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Get the quantity of an item the player owns.
     * Returns 0 if not found.
     */
    public static int getItemCount(String nameRegion) {
        for (Item item : GameSessionManager.getInstance().itemList) {
            if (item.nameRegion.equals(nameRegion)) {
                return item.quantity;
            }
        }
        return 0;
    }

    /**
     * Find an Item in the player's inventory by its nameRegion.
     * Returns null if not found.
     */
    public static Item findItem(String nameRegion) {
        for (Item item : GameSessionManager.getInstance().itemList) {
            if (item.nameRegion.equals(nameRegion)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Look up the base data for an item by nameRegion.
     */
    public static ItemBase findItemBase(String nameRegion) {
        return DataHelper.get(GameSessionManager.getInstance().itemBaseList, "nameRegion", nameRegion);
    }

    // ==================== EQUIP OPERATIONS ====================

    /**
     * Add a new equipment to the player's inventory.
     * Creates a new Equip instance with a unique ID.
     */
    public static Equip addEquip(String nameRegion) {
        Equip equip = new Equip(nameRegion);
        GameSessionManager.getInstance().equipList.add(equip);
        return equip;
    }

    /**
     * Remove an equipment by its unique ID.
     * Returns true if found and removed.
     */
    public static boolean removeEquip(String equipId) {
        List<Equip> equips = GameSessionManager.getInstance().equipList;
        for (int i = 0; i < equips.size(); i++) {
            if (equips.get(i).id.equals(equipId)) {
                equips.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Find an equip in the player's inventory by its unique ID.
     */
    public static Equip findEquipById(String equipId) {
        for (Equip equip : GameSessionManager.getInstance().equipList) {
            if (equip.id.equals(equipId)) {
                return equip;
            }
        }
        return null;
    }

    /**
     * Find an equip by its nameRegion (returns first match).
     */
    public static Equip findEquipByName(String nameRegion) {
        for (Equip equip : GameSessionManager.getInstance().equipList) {
            if (equip.nameRegion.equals(nameRegion)) {
                return equip;
            }
        }
        return null;
    }

    /**
     * Look up the base data for an equip by its nameRegion.
     */
    public static EquipBase findEquipBase(String nameRegion) {
        return DataHelper.get(GameSessionManager.getInstance().equipBaseList, "nameRegion", nameRegion);
    }

    /**
     * Attempt to upgrade the equip's level.
     * Deducts the upgrade cost from the player's coins.
     * Returns true if upgrade successful, false if insufficient funds.
     */
    public static boolean upgradeEquip(Equip equip) {
        if (equip == null) return false;
        EquipBase base = findEquipBase(equip.nameRegion);
        int cost = equip.getUpgradeCost(base);
        if (cost <= 0) return false;

        Profile profile = GameSessionManager.getInstance().profile;
        if (profile.coin < cost) return false;

        profile.coin -= cost;
        equip.level++;
        return true;
    }

    /**
     * Sell an item for coins.
     * Removes the specified quantity and adds coins to the player's profile.
     * Returns the coins earned, or 0 if the sale failed.
     */
    public static int sellItem(String nameRegion, int quantity) {
        ItemBase base = findItemBase(nameRegion);
        if (base == null) return 0;

        Item item = findItem(nameRegion);
        if (item == null || item.quantity < quantity) return 0;

        int pricePerUnit = item.getSellPrice(base);
        int totalCoins = pricePerUnit * quantity;

        if (removeItem(nameRegion, quantity)) {
            GameSessionManager.getInstance().profile.addCoin(totalCoins);
            return totalCoins;
        }
        return 0;
    }

    /**
     * Sell an equipment for coins.
     * Removes the equip from the inventory and adds coins.
     * Automatically unequips from any hero beforehand.
     * Returns the coins earned, or 0 if the sale failed.
     */
    public static int sellEquip(String equipId) {
        Equip equip = findEquipById(equipId);
        if (equip == null) return 0;

        EquipBase base = findEquipBase(equip.nameRegion);
        int coins = equip.getSellPrice(base);

        // Unequip from hero if worn
        if (equip.isEquipped()) {
            Hero hero = DataHelper.get(GameSessionManager.getInstance().heroList, "characterId", equip.target);
            if (hero != null) {
                String category = equip.getCategory(base);
                switch (category) {
                    case "weapon":  hero.equip.weapon = "empty"; break;
                    case "armor":   hero.equip.armor = "empty"; break;
                    case "jewelry": hero.equip.jewelry = "empty"; break;
                    case "support": hero.equip.support = "empty"; break;
                }
            }
        }

        if (removeEquip(equipId)) {
            GameSessionManager.getInstance().profile.addCoin(coins);
            return coins;
        }
        return 0;
    }

    // ==================== SUMMARY ====================

    /**
     * Get total number of unique items.
     */
    public static int getUniqueItemCount() {
        return GameSessionManager.getInstance().itemList.size();
    }

    /**
     * Get total number of equips (each is unique).
     */
    public static int getEquipCount() {
        return GameSessionManager.getInstance().equipList.size();
    }
}
