package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;

public class ActionQueueComponent implements Component {
    public static class Action {
        public float startX, startY;
        public float endX, endY;
        public Entity actor;
        public Entity target;
        public float duration;
        public SkillStateComponent.State state;
        public String action;
        public ObjectMap<String,Integer> note;

        public Action( Entity actor, Entity target,float duration, SkillStateComponent.State state, String action, ObjectMap<String,Integer> note) {
            this.actor = actor;
            this.startX = actor.getComponent(PositionComponent.class).x;
            this.startY = actor.getComponent(PositionComponent.class).y;
            this.target = target;
            this.endX = target.getComponent(PositionComponent.class).x;
            this.endY = target.getComponent(PositionComponent.class).y;
            this.duration = duration;
            this.state = state;
            this.action = action;
            this.note = note;
        }
    }

    public Queue<Action> actions = new Queue<>();
    public boolean isProcessing = false;
}
