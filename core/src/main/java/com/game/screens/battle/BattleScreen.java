package com.game.screens.battle;

import static com.game.utils.Constants.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.core.BattleConfig;
import com.game.core.BattleLogger;
import com.game.core.BattleSimulationResult;
import com.game.core.BattleSimulator;
import com.game.ecs.TurnProcessor;
import com.game.ecs.component.ActionQueueComponent;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.EnemyComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.InfoComponent;
import com.game.ecs.component.LabelComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.MoveToComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.ProgressBarComponent;
import com.game.ecs.component.Scene2dComponent;
import com.game.ecs.component.SizeComponent;
import com.game.ecs.component.SkillComponent;
import com.game.ecs.component.SkillStateComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatComponent;
import com.game.ecs.systems.AnimationStateSystem;
import com.game.ecs.systems.Scene2dRenderSystem;
import com.game.ecs.systems.TurnActionSystem;
import com.game.ecs.systems.SkillStateSystem;
import com.game.ecs.systems.SpriteDebugRenderSystem;
import com.game.ecs.systems.SpriteRenderSystem;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UIProgressBar;
import com.game.utils.JsonHelper;
import com.game.utils.data.AnimationCache;
import com.game.utils.GameSession;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.GridData;
import com.game.utils.JsonValueHelper;
import com.game.utils.json.Hero;
import com.game.utils.json.Lineup;
import com.game.utils.json.skill.SkillBase;

import java.util.List;

public class BattleScreen extends BaseScreen {
    private static final String bg = "texture/battle/summer.png";
    private static String ENEMY_TEAM;
    private static Entity skill;
    private boolean isPause;
    private static Entity target;
    private UILabel label;

    public static void setTarget(Entity target1) {
        target = target1;
    }

    public BattleScreen() {
        super();
        createScreen();
    }

    public static void loadingAsset() {
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
        BattleConfig.load();
    }

    @Override
    public void show() {
        ENEMY_TEAM = "data/enemy/"+GameSession.currentMapId+"_"+GameSession.enemyMapId +".json";
        super.show();
        createBG();
        createLabel();
        createBattleGridUI();
        createPopup();

        // System vẽ entity (dùng batch, KHÔNG liên quan Scene2D)
        engine.addSystem(new SpriteRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new SkillStateSystem(engine));
        engine.addSystem(new SpriteDebugRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new TurnActionSystem());
        engine.addSystem(new Scene2dRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
//        engine.addSystem(new ActionQueueSystem());
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
        AnimationCache.put(nameSkill, nameSkill + "_explode", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_explode"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_heal", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_heal"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_ultimate", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_ultimate"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_hide", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_hide"), Animation.PlayMode.LOOP));
    }

    private UIProgressBar createProgressBar(String name, int max, float x, float y, float w, float h) {
        return new UIProgressBar(0, max, 1, false, "linered2")
            .name("proB:" + name)
            .bounds(x, y, w, h)
            .value(max).visible(true)
            .parent(rootGroup);
    }

    private void createBG() {
        rootGroup.addActor(new UIImage(MainGame.getAsM().getTexture(bg)).bounds(0, 0, screenWidth, screenHeight));
    }

    private void createBattleGridUI() {
        Array<Entity> playerTeam = new Array<>();
        Array<Entity> enemyTeam = new Array<>();

        createGridUI(screenWidth * 0.1f, screenHeight * 0.2f, playerTeam, PARTY_ATTACK);
//        createGridUI(screenWidth * 0.65f, screenHeight * 0.2f, enemyTeam, ENEMY_TEAM);
        createGridUIEnemy(screenWidth * 0.65f, screenHeight * 0.2f, enemyTeam, ENEMY_TEAM);

        if (playerTeam.isEmpty() || enemyTeam.isEmpty()) {
            Gdx.app.error("createUI", "Teams are empty: player=" + playerTeam.size + ", enemy=" + enemyTeam.size);
            return;
        }

        Gdx.app.log("createUI", "Starting battle with " + playerTeam.size + " players and " + enemyTeam.size + " enemies");

        Array<Entity> playerTeamCR = new Array<>(playerTeam);
        Array<Entity> enemyTeamCR = new Array<>(enemyTeam);

        BattleSimulationResult listResult = new BattleSimulator().run(playerTeamCR, enemyTeamCR);
        BattleLogger.logBattleResult(listResult, playerTeamCR, enemyTeamCR);

//        BattleSimulationResultSaver.saveBattleSimulationResult(listResult, "assets/data/battleresult/battle_result.json");

        loadAllAnimationsSkill(GameSession.skillCharacter, SKILL_SKILL);
        skill = engine.createEntity();
        skill.add(new PositionComponent(0, 0));
        skill.add(new SpriteComponent(GameSession.skillCharacter, "_hide", true));
        skill.add(new SizeComponent(screenHeight * .15f, screenHeight * .18f));
        skill.add(new ActionQueueComponent());
        skill.add(new SkillStateComponent());
        skill.add(new MoveToComponent());
        engine.addEntity(skill);
        loadStat(playerTeam);
        loadStat(enemyTeam);
        TurnProcessor turnProcessor = new TurnProcessor();
        turnProcessor.processTurns(skill, listResult);
    }

    private void loadStat(Array<Entity> playerTeam) {
        for (Entity entity : playerTeam) {
            CharacterComponent dataEntity = entity.getComponent(CharacterComponent.class);
            InfoComponent infoComponent = entity.getComponent(InfoComponent.class);
            entity.add(new StatComponent(
                dataEntity.hp > 0 ? (int) (dataEntity.hp * Math.pow(1.1f, infoComponent.level)) : 100,
                dataEntity.mp > 0 ? (int) (dataEntity.mp * Math.pow(1.1f, infoComponent.level)) : 50,
                dataEntity.atk > 0 ? (int) (dataEntity.atk * Math.pow(1.1f, infoComponent.level)) : 20,
                dataEntity.def > 0 ? (int) (dataEntity.def * Math.pow(1.1f, infoComponent.level)) : 10,
                dataEntity.agi > 0 ? (int) (dataEntity.agi * Math.pow(1.1f, infoComponent.level)) : 30,
                dataEntity.crit > 0 ? (int) (dataEntity.crit * Math.pow(1.1f, infoComponent.level)) : 10
            ));
        }
    }


    private void createLabel() {
        Entity labelEntity = engine.createEntity();
        label = new UILabel("Critical").pos(-100, -100).name("label");
        label.setFontScale(3.5f);
        label.setAlignment(Align.center);
        label.setColor(Color.RED);
        label.setVisible(false);
        label.setText("Critical");
        label.name("label");
        label.setZIndex(Integer.MAX_VALUE);
        labelEntity.add(new LabelComponent(label));
        labelEntity.add(new Scene2dComponent());
        rootGroup.addActor(label);
        engine.addEntity(labelEntity);
    }

    private void createGridUI(float x, float y, Array<Entity> team, String path) {
        if (rootGroup == null || engine == null) {
            Gdx.app.error("createGridUI", "rootGroup or engine is null");
            return;
        }
        List<GridData> gridDataList = JsonHelper.loadGrids(path, true);
        List<Hero> fullHero = JsonHelper.loadFullHero(PARTY_FULL, true);
        List<SkillBase> skillBaseList = JsonHelper.loadSkillBase(SKILL_JSON, true);

        float tileSize = screenHeight * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX = x + i * tileSize * 1.1f;
                float posY = y + j * tileSize * 1.1f;
                new UIImage(UI_POPUP, "empty").parent(rootGroup).bounds(posX, posY, tileSize, tileSize);
                if (path == null) continue;

//                GridData gridData = JsonValueHelper.getValueClassByKey(path, "grid", i + "," + j, GridData.class);
                GridData gridData = JsonHelper.get(gridDataList, "grid", i + "," + j);

                if (gridData == null || gridData.characterId == null) {
                    continue;
                }

//                JsonValueHelper.loadArray(PARTY_FULL, InfoComponent.class, false);

                Hero hero = JsonHelper.get(fullHero, "characterId", gridData.characterId);
//                InfoComponent infoCharacter = JsonValueHelper.getValueClassByKey(PARTY_FULL, "characterId", gridData.characterId, InfoComponent.class);
                InfoComponent infoCharacter = new InfoComponent();
                infoCharacter.characterId = gridData.characterId;
                infoCharacter.characterBaseId = gridData.getCharacterId;
                infoCharacter.level = hero.level;
                infoCharacter.star = hero.star;
                infoCharacter.equip = new InfoComponent.Equipment();

//                CharacterComponent dataEntity = JsonValueHelper.getValueClassByKey(CHARACTER_BASE_JSON, "characterBaseId", infoCharacter.characterBaseId, CharacterComponent.class);
                CharacterComponent dataEntity = new CharacterComponent(JsonHelper.get(JsonHelper.baseHero, "characterBaseId", infoCharacter.characterBaseId));

                JsonValue fullSkillJson = JsonValueHelper.getJsonValue(SKILL_JSON, false);

                JsonValue skillSet = fullSkillJson.get(dataEntity.name.toLowerCase());

                boolean isEnemy = false;
                loadAllAnimations(dataEntity.characterBaseId, CHARACTER_ATLAS + dataEntity.characterBaseId + ".atlas");
                Entity entity = engine.createEntity();

                // Add components
                entity.add(new PositionComponent(posX, posY + tileSize * 0.3f));
                entity.add(new GridComponent(i, j));
                entity.add(new SpriteComponent(dataEntity.characterBaseId, "idle", isEnemy));
                entity.add(isEnemy ? new EnemyComponent() : new PlayerComponent());
                entity.add(new SizeComponent(tileSize, tileSize));
                entity.add(infoCharacter);

                StatComponent stat = new StatComponent(
                    dataEntity.hp > 0 ? (int) (dataEntity.hp * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 100,
                    dataEntity.mp > 0 ? (int) (dataEntity.mp * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 50,
                    dataEntity.atk > 0 ? (int) (dataEntity.atk * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 20,
                    dataEntity.def > 0 ? (int) (dataEntity.def * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 10,
                    dataEntity.agi > 0 ? (int) (dataEntity.agi * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 30,
                    dataEntity.crit > 0 ? (int) (dataEntity.crit * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 10
                );
                entity.add(stat);
//                System.out.println(entity.getComponent(StatComponent.class).hp);
//                System.out.println(dataEntity.hp * Math.pow(1.1f, infoCharacter.level));
//                System.out.println(infoCharacter.level);

                // Initialize skills
                ListSkillComponent listSkill = new ListSkillComponent(JsonHelper.get(skillBaseList, "name", dataEntity.classType.toLowerCase()));
                // Ensure basic attack is always available
//                listSkill.skills.add(new SkillComponent(1, "Basic Attack", "Deals basic damage", null));
//                for (JsonValue skill = skillSet.child(); skill != null; skill = skill.next()) {
//                    String skillId = skill.name;
//                    String name = skill.getString("name", "Unknown Skill");
//                    String description = skill.getString("description", "");
//                    JsonValue effect = skill.get("effect");
//                    int id = Integer.parseInt(skillId);
//                    if (id > 1) { // Skip overriding basic attack
//                        listSkill.skills.add(new SkillComponent(id, name, description, effect));
//                    }
//                }
                entity.add(listSkill);

                // Add character data
//                dataEntity.characterBaseId = gridData.characterId; // Ensure characterId is set
                entity.add(dataEntity);
                entity.add(new AnimationStateComponent());

                UIProgressBar a = createProgressBar(i + "," + j, stat.hp, posX, posY, tileSize * 1f, tileSize * 0.1f);
                entity.add(new ProgressBarComponent(a));
                entity.add(new LabelComponent(label));
                team.add(entity);
                engine.addEntity(entity);
            }
        }
    }

    private void createGridUIEnemy(float x, float y, Array<Entity> team, String path) {
        List<GridData> gridDataList = JsonHelper.loadGrids(path, true);
        System.out.println("gridDataList:"+gridDataList.toString());
//        List<Hero> fullHero = JsonHelper.loadFullHero(PARTY_FULL, true);
        List<Hero> heroes = JsonHelper.loadFullHero(CHARACTER_BASE_JSON, true);
        List<SkillBase> skillBaseList = JsonHelper.loadSkillBase(SKILL_JSON, true);

        float tileSize = screenHeight * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX = x + i * tileSize * 1.1f;
                float posY = y + j * tileSize * 1.1f;
                new UIImage(UI_POPUP, "empty").parent(rootGroup).bounds(posX, posY, tileSize, tileSize);
                if (path == null) continue;

                GridData gridData = JsonHelper.get(gridDataList, "grid", i + "," + j);

                if (gridData == null || gridData.characterId == null) {
                    continue;
                }

                Hero hero = JsonHelper.get(heroes, "characterBaseId", gridData.getCharacterId);
                InfoComponent infoCharacter = new InfoComponent();
                infoCharacter.characterId = hero.characterId;
                infoCharacter.characterBaseId = hero.characterBaseId;
                infoCharacter.level = hero.level;
                infoCharacter.star = hero.star;
                infoCharacter.equip = new InfoComponent.Equipment();

                CharacterComponent dataEntity = new CharacterComponent(JsonHelper.get(JsonHelper.baseHero, "characterBaseId", infoCharacter.characterBaseId));

                boolean isEnemy = true;
                loadAllAnimations(dataEntity.characterBaseId, CHARACTER_ATLAS + dataEntity.characterBaseId + ".atlas");
                Entity entity = engine.createEntity();

                // Add components
                entity.add(new PositionComponent(posX, posY + tileSize * 0.3f));
                entity.add(new GridComponent(i, j));
                entity.add(new SpriteComponent(dataEntity.characterBaseId, "idle", isEnemy));
                entity.add(isEnemy ? new EnemyComponent() : new PlayerComponent());
                entity.add(new SizeComponent(tileSize, tileSize));
                entity.add(infoCharacter);

                StatComponent stat = new StatComponent(
                    dataEntity.hp > 0 ? (int) (dataEntity.hp * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 100,
                    dataEntity.mp > 0 ? (int) (dataEntity.mp * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 50,
                    dataEntity.atk > 0 ? (int) (dataEntity.atk * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 20,
                    dataEntity.def > 0 ? (int) (dataEntity.def * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 10,
                    dataEntity.agi > 0 ? (int) (dataEntity.agi * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 30,
                    dataEntity.crit > 0 ? (int) (dataEntity.crit * Math.pow(1.1f, infoCharacter.level) * Math.pow(1.5f, infoCharacter.star)) : 10
                );
                entity.add(stat);

                // Initialize skills
                ListSkillComponent listSkill = new ListSkillComponent(JsonHelper.get(skillBaseList, "name", dataEntity.classType.toLowerCase()));
                entity.add(listSkill);

                // Add character data
                entity.add(dataEntity);
                entity.add(new AnimationStateComponent());

                UIProgressBar a = createProgressBar(i + "," + j, stat.hp, posX, posY, tileSize * 1f, tileSize * 0.1f);
                entity.add(new ProgressBarComponent(a));
                entity.add(new LabelComponent(label));
                team.add(entity);
                engine.addEntity(entity);
            }
        }
    }

    public void showResultPP() {
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
    }

    private void hidePopupPause() {
        rootGroup.findActor("overlay").setVisible(false);
        rootGroup.findActor("board").setVisible(false);
        rootGroup.findActor("home").setVisible(false);
        rootGroup.findActor("menu").setVisible(false);
        rootGroup.findActor("setting").setVisible(false);
    }

    @Override
    protected void updateLogic(float delta) {
    }

    private Entity checkMiss() {
        if (target == null) return null;
        return target;
    }

    private Entity checkCritical() {
        if (skill == null) return null;

        return null;
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
        MainGame.getScM().removeScreen(ScreenType.BATTLE);
    }

    @Override
    public void dispose() {
        unLoadingAsset();
    }
}
