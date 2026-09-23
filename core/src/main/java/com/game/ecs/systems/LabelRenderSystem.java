package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.ecs.component.LabelComponent;
import com.game.MainGame;

import java.util.Iterator;

public class LabelRenderSystem extends IteratingSystem {
    private ComponentMapper<LabelComponent> labelMapper;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;

    public LabelRenderSystem(OrthographicCamera camera, String font) {
        super(Family.all(LabelComponent.class).get());
        labelMapper = ComponentMapper.getFor(LabelComponent.class);
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.font = MainGame.getAsM().getFont(font);
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LabelComponent labelComp = labelMapper.get(entity);

        Iterator<LabelComponent.DamageText> iterator = labelComp.activeLabels.iterator();
        while (iterator.hasNext()) {
            LabelComponent.DamageText damageText = iterator.next();

            // Update physics (parabolic arc)
            damageText.lifeTime -= deltaTime;
            damageText.x += damageText.vx * deltaTime;
            damageText.y += damageText.vy * deltaTime;
            damageText.vy -= 600 * deltaTime; // Gravity pull

            if (damageText.lifeTime <= 0) {
                iterator.remove();
                continue;
            }

            float progress = 1.0f - (damageText.lifeTime / damageText.maxLifeTime);
            
            // Render scaling (pop out effect)
            float baseScale = damageText.isCritical ? 2.0f : 1.3f;
            float scale = baseScale;
            if (progress < 0.15f) {
                // scale pops up to 1.5x of base
                scale = baseScale * (1.0f + (progress / 0.15f) * 0.5f);
            } else if (progress < 0.3f) {
                // scale settles back to base
                scale = baseScale * (1.5f - ((progress - 0.15f) / 0.15f) * 0.5f);
            }

            // Alpha fade out in last 50%
            float alpha = 1.0f;
            if (progress > 0.5f) {
                alpha = 1.0f - ((progress - 0.5f) * 2.0f);
            }

            font.getData().setScale(scale);
            
            if (damageText.isHeal) {
                font.setColor(0, 1, 0, alpha); // Green for heal
            } else if (damageText.isCritical) {
                font.setColor(1, 0.2f, 0, alpha); // Red-orange for critical
            } else if (damageText.text.equals("Miss")) {
                font.setColor(0.7f, 0.7f, 0.7f, alpha); // Gray for miss
            } else {
                font.setColor(1, 1, 1, alpha); // White for normal damage
            }

            font.draw(batch, damageText.text, damageText.x, damageText.y);

            // Reset color/scale
            font.setColor(Color.WHITE);
            font.getData().setScale(1.0f);
        }
    }
}
