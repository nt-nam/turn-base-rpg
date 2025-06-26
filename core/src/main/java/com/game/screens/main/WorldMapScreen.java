package com.game.screens.main;

import static com.game.utils.Constants.CHARACTER_ATLAS;
import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;
import static com.game.utils.Constants.WAREHOUSE_JSON;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.TeleportTriggerComponent;
import com.game.ecs.component.TileMapComponent;
import com.game.ecs.component.WarehouseComponent;
import com.game.ecs.systems.AnimationStateSystem;
import com.game.ecs.systems.CameraClampSystem;
import com.game.ecs.systems.CollisionSystem;
import com.game.ecs.systems.DebugDrawSystem;
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
import com.game.ui.base.UITable;
import com.game.utils.data.AnimationCache;
import com.game.utils.data.GameSession;
import com.game.utils.data.JsonLoader;


public class WorldMapScreen extends BaseScreen {
    public static final float SCALE = 6f;
    public static TiledMap map;
    private static UIButton btnNextMap;
    private boolean isPopupInventory;

    public WorldMapScreen() {
        super();
        createControl();
    }


    public static void loadingAsset() {
        MainGame.getAsM().loadTiledMap((GameSession.pendingTeleport != null ? GameSession.pendingTeleport.nextMap : GameSession.currentMapId));
        MainGame.getAsM().loadAtlas(ATLAS_ITEM);
    }

    public static void unLoadingAsset() {
        MainGame.getAsM().unload((GameSession.pendingTeleport != null ? GameSession.pendingTeleport.nextMap : GameSession.currentMapId));
    }

    @Override
    protected void createScreen() {
        createControl();
    }

    private void createControl() {
        TextureRegion upRight = MainGame.getAsM().getRegion(UI_WOOD, "nextb_up_009");
        TextureRegion downRight = MainGame.getAsM().getRegion(UI_WOOD, "nextb_down_010");
        UIButton btnRight = new UIButton(upRight, downRight)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.3f, screenHeight * 0.2f)
            .onTouchDown(() -> GameSession.moveRight = true)
            .onTouchUp(() -> GameSession.moveRight = false);
        rootGroup.addActor(btnRight);

        TextureRegion upLeft = new TextureRegion(upRight);
        TextureRegion downLeft = new TextureRegion(downRight);
        upLeft.flip(true, false);
        downLeft.flip(true, false);
        UIButton btnLeft = new UIButton(upLeft, downLeft)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.1f, screenHeight * 0.2f)
            .onTouchDown(() -> GameSession.moveLeft = true)
            .onTouchUp(() -> GameSession.moveLeft = false);
        rootGroup.addActor(btnLeft);

        TextureRegion upTop = new TextureRegion(upRight);
        TextureRegion downTop = new TextureRegion(downRight);
        UIButton btnTop = new UIButton(upTop, downTop)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.2f, screenHeight * 0.3f)
            .onTouchDown(() -> GameSession.moveUp = true)
            .onTouchUp(() -> GameSession.moveUp = false);
        rootGroup.addActor(btnTop);

        TextureRegion upBottom = new TextureRegion(upRight);
        TextureRegion downBottom = new TextureRegion(downRight);
        upBottom.flip(true, false);
        downBottom.flip(true, false);
        UIButton btnBottom = new UIButton(upBottom, downBottom)
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.2f, screenHeight * 0.1f)
            .onTouchDown(() -> GameSession.moveDown = true)
            .onTouchUp(() -> GameSession.moveDown = false);
        rootGroup.addActor(btnBottom);

        btnNextMap = new UIButton("next")
            .size(screenWidth * 0.13f, screenHeight * 0.1f)
            .pos(screenWidth * 0.03f, screenHeight * 0.4f)
            .fontScale(2)
            .onClick(() -> {
                MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
            });
        btnNextMap.setVisible(false);
        rootGroup.addActor(btnNextMap);

        new UIButton("Skill")
            .size(screenWidth * 0.13f, screenHeight * 0.1f)
            .pos(screenWidth * 0.8f, screenHeight * 0.35f)
            .fontScale(2)
            .onClick(() -> {
                showPopupSkill();
                isPopupInventory = true;
            }).parent(rootGroup);
        createPopupSkill();
        new UIButton("Inventory")
            .size(screenWidth * 0.13f, screenHeight * 0.1f)
            .pos(screenWidth * 0.8f, screenHeight * 0.2f)
            .fontScale(2)
            .onClick(() -> {
                showPopupInventory();
                isPopupInventory = true;
            }).parent(rootGroup);
        createPopupInventory();
    }

    private void createPopupSkill() {

    }

    private void createPopupInventory() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);

        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "profile");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("profile")
            .parent(rootGroup)
            .bounds(screenWidth * 0.01f, screenHeight * 0.05f, screenWidth * 0.38f, screenHeight * 0.9f);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(rootGroup)
            .bounds(screenWidth * 0.4f, screenHeight * 0.05f, screenWidth * 0.6f, screenHeight * 0.9f);

        new UIButton(
            MainGame.getAsM().getRegion(UI_WOOD, "x_up_037"),
            MainGame.getAsM().getRegion(UI_WOOD, "x_down_038"))
            .name("closeBtn")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.9f, screenHeight * 0.75f)
            .onClick(() -> {
                if (!isPopupInventory) {
                    isPopupInventory = true;
                    showPopupInventory();
                } else {
                    isPopupInventory = false;
                    hidePopupInventory();
                }
            })
            .parent(rootGroup);

        Array<WarehouseComponent> warehouse = JsonLoader.loadArray(WAREHOUSE_JSON, WarehouseComponent.class, false);
        float size = screenWidth * 0.1f;
        float margin = size * 0.2f;

        UITable table = new UITable().name("table").size(size * 5, size * 3).pos(screenWidth * 0.43f, screenHeight * 0.12f);

        for (int i = 0; i < 15; i++) {
            UIGroup uiGroup;
            if (i < warehouse.size) {
                WarehouseComponent item = warehouse.get(i);
                uiGroup = new UIGroup().name(item.id).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.id))
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.5f)
                ).onClick(() -> {
                    // Logic xử lý khi click vào item
                });
            } else {
                uiGroup = new UIGroup().name("empty").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size));
            }


            table.add(uiGroup).size(size, size);

            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }
        rootGroup.addActor(table);

//        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "menu"))
//            .name("menu")
//            .size(screenWidth * 0.1f, screenWidth * 0.1f)
//            .pos(screenWidth * 0.3f, screenHeight * 0.18f)
//            .onClick(() -> {
//
//            })
//            .parent(rootGroup);
//
//        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "home"))
//            .name("home")
//            .size(screenWidth * 0.1f, screenWidth * 0.1f)
//            .pos(screenWidth * 0.45f, screenHeight * 0.18f)
//            .onClick(() -> {
//                isPopupInventory = false;
//                hidePopupInventory();
//            })
//            .parent(rootGroup);
//
//        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "setting"))
//            .name("setting")
//            .size(screenWidth * 0.1f, screenWidth * 0.1f)
//            .pos(screenWidth * 0.6f, screenHeight * 0.18f)
//            .onClick(() -> {
//
//            })
//            .parent(rootGroup);

        hidePopupInventory();
    }

    private void hidePopupInventory() {
        rootGroup.findActor("closeBtn").setVisible(false);
        rootGroup.findActor("overlay").setVisible(false);
        rootGroup.findActor("board").setVisible(false);
        rootGroup.findActor("profile").setVisible(false);
        rootGroup.findActor("table").setVisible(false);
//        rootGroup.findActor("home").setVisible(false);
//        rootGroup.findActor("menu").setVisible(false);
//        rootGroup.findActor("setting").setVisible(false);
    }

    private void showPopupInventory() {
        rootGroup.findActor("closeBtn").setVisible(true);
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("board").setVisible(true);
        rootGroup.findActor("profile").setVisible(true);
        rootGroup.findActor("table").setVisible(true);
//        rootGroup.findActor("home").setVisible(true);
//        rootGroup.findActor("menu").setVisible(true);
//        rootGroup.findActor("setting").setVisible(true);
    }


    private void hidePopupSkill() {
    }
    private void showPopupSkill() {
    }

    public static void showBtnNextMap(boolean b) {
        btnNextMap.setVisible(b);
        if (b) btnNextMap.setText(GameSession.pendingTeleport.name);
    }

    private void loadAllAnimations(String characterId, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(characterId, "idle", new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(characterId, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.LOOP));
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
                    String name = obj.getProperties().containsKey("name") ? (String) obj.getProperties().get("name") : "";
                    int level = obj.getProperties().containsKey("level") ? ((Number) obj.getProperties().get("level")).intValue() : 1;

                    Entity enemyTrigger = engine.createEntity();
                    enemyTrigger.add(new EnemyTriggerComponent(name, level));
                    enemyTrigger.add(new BoundComponent(rect));
                    engine.addEntity(enemyTrigger);
                }
            }
        }
    }


    @Override
    public void show() {
        super.show();
        // 1. Khởi tạo camera
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        // 2. Load tiled map
        map = MainGame.getAsM().getTiledMap(GameSession.currentMapId);
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, SCALE);

//        String characterId = GameSession.selectedCharacterId;
//        TextureAtlas atlas = MainGame.getAsM().get(CHARACTER + characterId + ".atlas", TextureAtlas.class);
//        Animation<TextureRegion> idleAnim = new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP);
//        AnimationCache.put(characterId, "idle", idleAnim);

        loadAllAnimations(GameSession.selectedCharacterId, CHARACTER_ATLAS + GameSession.selectedCharacterId + ".atlas");

        // 3. Tạo entity và add TileMapComponent
        Entity mapEntity = engine.createEntity();
        mapEntity.add(new TileMapComponent(map, renderer));
        engine.addEntity(mapEntity);

        // 4. Add TileMapRenderSystem vào engine
        engine.addSystem(new TileMapRenderSystem(camera));
        engine.addSystem(new DebugDrawSystem(map, camera, SCALE));
        engine.addSystem(new CameraClampSystem(engine, camera));
        engine.addSystem(new SpriteRenderSystem(engine, camera));
        engine.addSystem(new TileMapPlayerSpawnSystem(engine, map, GameSession.selectedPlayerSpawnIndex, camera));
        engine.addSystem(new PlayerInputSystem(engine));
        engine.addSystem(new AnimationStateSystem(engine));
        engine.addSystem(new CollisionSystem(engine, map, SCALE));
//        engine.addSystem(new CollisionUpdateSystem());
        engine.addSystem(new TeleportTriggerSystem());
        setupTeleportTriggers(engine, map, SCALE);
        engine.addSystem(new EnemyCollisionSystem());
        setupEnemies(engine, map, SCALE);
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
