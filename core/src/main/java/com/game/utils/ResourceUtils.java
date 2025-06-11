// utils/ResourceUtils.java
package com.game.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ResourceUtils {

    /**
     * Duyệt đệ quy qua một Group và gọi dispose() trên tất cả các Actor
     * implement "Disposable".
     * @param group Group cần dọn dẹp.
     */
    public static void disposeGroup(Group group) {
        if (group == null) return;

        // Tạo một bản sao của danh sách children để tránh lỗi ConcurrentModificationException
        // khi một actor tự remove nó trong lúc duyệt.
        Array<Actor> children = new Array<>(group.getChildren());

        for (Actor actor : children) {
            // 1. Nếu actor là một Group khác, gọi đệ quy để dọn dẹp group con trước
            if (actor instanceof Group) {
                disposeGroup((Group) actor);
            }

            // 2. Nếu actor có implement Disposable (chứa tài nguyên nặng)
            if (actor instanceof Disposable) {
                // Giải phóng tài nguyên của nó!
                ((Disposable) actor).dispose();
            }
        }
    }
}
