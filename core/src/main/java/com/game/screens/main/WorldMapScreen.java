package com.game.screens.main;

import static com.game.utils.Constants.ATLAS_ICON;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.CHARACTER_ATLAS;

import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ecs.component.TileMapComponent;
import com.game.ecs.factory.WorldMapEntityBuilder;
import com.game.ecs.systems.AgentControlSystem;
import com.game.ecs.systems.AnimationStateSystem;
import com.game.ecs.systems.CameraClampSystem;
import com.game.ecs.systems.CollisionSystem;
import com.game.ecs.systems.EnemyCollisionSystem;
import com.game.ecs.systems.PlayerInputSystem;
import com.game.ecs.systems.SpriteRenderSystem;
import com.game.ecs.systems.TeleportTriggerSystem;
import com.game.ecs.systems.TileMapPlayerSpawnSystem;
import com.game.ecs.systems.TileMapRenderSystem;
import com.game.screens.BaseScreen;
import com.game.screens.ScreenType;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UIJoystick;
import com.game.ui.base.UILabel;
import com.game.ui.widget.BagPP;
import com.game.ui.widget.BattleDetailPP;
import com.game.ui.widget.DailyPP;
import com.game.ui.widget.HerosPP;
import com.game.ui.widget.PotentialPP;
import com.game.ui.widget.RecruitPP;
import com.game.ui.widget.RolePP;
import com.game.ui.widget.SettingPP;
import com.game.ui.widget.ShopPP;
import com.game.utils.data.AnimationCache;
import com.game.managers.GameSessionManager;

public class WorldMapScreen extends BaseScreen {
    public static final float SCALE = 6f;
    private TiledMap map;
    private static UIButton btnNextMap;
    private static UIButton btnAttackBattle;
    private UIJoystick joystick;
    private UILabel coinLB;
    private UILabel gemLB;

    @Override
    protected boolean usesEngine() {
        return true;
    }

    public WorldMapScreen() {
        super();
        createScreen();
    }

    @Override
    protected void createScreen() {
        createJoystick();
        createHUD();
        createPopupFF();
    }

    private void createHUD() {
        coinLB = new UILabel(GameSessionManager.getInstance().profile.coin + "", BMF).pos(screenHeight * 0.1f, 0)
                .size(screenWidth * 0.1f, screenHeight * 0.12f).align(Align.center);
        new UIGroup().name("coin").pos(screenWidth * 0.025f, screenHeight * 0.85f)
                .size(screenWidth * 0.15f, screenHeight * 0.12f).child(
                        new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20))
                                .size(screenWidth * 0.15f, screenHeight * 0.12f),
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "coin"))
                                .pos(screenHeight * 0.01f, screenHeight * 0.01f)
                                .size(screenHeight * 0.1f, screenHeight * 0.1f),
                        coinLB)
                .parent(rootGroup).onClick(() -> {
                    GameSessionManager.getInstance().profile.addCoin(100);
                    coinLB.setText(GameSessionManager.getInstance().profile.coin);
                });

        gemLB = new UILabel(GameSessionManager.getInstance().profile.gem + "", BMF).pos(screenHeight * 0.1f, 0)
                .size(screenWidth * 0.1f, screenHeight * 0.12f).align(Align.center);
        new UIGroup().name("gem").pos(screenWidth * 0.2f, screenHeight * 0.85f)
                .size(screenWidth * 0.15f, screenHeight * 0.12f).child(
                        new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"), 20, 20, 20, 20))
                                .size(screenWidth * 0.15f, screenHeight * 0.12f),
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink"))
                                .pos(screenHeight * 0.01f, screenHeight * 0.01f)
                                .size(screenHeight * 0.1f, screenHeight * 0.10f),
                        gemLB)
                .parent(rootGroup).onClick(() -> {
                    GameSessionManager.getInstance().profile.gem += 100;
                    coinLB.setText(GameSessionManager.getInstance().profile.gem);
                });

    }

    private void createJoystick() {
        joystick = new UIJoystick(100, 100);
        joystick.debug();
        rootGroup.addActor(joystick);
    }

    private void createPopupFF() {
        btnNextMap = new UIButton("", MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"))
                .size(screenWidth * 0.13f, screenHeight * 0.1f)
                .pos(screenWidth * 0.3f, screenHeight * 0.1f)
                .visible(false)
                .parent(rootGroup)
                .onClick(() -> {
                    GameSessionManager.getInstance().profile.pos.x = -1;
                    GameSessionManager.getInstance().profile.pos.y = -1;
                    GameSessionManager.getInstance().profile.area = GameSessionManager.getInstance().targetMapId;
                    MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
                });
        btnAttackBattle = new UIButton("", MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"))
                .size(screenWidth * 0.13f, screenHeight * 0.1f)
                .pos(screenWidth * 0.3f, screenHeight * 0.1f)
                .visible(false)
                .parent(rootGroup)
                .onClick(() -> {
                    // MainGame.getScM().showScreen(ScreenType.BATTLE);
                    rootGroup.addActor(BattleDetailPP.pp(screenWidth, screenHeight));
                    rootGroup.findActor("overlay").setVisible(true);
                });

        float y = screenHeight * 0.08f;

        UIButton btnClose = createBtnClose().visible(false);
        btnClose.onClick(() -> {
            btnClose.getParent().remove();
            rootGroup.findActor("overlay").setVisible(false);
        });

        createButton("bag", "bag", "Túi đồ", screenWidth * 0.9f, y, () -> {
            rootGroup.addActor(BagPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("heros", "hero", "Đội hình", screenWidth * 0.8f, y, () -> {
            rootGroup.addActor(HerosPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("role", "role", "Nhân vật", screenWidth * 0.7f, y, () -> {
            rootGroup.addActor(RolePP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("checkin", "checkin", "Hằng ngày", screenWidth * 0.6f, y, () -> {
            rootGroup.addActor(DailyPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("shop", "shop", "Cửa hàng", screenWidth * 0.5f, y, () -> {
            rootGroup.addActor(ShopPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("setting", "setting", "Cài đặt", screenWidth * 0.9f, screenHeight * 0.81f, () -> {
            rootGroup.addActor(SettingPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("potential", "potential", "Tiến hóa", screenWidth * 0.9f, screenHeight * 0.55f, () -> {
            rootGroup.addActor(PotentialPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });
        createButton("support", "recruit", "Chiêu mộ", screenWidth * 0.9f, screenHeight * 0.32f, () -> {
            rootGroup.addActor(RecruitPP.pp(screenWidth, screenHeight).child(btnClose.visible(true)));
            rootGroup.findActor("overlay").setVisible(true);
        });

        rootGroup.addActor(createOverLay().visible(false));

    }

    private void createButton(String regionName, String popupName, String text, float x, float y, Runnable runnable) {
        if (!text.isEmpty()) {
            new UIButton(text, MainGame.getAsM().getRegion(UI_POPUP, "tile_origin"))
                    .name("btn1" + popupName)
                    .pos(x, y - screenWidth * 0.03f)
                    .size(screenWidth * 0.08f, screenWidth * 0.03f)
                    .onClick(runnable)
                    .fontScale(0.7f)
                    .parent(rootGroup)
                    .scale(1.2f)
                    .setOrigin(Align.center);
        }
        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, regionName))
                .name("btn2" + popupName)
                .size(screenWidth * 0.08f, screenWidth * 0.08f)
                .pos(x, y)
                .fontScale(2)
                .onClick(runnable)
                .parent(rootGroup);
    }

    public static void loadingAsset() {
        WorldMapAssets.load();
    }

    private UIButton createBtnClose() {
        return new UIButton(
                MainGame.getAsM().getRegion(UI_WOOD, "x_up_037"),
                MainGame.getAsM().getRegion(UI_WOOD, "x_down_038"))
                .name("closeBtn")
                .size(screenWidth * 0.1f, screenWidth * 0.1f)
                .pos(screenWidth * 0.9f, screenHeight * 0.75f);
    }

    private UIImage createOverLay() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        return new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);
    }

    public static void showBtnNextMap(boolean b) {
        btnNextMap.setVisible(b);
        if (b)
            btnNextMap.setText(GameSessionManager.getInstance().pendingTeleport.name);
    }

    public static void showBtnAttackBattle(boolean b) {
        btnAttackBattle.setVisible(b);
        if (b)
            btnAttackBattle.setText("Kiểm tra");
    }

    private void loadAllAnimations(String nameRegion, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(nameRegion, "idle",
                new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameRegion, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.LOOP));
    }

    @Override
    protected void onEnter() {
        Gdx.app.log("WorldMapScreen", "onEnter() called");
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        map = MainGame.getAsM().getTiledMap(GameSessionManager.getInstance().profile.area);
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, SCALE);

        loadAllAnimations(GameSessionManager.getInstance().selectedCharacterId,
                CHARACTER_ATLAS + GameSessionManager.getInstance().selectedCharacterId + ".atlas");

        Entity mapEntity = engine.createEntity();
        mapEntity.add(new TileMapComponent(map, renderer));
        engine.addEntity(mapEntity);

        engine.addSystem(new TileMapRenderSystem(camera));
        if (com.game.utils.DebugConfig.ENABLED) {
            engine.addSystem(new com.game.ecs.systems.DebugDrawSystem(map, camera, SCALE));
        }
        engine.addSystem(new CameraClampSystem(engine, camera));
        engine.addSystem(new SpriteRenderSystem(engine, camera));
        engine.addSystem(new TileMapPlayerSpawnSystem(engine, map,
                GameSessionManager.getInstance().selectedPlayerSpawnIndex, camera));
        engine.addSystem(new AgentControlSystem(engine)); // chay TRUOC PlayerInputSystem (gia lap input)
        engine.addSystem(new PlayerInputSystem(engine, joystick));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new CollisionSystem(engine, map, SCALE));
        engine.addSystem(new TeleportTriggerSystem());
        WorldMapEntityBuilder.setupTeleportTriggers(engine, map, SCALE);
        engine.addSystem(new EnemyCollisionSystem());
        WorldMapEntityBuilder.setupEnemies(engine, map, SCALE);
        hideBtnFuncByTypeMap(GameSessionManager.getInstance().profile.area.equals("village_0"));
    }

    private void hideBtnFuncByTypeMap(boolean a) {
        rootGroup.findActor("btn1checkin").setVisible(a);
        rootGroup.findActor("btn1shop").setVisible(a);
        rootGroup.findActor("btn1recruit").setVisible(a);
        rootGroup.findActor("btn1potential").setVisible(a);

        rootGroup.findActor("btn2checkin").setVisible(a);
        rootGroup.findActor("btn2shop").setVisible(a);
        rootGroup.findActor("btn2recruit").setVisible(a);
        rootGroup.findActor("btn2potential").setVisible(a);
    }

    @Override
    protected void updateUI(float delta) {
        super.updateUI(delta);
        // Phim tat G: bat/tat agent gia lap nguoi choi (mac dinh WANDER).
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
            GameSessionManager gsm = GameSessionManager.getInstance();
            if (gsm.agentEnabled) {
                gsm.disableAgent();
                Gdx.app.log("Agent", "TAT agent - tra quyen nguoi choi");
            } else {
                gsm.enableAgent("WANDER");
                Gdx.app.log("Agent", "BAT agent - behavior WANDER");
            }
        }
        coinLB.setText(GameSessionManager.getInstance().profile.coin);
        gemLB.setText(GameSessionManager.getInstance().profile.gem);
    }

    @Override
    protected void onExit() {
        super.onExit(); // BaseScreen don ECS (removeAllEntities + removeAllSystems)
        AnimationCache.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
