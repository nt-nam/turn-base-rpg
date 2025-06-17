package com.game.managers.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {

    private static Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public <T extends GameEvent> void subscribe(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    public <T extends GameEvent> void unsubscribe(Class<T> type, Consumer<T> listener) {
        List<Consumer<?>> consumerList = listeners.get(type);
        if (consumerList != null) {
            consumerList.remove(listener);
            // Nếu không còn ai lắng nghe, remove luôn khỏi map cho sạch
            if (consumerList.isEmpty()) listeners.remove(type);
        }
    }

    public <T extends GameEvent> void dispatch(T event) {
        List<Consumer<?>> consumerList = listeners.get(event.getClass());
        if (consumerList != null) {
            for (Consumer<?> c : consumerList) {
                ((Consumer<T>) c).accept(event);
            }
        }
    }

    public void clear() {
        listeners.clear();
    }
}
