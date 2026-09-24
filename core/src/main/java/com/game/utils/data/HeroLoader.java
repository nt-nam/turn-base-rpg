package com.game.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.game.ecs.component.EquipComponent;
import com.game.ecs.component.InfoComponent;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Hero;
import com.game.models.entity.Lineup;
import com.game.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Loader cho Hero, danh sach doi hinh (Lineup) va InfoComponent.
 * Tach tu DataHelper (god class). Logic giu nguyen; DataHelper delegate sang day.
 */
public final class HeroLoader {

    private HeroLoader() {
    }

    public static List<Hero> sortHero(List<Hero> heroList) {
        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                return Integer.compare(hero2.star, hero1.star);
            }
        });
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

    public static List<Hero> loadHeroList(String filePath, boolean reload) {
        boolean player = filePath.equals(Constants.playerPath("hero_full.json"));
        if (player) {
            if (reload || GameSessionManager.getInstance().heroList.isEmpty()) {
                return sortHero(loadHeroListPrivate(filePath, player));
            } else {
                return GameSessionManager.getInstance().heroList;
            }
        } else {
            if (reload || GameSessionManager.getInstance().heroEnemyList.isEmpty()) {
                return loadEnemyListPrivate(filePath, player);
            } else {
                return GameSessionManager.getInstance().heroEnemyList;
            }
        }
    }

    private static List<Hero> loadHeroListPrivate(String filePath, boolean b) {
        List<Hero> heroes = new ArrayList<>();

        FileHandle fileHandle = Gdx.files.local(filePath);
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(fileHandle);

        if (root.get("reward") != null) {
            root = root.get("grid");
        }

        for (JsonValue hero : root) {
            Hero newHero = new Hero();
            newHero.characterId = hero.getString("characterId", "characterIdDefault");
            newHero.nameRegion = hero.getString("nameRegion", "nameRegionDefault");
            newHero.grid = hero.getString("grid", "empty");
            newHero.star = hero.getInt("star", 0);
            newHero.level = hero.getInt("level", 1);
            newHero.exp = hero.getInt("exp", 0);

            JsonValue equip = hero.get("equip");
            newHero.equip = new Hero.Equip();
            if (equip != null) {
                newHero.equip.weapon = equip.getString("weapon", "empty");
                newHero.equip.armor = equip.getString("armor", "empty");
                newHero.equip.jewelry = equip.getString("jewelry", "empty");
                newHero.equip.support = equip.getString("support", "empty");
            }

            heroes.add(newHero);
        }
        if (b) {
            GameSessionManager.getInstance().heroList = heroes;
            return GameSessionManager.getInstance().heroList;
        } else {
            GameSessionManager.getInstance().heroEnemyList = heroes;
            return GameSessionManager.getInstance().heroEnemyList;
        }
    }

    private static List<Hero> loadEnemyListPrivate(String filePath, boolean b) {
        List<Hero> heroes = new ArrayList<>();

        FileHandle fileHandle = Gdx.files.internal(filePath);
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(fileHandle);

        if (root.get("reward") != null) {
            root = root.get("grid");
        }

        for (JsonValue hero : root) {
            Hero newHero = new Hero();
            newHero.characterId = hero.getString("characterId", "characterIdDefault");
            newHero.nameRegion = hero.getString("nameRegion", "nameRegionDefault");
            newHero.grid = hero.getString("grid", "empty");
            newHero.star = hero.getInt("star", 0);
            newHero.level = hero.getInt("level", 1);

            JsonValue equip = hero.get("equip");
            newHero.equip = new Hero.Equip();
            if (equip != null) {
                newHero.equip.weapon = equip.getString("weapon", "empty");
                newHero.equip.armor = equip.getString("armor", "empty");
                newHero.equip.jewelry = equip.getString("jewelry", "empty");
                newHero.equip.support = equip.getString("support", "empty");
            }

            heroes.add(newHero);
        }
        if (b) {
            GameSessionManager.getInstance().heroList = heroes;
            return GameSessionManager.getInstance().heroList;
        } else {
            GameSessionManager.getInstance().heroEnemyList = heroes;
            return GameSessionManager.getInstance().heroEnemyList;
        }
    }

    public static List<Lineup> loadLineupList(boolean b) {
        if (b || GameSessionManager.getInstance().lineupList.isEmpty()) {
            GameSessionManager.getInstance().lineupList.clear();

            for (Hero hero : GameSessionManager.getInstance().heroList) {
                if (hero.grid.equals("empty")) continue;
                Lineup c = new Lineup();
                c.grid = hero.grid;
                c.characterId = hero.characterId;
                c.nameRegion = hero.nameRegion;
                GameSessionManager.getInstance().lineupList.add(c);
            }
        }
        return GameSessionManager.getInstance().lineupList;
    }

    public static List<InfoComponent> loadInfoComponentList(String filePath, boolean b) {
        if (b || GameSessionManager.getInstance().infoComponentList.isEmpty()) {
            GameSessionManager.getInstance().infoComponentList.clear();
            FileHandle fileHandle = Gdx.files.local(filePath);

            if (!fileHandle.exists()) {
                return null;
            }

            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(fileHandle);

            if (root == null || root.size == 0) {
                return null;
            }

            for (JsonValue equip : root) {
                InfoComponent newBag = new InfoComponent();
                newBag.characterId = equip.getString("id");
                newBag.nameRegion = equip.getString("type");
                newBag.star = equip.getInt("index", 1);
                newBag.level = equip.getInt("index", 1);

                JsonValue equipItem = equip.get("equip");
                newBag.equip = new InfoComponent.Equipment();

                newBag.equip.weapon = new EquipComponent(equipItem.get("weapon"));
                newBag.equip.armor = new EquipComponent(equipItem.get("armor"));
                newBag.equip.jewelry = new EquipComponent(equipItem.get("jewelry"));
                newBag.equip.support = new EquipComponent(equipItem.get("support"));

                GameSessionManager.getInstance().infoComponentList.add(newBag);
            }
        }

        return GameSessionManager.getInstance().infoComponentList.isEmpty() ? null : GameSessionManager.getInstance().infoComponentList;
    }
}
