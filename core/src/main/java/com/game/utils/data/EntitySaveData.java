package com.game.utils.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Lưu toàn bộ dữ liệu 1 entity (player, enemy, item, ...)
 */
public class EntitySaveData {
    // Kiểu entity ("player", "enemy", "item", "npc", "object"...)
    public String entityType;
    // ID duy nhất trong scene (nếu có, hoặc để 0/null nếu không dùng)
    public int entityId;
    // Mỗi component sẽ được lưu dưới dạng key-value
    // Key: tên component ("Position", "Stats", "Inventory"...)
    // Value: object state của component (có thể là Map, object, primitive...)
    public Map<String, Object> components = new HashMap<>();

    // -- Constructor trống để JSON deserializer sử dụng
    public EntitySaveData() {}

    // -- Constructor gọn khi tạo entity mới
    public EntitySaveData(String entityType, int entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }

    // -- Thêm dữ liệu component vào entity này (hàm tiện ích)
    public void putComponent(String componentName, Object data) {
        components.put(componentName, data);
    }

    // -- Lấy dữ liệu component
    public Object getComponent(String componentName) {
        return components.get(componentName);
    }

    // -- Lấy dữ liệu component dưới dạng kiểu mong muốn
    @SuppressWarnings("unchecked")
    public <T> T getComponent(String componentName, Class<T> clazz) {
        return (T) components.get(componentName);
    }
}
