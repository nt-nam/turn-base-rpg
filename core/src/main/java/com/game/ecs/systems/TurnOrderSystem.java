package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.game.core.Mappers;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.StatComponent;

public class TurnOrderSystem extends IteratingSystem {
    private Array<Entity> turnOrder;

    public TurnOrderSystem() {
        super(Family.all(CharacterComponent.class, StatComponent.class).get());
        turnOrder = new Array<>();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StatComponent stat = Mappers.stat.get(entity);
        CharacterComponent base = Mappers.base.get(entity);
        if (stat != null && stat.hp > 0 && base != null) {
            if (!turnOrder.contains(entity, true)) {
                turnOrder.add(entity);
                Gdx.app.log("TurnOrderSystem", "Added entity " + base.characterId + " to turn order (HP=" + stat.hp + ", AGI=" + stat.agi + ")");
            }
        } else {
            Gdx.app.log("TurnOrderSystem", "Skipped entity " + (base != null ? base.characterId : "null") + ": invalid stats or dead");
        }
    }

    public Array<Entity> getTurnOrder() {
        turnOrder.clear(); // Clear previous order
        update(0); // Process entities to populate turnOrder
        turnOrder.sort((a, b) -> {
            StatComponent sa = Mappers.stat.get(a);
            StatComponent sb = Mappers.stat.get(b);
            int agiCompare = Integer.compare(sb.agi, sa.agi);
            Gdx.app.log("TurnOrderSystem", "Sorting: " + Mappers.base.get(a).characterId + "(AGI=" + sa.agi + ") vs " +
                Mappers.base.get(b).characterId + "(AGI=" + sb.agi + ") -> " + agiCompare);
            return agiCompare;
        });
        Gdx.app.log("TurnOrderSystem", "Turn order size: " + turnOrder.size);
        return turnOrder;
    }

    public void reset() {
        turnOrder.clear();
        Gdx.app.log("TurnOrderSystem", "Turn order reset");
    }
}
