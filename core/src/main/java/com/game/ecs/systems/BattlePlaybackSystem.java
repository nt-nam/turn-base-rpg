// 📦 ecs/system/BattlePlaybackSystem.java
package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.*;
import com.game.ecs.component.*;
import com.game.core.*;

public class BattlePlaybackSystem extends EntitySystem {
    private ComponentMapper<BattlePlaybackComponent> mPlayback = ComponentMapper.getFor(BattlePlaybackComponent.class);
    private ComponentMapper<BattleActorComponent> mActor = ComponentMapper.getFor(BattleActorComponent.class);
    private ComponentMapper<StatComponent> mStat = ComponentMapper.getFor(StatComponent.class);

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BattlePlaybackComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity e : entities) {
            BattlePlaybackComponent b = mPlayback.get(e);
            if (b.currentTurn >= b.result.turns.size) continue;

            b.turnTimer += deltaTime;
            if (b.turnTimer >= b.delayPerTurn) {
                TurnResult t = b.result.turns.get(b.currentTurn);
                applyTurn(t);
                b.turnTimer = 0f;
                b.currentTurn++;
            }
        }
    }

    private void applyTurn(TurnResult t) {
        for (Entity e : getEngine().getEntities()) {
            BattleActorComponent actor = mActor.get(e);
            if (actor != null && actor.id.equals(t.targetId)) {
                StatComponent stat = mStat.get(e);
                stat.hp -= t.damage;
                // TODO: trigger damage animation, check death...
            }
        }
    }
}
