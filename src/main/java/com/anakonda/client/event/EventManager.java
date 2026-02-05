package com.anakonda.client.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {
    public static final EventManager INSTANCE = new EventManager();
    private final List<Consumer<Event>> listeners = new ArrayList<>();

    public void register(Consumer<Event> listener) {
        listeners.add(listener);
    }

    public void post(Event event) {
        for (Consumer<Event> listener : listeners) {
            listener.accept(event);
        }
    }
}
