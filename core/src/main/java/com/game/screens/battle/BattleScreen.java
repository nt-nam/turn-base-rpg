package com.game.screens.battle;

import static com.game.utils.Constants.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.core.BattleSimulationResult;
import com.game.core.BattleSimulator;
import com.game.core.TurnResult;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.BattleCharacterComponent;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.MoveToComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SizeComponent;
import com.game.ecs.component.SkillStateComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.SpriteDebugComponent;
import com.game.ecs.component.StatComponent;
import com.game.ecs.systems.AnimationStateSystem;
import com.game.ecs.systems.SkillStateSystem;
import com.game.ecs.systems.SkillSystem;
import com.game.ecs.systems.SpriteDebugRenderSystem;
import com.game.ecs.systems.SpriteRenderSystem;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UIProgressBar;
import com.game.utils.data.AnimationCache;
import com.game.utils.data.CharacterBaseData;
import com.game.utils.data.GameSession;
import com.game.utils.data.JsonLoader;

public class BattleScreen extends BaseScreen {
    private static final String bg = "texture/battle/summer.png";
    private static Entity player;
    private static Entity enemy;
    private static Entity skill;
    private AnimationStateComponent state;
    private SkillStateComponent stateSkill;
    private boolean isPause;

    public BattleScreen() {
        super();
        createScreen();
    }

    public static void loadingAsset() {

        MainGame.getAsM().loadAtlas("atlas/characters/02Knight.atlas");
        MainGame.getAsM().loadAtlas(SKILL_SKILL);
        MainGame.getAsM().loadAtlas(UI_WOOD);
        MainGame.getAsM().loadAtlas(UI_POPUP);
        MainGame.getAsM().load(bg, Texture.class);
    }

    public static void unLoadingAsset() {
        MainGame.getAsM().unload(SKILL_SKILL);
        MainGame.getAsM().unload(UI_WOOD);
        MainGame.getAsM().unload(UI_POPUP);
        MainGame.getAsM().unload(bg);
    }

    @Override
    protected void createScreen() {
        isPause = false;
        createUI();
        createProgressBar();
        createPopup();
    }

    @Override
    public void show() {
        super.show();

        System.out.println("BattleScreen.show");

        // --- TẠO ECS ENTITY ---
        float playerX = screenWidth * 0.1f;
        float playerY = screenHeight * 0.25f;

        float enemyX = screenWidth * 0.7f;
        float enemyY = screenHeight * 0.5f;


        String enemiesId = "assassin_Knight";
        loadAllAnimations(enemiesId, CHARACTER + enemiesId + ".atlas");
        loadAllAnimations(GameSession.selectedCharacterId, CHARACTER + GameSession.selectedCharacterId + ".atlas");
        loadAllAnimationsSkill(GameSession.skillCharacter, SKILL_SKILL);


        // Enemy entity
        CharacterBaseData dataEnemies = JsonLoader.getValue(CHARACTER_BASE, "characterId", enemiesId, CharacterBaseData.class);
        enemy = engine.createEntity();
        enemy.add(new PositionComponent(enemyX, enemyY));
        enemy.add(new SpriteComponent(enemiesId, "idle", true)); // true = flip, hướng qua trái
        enemy.add(new StatComponent(150, 150, 14, 8, 30, 30));
        enemy.add(new BattleCharacterComponent(false, Array.with("fireball", "taunt")));
        enemy.add(CharacterBaseDataComponent.from(dataEnemies));
        enemy.add(new AnimationStateComponent());
        enemy.add(new SpriteDebugComponent());
        engine.addEntity(enemy);

        //Skill entity
        skill = engine.createEntity();
        skill.add(new PositionComponent(0, 0));
        skill.add(new SpriteComponent(GameSession.skillCharacter, "_hide", true));
        skill.add(new BoundComponent(new Rectangle(0, 0, 100, 100)));
        skill.add(new SkillStateComponent());
        skill.add(new MoveToComponent(playerX, playerY, enemyX, enemyY, 1));
        engine.addEntity(skill);


        // System vẽ entity (dùng batch, KHÔNG liên quan Scene2D)
        engine.addSystem(new SpriteRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new SkillStateSystem(engine));
        engine.addSystem(new SkillSystem(engine));
        engine.addSystem(new SpriteDebugRenderSystem(engine,(OrthographicCamera) stage.getCamera()));


        resultBattle();
    }

    private void resultBattle() {
        Array<Entity> playerTeam = new Array<>();
        Array<Entity> enemyTeam = new Array<>();

        Entity entity = engine.createEntity();
        String enemyId1 = "warrior_Knight";
        entity.add(new StatComponent(150, 100, 18, 6, 40, 10));
        CharacterBaseData dataEntity = JsonLoader.getValue(CHARACTER_BASE, "characterId", enemyId1, CharacterBaseData.class);
        entity.add(CharacterBaseDataComponent.from(dataEntity));
        entity.add(new GridComponent(1, 1));
        playerTeam.add(entity);

        Entity entity2 = engine.createEntity();
        String enemyId2 = "support_Knight";
        entity2.add(new StatComponent(120, 180, 22, 4, 35, 25));
        dataEntity = JsonLoader.getValue(CHARACTER_BASE, "characterId", enemyId2, CharacterBaseData.class);
        entity2.add(CharacterBaseDataComponent.from(dataEntity));
        entity.add(new GridComponent(0, 1));
        playerTeam.add(entity2);

        Entity entity3 = engine.createEntity();
        String enemyId3 = "assassin_Knight";
        entity3.add(new StatComponent(200, 80, 12, 15, 28, 5));
        dataEntity = JsonLoader.getValue(CHARACTER_BASE, "characterId", enemyId3, CharacterBaseData.class);
        entity3.add(CharacterBaseDataComponent.from(dataEntity));
        entity.add(new GridComponent(2, 1));
        playerTeam.add(entity3);

        Entity entity4 = engine.createEntity();
        String enemyId4 = "mage_Knight";
        entity4.add(new StatComponent(170, 130, 20, 8, 33, 15));
        dataEntity = JsonLoader.getValue(CHARACTER_BASE, "characterId", enemyId4, CharacterBaseData.class);
        entity4.add(CharacterBaseDataComponent.from(dataEntity));
        entity.add(new GridComponent(1, 1));
        enemyTeam.add(entity4);

        Entity entity5 = engine.createEntity();
        String enemyId5 = "ranger_Knight";
        entity5.add(new StatComponent(140, 160, 16, 10, 38, 20));
        dataEntity = JsonLoader.getValue(CHARACTER_BASE, "characterId", enemyId5, CharacterBaseData.class);
        entity5.add(CharacterBaseDataComponent.from(dataEntity));
        entity.add(new GridComponent(6, 1));
        enemyTeam.add(entity5);

//        playerTeam.add(player);
//        enemyTeam.add(enemy);

        // Chạy mô phỏng kết quả trận đấu
        BattleSimulationResult result = BattleSimulator.run(playerTeam, enemyTeam);

        // Log ra từng lượt đánh
        int round = 0;
        for (TurnResult turn : result.turns) {
            System.out.println(
                "Battle" + ++round + ": " + turn.actorId +
                    " X " + turn.targetId +
                    " :: " + turn.damage +
                    " damage :: " + turn.tempStat.hp + " hp :: " + turn.tempStat.mp + " mp" +
                    (turn.isCritical ? " [CRIT]" : "") +
                    (turn.targetDead ? " -> Doi phuong chet!" : "")
            );
        }

        // Log ra đội thắng
        System.out.println("Doi thang: " + result.winner);
    }

    private void loadAllAnimations(String characterId, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(characterId, "idle", new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(characterId, "attack", new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "jump", new Animation<>(0.1f, atlas.findRegions("jump"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "die", new Animation<>(0.1f, atlas.findRegions("die"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "hurt", new Animation<>(0.1f, atlas.findRegions("hurt"), Animation.PlayMode.NORMAL));
    }

    private void loadAllAnimationsSkill(String nameSkill, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().getAtlas(atlasPath);
        AnimationCache.put(nameSkill, nameSkill + "_attack", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_attack"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_attack_big", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_attack_big"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_explode", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_explode"), Animation.PlayMode.NORMAL));
        AnimationCache.put(nameSkill, nameSkill + "_heal", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_heal"), Animation.PlayMode.NORMAL));
        AnimationCache.put(nameSkill, nameSkill + "_ultimate", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_ultimate"), Animation.PlayMode.NORMAL));
        AnimationCache.put(nameSkill, nameSkill + "_hide", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_hide"), Animation.PlayMode.LOOP));
    }

    private void createProgressBar() {
        new UIProgressBar(0, 100, 1, false, "line_red")
            .name("progressBarPlayer")
            .bounds(screenWidth * 0.1f, screenHeight * 0.8f, screenWidth * 0.3f, screenHeight * 0.05f)
            .value(100).visible(false)
            .parent(rootGroup);

        new UIProgressBar(0, 100, 1, false, "line_red")
            .name("progressBarEnemies")
            .bounds(screenWidth * 0.6f, screenHeight * 0.3f, screenWidth * 0.3f, screenHeight * 0.05f)
            .value(100).visible(false)
            .parent(rootGroup);
    }

    private void createUI() {
        rootGroup.addActor(new UIImage(MainGame.getAsM().getTexture(bg)).bounds(0, 0, screenWidth, screenHeight));
        Array<Entity> playerTeam = new Array<>();
        Array<Entity> enemyTeam = new Array<>();
        createGridUI(screenWidth * 0.1f, screenHeight * 0.2f,playerTeam);
        createGridUI(screenWidth * 0.65f, screenHeight * 0.2f,enemyTeam);
//createBtn();
    }

    private void createGridUI(float x, float y, Array<Entity> team) {
        float tileSize = screenHeight * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX=x + i * tileSize * 1.1f;
                float posY= y + j * tileSize * 1.1f;
                new UIImage(UI_POPUP, "empty").parent(rootGroup).bounds(posX,posY,tileSize,tileSize);//ô grid
                if(true){
//                    CharacterBaseData dataEntity = CharacterLoader.getCharacterBaseData(GameSession.selectedCharacterId);
                    CharacterBaseData dataEntity = JsonLoader.getValue(CHARACTER_BASE, "characterId", GameSession.selectedCharacterId, CharacterBaseData.class);
                    Entity entity = engine.createEntity();
                    entity.add(new PositionComponent(posX, posY+tileSize*0.3f));
                    entity.add(new SpriteComponent(GameSession.selectedCharacterId, "idle", false));
                    entity.add(new SizeComponent(tileSize, tileSize));
                    entity.add(new StatComponent(120, 120, 18, 12, 40, 40));
                    entity.add(new BattleCharacterComponent(true, Array.with("slash", "shield_bash")));
                    entity.add(CharacterBaseDataComponent.from(dataEntity));
                    entity.add(new AnimationStateComponent());
                    entity.add(new SpriteDebugComponent());
                    team.add(entity);
                    engine.addEntity(entity);
                }
            }
        }
    }

    private void createBtn() {

        TextureRegion up = MainGame.getAsM().getRegion(UI_WOOD, "btn_up");
        TextureRegion down = MainGame.getAsM().getRegion(UI_WOOD, "btn_down");
        TextureRegion skill1 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSession.skillCharacter + "_attack");
        UIButton btnSkill1 = new UIButton(skill1, up, down)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.05f, screenHeight * 0.05f)
            .onClick(() -> {
//                state = player.getComponent(AnimationStateComponent.class);
//                state.current = AnimationStateComponent.State.ATTACK;
//                state = enemy.getComponent(AnimationStateComponent.class);
//                state.current = AnimationStateComponent.State.HURT;
//                stateSkill = skill.getComponent(SkillStateComponent.class);
//                stateSkill.current = SkillStateComponent.State.ATTACK;
                battle(player, enemy, SkillStateComponent.State.ATTACK, -10);
            });
        rootGroup.addActor(btnSkill1);

        TextureRegion upLeft = new TextureRegion(up);
        TextureRegion downLeft = new TextureRegion(down);
        TextureRegion skill2 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSession.skillCharacter + "_attack_big");
        UIButton btnSkill2 = new UIButton(skill2, upLeft, downLeft)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.15f, screenHeight * 0.05f)
            .onClick(() -> {
                battle(player, enemy, SkillStateComponent.State.ATTACK_BIG, -20);
            });
        rootGroup.addActor(btnSkill2);

        TextureRegion upTop = new TextureRegion(up);
        TextureRegion downTop = new TextureRegion(down);
        TextureRegion skill3 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSession.skillCharacter + "_heal");
        UIButton btnSkill3 = new UIButton(skill3, upTop, downTop)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.25f, screenHeight * 0.05f)
            .onClick(() -> {
//                state = player.getComponent(AnimationStateComponent.class);
//                state.current = AnimationStateComponent.State.ATTACK;
//                state = enemy.getComponent(AnimationStateComponent.class);
//                state.current = AnimationStateComponent.State.IDLE;
//                stateSkill = skill.getComponent(SkillStateComponent.class);
//                stateSkill.current = SkillStateComponent.State.HEAL;
                battle(player, enemy, SkillStateComponent.State.HEAL, 10);
            });
        rootGroup.addActor(btnSkill3);

        TextureRegion upBottom = new TextureRegion(up);
        TextureRegion downBottom = new TextureRegion(down);
        TextureRegion skill4 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSession.skillCharacter + "_ultimate");
        UIButton btnSkill4 = new UIButton(skill4, upBottom, downBottom)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.35f, screenHeight * 0.05f)
            .onClick(() -> {
//                state = player.getComponent(AnimationStateComponent.class);
//                state.current = AnimationStateComponent.State.ATTACK;
//                state = enemy.getComponent(AnimationStateComponent.class);
//                state.current = AnimationStateComponent.State.DIE;
//                stateSkill = skill.getComponent(SkillStateComponent.class);
//                stateSkill.current = SkillStateComponent.State.ULTIMATE;
                battle(player, enemy, SkillStateComponent.State.ATTACK_BIG, -50);
            });
        rootGroup.addActor(btnSkill4);

    }

    private void battle(Entity offensive, Entity defensive, SkillStateComponent.State type, int hp) {
        switch (type) {
            case ATTACK:
                state = offensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = defensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.ATTACK;
                ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).update(hp);
                break;
            case ATTACK_BIG:
                state = offensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = defensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.ATTACK_BIG;
                ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).update(hp);
                break;
            case HEAL:
                state = offensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.JUMP;
                state = defensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.HEAL;
                ((UIProgressBar) rootGroup.findActor("progressBarPlayer")).update(hp);
                break;
            case ULTIMATE:
                state = offensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.ATTACK;
                state = defensive.getComponent(AnimationStateComponent.class);
                state.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.ULTIMATE;
                ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).update(hp);
                break;

        }
    }


    private void createPopup() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);

        new UIButton(
            MainGame.getAsM().getRegion(UI_WOOD, "resume_up_007"),
            MainGame.getAsM().getRegion(UI_WOOD, "resume_down_008"))
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.9f, screenHeight * 0.8f)
            .onClick(() -> {
                if (!isPause) {
                    isPause = true;
                    showPopupPause();
                } else {
                    isPause = false;
                    hidePopupPause();
                }
            })
            .parent(rootGroup);

        TextureRegion origin = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(origin).nine(origin, 30, 30, 30, 30)
            .name("board")
            .parent(rootGroup)
            .bounds(screenWidth * 0.2f, screenHeight * 0.1f, screenWidth * 0.6f, screenHeight * 0.8f);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "menu"))
            .name("menu")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.3f, screenHeight * 0.18f)
            .onClick(() -> {

            })
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "home"))
            .name("home")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.45f, screenHeight * 0.18f)
            .onClick(() -> {
                isPause = false;
                hidePopupPause();
                MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
            })
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "setting"))
            .name("setting")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.6f, screenHeight * 0.18f)
            .onClick(() -> {

            })
            .parent(rootGroup);

        hidePopupPause();
    }

    private void showPopupPause() {
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("board").setVisible(true);
        rootGroup.findActor("home").setVisible(true);
        rootGroup.findActor("menu").setVisible(true);
        rootGroup.findActor("setting").setVisible(true);
//        rootGroup.findActor("progressBarPlayer").setVisible(false);
//        rootGroup.findActor("progressBarEnemies").setVisible(false);
    }

    private void hidePopupPause() {
        rootGroup.findActor("overlay").setVisible(false);
        rootGroup.findActor("board").setVisible(false);
        rootGroup.findActor("home").setVisible(false);
        rootGroup.findActor("menu").setVisible(false);
        rootGroup.findActor("setting").setVisible(false);
//        rootGroup.findActor("progressBarPlayer").setVisible(true);
//        rootGroup.findActor("progressBarEnemies").setVisible(true);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateLogic(delta);

        stage.act(delta);

        stage.draw();
        if (!isPause) {
            engine.update(delta);
        }


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
        engine.removeAllSystems();
        engine.removeAllEntities();
        ((UIProgressBar) rootGroup.findActor("progressBarPlayer")).value(100);
        ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).value(100);
    }

    @Override
    public void dispose() {
        unLoadingAsset();
    }
}
