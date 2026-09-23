package com.game.utils;

import com.badlogic.gdx.utils.JsonValue;
import com.game.ecs.component.InfoComponent;
import com.game.models.entity.Account;
import com.game.models.entity.Achievement;
import com.game.models.entity.CharacterBase;
import com.game.models.entity.CheckMap;
import com.game.models.entity.DailyReward;
import com.game.models.entity.Equip;
import com.game.models.entity.EquipBase;
import com.game.models.entity.Hero;
import com.game.models.entity.Item;
import com.game.models.entity.ItemBase;
import com.game.models.entity.Lineup;
import com.game.models.entity.MapBattle;
import com.game.models.entity.Mission;
import com.game.models.entity.Profile;
import com.game.models.entity.skill.SkillBase;
import com.game.utils.data.AccountProfileLoader;
import com.game.utils.data.BattleMapLoader;
import com.game.utils.data.HeroLoader;
import com.game.utils.data.ItemEquipLoader;
import com.game.utils.data.JsonAccess;
import com.game.utils.data.MissionRewardLoader;
import com.game.utils.data.SkillCharacterLoader;

import java.util.List;

/**
 * Facade cho tang tai du lieu (data loading).
 *
 * <p>Truoc day day la god class ~647 LOC. Nay da tach logic thanh cac loader theo nhom
 * trong {@code com.game.utils.data}:
 * <ul>
 *   <li>{@link AccountProfileLoader} - account, profile</li>
 *   <li>{@link HeroLoader} - hero, lineup, info component</li>
 *   <li>{@link ItemEquipLoader} - item, equip (base & instance)</li>
 *   <li>{@link SkillCharacterLoader} - skill base, character base</li>
 *   <li>{@link MissionRewardLoader} - mission, achievement, daily, checkmap</li>
 *   <li>{@link BattleMapLoader} - map battle, reward battle</li>
 *   <li>{@link JsonAccess} - tien ich get()/getJsonValue()</li>
 * </ul>
 *
 * <p>DataHelper giu nguyen API cong khai (facade) de khong pha ~60 caller hien co.
 * Chi delegate sang loader tuong ung, khong doi hanh vi.
 */
public class DataHelper {

    // ---- Account / Profile ----
    public static List<Account> loadAccountList(boolean b) {
        return AccountProfileLoader.loadAccountList(b);
    }

    public static Profile loadProfile(boolean b) {
        return AccountProfileLoader.loadProfile(b);
    }

    public static void clearDataProfile() {
        AccountProfileLoader.clearDataProfile();
    }

    // ---- Hero / Lineup / Info ----
    public static List<Hero> sortHero(List<Hero> heroList) {
        return HeroLoader.sortHero(heroList);
    }

    public static List<Hero> loadHeroList(String filePath, boolean reload) {
        return HeroLoader.loadHeroList(filePath, reload);
    }

    public static List<Lineup> loadLineupList(boolean b) {
        return HeroLoader.loadLineupList(b);
    }

    public static List<InfoComponent> loadInfoComponentList(String filePath, boolean b) {
        return HeroLoader.loadInfoComponentList(filePath, b);
    }

    // ---- Item / Equip ----
    public static List<EquipBase> loadEquipBaseList(boolean b) {
        return ItemEquipLoader.loadEquipBaseList(b);
    }

    public static List<Equip> loadEquipList(boolean b) {
        return ItemEquipLoader.loadEquipList(b);
    }

    public static List<ItemBase> loadItemBaseList(boolean b) {
        return ItemEquipLoader.loadItemBaseList(b);
    }

    public static List<Item> loadItemList(boolean b) {
        return ItemEquipLoader.loadItemList(b);
    }

    // ---- Skill / Character base ----
    public static List<SkillBase> loadSkillBaseList(boolean b) {
        return SkillCharacterLoader.loadSkillBaseList(b);
    }

    public static List<CharacterBase> loadCharacterBaseList() {
        return SkillCharacterLoader.loadCharacterBaseList();
    }

    // ---- Mission / Achievement / Daily / CheckMap ----
    public static List<Achievement> loadAchievementList(boolean b) {
        return MissionRewardLoader.loadAchievementList(b);
    }

    public static List<CheckMap> loadCheckMapList(boolean b) {
        return MissionRewardLoader.loadCheckMapList(b);
    }

    public static List<DailyReward> loadDailyRewardList(boolean b) {
        return MissionRewardLoader.loadDailyRewardList(b);
    }

    public static List<Mission> loadMissionList(boolean b) {
        return MissionRewardLoader.loadMissionList(b);
    }

    // ---- Battle map ----
    public static MapBattle loadMapBattle(String filePath) {
        return BattleMapLoader.loadMapBattle(filePath);
    }

    // ---- Tien ich JSON chung ----
    public static <T> T get(List<T> list, String key, Object value) {
        return JsonAccess.get(list, key, value);
    }

    public static JsonValue getJsonValue(String filePath) {
        return JsonAccess.getJsonValue(filePath);
    }
}
