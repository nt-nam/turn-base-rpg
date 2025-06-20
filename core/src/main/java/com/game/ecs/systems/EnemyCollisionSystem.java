package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.screens.ScreenType;
import com.game.MainGame;
import com.game.utils.CollisionUtils;
import com.game.utils.data.GameSession;

public class EnemyCollisionSystem extends EntitySystem {
    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> enemyEntities;

    private final ComponentMapper<BoundComponent> boundMapper = ComponentMapper.getFor(BoundComponent.class);
    private final ComponentMapper<PositionComponent> posMapper = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<EnemyTriggerComponent> enemyMapper = ComponentMapper.getFor(EnemyTriggerComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class, BoundComponent.class).get());
        enemyEntities = engine.getEntitiesFor(Family.all(EnemyTriggerComponent.class, BoundComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (playerEntities.size() == 0 || enemyEntities.size() == 0) return;

        Entity player = playerEntities.first();
        BoundComponent playerBound = boundMapper.get(player);
        PositionComponent playerPos = posMapper.get(player);

        // Update bound vị trí theo position (nếu chưa có BoundUpdateSystem)
        if (playerBound.type == BoundComponent.BoundType.RECT && playerBound.rect != null) {
            playerBound.rect.setPosition(playerPos.x, playerPos.y);
        } else if (playerBound.type == BoundComponent.BoundType.POLYGON && playerBound.polygon != null) {
            playerBound.polygon.setPosition(playerPos.x, playerPos.y);
        }

        boolean collided = false;
        for (Entity enemy : enemyEntities) {
            EnemyTriggerComponent triggerComponent = enemyMapper.get(enemy);
            BoundComponent enemyBound = boundMapper.get(enemy);
            PositionComponent enemyPos = posMapper.get(enemy);

            // Nếu enemy trigger cũng di chuyển, update bound của nó
            if (enemyPos != null) {
                if (enemyBound.type == BoundComponent.BoundType.RECT && enemyBound.rect != null) {
                    enemyBound.rect.setPosition(enemyPos.x, enemyPos.y);
                } else if (enemyBound.type == BoundComponent.BoundType.POLYGON && enemyBound.polygon != null) {
                    enemyBound.polygon.setPosition(enemyPos.x, enemyPos.y);
                }
            }

            // Kiểm tra va chạm Bound với Bound
            if (CollisionUtils.check(playerBound, enemyBound)) {
                collided = true;
                if (GameSession.currentEnemy == null) {
                    GameSession.currentEnemy = triggerComponent;
                    System.out.println("Chạm enemy: " + triggerComponent.characterId + " - level " + triggerComponent.level);
                    GameSession.playerX = playerPos.x;
                    GameSession.playerY = playerPos.y;
                    MainGame.getScM().showScreen(ScreenType.BATTLE);
                    // Nếu muốn xoá trigger entity sau va chạm, bật dòng này:
                    // getEngine().removeEntity(enemy);
                }
                return; // chỉ xử lý 1 enemy 1 lần
            }
        }

        // Nếu player đã rời vùng enemy trigger thì reset currentEnemy
        if (!collided && GameSession.currentEnemy != null) {
            System.out.println("Player đã rời vùng enemy.");
            GameSession.currentEnemy = null;
        }
    }
}
