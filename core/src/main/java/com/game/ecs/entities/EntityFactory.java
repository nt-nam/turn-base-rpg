package com.game.ecs.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.game.MainGame;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatsComponent;

public class EntityFactory {
    public static Entity createPlayer(TextureAtlas.AtlasRegion region){
        Entity entity = MainGame.getEngine().createEntity();
        entity.add(new PositionComponent(5, 5));
        entity.add(new StatsComponent(100, 100, 20, 10, 30, 30, 1));
//        entity.add(new SpriteComponent(region, 0, 0));
        // Add more component (InputComponent, InventoryComponent...)
        MainGame.getEngine().addEntity(entity);
        return entity;
    }
}
