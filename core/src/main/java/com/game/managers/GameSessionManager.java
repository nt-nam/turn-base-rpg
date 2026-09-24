package com.game.managers;

import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.InfoComponent;
import com.game.models.entity.Account;
import com.game.models.entity.Achievement;
import com.game.models.entity.BattleResult;
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
    /** true khi AgentControlSystem dang lai nhan vat -> PlayerInputSystem khong ghi de flag tu joystick. */
    public boolean agentDriving = false;
    /** Cau hinh: co bat agent gia lap nguoi choi khi spawn player khong. */
    public boolean agentEnabled = false;
    /** Cau hinh: hanh vi khoi tao cua agent (ten enum AgentControlComponent.Behavior). */
    public String agentBehavior = "WANDER";

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

    /** Ket qua tran dau gan nhat (cho BattleResultScreen hien thi). */
    public BattleResult lastBattleResult = new BattleResult();


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

    // --- Tien ich reset (new game)
    public void reset() {
        // User/account
        playerName = "";
        profile = new Profile();
        coin = 0;
        exp = 0;
        level = 1;

        // Character selection
        selectedCharacterId = "";
        selectedPlayerSpawnIndex = 0;
        skillCharacter = "orange";

        // Position/state
        targetMapId = "village_0";
        enemyMapId = "";
        playerX = -1;
        playerY = -1;
        playerDirection = "down";
        pendingTeleport = null;
        moveLeft = moveRight = moveUp = moveDown = false;
        agentDriving = false;
        agentEnabled = false;
        agentBehavior = "WANDER";

        // Danh sach du lieu
        accountList.clear();
        itemBaseList.clear();
        equipBaseList.clear();
        characterBaseList.clear();
        skillBaseList.clear();
        mapBattle = new MapBattle();
        equipList.clear();
        itemList.clear();
        lineupList.clear();
        heroList.clear();
        heroEnemyList.clear();
        checkMapList.clear();
        achievementList.clear();
        missionList.clear();
        dailyRewardList.clear();
        infoComponentList.clear();

        // Stats/resources
        currentHP = 100;
        currentMP = 20;
        partyMembers.clear();
        currentEnemy = null;
        lastBattleResult = new BattleResult();

        // Quests/progress
        unlockedAreas.clear();
        achievements.clear();

        // Misc
        playTime = 0;
        lastSaveTime = 0;
    }

    public boolean isRecruit() {
        if (profile.gem >= 5) {
            profile.gem -= 5;
            return true;
        }
        return false;
    }

    // --- API dieu khien Agent gia lap nguoi choi ---

    /** Bat agent voi hanh vi cho truoc (vd "WANDER", "HUNT_ENEMY", "GOTO_TELEPORT", "IDLE"). */
    public void enableAgent(String behavior) {
        this.agentEnabled = true;
        this.agentBehavior = behavior;
    }

    /** Tat agent, tra quyen dieu khien cho nguoi choi. */
    public void disableAgent() {
        this.agentEnabled = false;
        this.agentDriving = false;
        this.moveLeft = this.moveRight = this.moveUp = this.moveDown = false;
    }
}
