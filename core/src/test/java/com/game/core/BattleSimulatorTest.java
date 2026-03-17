package com.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.game.GdxTestRunner;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.EnemyComponent;
import com.game.ecs.component.StatComponent;
import com.game.ecs.component.SkillComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho BattleSimulator.
 * BattleSimulator dùng Gdx.app.log/error, nên cần HeadlessApplication.
 * BattleConfig.load() đọc file JSON → để tránh phụ thuộc file,
 * ta mock BattleConfig bằng cách cấu hình thủ công trước khi chạy.
 */
class BattleSimulatorTest {

    private BattleSimulator simulator;

    @BeforeAll
    static void setUpGdx() {
        GdxTestRunner.setup();
        // Thiết lập BattleConfig mặc định (tránh đọc file)
        setupBattleConfig();
    }

    @BeforeEach
    void setUp() {
        simulator = new BattleSimulator();
    }

    // ===================== Kiểm tra điều kiện đầu vào =====================

    @Test
    @DisplayName("run() với playerTeam rỗng phải ném BattleSimulationException")
    void run_emptyPlayerTeam_throwsException() {
        Array<Entity> empty = new Array<>();
        Array<Entity> enemies = makeTeam(1, true);
        assertThrows(BattleSimulationException.class,
            () -> simulator.run(empty, enemies));
    }

    @Test
    @DisplayName("run() với enemyTeam rỗng phải ném BattleSimulationException")
    void run_emptyEnemyTeam_throwsException() {
        Array<Entity> players = makeTeam(1, false);
        Array<Entity> empty = new Array<>();
        assertThrows(BattleSimulationException.class,
            () -> simulator.run(players, empty));
    }

    @Test
    @DisplayName("run() với null player team phải ném BattleSimulationException")
    void run_nullPlayerTeam_throwsException() {
        Array<Entity> enemies = makeTeam(1, true);
        assertThrows(BattleSimulationException.class,
            () -> simulator.run(null, enemies));
    }

    // ===================== Kiểm tra kết quả trận đấu =====================

    @Test
    @DisplayName("run() phải trả về winner là 'player' khi player mạnh hơn nhiều")
    void run_strongPlayer_winsWithPlayerWinner() {
        Array<Entity> players = makeTeam(3, false); // player agi cao, atk cao
        Array<Entity> enemies = makeTeam(1, true);  // enemy yếu
        BattleSimulationResult result = simulator.run(players, enemies);

        assertNotNull(result);
        assertNotNull(result.winner);
        assertFalse(result.winner.isEmpty(), "Winner phải được set");
    }

    @Test
    @DisplayName("run() phải trả về result.rounds không rỗng")
    void run_validTeams_hasRounds() {
        Array<Entity> players = makeTeam(1, false);
        Array<Entity> enemies = makeTeam(1, true);
        BattleSimulationResult result = simulator.run(players, enemies);
        assertFalse(result.rounds.isEmpty(), "Phải có ít nhất 1 round");
    }

    @Test
    @DisplayName("run() với player mạnh không có giới hạn round phải kết thúc với player thắng")
    void run_overwhelmingPlayer_playerWins() {
        // Player: atk=1000, def=1000, hp=10000
        // Enemy:  atk=1,    def=0,    hp=1
        Array<Entity> players = makeCustomTeam(10000, 1000, 1000, 100, 1, false);
        Array<Entity> enemies = makeCustomTeam(1,     1,    0,    0,   1, true);
        BattleSimulationResult result = simulator.run(players, enemies);
        assertEquals("player", result.winner,
            "Player mạnh áp đảo phải thắng, actual: " + result.winner);
    }

    @Test
    @DisplayName("run() với enemy mạnh không có giới hạn round phải kết thúc với enemy thắng")
    void run_overwhelmingEnemy_enemyWins() {
        Array<Entity> players = makeCustomTeam(1, 1, 0, 0, 1, false);
        Array<Entity> enemies = makeCustomTeam(10000, 1000, 1000, 100, 1, true);
        BattleSimulationResult result = simulator.run(players, enemies);
        assertEquals("enemy", result.winner,
            "Enemy mạnh áp đảo phải thắng, actual: " + result.winner);
    }

    // ===================== Helpers =====================

    /**
     * Tạo team với stat mặc định đơn giản.
     */
    private Array<Entity> makeTeam(int size, boolean isEnemy) {
        return makeCustomTeam(200, 30, 5, 20, size, isEnemy);
    }

    private Array<Entity> makeCustomTeam(int hp, int atk, int def, int agi, int size, boolean isEnemy) {
        Array<Entity> team = new Array<>();
        for (int i = 0; i < size; i++) {
            Entity entity = new Entity();

            CharacterComponent cc = new CharacterComponent();
            cc.nameRegion  = isEnemy ? "enemy" : "player";
            cc.classType   = "warrior";
            cc.hp = hp; cc.mp = 50; cc.atk = atk; cc.def = def; cc.agi = agi;
            entity.add(cc);

            StatComponent stat = new StatComponent(hp, 50, atk, def, agi, 10);
            stat.critRate   = 0f;   // Tắt crit để test deterministic
            stat.critDamage = 1f;
            entity.add(stat);

            entity.add(new GridComponent(i % 3, i / 3));
            entity.add(isEnemy ? new EnemyComponent() : new PlayerComponent());

            // Thêm kỹ năng đơn giản (không có effect → fallback sang basic attack trong TurnExecution)
            SkillComponent basicAttack = new SkillComponent(1, "basic", "Tấn công cơ bản", null);
            ListSkillComponent skills = new ListSkillComponent(null);
            skills.skills = new Array<>();
            skills.skills.add(basicAttack);
            entity.add(skills);

            team.add(entity);
        }
        return team;
    }

    /**
     * Cấu hình BattleConfig mà không đọc file JSON.
     * Sử dụng reflection để set field config.
     */
    private static void setupBattleConfig() {
        try {
            // Tạo JsonValue giả để BattleConfig dùng
            com.badlogic.gdx.utils.JsonValue fakeConfig = new com.badlogic.gdx.utils.JsonValue(
                com.badlogic.gdx.utils.JsonValue.ValueType.object);

            fakeConfig.addChild("maxRounds",
                new com.badlogic.gdx.utils.JsonValue(50L));
            fakeConfig.addChild("mpBasicAttackGain",
                new com.badlogic.gdx.utils.JsonValue(10L));
            fakeConfig.addChild("mpTakenDamageLoss",
                new com.badlogic.gdx.utils.JsonValue(5L));
            fakeConfig.addChild("mpSkill2Cost",
                new com.badlogic.gdx.utils.JsonValue(20L));
            fakeConfig.addChild("mpSkill3Cost",
                new com.badlogic.gdx.utils.JsonValue(35L));
            fakeConfig.addChild("counterMultiplier",
                new com.badlogic.gdx.utils.JsonValue(1.25));

            java.lang.reflect.Field f = BattleConfig.class.getDeclaredField("config");
            f.setAccessible(true);
            f.set(null, fakeConfig);
        } catch (Exception e) {
            throw new RuntimeException("Không thể cấu hình BattleConfig cho test", e);
        }
    }
}
