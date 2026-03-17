package com.game.screens.battle;

import com.game.ecs.factory.EntityFactory;
import com.game.ecs.systems.HealthBarRenderSystem;
import com.game.ecs.systems.LabelRenderSystem;
import com.game.utils.Constants;

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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.combat.BattleConfig;
import com.game.combat.BattleLogger;
import com.game.combat.BattleSimulationResult;
import com.game.combat.BattleSimulator;
import com.game.combat.StatCalculator;
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
import com.game.ecs.component.SizeComponent;
import com.game.ecs.component.SkillStateComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatComponent;
import com.game.ecs.systems.AnimationStateSystem;
import com.game.ecs.systems.TurnActionSystem;
import com.game.ecs.systems.SkillStateSystem;
import com.game.ecs.systems.SpriteDebugRenderSystem;
import com.game.ecs.systems.SpriteRenderSystem;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UIProgressBar;
import com.game.utils.DataHelper;
import com.game.utils.JsonSaver;
import com.game.utils.data.AnimationCache;
import com.game.managers.GameSessionManager;
import com.game.models.entity.CharacterBase;
import com.game.models.entity.Equip;
import com.game.models.entity.Item;
import com.game.models.entity.Lineup;
import com.game.models.entity.Hero;
import com.game.models.entity.MapBattle;
import com.game.models.entity.Reward;
import com.game.models.entity.skill.SkillBase;
import com.game.ui.views.BattleHudView;
import com.game.controllers.BattleController;

import java.util.List;

public class BattleScreen extends BaseScreen {
    public static BattleScreen instance;
    private BattleHudView hudView;
    private static final String bg = "texture/battle/summer.png";
    private static String ENEMY_TEAM;
    private static Entity skill;
    private static boolean isPause;
    private static Entity target;
    private static Group popup;
    private int maxLevelEnemy = 0;

    public BattleController battleController;

    private static MapBattle mapBattle;
    private static List<Lineup> lineupList;
    private static List<SkillBase> skillBaseList;
    private static List<CharacterBase> characterBaseList;
    private static List<Hero> heroList;

    public static void setTarget(Entity target1) {
        target = target1;
    }

    public BattleScreen() {
        super();
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
        instance = this;
        isPause = false;
        BattleConfig.load();
    }


    @Override
    public void show() {
        ENEMY_TEAM = "data/enemy/" + GameSessionManager.getInstance().profile.area + "_" + GameSessionManager.getInstance().enemyMapId + ".json";
        skillBaseList = DataHelper.loadSkillBaseList(true);
        characterBaseList = DataHelper.loadCharacterBaseList();
        super.show();
        popup = rootGroup;
        createBG();
        createBattleGridUI();

        hudView = new BattleHudView(rootGroup, screenWidth, screenHeight, () -> {
            if (!isPause) {
                isPause = true;
                hudView.showPopupPause();
            } else {
                isPause = false;
                hudView.hidePopupPause();
            }
        });

        hudView.createPopup(() -> {
            isPause = false;
            hudView.hidePopupPause();
            MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
        });

        engine.addSystem(new SpriteRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new HealthBarRenderSystem((OrthographicCamera) stage.getCamera()));
        engine.addSystem(new LabelRenderSystem((OrthographicCamera) stage.getCamera(), Constants.BMF));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new SkillStateSystem(engine));
        engine.addSystem(new SpriteDebugRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new TurnActionSystem());
    }


    private EntityFactory entityFactory;

    private void loadAllAnimationsSkill(String nameSkill, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().getAtlas(atlasPath);
        AnimationCache.put(nameSkill, nameSkill + "_attack", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_attack"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_attack_big", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_attack_big"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_explode", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_explode"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_heal", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_heal"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_ultimate", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_ultimate"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameSkill, nameSkill + "_hide", new Animation<>(0.1f, atlas.findRegions(nameSkill + "_hide"), Animation.PlayMode.LOOP));
    }

    private void createBG() {
        rootGroup.addActor(new UIImage(MainGame.getAsM().getTexture(bg)).bounds(0, 0, screenWidth, screenHeight));
    }

    private void createBattleGridUI() {
        Array<Entity> playerTeam = new Array<>();
        Array<Entity> enemyTeam = new Array<>();

        entityFactory = new com.game.ecs.factory.EntityFactory(engine, skillBaseList, characterBaseList);

        entityFactory.createPlayerTeam(screenWidth * 0.1f, screenHeight * 0.2f, screenHeight * 0.15f, rootGroup, playerTeam);

        mapBattle = DataHelper.loadMapBattle(ENEMY_TEAM);
        maxLevelEnemy = entityFactory.createEnemyTeam(screenWidth * 0.6f, screenHeight * 0.2f, screenHeight * 0.15f, rootGroup, enemyTeam, ENEMY_TEAM, mapBattle);

        battleController = new BattleController(this, hudView, mapBattle, maxLevelEnemy);

        if (playerTeam.isEmpty() || enemyTeam.isEmpty()) {
            Gdx.app.error("createUI", "Teams are empty: player=" + playerTeam.size + ", enemy=" + enemyTeam.size);
            return;
        }

        Gdx.app.log("createUI", "Starting battle with " + playerTeam.size + " players and " + enemyTeam.size + " enemies");

        Array<Entity> playerTeamCR = new Array<>(playerTeam);
        Array<Entity> enemyTeamCR = new Array<>(enemyTeam);

        BattleSimulationResult listResult = new BattleSimulator().run(playerTeamCR, enemyTeamCR);
        BattleLogger.logBattleResult(listResult, playerTeamCR, enemyTeamCR);


        loadAllAnimationsSkill(GameSessionManager.getInstance().skillCharacter, SKILL_SKILL);
        skill = engine.createEntity();
        skill.add(new PositionComponent(0, 0));
        skill.add(new SpriteComponent(GameSessionManager.getInstance().skillCharacter, "_hide", true));
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
            entity.add(StatCalculator.calculate(dataEntity, infoComponent.level, 0));
        }
    }

    public void triggerWin() {
        isPause = true;
        if (battleController != null) {
            battleController.onBattleWin();
        }
    }

    public void triggerFail() {
        isPause = true;
        if (battleController != null) {
            battleController.onBattleFail();
        }
    }


    /**
     * Custom render: KHÔNG gọi super.render() vì BattleScreen cần
     * điều khiển engine.update() dựa trên isPause.
     */
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
