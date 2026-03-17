package com.game.screens.battle;

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
import com.game.core.BattleConfig;
import com.game.core.BattleLogger;
import com.game.core.BattleSimulationResult;
import com.game.core.BattleSimulator;
import com.game.core.StatCalculator;
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
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UIProgressBar;
import com.game.utils.DataHelper;
import com.game.utils.JsonSaver;
import com.game.utils.data.AnimationCache;
import com.game.utils.GameSession;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.Equip;
import com.game.utils.json.Item;
import com.game.utils.json.Lineup;
import com.game.utils.json.Hero;
import com.game.utils.json.MapBattle;
import com.game.utils.json.Reward;
import com.game.utils.json.skill.SkillBase;

import java.util.List;

public class BattleScreen extends BaseScreen {
    private static final String bg = "texture/battle/summer.png";
    private static String ENEMY_TEAM;
    private static Entity skill;
    private static boolean isPause;
    private static Entity target;
    private static Group popup;
    private static int maxLevelEnemy = 0;
    private UILabel label;

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
        isPause = false;
        BattleConfig.load();
    }

    @Override
    public void show() {
        ENEMY_TEAM = "data/enemy/" + GameSession.profile.area + "_" + GameSession.enemyMapId + ".json";
        skillBaseList = DataHelper.loadSkillBaseList(true);
        characterBaseList = DataHelper.loadCharacterBaseList();
        super.show();
        popup = rootGroup;
        createBG();
        createLabel();
        createBattleGridUI();
        createPopup();

        engine.addSystem(new SpriteRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new SkillStateSystem(engine));
        engine.addSystem(new SpriteDebugRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
        engine.addSystem(new TurnActionSystem());
        engine.addSystem(new Scene2dRenderSystem(engine, (OrthographicCamera) stage.getCamera()));
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

        createGridUI(screenWidth * 0.1f, screenHeight * 0.2f, playerTeam);
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
            entity.add(StatCalculator.calculate(dataEntity, infoComponent.level, 0));
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

    private void createGridUI(float x, float y, Array<Entity> team) {
        if (rootGroup == null || engine == null) {
            Gdx.app.error("createGridUI", "rootGroup or engine is null");
            return;
        }

        float tileSize = screenHeight * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX = x + i * tileSize * 1.1f;
                float posY = y + j * tileSize * 1.1f;
                new UIImage(UI_POPUP, "empty").parent(rootGroup).bounds(posX, posY, tileSize, tileSize);

                Lineup lineup = DataHelper.get(DataHelper.loadLineupList(true), "grid", i + "," + j);

                if (lineup == null || lineup.characterId == null) {
                    continue;
                }


                Hero hero = DataHelper.get(DataHelper.loadHeroList(Constants.playerPath("hero_full.json"), false), "characterId", lineup.characterId);
                InfoComponent infoCharacter = new InfoComponent();
                infoCharacter.characterId = lineup.characterId;
                infoCharacter.nameRegion = lineup.nameRegion;
                infoCharacter.level = hero.level;
                infoCharacter.star = hero.star;
                infoCharacter.equip = new InfoComponent.Equipment();
                System.out.println(infoCharacter.nameRegion);

                CharacterComponent dataEntity = new CharacterComponent(DataHelper.get(GameSession.characterBaseList, "nameRegion", infoCharacter.nameRegion));

                boolean isEnemy = false;
                loadAllAnimations(dataEntity.nameRegion, CHARACTER_ATLAS + dataEntity.nameRegion + ".atlas");
                Entity entity = engine.createEntity();

                // Add components
                entity.add(new PositionComponent(posX, posY + tileSize * 0.3f));
                entity.add(new GridComponent(i, j));
                entity.add(new SpriteComponent(dataEntity.nameRegion, "idle", isEnemy));
                entity.add(isEnemy ? new EnemyComponent() : new PlayerComponent());
                entity.add(new SizeComponent(tileSize, tileSize));
                entity.add(infoCharacter);

                StatComponent stat = StatCalculator.calculate(dataEntity, infoCharacter.level, infoCharacter.star);
                entity.add(stat);

                // Initialize skills
                ListSkillComponent listSkill = new ListSkillComponent(DataHelper.get(skillBaseList, "name", dataEntity.classType.toLowerCase()));

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

    private void createGridUIEnemy(float x, float y, Array<Entity> team, String path) {

        mapBattle = DataHelper.loadMapBattle(path);

        float tileSize = screenHeight * 0.15f;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX = x + i * tileSize * 1.1f;
                float posY = y + j * tileSize * 1.1f;
                float posBossX = x;
                float posBossY = y;

                new UIImage(UI_POPUP, "empty").parent(rootGroup).bounds(posX, posY, tileSize, tileSize);
                if (path == null) continue;

                Hero hero = DataHelper.get(mapBattle.heroEnemyList, "grid", i + "," + j);
                if (hero == null || hero.characterId == null) {
                    continue;
                }

                InfoComponent infoCharacter = new InfoComponent();
                infoCharacter.characterId = hero.characterId;
                infoCharacter.nameRegion = hero.nameRegion;
                infoCharacter.level = hero.level;
                infoCharacter.star = hero.star;
                infoCharacter.equip = new InfoComponent.Equipment();

                maxLevelEnemy = Math.max(maxLevelEnemy, infoCharacter.level);

                CharacterComponent dataEntity = new CharacterComponent(DataHelper.get(characterBaseList, "nameRegion", infoCharacter.nameRegion));

                boolean isEnemy = true;
                loadAllAnimations(dataEntity.nameRegion, CHARACTER_ATLAS + dataEntity.nameRegion + ".atlas");
                Entity entity = engine.createEntity();

                // Add components
                entity.add(new PositionComponent(
                    hero.characterId.equals("boss") ? posBossX + tileSize * 0.5f : posX,
                    hero.characterId.equals("boss") ? posBossY + tileSize * 0.5f : posY + tileSize * 0.3f));
                entity.add(new GridComponent(i, j));
                entity.add(new SpriteComponent(dataEntity.nameRegion, "idle", isEnemy));
                entity.add(isEnemy ? new EnemyComponent() : new PlayerComponent());
                entity.add(new SizeComponent(hero.characterId.equals("boss") ? tileSize * 2.5f : tileSize, hero.characterId.equals("boss") ? tileSize * 2.5f : tileSize));
                entity.add(infoCharacter);

                StatComponent stat = StatCalculator.calculate(dataEntity, infoCharacter.level, infoCharacter.star);
                entity.add(stat);

                // Initialize skills
                ListSkillComponent listSkill = new ListSkillComponent(DataHelper.get(skillBaseList, "name", dataEntity.classType.toLowerCase()));
                entity.add(listSkill);

                // Add character data
                entity.add(dataEntity);
                entity.add(new AnimationStateComponent());

                UIProgressBar a = createProgressBar(i + "," + j, stat.hp,
                    hero.characterId.equals("boss") ? posBossX + 0.5f * tileSize : posX,
                    hero.characterId.equals("boss") ? posBossY - tileSize * 0.2f : posY,
                    hero.characterId.equals("boss") ? tileSize * 2 : tileSize,
                    hero.characterId.equals("boss") ? tileSize * 0.2f : tileSize * 0.1f);
                entity.add(new ProgressBarComponent(a));
                entity.add(new LabelComponent(label));
                team.add(entity);
                engine.addEntity(entity);
            }
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
            .name("resume")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.9f, screenHeight * 0.8f)
            .onClick(() -> {
                if (!isPause) {
                    showPopupPause();
                } else {
                    hidePopupPause();
                }
            })
            .parent(rootGroup);

        TextureRegion origin = MainGame.getAsM().getRegion(UI_POPUP, "tile_origin");
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
        isPause = true;
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("board").setVisible(true);
        rootGroup.findActor("home").setVisible(true);
        rootGroup.findActor("menu").setVisible(true);
        rootGroup.findActor("setting").setVisible(true);
    }

    public static void showPopupWin() {
        isPause = true;
        popup.findActor("overlay").setVisible(true);
        popup.findActor("board").setVisible(true);
        popup.findActor("home").setVisible(true);
        popup.findActor("resume").setVisible(false);

        popup.addActor(new UIGroup().child(
            new UILabel("Chiến thắng", BMF).pos(popup.getWidth() * 0.2f, popup.getHeight() * 0.6f).size(popup.getWidth() * 0.6f, popup.getHeight() * 0.3f).fontScale(4).align(Align.center)
        ));

        int i = 0;
        float sizeTile = popup.getHeight() * 0.2f;
        float sizeItem = sizeTile * 0.6f;
        float posH = popup.getHeight() * 0.4f;
        float posStart = popup.getWidth() * 0.5f - sizeTile * 0.5f * (mapBattle.rewardList != null ? mapBattle.rewardList.size() : 0);
        for (Reward reward : mapBattle.rewardList) {

            if (reward.type.equals("coin")) GameSession.profile.addCoin(reward.quantity);
            if (reward.type.equals("gem")) GameSession.profile.gem += reward.quantity;
            if (reward.type.equals("item")) {
                GameSession.itemList.add(new Item(reward.nameRegion, reward.quantity));
            }
            if (reward.type.equals("equip")) {
                GameSession.equipList.add(new Equip(reward.nameRegion));
            }

            if (reward.type.equals("hero")) {
                //add Hero
            }

            plusEXP(0.35f);
            GameSession.profile.numberOfEnemies++;
            if(GameSession.targetMapId == "village_0"){
                GameSession.missionList.get(0).progress = 1;
                JsonSaver.saveObject(Constants.playerPath("mission.json"),GameSession.missionList);
            }

            System.out.println("create reward it pp");
            popup.addActor(
                new UIGroup().child(
                    new UIImage(UI_POPUP, "tile_rarity0").size(sizeTile, sizeTile),
                    new UIImage(UI_POPUP, reward.nameRegion).bounds(sizeTile * (0.2f), sizeTile * 0.2f, sizeItem, sizeItem),
                    new UILabel(reward.quantity + "", BMF).pos(sizeTile * (0.2f), sizeTile * 0.2f).color(Color.SKY).fontScale(1.2f)
                ).pos(posStart + sizeTile * i, posH)
            );
            i++;
        }
    }

    public static void showPopupFail() {
        isPause = true;
        popup.findActor("overlay").setVisible(true);
        popup.findActor("board").setVisible(true);
        popup.findActor("home").setVisible(true);

        popup.addActor(new UIGroup().child(
            new UILabel("Thất bại", BMF).pos(popup.getWidth() * 0.2f, popup.getHeight() * 0.6f).size(popup.getWidth() * 0.6f, popup.getHeight() * 0.3f).fontScale(4).align(Align.center)
        ));
        plusEXP(0.15f);
    }

    private static void plusEXP(float per) {
        for (Hero he : GameSession.heroList) {
            if (!he.grid.equals("empty")) {
                System.out.println(he.grid + " só cong exp");
                he.exp += (int) ((maxLevelEnemy * 100) * per);
                he.checkLevel();
            }
        }
        GameSession.profile.exp += (int) ((maxLevelEnemy * 100) * per);
        if(GameSession.profile.exp >= GameSession.profile.level *100){
            GameSession.profile.exp -= GameSession.profile.level * 100;
            GameSession.profile.level++;
        }
        GameSession.achievementList.get(2).number++;
        JsonSaver.saveObject(Constants.playerPath("achievement.json"), GameSession.achievementList);
        JsonSaver.saveObject(Constants.playerPath("hero_full.json"), GameSession.heroList);
        JsonSaver.saveObject(Constants.playerPath("info.json"), GameSession.profile);
    }

    private void hidePopupPause() {
        isPause = false;
        rootGroup.findActor("overlay").setVisible(false);
        rootGroup.findActor("board").setVisible(false);
        rootGroup.findActor("home").setVisible(false);
        rootGroup.findActor("menu").setVisible(false);
        rootGroup.findActor("setting").setVisible(false);
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
