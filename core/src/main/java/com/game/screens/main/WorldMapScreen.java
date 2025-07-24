package com.game.screens.main;

import static com.game.utils.Constants.ATLAS_ICON;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.CHARACTER_ATLAS;
import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.CHARACTER_BASE_JSON;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.TeleportTriggerComponent;
import com.game.ecs.component.TileMapComponent;
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
import com.game.ui.widget.DailyPP;
import com.game.ui.widget.HerosPP;
import com.game.ui.widget.RecruitPP;
import com.game.ui.widget.RolePP;
import com.game.ui.widget.SettingPP;
import com.game.ui.widget.ShopPP;
import com.game.utils.DataHelper;
import com.game.utils.data.AnimationCache;
import com.game.utils.GameSession;


public class WorldMapScreen extends BaseScreen {
    public static final float SCALE = 6f;
    public static TiledMap map;
    private static UIButton btnNextMap;
    private static UIButton btnAttackBattle;
    private UIJoystick joystick;
    private UILabel coinLB;
    private UILabel gemLB;

    public WorldMapScreen() {
        super();
        createJoystick();
        createHUD();
        createPopupFF();
    }

    private void createHUD() {
        coinLB = new UILabel(GameSession.coin + "", BMF).pos(screenHeight * 0.15f, 0).size(screenWidth * 0.15f, screenHeight * 0.12f);
        new UIGroup().name("coin").pos(screenWidth * 0.025f, screenHeight * 0.85f).size(screenWidth * 0.15f, screenHeight * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "origin"), 20, 20, 20, 20)).size(screenWidth * 0.15f, screenHeight * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "coin")).pos(screenHeight * 0.01f, screenHeight * 0.01f).size(screenHeight * 0.1f, screenHeight * 0.1f),
            coinLB
        ).parent(rootGroup);
        gemLB = new UILabel(GameSession.gem + "", BMF).pos(screenHeight * 0.15f, 0).size(screenWidth * 0.15f, screenHeight * 0.12f);
        new UIGroup().name("gem").pos(screenWidth * 0.2f, screenHeight * 0.85f).size(screenWidth * 0.15f, screenHeight * 0.12f).child(
            new UIImage(new NinePatch(MainGame.getAsM().getRegion(UI_POPUP, "origin"), 20, 20, 20, 20)).size(screenWidth * 0.15f, screenHeight * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink")).pos(screenHeight * 0.01f, screenHeight * 0.01f).size(screenHeight * 0.1f, screenHeight * 0.10f),
            gemLB
        ).parent(rootGroup);
    }

    private void createJoystick() {
        joystick = new UIJoystick(100, 100);
        joystick.debug();
        rootGroup.addActor(joystick);
    }

    private void createPopupFF() {
        btnNextMap = new UIButton("", MainGame.getAsM().getRegion(UI_POPUP, "origin"))
            .size(screenWidth * 0.13f, screenHeight * 0.1f)
            .pos(screenWidth * 0.3f, screenHeight * 0.1f)
            .visible(false)
            .parent(rootGroup)
            .onClick(() -> {
                GameSession.profile.pos.x = -1;
                GameSession.profile.pos.y = -1;
                GameSession.profile.area = GameSession.targetMapId;
                MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
            });
        btnAttackBattle = new UIButton("", MainGame.getAsM().getRegion(UI_POPUP, "origin"))
            .size(screenWidth * 0.13f, screenHeight * 0.1f)
            .pos(screenWidth * 0.3f, screenHeight * 0.1f)
            .visible(false)
            .parent(rootGroup)
            .onClick(() -> {
                MainGame.getScM().showScreen(ScreenType.BATTLE);
            });

        float y = screenHeight * 0.08f;

        createButton("bag", "bag", "Túi đồ", screenWidth * 0.9f, y);
        createButton("heros", "hero", "Đội hình", screenWidth * 0.8f, y);
        createButton("role", "role", "Nhân vật", screenWidth * 0.7f, y);
        createButton("checkin", "checkin", "Hằng ngày", screenWidth * 0.6f, y);
        createButton("shop", "shop", "Cửa hàng", screenWidth * 0.5f, y);
        createButton("setting", "setting", "Cài đặt", screenWidth * 0.9f, screenHeight * 0.8f);
        createButton("support", "recruit", "Chiêu mộ", screenWidth * 0.9f, screenHeight * 0.35f);


        createOverLay();
        rootGroup.addActor(SettingPP.pp(screenWidth, screenHeight));
        rootGroup.addActor(BagPP.pp(screenWidth, screenHeight));
        rootGroup.addActor(RolePP.pp(screenWidth, screenHeight));
        rootGroup.addActor(HerosPP.pp(screenWidth, screenHeight));
        rootGroup.addActor(DailyPP.pp(screenWidth, screenHeight));
        rootGroup.addActor(ShopPP.pp(screenWidth, screenHeight));
        rootGroup.addActor(RecruitPP.pp(screenWidth, screenHeight));
        createBtnClose();
        hidePopup();
    }

    private void createButton(String regionName, String popupName, String text, float x, float y) {
        if (text != "") {
            new UIButton(text, MainGame.getAsM().getRegion(UI_POPUP, "origin"))
                .name("btn1"+popupName)
                .pos(x, y - screenWidth * 0.03f)
                .size(screenWidth * 0.08f, screenWidth * 0.03f)
                .onClick(() -> showPopup(popupName))
                .fontScale(0.7f)
                .parent(rootGroup)
                .scale(1.2f)
                .setOrigin(Align.center);
        }
        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, regionName))
            .name("btn2"+popupName)
            .size(screenWidth * 0.08f, screenWidth * 0.08f)
            .pos(x, y)
            .fontScale(2)
            .onClick(() -> showPopup(popupName))
            .parent(rootGroup);
    }


    private void hidePopup() {
        rootGroup.findActor("coin").setVisible(true);
        rootGroup.findActor("gem").setVisible(true);
        hidePopup("setting");
        hidePopup("bag");
        hidePopup("hero");
        hidePopup("role");
        hidePopup("checkin");
        hidePopup("shop");
        hidePopup("recruit");
        hidePopoupGen();
    }


    public static void loadingAsset() {
        JsonValue characterBase = DataHelper.getJsonValue(CHARACTER_BASE_JSON);
        for (JsonValue character : characterBase) {
            String nameRegion = character.getString("nameRegion");
            MainGame.getAsM().loadAtlas(CHARACTER_ATLAS + nameRegion + ".atlas");
        }
        MainGame.getAsM().loadTiledMap((GameSession.pendingTeleport != null ? GameSession.pendingTeleport.nextMap : GameSession.profile.area));
        MainGame.getAsM().loadAtlas(ATLAS_ITEM);
        MainGame.getAsM().loadAtlas(ATLAS_ICON);
    }

    private void createBtnClose() {
        new UIButton(
            MainGame.getAsM().getRegion(UI_WOOD, "x_up_037"),
            MainGame.getAsM().getRegion(UI_WOOD, "x_down_038"))
            .name("closeBtn")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.9f, screenHeight * 0.75f)
            .onClick(() -> {
                hidePopup();
            })
            .parent(rootGroup);
    }


    private void createOverLay() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);

    }

    private void showPopup(String a) {
        if (rootGroup.findActor(a) == null) {
            Gdx.app.error("WorldMapScreen", "Actor with name '" + a + "' not found");
            return;
        }
        rootGroup.findActor("coin").setVisible(false);
        rootGroup.findActor("gem").setVisible(false);
        rootGroup.findActor(a).setVisible(true);
        ((UIGroup) rootGroup.findActor(a)).run();
        showPopoupGen();
    }

    private void showPopoupGen() {
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("closeBtn").setVisible(true);
    }

    private void hidePopoupGen() {
        rootGroup.findActor("overlay").setVisible(false);
        if (rootGroup.findActor("closeBtn") != null) {
            rootGroup.findActor("closeBtn").setVisible(false);
        }
    }

    private void hidePopup(String nameActor) {
        rootGroup.findActor(nameActor).setVisible(false);
        hidePopoupGen();
    }

    public static void showBtnNextMap(boolean b) {
        btnNextMap.setVisible(b);
        if (b) btnNextMap.setText(GameSession.pendingTeleport.name);
    }

    public static void showBtnAttackBattle(boolean b) {
        btnAttackBattle.setVisible(b);
        if (b) btnAttackBattle.setText("Tấn công");
    }

    private void loadAllAnimations(String nameRegion, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(nameRegion, "idle", new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(nameRegion, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.LOOP));
    }

    public void setupTeleportTriggers(Engine engine, TiledMap tiledMap, float SCALE) {
        MapLayer teleportLayer = tiledMap.getLayers().get("teleport");
        if (teleportLayer != null) {
            for (MapObject obj : teleportLayer.getObjects()) {
                Object x = obj.getProperties().get("x");
                Object y = obj.getProperties().get("y");
                Object w = obj.getProperties().get("width");
                Object h = obj.getProperties().get("height");
                if (x != null && y != null && w != null && h != null) {
                    Rectangle rect = new Rectangle(
                        ((Number) x).floatValue() * SCALE,
                        ((Number) y).floatValue() * SCALE,
                        ((Number) w).floatValue() * SCALE,
                        ((Number) h).floatValue() * SCALE
                    );
                    String nextMap = obj.getProperties().containsKey("map") ? (String) obj.getProperties().get("map") : "";
                    int nextSpawn = obj.getProperties().containsKey("spawn") ? ((Number) obj.getProperties().get("spawn")).intValue() : 0;
                    String name = obj.getProperties().containsKey("name") ? (String) obj.getProperties().get("name") : "";
                    Entity teleportTrigger = new Entity();
                    teleportTrigger.add(new TeleportTriggerComponent(nextMap, nextSpawn, name));
                    teleportTrigger.add(new BoundComponent(rect));
                    engine.addEntity(teleportTrigger);
                }
            }
        }
    }

    public void setupEnemies(Engine engine, TiledMap map, float SCALE) {
        MapLayer enemiesLayer = map.getLayers().get("enemies");
        if (enemiesLayer != null) {
            for (MapObject obj : enemiesLayer.getObjects()) {
                Object x = obj.getProperties().get("x");
                Object y = obj.getProperties().get("y");
                Object w = obj.getProperties().get("width");
                Object h = obj.getProperties().get("height");
                if (x != null && y != null && w != null && h != null) {
                    Rectangle rect = new Rectangle(
                        ((Number) x).floatValue() * SCALE,
                        ((Number) y).floatValue() * SCALE,
                        ((Number) w).floatValue() * SCALE,
                        ((Number) h).floatValue() * SCALE
                    );
                    int id = obj.getProperties().containsKey("id") ? ((Number) obj.getProperties().get("id")).intValue() : 1;
                    String name = obj.getProperties().containsKey("name") ? (String) obj.getProperties().get("name") : "";
                    int level = obj.getProperties().containsKey("level") ? ((Number) obj.getProperties().get("level")).intValue() : 1;

                    Entity enemyTrigger = engine.createEntity();
                    enemyTrigger.add(new EnemyTriggerComponent(id, name, level));
                    enemyTrigger.add(new BoundComponent(rect));
                    engine.addEntity(enemyTrigger);
                }
            }
        }
    }


    @Override
    public void show() {
        super.show();
        Gdx.app.log("WorldMapScreen", "show() called");
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        map = MainGame.getAsM().getTiledMap(GameSession.profile.area);
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, SCALE);

        loadAllAnimations(GameSession.selectedCharacterId, CHARACTER_ATLAS + GameSession.selectedCharacterId + ".atlas");

        Entity mapEntity = engine.createEntity();
        mapEntity.add(new TileMapComponent(map, renderer));
        engine.addEntity(mapEntity);

        engine.addSystem(new TileMapRenderSystem(camera));
//        engine.addSystem(new DebugDrawSystem(map, camera, SCALE));
        engine.addSystem(new CameraClampSystem(engine, camera));
        engine.addSystem(new SpriteRenderSystem(engine, camera));
        engine.addSystem(new TileMapPlayerSpawnSystem(engine, map, GameSession.selectedPlayerSpawnIndex, camera));
        engine.addSystem(new PlayerInputSystem(engine, joystick));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new CollisionSystem(engine, map, SCALE));
        engine.addSystem(new TeleportTriggerSystem());
        setupTeleportTriggers(engine, map, SCALE);
        engine.addSystem(new EnemyCollisionSystem());
        setupEnemies(engine, map, SCALE);
        hidePopup();
        updatePopup();
        hideBtnFuncByTypeMap(GameSession.profile.area.equals("village_0"));
    }

    private void hideBtnFuncByTypeMap(boolean a) {
            rootGroup.findActor("btn1checkin").setVisible(a);
            rootGroup.findActor("btn1shop").setVisible(a);
            rootGroup.findActor("btn1recruit").setVisible(a);

            rootGroup.findActor("btn2checkin").setVisible(a);
            rootGroup.findActor("btn2shop").setVisible(a);
            rootGroup.findActor("btn2recruit").setVisible(a);
    }

    private void updatePopup() {
        BagPP.update();
        RolePP.update();
        HerosPP.update();
        DailyPP.update();
        ShopPP.update();
    }

    @Override
    protected void updateUI(float delta) {
        super.updateUI(delta);
        coinLB.setText(GameSession.coin);
        gemLB.setText(GameSession.gem);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void hide() {
        super.hide();
        engine.removeAllEntities();
        engine.removeAllSystems();
        AnimationCache.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
