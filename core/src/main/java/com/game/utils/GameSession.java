package com.game.utils;

import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.InfoComponent;
import com.game.utils.json.Account;
import com.game.utils.json.Achievement;
import com.game.utils.json.Bag;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.EquipBase;
import com.game.utils.json.Lineup;
import com.game.utils.data.PendingTeleport;
import com.game.utils.json.DailyReward;
import com.game.utils.json.Hero;
import com.game.utils.json.Profile;
import com.game.utils.json.ItemBase;
import com.game.utils.json.Mission;
import com.game.utils.json.skill.SkillBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {
    // User/account
    public static String playerName = "";
    public static String mainInfo ="";
    public static String playerId = "";
    public static int selectedSlot = 0;
    public static int sizeTeam =5;
    public static Profile profile = new Profile();

    // Character selection
    public static String selectedCharacterId = "";
    public static int selectedPlayerSpawnIndex = 0;
    public static String skillCharacter = "orange";

    // Position/state
    public static String currentMapId = "village_0";
    public static String enemyMapId = "";
    public static float playerX = 0, playerY = 0;
    public static String playerDirection = "down";
    public static PendingTeleport pendingTeleport = null;
    public static boolean moveLeft = false;
    public static boolean moveRight = false;
    public static boolean moveUp = false;
    public static boolean moveDown = false;

    public static List<Account> accountList = new ArrayList<>();
    public static List<ItemBase> itemBaseList = new ArrayList<>();
    public static List<EquipBase> equipBaseList = new ArrayList<>();
    public static List<CharacterBase> characterBaseList = new ArrayList<>();
    public static List<SkillBase> skillBaseList = new ArrayList<>();

    public static List<Bag> bagList = new ArrayList<>();
    public static List<Lineup> lineupList = new ArrayList<>();
    public static List<Hero> heroList = new ArrayList<>();
    public static List<Achievement> achievementList = new ArrayList<>();
    public static List<Mission> missionList = new ArrayList<>();
    public static List<DailyReward> dailyRewardList = new ArrayList<>();
    public static List<InfoComponent> infoComponentList = new ArrayList<>();

    // Stats/resources
    public static int coin = 0;
    public static int gem = 15;
    public static int exp = 0;
    public static int level = 1;
    public static int currentHP = 100;
    public static int currentMP = 20;

    public static List<String> partyMembers = new ArrayList<>();

    public static EnemyTriggerComponent currentEnemy = null;


    // Quests/progress
    public static Set<String> unlockedAreas = new HashSet<>();
    public static Set<String> achievements = new HashSet<>();

    // Settings
    public static float musicVolume = 1.0f;
    public static float sfxVolume = 1.0f;
    public static String language = "vn";
    public static boolean autoSave = true;

    // Misc
    public static long playTime = 0;
    public static long lastSaveTime = 0;

    public GameSession(){}

    // --- Tiện ích reset (new game)
    public static void reset() {
    }

    public static boolean isRecruit() {
        if(gem >= 5 ){
            gem -=5;
            return true;
        }
        return false;
    }
}

