package com.game.core;

import com.badlogic.gdx.utils.Pool;

public class TurnResultPool extends Pool<TurnResult> {
    // Singleton instance để truy cập pool
    private static final TurnResultPool instance = new TurnResultPool();

    // Constructor private để ngăn khởi tạo bên ngoài
    private TurnResultPool() {
        super(16, 512); // Khởi tạo pool với initialCapacity=16, max=512
    }

    // Lấy instance của pool
    public static TurnResultPool getInstance() {
        return instance;
    }

    @Override
    protected TurnResult newObject() {
        return new TurnResult();
    }

    // Ghi đè obtain() như một phương thức instance
    @Override
    public TurnResult obtain() {
        TurnResult result = super.obtain();
        result.reset(); // Reset để đảm bảo đối tượng sạch
        return result;
    }

    // Ghi đè free() như một phương thức instance
    @Override
    public void free(TurnResult result) {
        result.reset(); // Reset trước khi trả về pool
        super.free(result);
    }
}
