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

            // Update lifetime and position
            damageText.lifeTime -= deltaTime;
            damageText.y += 50 * deltaTime; // Move up

            if (damageText.lifeTime <= 0) {
                iterator.remove();
                continue;
            }

            // Render
            font.getData().setScale(1.5f);
            if (damageText.isCritical) {
                font.setColor(Color.RED);
                font.getData().setScale(2.0f);
            } else if (damageText.text.equals("Miss")) {
                font.setColor(Color.GRAY);
            } else {
                font.setColor(Color.GREEN);
            }

            // Simple fade out
            Color color = font.getColor();
            font.setColor(color.r, color.g, color.b, damageText.lifeTime);

            font.draw(batch, damageText.text, damageText.x, damageText.y);

            // Reset color/scale
            font.setColor(Color.WHITE);
            font.getData().setScale(1.0f);
        }
    }
}
