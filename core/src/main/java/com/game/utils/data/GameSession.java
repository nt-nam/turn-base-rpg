package com.game.utils.data;

import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.systems.TriggerComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {
    // User/account
    public static String playerName = "";
    public static String playerId = "";
    public static int selectedSlot = 0; // Save slot

    // Character selection
    public static String selectedCharacterId = "";
    public static int selectedPlayerSpawnIndex = 0;
    public static String skillCharacter = "";

    // Position/state
    public static String currentMapId = "village_0";
    public static float playerX = 0, playerY = 0;
    public static String playerDirection = "down";
    public static PendingTeleport pendingTeleport = null;
    public static boolean moveLeft = false;
    public static boolean moveRight = false;
    public static boolean moveUp = false;
    public static boolean moveDown = false;

    // Stats/resources
    public static int gold = 0;
    public static int exp = 0;
    public static int level = 1;
    public static int currentHP = 100;
    public static int currentMP = 20;

    // Inventory/skills
//    public static List<ItemData> inventory = new ArrayList<>();
//    public static List<SkillData> skills = new ArrayList<>();
    public static List<String> partyMembers = new ArrayList<>();

    public static EnemyTriggerComponent currentEnemy = null;


    // Quests/progress
//    public static Map<String, QuestState> questStates = new HashMap<>();
    public static Set<String> unlockedAreas = new HashSet<>();
    public static Set<String> achievements = new HashSet<>();

    // Settings
    public static float musicVolume = 1.0f;
    public static float sfxVolume = 1.0f;
    public static String language = "en";
    public static boolean autoSave = true;

    // Misc
    public static long playTime = 0;
    public static long lastSaveTime = 0;

    public GameSession(){}

    // --- Tiện ích reset (new game)
    public static void reset() {
    }
}

