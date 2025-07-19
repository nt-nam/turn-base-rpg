package com.game.utils.json;

import com.badlogic.gdx.math.MathUtils;
import com.game.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class Stat {
    public int hp;
    public int mp;
    public int atk;
    public int def;
    public int agi;
    public int crit;

    public Stat(Hero hero, CharacterBase characterBase, List<EquipBase> equipBaseList) {
//        if (hero == null) {
//            return null;
//        }
//        if (characterBase == null) {
//            return null;
//        }

        int hpPlus = 0;
        int mpPlus = 0;
        int atkPlus = 0;
        int defPlus = 0;
        int agiPlus = 0;
        int critPlus = 0;

        double multiplierLevel = Math.pow(1.1f, hero.level - 1);
        double multiplierStar = Math.pow(1.5f, hero.star);

        EquipBase weapon = JsonHelper.get(equipBaseList, "nameRegion", hero.equip.weapon);
        if (weapon != null) {
            hpPlus += (weapon.stats != null ? weapon.stats.get("hp") : 0);
            mpPlus += (weapon.stats != null ? weapon.stats.get("mp") : 0);
            atkPlus += (weapon.stats != null ? weapon.stats.get("atk") : 0);
            defPlus += (weapon.stats != null ? weapon.stats.get("def") : 0);
            agiPlus += (weapon.stats != null ? weapon.stats.get("agi") : 0);
            critPlus += (weapon.stats != null ? weapon.stats.get("crit") : 0);

        }

        EquipBase armor = JsonHelper.get(equipBaseList, "nameRegion", hero.equip.armor);
        if (armor != null) {
            hpPlus += (armor.stats != null ? armor.stats.get("hp") : 0);
            mpPlus += (armor.stats != null ? armor.stats.get("mp") : 0);
            atkPlus += (armor.stats != null ? armor.stats.get("atk") : 0);
            defPlus += (armor.stats != null ? armor.stats.get("def") : 0);
            agiPlus += (armor.stats != null ? armor.stats.get("agi") : 0);
            critPlus += (armor.stats != null ? armor.stats.get("crit") : 0);
        }

        EquipBase jewelry = JsonHelper.get(equipBaseList, "nameRegion", hero.equip.jewelry);
        if (jewelry != null) {
            hpPlus += (jewelry.stats != null ? jewelry.stats.get("hp") : 0);
            mpPlus += (jewelry.stats != null ? jewelry.stats.get("mp") : 0);
            atkPlus += (jewelry.stats != null ? jewelry.stats.get("atk") : 0);
            defPlus += (jewelry.stats != null ? jewelry.stats.get("def") : 0);
            agiPlus += (jewelry.stats != null ? jewelry.stats.get("agi") : 0);
            critPlus += (jewelry.stats != null ? jewelry.stats.get("crit") : 0);
        }

        EquipBase support = JsonHelper.get(equipBaseList, "nameRegion", hero.equip.support);
        if (support != null) {
            hpPlus += (support.stats != null ? support.stats.get("hp") : 0);
            mpPlus += (support.stats != null ? support.stats.get("mp") : 0);
            atkPlus += (support.stats != null ? support.stats.get("atk") : 0);
            defPlus += (support.stats != null ? support.stats.get("def") : 0);
            agiPlus += (support.stats != null ? support.stats.get("agi") : 0);
            critPlus += (support.stats != null ? support.stats.get("crit") : 0);
        }

        this.hp = (int) (characterBase.hp * multiplierLevel * multiplierStar) + hpPlus;
        this.mp = (int) (characterBase.mp * multiplierLevel * multiplierStar) + mpPlus;
        this.atk = (int) (characterBase.atk * multiplierLevel * multiplierStar) + atkPlus;
        this.def = (int) (characterBase.def * multiplierLevel * multiplierStar) + defPlus;
        this.agi = (int) (characterBase.agi * multiplierLevel * multiplierStar) + agiPlus;
        this.crit = (int) (characterBase.crit * multiplierLevel * multiplierStar) + critPlus;


//        return this;
    }
}
