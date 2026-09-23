package com.game.models.entity;

/**
 * Represents a stackable item in the player's inventory.
 * Links to ItemBase via nameRegion for metadata (name, tier, price, detail).
 * Field 'quantity' replaces the old 'index' to clarify intent.
 * nameRegion doubles as the texture key for the UI atlas.
 */
public class Item {
    public String nameRegion; // Matches ItemBase.nameRegion and texture atlas key
    public int quantity;      // Number of this item the player owns

    public Item() {
    }

    public Item(String nameRegion, int quantity) {
        this.nameRegion = nameRegion;
        this.quantity = quantity;
    }

    /**
     * Calculate sell price based on the base price and tier.
     * Sell price = basePrice * tier * 0.5 (half of buy price).
     */
    public int getSellPrice(ItemBase base) {
        if (base == null || base.price <= 0) return 0;
        return (int) (base.price * base.tier * 0.5f);
    }

    /**
     * Calculate buy price based on the base price and tier.
     * Buy price = basePrice * tier.
     */
    public int getBuyPrice(ItemBase base) {
        if (base == null || base.price <= 0) return 0;
        return base.price * base.tier;
    }

    /**
     * Get the effect value of a consumable item.
     * For food: returns exp gained (tier * 100).
     * Override in subclasses or check name prefix for specific behavior.
     */
    public int getEffectValue(ItemBase base) {
        if (base == null) return 0;
        // Food items grant exp = tier * 100
        if (base.nameRegion.startsWith("food_")) {
            return base.tier * 100;
        }
        // Water/potion items use itemConfig tier values (handled externally)
        return base.tier;
    }

    @Override
    public String toString() {
        return "Item{" + nameRegion + " x" + quantity + "}";
    }
}
