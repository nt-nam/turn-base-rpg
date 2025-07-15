package com.game.utils;

import com.game.ecs.component.EnemyTriggerComponent;
import com.game.utils.json.GridData;
import com.game.utils.data.PendingTeleport;
import com.game.utils.json.DailyReward;
import com.game.utils.json.Info;

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
    public static Info playerInfo = new Info();

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

