package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.utils.data.GameSession;

public class PlayerInputSystem extends EntitySystem {
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpriteComponent> spr = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<AnimationStateComponent> asm = ComponentMapper.getFor(AnimationStateComponent.class);
    private final ImmutableArray<Entity> players;

    private static final float SPEED = 250f;

    public PlayerInputSystem(Engine engine) {
        this.players = engine.getEntitiesFor(Family.all(
            PlayerComponent.class,
            PositionComponent.class,
            SpriteComponent.class,
            AnimationStateComponent.class
        ).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : players) {
            PositionComponent pos = pm.get(entity);
            SpriteComponent sprite = spr.get(entity);
            AnimationStateComponent state = asm.get(entity);

            float dx = 0, dy = 0;

            // Keyboard input
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                dx -= SPEED * deltaTime;
                sprite.flipX = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                dx += SPEED * deltaTime;
                sprite.flipX = false;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                dy += SPEED * deltaTime;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                dy -= SPEED * deltaTime;
            }

            if (GameSession.moveLeft) {
                dx -= SPEED * deltaTime;
                sprite.flipX = true;
            }
            if (GameSession.moveRight) {
                dx += SPEED * deltaTime;
                sprite.flipX = false;
            }
            if (GameSession.moveUp) {
                dy += SPEED * deltaTime;
            }
            if (GameSession.moveDown) {
                dy -= SPEED * deltaTime;
            }

            // Thực hiện di chuyển nếu có input
            if (dx != 0 || dy != 0) {
                pos.x += dx;
                pos.y += dy;
                if (state != null && state.current != AnimationStateComponent.State.RUN) {
                    state.current = AnimationStateComponent.State.RUN;
                }
            } else {
                if (state != null && state.current == AnimationStateComponent.State.RUN) {
                    state.current = AnimationStateComponent.State.IDLE;
                }
            }
        }
    }
}
