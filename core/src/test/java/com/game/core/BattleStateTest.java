package com.game.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho BattleState.
 * BattleState chỉ dùng Ashley Entity (không gọi Gdx.app),
 * không cần khởi tạo headless backend.
 */
class BattleStateTest {

    private BattleState battleState;

    @BeforeEach
    void setUp() {
        battleState = new BattleState();
    }

    // ===================== isDead =====================

    @Test
    @DisplayName("isDead: entity không có component → true")
    void isDead_noComponents_returnsTrue() {
        Entity entity = new Entity();
        assertTrue(battleState.isDead(entity));
    }

    @Test
    @DisplayName("isDead: entity có CharacterComponent nhưng không có StatComponent → true")
    void isDead_noStatComponent_returnsTrue() {
        Entity entity = new Entity();
        entity.add(new CharacterComponent());
        assertTrue(battleState.isDead(entity));
    }

    @Test
    @DisplayName("isDead: entity có stat.hp > 0 → false")
    void isDead_aliveEntity_returnsFalse() {
        Entity entity = makeAliveEntity(100);
        assertFalse(battleState.isDead(entity));
    }

    @Test
    @DisplayName("isDead: entity có stat.hp = 0 → true")
    void isDead_zeroHp_returnsTrue() {
        Entity entity = makeAliveEntity(0);
        assertTrue(battleState.isDead(entity));
    }

    @Test
    @DisplayName("isDead: entity có stat.hp âm → true")
    void isDead_negativeHp_returnsTrue() {
        Entity entity = makeAliveEntity(-10);
        assertTrue(battleState.isDead(entity));
    }

    // ===================== isTeamDead / isBattleOver =====================

    @Test
    @DisplayName("isBattleOver: team trống → true")
    void isBattleOver_emptyTeams_returnsTrue() {
        assertTrue(battleState.isBattleOver(new Array<>(), new Array<>()));
    }

    @Test
    @DisplayName("isBattleOver: cả 2 team còn sống → false")
    void isBattleOver_bothAlive_returnsFalse() {
        Array<Entity> players = makeTeam(1, 100);
        Array<Entity> enemies = makeTeam(1, 100);
        assertFalse(battleState.isBattleOver(players, enemies));
    }

    @Test
    @DisplayName("isBattleOver: player team chết hoàn toàn → true")
    void isBattleOver_playersDead_returnsTrue() {
        Array<Entity> players = makeTeam(2, 0);
        Array<Entity> enemies = makeTeam(1, 100);
        assertTrue(battleState.isBattleOver(players, enemies));
    }

    @Test
    @DisplayName("isBattleOver: enemy team chết hoàn toàn → true")
    void isBattleOver_enemiesDead_returnsTrue() {
        Array<Entity> players = makeTeam(2, 100);
        Array<Entity> enemies = makeTeam(2, 0);
        assertTrue(battleState.isBattleOver(players, enemies));
    }

    @Test
    @DisplayName("isBattleOver: team hỗn hợp (một số chết, một số sống) → false")
    void isBattleOver_mixedTeam_returnsFalse() {
        Array<Entity> players = new Array<>();
        players.add(makeAliveEntity(0));    // Chết
        players.add(makeAliveEntity(50));   // Sống
        Array<Entity> enemies = makeTeam(1, 100);
        assertFalse(battleState.isBattleOver(players, enemies));
    }

    // ===================== checkWinner =====================

    @Test
    @DisplayName("checkWinner: enemy chết hoàn toàn → 'player' thắng")
    void checkWinner_enemyTeamDead_returnsPlayer() {
        Array<Entity> players = makeTeam(1, 100);
        Array<Entity> enemies = makeTeam(1, 0);
        assertEquals("player", battleState.checkWinner(players, enemies));
    }

    @Test
    @DisplayName("checkWinner: player chết hoàn toàn → 'enemy' thắng")
    void checkWinner_playerTeamDead_returnsEnemy() {
        Array<Entity> players = makeTeam(1, 0);
        Array<Entity> enemies = makeTeam(1, 100);
        assertEquals("enemy", battleState.checkWinner(players, enemies));
    }

    @Test
    @DisplayName("checkWinner: cả 2 còn sống → hòa")
    void checkWinner_bothAlive_returnsDraw() {
        Array<Entity> players = makeTeam(1, 100);
        Array<Entity> enemies = makeTeam(1, 100);
        String result = battleState.checkWinner(players, enemies);
        assertTrue(result.contains("draw"), "Kết quả hòa phải chứa 'draw', actual: " + result);
    }

    // ===================== Helpers =====================

    private Entity makeAliveEntity(int hp) {
        Entity entity = new Entity();
        entity.add(new CharacterComponent());
        StatComponent stat = new StatComponent();
        stat.hp = hp;
        entity.add(stat);
        return entity;
    }

    private Array<Entity> makeTeam(int size, int hp) {
        Array<Entity> team = new Array<>();
        for (int i = 0; i < size; i++) {
            team.add(makeAliveEntity(hp));
        }
        return team;
    }
}
