package com.game.screens.main;

import static com.game.utils.Constants.ATLAS_ICON;
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
import com.game.ui.base.UIImage;
import com.game.ui.base.UIJoystick;
import com.game.ui.widget.BagPP;
import com.game.ui.widget.CheckinPP;
import com.game.ui.widget.HerosPP;
import com.game.ui.widget.RolePP;
import com.game.ui.widget.ShopPP;
import com.game.utils.data.AnimationCache;
import com.game.utils.data.GameSession;


public class WorldMapScreen extends BaseScreen {
    public static final float SCALE = 6f;
    public static TiledMap map;
    private static UIButton btnNextMap;
    private boolean optionBattle;
    private UIJoystick joystick;
    public WorldMapScreen() {
        super();
//        createControl();
        createJoystick();
        createPopupFF();
    }

    private void createJoystick() {
        joystick = new UIJoystick(100, 100);
        joystick.debug();
        rootGroup.addActor(joystick);
    }

    private void createPopupFF() {
        btnNextMap = new UIButton("next")
            .size(screenWidth * 0.13f, screenHeight * 0.1f)
            .pos(screenWidth * 0.03f, screenHeight * 0.4f)
            .fontScale(2)
            .visible(false)
            .parent(rootGroup)
            .onClick(() -> {
                MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
            });

//        new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "top_title0", 20))
//            .size(screenWidth * 0.55f, screenHeight * 0.23f)
//            .pos(screenWidth * 0.45f, -10)
//            .parent(rootGroup);

        float y = screenHeight * 0.03f;
        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, "bag"))
            .pos(screenWidth * 0.9f, y)
            .fontScale(2)
            .onClick(this::showPopupInventory)
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, "heros"))
            .pos(screenWidth * 0.8f, y)
            .onClick(this::showPopupHero)
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, "role"))
            .pos(screenWidth * 0.7f, y)
            .onClick(this::showPopupRole)
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, "checkin"))
            .pos(screenWidth * 0.6f, y)
            .onClick(this::showPopupCheckin)
            .parent(rootGroup);

        new UIButton(MainGame.getAsM().getRegion(ATLAS_ICON, "shop"))
            .pos(screenWidth * 0.5f, y)
            .onClick(this::showPopupShop)
            .parent(rootGroup);

        createOverLay();
        createPopupInventory();
        createPopupHero();
        createPopupRole();
        createPopupCheckin();
        createPopupShop();
        createBtnClose();
        hidePopoupGen();
    }


    public static void loadingAsset() {
        MainGame.getAsM().loadTiledMap((GameSession.pendingTeleport != null ? GameSession.pendingTeleport.nextMap : GameSession.currentMapId));
        MainGame.getAsM().loadAtlas(ATLAS_ITEM);
        MainGame.getAsM().loadAtlas(ATLAS_ICON);
    }

    public static void unLoadingAsset() {
        MainGame.getAsM().unload((GameSession.pendingTeleport != null ? GameSession.pendingTeleport.nextMap : GameSession.currentMapId));
    }

    @Override
    protected void createScreen() {
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

    }

    private void createBtnClose() {
        new UIButton(
            MainGame.getAsM().getRegion(UI_WOOD, "x_up_037"),
            MainGame.getAsM().getRegion(UI_WOOD, "x_down_038"))
            .name("closeBtn")
            .size(screenWidth * 0.1f, screenWidth * 0.1f)
            .pos(screenWidth * 0.9f, screenHeight * 0.75f)
            .onClick(() -> {
                hidePopupInventory();
                hidePopupRole();
                hidePopupShop();
                hidePopupCheckin();
                hidePopupHero();
                hidePopoupGen();

            })
            .parent(rootGroup);
    }

    private void createPopupShop() {
        rootGroup.addActor(ShopPP.pp(screenWidth,screenHeight));
        hidePopupShop();
    }

    private void createPopupCheckin() {
        rootGroup.addActor(CheckinPP.pp(screenWidth,screenHeight));
        hidePopupCheckin();
    }


    private void createPopupRole() {
        rootGroup.addActor(RolePP.pp(screenWidth,screenHeight));
        hidePopupRole();
    }

    private void createPopupHero() {
        rootGroup.addActor(HerosPP.pp(screenWidth,screenHeight));
        hidePopupHero();
    }


    private void createOverLay() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);

    }

    private void showPopupInventory() {
        rootGroup.findActor("inventory").setVisible(true);
        showPopoupGen();
    }

    private void showPopupShop() {
        rootGroup.findActor("shop").setVisible(true);
        showPopoupGen();
    }

    private void showPopupCheckin() {
        rootGroup.findActor("checkin").setVisible(true);
        showPopoupGen();
    }

    private void showPopupRole() {
        rootGroup.findActor("role").setVisible(true);
        showPopoupGen();
    }

    private void showPopupHero() {
        rootGroup.findActor("hero").setVisible(true);
        showPopoupGen();
    }
    private void showPopoupGen(){
        rootGroup.findActor("overlay").setVisible(true);
        rootGroup.findActor("closeBtn").setVisible(true);
    }
    private void hidePopoupGen(){
        rootGroup.findActor("overlay").setVisible(false);
        if(rootGroup.findActor("closeBtn")!=null){
            rootGroup.findActor("closeBtn").setVisible(false);
        }
    }

    private void hidePopupInventory() {
        rootGroup.findActor("inventory").setVisible(false);
        hidePopoupGen();
    }

    private void hidePopupShop() {
        rootGroup.findActor("shop").setVisible(false);
        hidePopoupGen();

    }

    private void hidePopupCheckin() {
        rootGroup.findActor("checkin").setVisible(false);
        hidePopoupGen();
    }

    private void hidePopupRole() {
        rootGroup.findActor("role").setVisible(false);
        hidePopoupGen();
    }

    private void hidePopupHero() {
        rootGroup.findActor("hero").setVisible(false);
        hidePopoupGen();
    }


    private void createPopupInventory() {
        rootGroup.addActor(BagPP.pp(screenWidth,screenHeight));
        hidePopupInventory();
    }

    private void createPopupOptionBattle() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture overlay = new Texture(pixmap);
        pixmap.dispose();
        new UIImage(overlay).name("overlay").parent(rootGroup).bounds(0, 0, screenWidth, screenHeight);
    }


    private void hidePopupSkill() {
    }

    private void showPopupSkill() {
    }

    public static void showBtnNextMap(boolean b) {
        btnNextMap.setVisible(b);
        if (b) btnNextMap.setText(GameSession.pendingTeleport.name);
    }

    private void loadAllAnimations(String characterBaseId, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(characterBaseId, "idle", new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(characterBaseId, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.LOOP));
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
        engine.addSystem(new PlayerInputSystem(engine,joystick));
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
