package com.game.ecs.systems.character;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.game.ecs.component.PlayerSelectedComponent;

public class PlayerInitSystem extends IteratingSystem {
    private ComponentMapper<PlayerSelectedComponent> playerSelectedM = ComponentMapper.getFor(PlayerSelectedComponent.class);

    public PlayerInitSystem() {
        super(Family.all(PlayerSelectedComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerSelectedComponent comp = playerSelectedM.get(entity);
        Gdx.app.log("PlayerInit", "Player: " + comp.playerName + ", Knight: " + comp.knightId);

        // TODO: Tạo player entity, lưu dữ liệu, chuyển scene...

        // Xoá entity event để không xử lý lại
        getEngine().removeEntity(entity);
    }
}
