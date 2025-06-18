package com.game.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.ecs.component.TileMapComponent;

public class TileMapRenderSystem extends EntitySystem {
    private ImmutableArray<Entity> tileMapEntities;
    private OrthographicCamera camera;

    public TileMapRenderSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        tileMapEntities = engine.getEntitiesFor(Family.all(TileMapComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < tileMapEntities.size(); i++) {
            TileMapComponent mapComp = tileMapEntities.get(i).getComponent(TileMapComponent.class);
            mapComp.renderer.setView(camera);
            mapComp.renderer.render();
        }
    }
}
