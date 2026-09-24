package com.game.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test cho GameSessionManager.reset() - logic thuan Java, khong can Gdx.
 */
class GameSessionManagerResetTest {

    @Test
    @DisplayName("reset() dua session ve gia tri mac dinh cho new game")
    void reset_restoresDefaults() {
        GameSessionManager s = GameSessionManager.getInstance();

        // Lam ban session
        s.playerName = "hero1";
        s.coin = 999;
        s.level = 42;
        s.selectedCharacterId = "orange";
        s.enemyMapId = "village_0_1";
        s.moveLeft = true;
        s.agentEnabled = true;
        s.agentDriving = true;
        s.heroList.add(new com.game.models.entity.Hero());
        s.itemList.add(new com.game.models.entity.Item());
        s.missionList.add(new com.game.models.entity.Mission());
        s.currentEnemy = new com.game.ecs.component.EnemyTriggerComponent(1, "goblin", 1);
        s.unlockedAreas.add("forest");

        // Reset
        s.reset();

        // Kiem tra gia tri mac dinh
        assertEquals("", s.playerName);
        assertEquals(0, s.coin);
        assertEquals(1, s.level);
        assertEquals("", s.selectedCharacterId);
        assertEquals("village_0", s.targetMapId);
        assertEquals("", s.enemyMapId);
        assertFalse(s.moveLeft);
        assertFalse(s.agentEnabled);
        assertFalse(s.agentDriving);
        assertEquals("WANDER", s.agentBehavior);

        // Cac list phai rong
        assertTrue(s.heroList.isEmpty());
        assertTrue(s.itemList.isEmpty());
        assertTrue(s.missionList.isEmpty());
        assertTrue(s.equipList.isEmpty());
        assertTrue(s.unlockedAreas.isEmpty());

        // Trang thai
        assertNull(s.currentEnemy);
        assertNull(s.pendingTeleport);
        assertNotNull(s.profile);
        assertNotNull(s.lastBattleResult);
        assertFalse(s.lastBattleResult.won);
    }
}
