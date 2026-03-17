package com.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;

/**
 * Khởi tạo LibGDX Headless backend dùng chung cho các unit test cần Gdx.app.
 * Gọi GdxTestRunner.setup() trong @BeforeAll hoặc constructor của test class.
 */
public class GdxTestRunner {

    private static boolean initialized = false;

    public static void setup() {
        if (initialized) return;
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        }, config);
        // Sử dụng MockGL20 của LibGDX headless thay vì Mockito
        if (Gdx.gl == null) {
            Gdx.gl = Gdx.gl20 = new MockGL20();
        }
        initialized = true;
    }
}
