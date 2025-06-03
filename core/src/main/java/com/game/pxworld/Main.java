package com.game.pxworld;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Quan trọng
import com.game.pxworld.screens.MainMenuScreen;

public class Main extends Game { // Hoặc tên lớp Game của bạn
    // Thường thì SpriteBatch được tạo ở đây và truyền cho các Screen
    public SpriteBatch batch; // Public để các Screen có thể truy cập hoặc có getter

    @Override
    public void create() {
        batch = new SpriteBatch(); // Khởi tạo SpriteBatch
        // Thay vì màn hình mặc định của LibGDX, chúng ta sẽ set MainMenuScreen
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Quan trọng! Dòng này sẽ gọi phương thức render của Screen hiện tại
    }

    @Override
    public void dispose() {
        batch.dispose(); // Giải phóng SpriteBatch khi game đóng
        // Screen hiện tại cũng nên được dispose nếu cần
        if (screen != null) {
            screen.dispose();
        }
    }
}
