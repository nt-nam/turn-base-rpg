package com.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ui.base.UIJoystick;
import com.game.managers.GameSessionManager;

public class PlayerInputSystem extends EntitySystem {
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpriteComponent> spr = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<AnimationStateComponent> asm = ComponentMapper.getFor(AnimationStateComponent.class);
    private final ImmutableArray<Entity> players;
    private final UIJoystick joystick;

    private static final float SPEED = 1000f;

    public PlayerInputSystem(Engine engine, UIJoystick joystick) {
        this.players = engine.getEntitiesFor(Family.all(
            PlayerComponent.class,
            PositionComponent.class,
            SpriteComponent.class,
            AnimationStateComponent.class
        ).get());
        this.joystick = joystick;
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
            // Keyboard input
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                dx -= SPEED * deltaTime;
                sprite.flipX = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                dx += SPEED * deltaTime;
                sprite.flipX = false;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                dy += SPEED * deltaTime;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                dy -= SPEED * deltaTime;
            }
            logicJoystick();
            if (GameSessionManager.getInstance().moveLeft) {
                dx -= SPEED * deltaTime;
                sprite.flipX = true;
            }
            if (GameSessionManager.getInstance().moveRight) {
                dx += SPEED * deltaTime;
                sprite.flipX = false;
            }
            if (GameSessionManager.getInstance().moveUp) {
                dy += SPEED * deltaTime;
            }
            if (GameSessionManager.getInstance().moveDown) {
                dy -= SPEED * deltaTime;
            }


            // Thực hiện di chuyển nếu có input
            if (dx != 0 || dy != 0) {
                // Chuẩn hóa vector di chuyển để không bị di chuyển nhanh khi di chuyển chéo
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                if (length != 0) {
                    dx = (dx / length) * 2f;
                    dy = (dy / length) * 2f;
                }

                pos.x += dx;
                pos.y += dy;
                GameSessionManager.getInstance().profile.pos.x = pos.x;
                GameSessionManager.getInstance().profile.pos.y = pos.y;

                if (state != null && state.requested != AnimationStateComponent.State.RUN) {
                    state.requested = AnimationStateComponent.State.RUN;
                }
            } else {
                if (state != null && state.requested == AnimationStateComponent.State.RUN) {
                    state.requested = AnimationStateComponent.State.IDLE;
                }
            }
        }
    }

    private void logicJoystick() {
        float xValue = joystick.getXValue();
        float yValue = joystick.getYValue();


        if (xValue > 0.2f) {  // Nếu joystick nghiêng sang phải (từ 0 đến 1)
            GameSessionManager.getInstance().moveRight = true;
            GameSessionManager.getInstance().moveLeft = false;
        } else if (xValue < -0.2f) {  // Nếu joystick nghiêng sang trái (từ 0 đến -1)
            GameSessionManager.getInstance().moveLeft = true;
            GameSessionManager.getInstance().moveRight = false;
        } else {
            GameSessionManager.getInstance().moveRight = false;
            GameSessionManager.getInstance().moveLeft = false;
        }

        if (yValue > 0.2f) {  // Nếu joystick nghiêng lên
            GameSessionManager.getInstance().moveUp = true;
            GameSessionManager.getInstance().moveDown = false;
        } else if (yValue < -0.2f) {  // Nếu joystick nghiêng xuống
            GameSessionManager.getInstance().moveDown = true;
            GameSessionManager.getInstance().moveUp = false;
        } else {
            GameSessionManager.getInstance().moveUp = false;
            GameSessionManager.getInstance().moveDown = false;
        }

        if (xValue == 0 && yValue == 0) {
            GameSessionManager.getInstance().moveRight = false;
            GameSessionManager.getInstance().moveLeft = false;
            GameSessionManager.getInstance().moveUp = false;
            GameSessionManager.getInstance().moveDown = false;
        }
    }

}
