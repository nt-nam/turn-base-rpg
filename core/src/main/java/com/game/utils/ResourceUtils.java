// utils/ResourceUtils.java
package com.game.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ResourceUtils {
    public static void disposeGroup(Group group) {
        if (group == null) return;

        Array<Actor> children = new Array<>(group.getChildren());

        for (Actor actor : children) {
            if (actor instanceof Group) {
                disposeGroup((Group) actor);
            }

            if (actor instanceof Disposable) {
                ((Disposable) actor).dispose();
            }
        }
    }
}
