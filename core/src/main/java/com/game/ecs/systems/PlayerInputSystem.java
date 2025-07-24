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
import com.game.utils.GameSession;

public class PlayerInputSystem extends EntitySystem {
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpriteComponent> spr = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<AnimationStateComponent> asm = ComponentMapper.getFor(AnimationStateComponent.class);
    private final ImmutableArray<Entity> players;
    private final UIJoystick joystick;

    private static final float SPEED = 250f;

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
            logicJoystick();
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
                GameSession.profile.pos.x = pos.x;
                GameSession.profile.pos.y = pos.y;
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
            GameSession.moveRight = true;
            GameSession.moveLeft = false;
        } else if (xValue < -0.2f) {  // Nếu joystick nghiêng sang trái (từ 0 đến -1)
            GameSession.moveLeft = true;
            GameSession.moveRight = false;
        } else {
            GameSession.moveRight = false;
            GameSession.moveLeft = false;
        }

        if (yValue > 0.2f) {  // Nếu joystick nghiêng lên
            GameSession.moveUp = true;
            GameSession.moveDown = false;
        } else if (yValue < -0.2f) {  // Nếu joystick nghiêng xuống
            GameSession.moveDown = true;
            GameSession.moveUp = false;
        } else {
            GameSession.moveUp = false;
            GameSession.moveDown = false;
        }

        if (xValue == 0 && yValue == 0) {
            GameSession.moveRight = false;
            GameSession.moveLeft = false;
            GameSession.moveUp = false;
            GameSession.moveDown = false;
        }
    }

}
