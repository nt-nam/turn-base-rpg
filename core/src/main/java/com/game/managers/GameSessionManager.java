package com.game.managers;

import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.InfoComponent;
import com.game.models.entity.Account;
import com.game.models.entity.Achievement;
import com.game.models.entity.CharacterBase;
import com.game.models.entity.CheckMap;
import com.game.models.entity.Equip;
import com.game.models.entity.EquipBase;
import com.game.models.entity.Item;
import com.game.models.entity.Lineup;
import com.game.utils.data.PendingTeleport;
import com.game.models.entity.DailyReward;
import com.game.models.entity.Hero;
import com.game.models.entity.MapBattle;
import com.game.models.entity.Profile;
import com.game.models.entity.ItemBase;
import com.game.models.entity.Mission;
import com.game.models.entity.skill.SkillBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSessionManager {

    private static GameSessionManager instance;

    private GameSessionManager() {
    }

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    // User/account
    public String playerName = "";
    public Profile profile = new Profile();
    public int coin = 0;
    public int exp = 0;
    public int level = 1;

    // Character selection
    public String selectedCharacterId = "";
    public int selectedPlayerSpawnIndex = 0;
    public String skillCharacter = "orange";

    // Position/state
    public String targetMapId = "village_0";
    public String enemyMapId = "";
    public float playerX = -1, playerY = -1;
    public String playerDirection = "down";
    public PendingTeleport pendingTeleport = null;
    public boolean moveLeft = false;
    public boolean moveRight = false;
    public boolean moveUp = false;
    public boolean moveDown = false;

    public List<Account> accountList = new ArrayList<>();
    public List<ItemBase> itemBaseList = new ArrayList<>();
    public List<EquipBase> equipBaseList = new ArrayList<>();
    public List<CharacterBase> characterBaseList = new ArrayList<>();
    public List<SkillBase> skillBaseList = new ArrayList<>();
    public MapBattle mapBattle = new MapBattle();

    public List<Equip> equipList = new ArrayList<>();
    public List<Item> itemList = new ArrayList<>();
    public List<Lineup> lineupList = new ArrayList<>();
    public List<Hero> heroList = new ArrayList<>();
    public List<Hero> heroEnemyList = new ArrayList<>();
    public List<CheckMap> checkMapList = new ArrayList<>();
    public List<Achievement> achievementList = new ArrayList<>();
    public List<Mission> missionList = new ArrayList<>();
    public List<DailyReward> dailyRewardList = new ArrayList<>();
    public List<InfoComponent> infoComponentList = new ArrayList<>();

    // Stats/resources

    public int currentHP = 100;
    public int currentMP = 20;

    public List<String> partyMembers = new ArrayList<>();

    public EnemyTriggerComponent currentEnemy = null;


    // Quests/progress
    public Set<String> unlockedAreas = new HashSet<>();
    public Set<String> achievements = new HashSet<>();

    // Settings
    public float musicVolume = 1.0f;
    public float sfxVolume = 1.0f;
    public String language = "vn";
    public boolean autoSave = true;

    // Misc
    public long playTime = 0;
    public long lastSaveTime = 0;

    // --- Tiện ích reset (new game)
    public void reset() {
    }

    public boolean isRecruit() {
        if (profile.gem >= 5) {
            profile.gem -= 5;
            return true;
        }
        return false;
    }
}
