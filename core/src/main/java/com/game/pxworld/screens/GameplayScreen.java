package com.game.pxworld.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.pxworld.Main;
import com.game.pxworld.manager.LevelManager; // Thêm LevelManager
import com.game.pxworld.debug.DebugRenderer; // Thêm DebugRenderer

public class GameplayScreen extends BaseScreen {

    private LevelManager levelManager; // Khai báo LevelManager
    private DebugRenderer debugRenderer; // Khai báo DebugRenderer
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TextureAtlas knightAtlas;
    private ObjectMap<String, Animation<TextureRegion>> knightAnimations;
    private Animation<TextureRegion> currentKnightAnimation;
    private String currentAnimationName;
    private float stateTime;

    private Array<Rectangle> walls; // Lưu trữ các tường (wall)
    private Array<Polygon> lines;   // Lưu trữ các đường (line)

    private Vector2 knightPosition;
    private final float movementSpeed = 100f; // Tốc độ di chuyển của nhân vật

    private final float knightWidth = 24f;
    private final float knightHeight = 12f;

    public GameplayScreen(final Main game) {
        super(game);

        // Khởi tạo LevelManager và DebugRenderer
        levelManager = new LevelManager(); // Khởi tạo LevelManager
        debugRenderer = new DebugRenderer(); // Khởi tạo DebugRenderer

        knightPosition = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 3f);
        stateTime = 0f;

        // Tải texture atlas cho nhân vật
        knightAtlas = new TextureAtlas(Gdx.files.internal("atlas/knight/Knight_A.atlas"));
        loadAnimations();

        // Tải bản đồ TMX
        tiledMap = levelManager.loadLevel("tilemap/map/Village_0.tmx"); // Dùng LevelManager để tải level
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        loadMapObjects();  // Lấy các đối tượng từ map
        loadSpawnPoints(); // Lấy điểm spawn từ map
    }

    // Tải các animation của hiệp sĩ từ texture atlas
    private void loadAnimations() {
        knightAnimations = new ObjectMap<>();
        Array<TextureAtlas.AtlasRegion> allFrames = knightAtlas.findRegions("Knight_A");

        String[] animationNames = {"idle", "walk1", "walk2", "run", "jump", "attack", "hurt", "death"};
        int framesPerAnimation = 6; // Mỗi animation có 6 frame

        for (int i = 0; i < animationNames.length; i++) {
            Array<TextureRegion> animationFrames = new Array<>();
            for (int j = 0; j < framesPerAnimation; j++) {
                animationFrames.add(allFrames.get(i * framesPerAnimation + j));
            }
            knightAnimations.put(animationNames[i], new Animation<>(0.1f, animationFrames, Animation.PlayMode.LOOP));
        }

        setKnightAnimation("idle"); // Mặc định animation là "idle"
    }

    // Load các đối tượng từ map TMX (tường, đường...)
    public void loadMapObjects() {
        walls = new Array<>();
        lines = new Array<>();

        // Lấy các đối tượng từ layer "wall"
        for (MapObject object : tiledMap.getLayers().get("wall").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                walls.add(rect); // Thêm tường vào danh sách
            }
        }

        // Lấy các đối tượng từ layer "line"
        for (MapObject object : tiledMap.getLayers().get("line").getObjects()) {
            if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                lines.add(polygon); // Thêm đường vào danh sách
            }
        }
    }

    // Tải các điểm spawn từ map TMX
    private void loadSpawnPoints() {
        for (MapObject object : tiledMap.getLayers().get("player").getObjects()) {
            if (object instanceof RectangleMapObject) {
                if((int)(object.getProperties().get("index")) == 0){
                    Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                    float spawnX = rectangle.x;
                    float spawnY = rectangle.y;
                    // Lưu lại vị trí spawn của nhân vật
                    knightPosition.set(spawnX, spawnY); // Đặt Knight tại vị trí spawn
                    Gdx.app.log("GameplayScreen", "Spawn point found at: " + spawnX + ", " + spawnY);
                }
            }
        }
    }

    // Đổi animation dựa trên tên
    private void setKnightAnimation(String animationName) {
        if (knightAnimations.containsKey(animationName)) {
            if (currentAnimationName == null || !currentAnimationName.equals(animationName)) {
                currentKnightAnimation = knightAnimations.get(animationName);
                currentAnimationName = animationName;
                stateTime = 0f; // Reset thời gian
            }
        }
    }

    @Override
    protected void renderScreenContent(float delta) {
        // Di chuyển Knight nếu không va chạm với các đối tượng không thể đi qua (tường hoặc đường)
        float newX = knightPosition.x;
        float newY = knightPosition.y;

        // Di chuyển phải
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX += movementSpeed * delta;
            if (!checkCollisionWithWalls(newX, knightPosition.y, knightWidth, knightHeight) && !checkCollisionWithLines(newX, knightPosition.y)) {
                knightPosition.x = newX;  // Chỉ di chuyển nếu không va chạm
            }
            setKnightAnimation("walk1"); // Animation đi
        }

        // Di chuyển trái
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX -= movementSpeed * delta;
            if (!checkCollisionWithWalls(newX, knightPosition.y, knightWidth, knightHeight) && !checkCollisionWithLines(newX, knightPosition.y)) {
                knightPosition.x = newX;  // Chỉ di chuyển nếu không va chạm
            }
            setKnightAnimation("walk2"); // Animation đi
        }

        // Di chuyển lên
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newY += movementSpeed * delta;
            if (!checkCollisionWithWalls(knightPosition.x, newY, knightWidth, knightHeight) && !checkCollisionWithLines(knightPosition.x, newY)) {
                knightPosition.y = newY;  // Chỉ di chuyển nếu không va chạm
            }
            setKnightAnimation("run"); // Animation chạy
        }

        // Di chuyển xuống
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newY -= movementSpeed * delta;
            if (!checkCollisionWithWalls(knightPosition.x, newY, knightWidth, knightHeight) && !checkCollisionWithLines(knightPosition.x, newY)) {
                knightPosition.y = newY;  // Chỉ di chuyển nếu không va chạm
            }
            setKnightAnimation("run"); // Animation chạy
        }

        // Kiểm tra các phím khác như tấn công, nhảy v.v.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            setKnightAnimation("jump");
        }

        // Render bản đồ và animation
        stateTime += delta;
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (currentKnightAnimation != null) {
            TextureRegion currentFrame = currentKnightAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, knightPosition.x, knightPosition.y);
        }
        batch.end();

        // Debug: Vẽ các đường bao quanh các vật thể (tường và đường)
        debugRenderer.renderDebugShapes(tiledMap);
    }

    private boolean checkCollisionWithWalls(float x, float y, float width, float height) {
        for (Rectangle wall : walls) {
            // Kiểm tra va chạm giữa Knight và tường
            if (wall.overlaps(new Rectangle(x, y, width, height))) {
                return true; // Có va chạm, không thể di chuyển
            }
        }
        return false; // Không có va chạm
    }

    private boolean checkCollisionWithLines(float x, float y) {
        for (Polygon line : lines) {
            if (line.contains(x, y)) {  // Kiểm tra xem Knight có ở trong đường không
                return true; // Va chạm với đường
            }
        }
        return false; // Không có va chạm
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mapRenderer.setView(new OrthographicCamera(width, height)); // Cập nhật camera cho mapRenderer
    }

    @Override
    public void dispose() {
        super.dispose();
        knightAtlas.dispose();
        tiledMap.dispose();
    }
}
