package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.game.ecs.component.*;
import com.game.screens.main.WorldMapScreen;
import com.game.utils.CollisionUtils;
import com.game.utils.GameSession;
import com.game.utils.data.PendingTeleport;

public class TeleportTriggerSystem extends EntitySystem {
    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> triggerEntities;

    private final ComponentMapper<BoundComponent> colMapper = ComponentMapper.getFor(BoundComponent.class);
    private final ComponentMapper<TeleportTriggerComponent> triggerMapper = ComponentMapper.getFor(TeleportTriggerComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, BoundComponent.class).get());
        triggerEntities = engine.getEntitiesFor(Family.all(TeleportTriggerComponent.class, BoundComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (playerEntities.size() == 0) return;
        Entity player = playerEntities.first();
        BoundComponent playerCol = colMapper.get(player);

        boolean triggered = false;

        for (Entity trigger : triggerEntities) {
            TeleportTriggerComponent ttc = triggerMapper.get(trigger);
            BoundComponent triggerCol = colMapper.get(trigger);

            if (CollisionUtils.check(playerCol, triggerCol)) {
                triggered = true;
                if (GameSession.pendingTeleport == null) {
                    GameSession.pendingTeleport = new PendingTeleport(ttc.nextMap, ttc.nextSpawn, ttc.name);
                    System.out.println("Player đứng trên trigger teleport: " + ttc.name + " - spawn " + ttc.nextSpawn);
                    GameSession.currentMapId = ttc.nextMap;
                    GameSession.selectedPlayerSpawnIndex = ttc.nextSpawn;
                    WorldMapScreen.showBtnNextMap(true);
                }
                break;
            }
        }

        if (!triggered && GameSession.pendingTeleport != null) {
            System.out.println("Player đã rời khỏi trigger teleport.");
            GameSession.pendingTeleport = null;
            WorldMapScreen.showBtnNextMap(false);
        }
    }
}
