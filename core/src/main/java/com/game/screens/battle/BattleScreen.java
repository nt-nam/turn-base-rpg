package com.game.screens.battle;

import static com.game.utils.Constants.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.MainGame;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.BattleCharacterComponent;
import com.game.ecs.component.EffectComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatsComponent;
import com.game.ecs.systems.AnimationStateSystem;
import com.game.ecs.systems.SpriteRenderSystem;
import com.game.screens.BaseScreen;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UIProgressBar;
import com.game.utils.data.AnimationCache;
import com.game.utils.data.GameSession;

import java.util.Arrays;

public class BattleScreen extends BaseScreen {
    private static final String bg = "texture/battle/summer.png";
    private static Entity player;
    private static Entity enemy;
    private AnimationStateComponent state;

    public BattleScreen() {
        super();
    }

    public static void loadingAsset() {
        MainGame.getAsM().loadAtlas(SKILL_SKILL);
        MainGame.getAsM().loadAtlas(UI_WOOD);
        MainGame.getAsM().loadAtlas(UI_POPUP);
        MainGame.getAsM().load(bg, Texture.class);
        MainGame.getAsM().loadAtlas(CHARACTER + "02Knight.atlas");
    }

    public static void unLoadingAsset() {
        MainGame.getAsM().unload(SKILL_SKILL);
        MainGame.getAsM().unload(UI_WOOD);
        MainGame.getAsM().unload(UI_POPUP);
        MainGame.getAsM().unload(bg);
        MainGame.getAsM().unload(CHARACTER + "02Knight.atlas");
    }

    @Override
    protected void createScreen() {
    }

    @Override
    public void show() {
        super.show();
        System.out.println("BattleScreen.show");
        createUI();
        createProgessBar();
        // --- TẠO ECS ENTITY ---
        float playerX = screenWidth * 0.1f;
        float playerY = screenHeight * 0.25f;

        float enemyX = screenWidth * 0.7f;
        float enemyY = screenHeight * 0.5f;


        String enemiesId = "07Knight";
        loadAllAnimations(enemiesId, CHARACTER + enemiesId + ".atlas");
        loadAllAnimations(GameSession.selectedCharacterId, CHARACTER + GameSession.selectedCharacterId + ".atlas");
        loadAllAnimationsSkill(GameSession.skillCharacter, SKILL_SKILL);

        // Player entity
        player = engine.createEntity();
        player.add(new PositionComponent(playerX, playerY));
        player.add(new SpriteComponent(GameSession.selectedCharacterId, "idle", 10, false)); // false = không flip
        player.add(new StatsComponent(120, 120, 18, 12, 40, 40, 1));
        player.add(new BattleCharacterComponent(true, Arrays.asList("slash", "shield_bash")));
        player.add(new AnimationStateComponent());
        engine.addEntity(player);


        // Enemy entity
        enemy = engine.createEntity();
        enemy.add(new PositionComponent(enemyX, enemyY));
        enemy.add(new SpriteComponent(enemiesId, "idle", 7, true)); // true = flip, hướng qua trái
        enemy.add(new StatsComponent(150, 150, 14, 8, 30, 30, 1));
        enemy.add(new BattleCharacterComponent(false, Arrays.asList("fireball", "taunt")));
        enemy.add(new AnimationStateComponent());
        engine.addEntity(enemy);

        // System vẽ entity (dùng batch, KHÔNG liên quan Scene2D)
        engine.addSystem(new SpriteRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new AnimationStateSystem(engine));
//        engine.addSystem(new BattleTurnSystem(...));
//        engine.addSystem(new SkillEffectSystem(...));
    }

    private void loadAllAnimations(String characterId, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(characterId, "idle", new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(characterId, "attack", new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "die", new Animation<>(0.1f, atlas.findRegions("die"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "hurt", new Animation<>(0.1f, atlas.findRegions("hurt"), Animation.PlayMode.NORMAL));
    }

    private void loadAllAnimationsSkill(String nameSkill, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().getAtlas(atlasPath);
        AnimationCache.put(nameSkill, nameSkill + "_attack", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_attack"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_attack_big", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_attack_big"), Animation.PlayMode.NORMAL));
        AnimationCache.put(nameSkill, nameSkill + "_explode", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_explode"), Animation.PlayMode.NORMAL));
        AnimationCache.put(nameSkill, nameSkill + "_heal", new Animation<>(0.1f, atlas.findRegions(nameSkill + "heal"), Animation.PlayMode.NORMAL));
        AnimationCache.put(nameSkill, nameSkill + "_ultimate", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_ultimate"), Animation.PlayMode.NORMAL));
    }

    private void createSkillEffect(Entity entity,String skillAnimName) {
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        float x = pos.x;
        float y = pos.y;

        // Tạo entity effect mới
        Entity effect = engine.createEntity();
        effect.add(new PositionComponent(x, y));
        effect.add(new SpriteComponent(entity.getComponent(SpriteComponent.class).characterId, skillAnimName, 1f, false));
        effect.add(new EffectComponent());
        engine.addEntity(effect);
    }

    private void createProgessBar() {
        UIProgressBar progressBar = new UIProgressBar(0, 100, 1, false, "line_red");
        progressBar.setBounds(screenWidth * 0.1f, screenHeight * 0.8f, screenWidth * 0.3f, screenHeight * 0.05f);
        rootGroup.addActor(progressBar);
    }

    private void createUI() {
        rootGroup.addActor(new UIImage(MainGame.getAsM().getTexture(bg)).bounds(0, 0, screenWidth, screenHeight));
        TextureRegion upRight = MainGame.getAsM().getRegion(UI_WOOD, "btn_up");
        TextureRegion downRight = MainGame.getAsM().getRegion(UI_WOOD, "btn_down");
        UIButton btnSkill1 = new UIButton(upRight, downRight)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.05f, screenHeight * 0.05f)
            .onClick(() -> {
                state = player.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = enemy.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.HURT;

            });
        rootGroup.addActor(btnSkill1);

        TextureRegion upLeft = new TextureRegion(upRight);
        TextureRegion downLeft = new TextureRegion(downRight);
        UIButton btnSkill2 = new UIButton(upLeft, downLeft)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.15f, screenHeight * 0.05f)
            .onClick(() -> {
                state = player.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = enemy.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.RUN;

            });
        rootGroup.addActor(btnSkill2);

        TextureRegion upTop = new TextureRegion(upRight);
        TextureRegion downTop = new TextureRegion(downRight);
        UIButton btnSkill3 = new UIButton(upTop, downTop)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.25f, screenHeight * 0.05f)
            .onClick(() -> {
                state = player.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = enemy.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.DIE;

            });
        rootGroup.addActor(btnSkill3);

        TextureRegion upBottom = new TextureRegion(upRight);
        TextureRegion downBottom = new TextureRegion(downRight);
        UIButton btnSkill4 = new UIButton(upBottom, downBottom)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.35f, screenHeight * 0.05f)
            .onClick(() -> {
                state = player.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = enemy.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.DIE;

            });
        rootGroup.addActor(btnSkill4);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateLogic(delta);

        stage.act(delta);
        stage.draw();
        engine.update(delta);

        updateUI(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
//        unLoadingAsset();
    }

    @Override
    public void dispose() {

    }
}
