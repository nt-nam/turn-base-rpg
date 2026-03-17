package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.ecs.component.HealthBarComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.StatComponent;

public class HealthBarRenderSystem extends IteratingSystem {
    private ComponentMapper<HealthBarComponent> hbMapper;
    private ComponentMapper<StatComponent> statMapper;
    private ComponentMapper<PositionComponent> posMapper;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    public HealthBarRenderSystem(OrthographicCamera camera) {
        super(Family.all(HealthBarComponent.class, StatComponent.class, PositionComponent.class).get());
        hbMapper = ComponentMapper.getFor(HealthBarComponent.class);
        statMapper = ComponentMapper.getFor(StatComponent.class);
        posMapper = ComponentMapper.getFor(PositionComponent.class);
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        super.update(deltaTime);
        shapeRenderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthBarComponent hb = hbMapper.get(entity);
        StatComponent stat = statMapper.get(entity);
        PositionComponent pos = posMapper.get(entity);

        hb.currentHp = stat.hp;
        hb.maxHp = stat.maxHp;
        
        if (hb.maxHp > 0) {
            hb.percent = hb.currentHp / hb.maxHp;
        } else {
            hb.percent = 0;
        }
        
        if(hb.percent < 0) hb.percent = 0;

        float width = hb.width;
        float height = hb.height;
        float x = hb.x;
        float y = hb.y;

        // Background (Red/Gray for missing HP)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, width, height);

        // Foreground (Green/Red for current HP)
        if (hb.percent > 0.5f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (hb.percent > 0.2f) {
            shapeRenderer.setColor(Color.ORANGE);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        shapeRenderer.rect(x, y, width * hb.percent, height);
        
        // Border
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
    }
}
