package com.game.models.entity;

import com.game.managers.GameSessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single equipment instance in the player's inventory.
 * Links to EquipBase via nameRegion for metadata (name, category, stats, price).
 * Each equip has a unique id and can be leveled up to enhance stats.
 * 'target' is the characterId of the Hero wearing this equip, or "empty".
 * nameRegion doubles as the texture key for the UI atlas.
 */
public class Equip {
    public String id;          // Unique identifier (e.g., "equip1")
    public String nameRegion;  // Matches EquipBase.nameRegion and atlas key
    public int level;          // Current enhancement level (1+)
    public String target;      // characterId of equipped Hero, or "empty"

    public Equip() {
    }

    public Equip(String nameRegion) {
        id = "equip" + GameSessionManager.getInstance().profile.equipment++;
        this.nameRegion = nameRegion;
        level = 1;
        target = "empty";
    }

    /**
     * Calculate the effective stats of this equipment at its current level.
     * Each level adds +10% to the base stats (rounded down).
     * Formula: effectiveStat = baseStat + baseStat * (level - 1) * 0.1
     */
    public Map<String, Integer> getEffectiveStats(EquipBase base) {
        Map<String, Integer> effective = new HashMap<>();
        if (base == null || base.stats == null) return effective;
        for (Map.Entry<String, Integer> entry : base.stats.entrySet()) {
            int baseStat = entry.getValue();
            int scaled = (int) (baseStat + baseStat * (level - 1) * 0.1f);
            effective.put(entry.getKey(), scaled);
        }
        return effective;
    }

    /**
     * Get a single effective stat value by key.
     * Returns 0 if the stat doesn't exist.
     */
    public int getEffectiveStat(EquipBase base, String statKey) {
        Map<String, Integer> stats = getEffectiveStats(base);
        return stats.getOrDefault(statKey, 0);
    }

    /**
     * Calculate the cost to upgrade this equip to the next level.
     * Formula: base.price * level * 0.8
     */
    public int getUpgradeCost(EquipBase base) {
        if (base == null || base.price <= 0) return 0;
        return (int) (base.price * level * 0.8f);
    }

    /**
     * Calculate sell price: 50% of the base price * level.
     */
    public int getSellPrice(EquipBase base) {
        if (base == null || base.price <= 0) return 0;
        return (int) (base.price * level * 0.5f);
    }

    /**
     * Check if this equipment is currently worn by a Hero.
     */
    public boolean isEquipped() {
        return target != null && !target.equals("empty");
    }

    /**
     * Get the category of this equip by looking up the base data.
     * Returns "weapon", "armor", "jewelry", "support", or "unknown".
     */
    public String getCategory(EquipBase base) {
        if (base == null || base.category == null) return "unknown";
        return base.category;
    }

    /**
     * Calculate Battle Score contribution of this equip.
     * Sum of all effective stats (absolute values, negative stats reduce score).
     */
    public int getBattleScore(EquipBase base) {
        int score = 0;
        for (int val : getEffectiveStats(base).values()) {
            score += val;
        }
        return score;
    }

    @Override
    public String toString() {
        return "Equip{" + id + ", " + nameRegion + ", lv" + level + ", target=" + target + "}";
    }
}
