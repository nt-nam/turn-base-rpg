package com.game.ui.base;

import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.game.MainGame;
import com.game.managers.GAssetManager;

public class UIJoystick extends Group {
    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private float x, y;

    // Constructor khởi tạo joystick với vị trí ban đầu
    public UIJoystick(float x, float y) {
        this.x = x;
        this.y = y;

        // Lấy TouchpadStyle từ skin
//        touchpadStyle = skin.get("default", TouchpadStyle.class); // Đảm bảo bạn lấy đúng tên từ file skin
        Drawable background = new TextureRegionDrawable(MainGame.getAsM().getRegion(UI_POPUP, "js_bg"));
        TextureRegion knobRegion = MainGame.getAsM().getRegion(UI_POPUP, "js_knob");

        Drawable knob = new TextureRegionDrawable(knobRegion);

        touchpadStyle = new TouchpadStyle(background,knob);
        // Tạo touchpad (joystick) với style từ skin
        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setPosition(x, y);

        // Thêm touchpad vào group, giúp quản lý joystick cùng các phần tử khác trong UI
        this.addActor(touchpad);
    }

    // Cập nhật joystick mỗi frame
    public void update() {
        touchpad.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // Cập nhật trạng thái joystick
    }

    public UIJoystick size(){
        return this;
    }

    // Vẽ joystick lên màn hình
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);  // Gọi draw từ Group để vẽ các phần tử con
        touchpad.draw(batch, 1f);  // Vẽ joystick
    }

    // Lấy giá trị X của joystick (dùng để điều khiển đối tượng)
    public float getXValue() {
        return touchpad.getKnobPercentX();  // Trả về tỷ lệ di chuyển trên trục X
    }

    // Lấy giá trị Y của joystick (dùng để điều khiển đối tượng)
    public float getYValue() {
        return touchpad.getKnobPercentY();  // Trả về tỷ lệ di chuyển trên trục Y
    }

    // Đặt lại vị trí của joystick về vị trí ban đầu
    public void reset() {
        touchpad.setPosition(x, y);  // Đặt lại vị trí ban đầu của joystick
    }

    // Thay đổi kích thước joystick
    public UIJoystick size(float width, float height) {
        touchpad.setSize(width, height);
        return this;
    }

    // Thay đổi vị trí joystick
    public UIJoystick pos(float x, float y) {
        touchpad.setPosition(x, y);
        return this;
    }

    // Thiết lập độ nhạy của joystick (giá trị từ 0 đến 1)
    public void setSensitivity(float sensitivity) {
        touchpad.setDeadzone(sensitivity);  // Điều chỉnh độ nhạy (deadzone) của joystick
    }

    // Thiết lập các hình ảnh cho touchpad (background và knob) từ skin
    public void setTouchpadStyle(Drawable background, Drawable knob) {
        touchpadStyle.background = background;
        touchpadStyle.knob = knob;
        touchpad.setStyle(touchpadStyle);  // Cập nhật lại style của touchpad
    }

}
