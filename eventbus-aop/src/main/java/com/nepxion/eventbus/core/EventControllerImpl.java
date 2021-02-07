package com.nepxion.eventbus.core;

import com.google.common.eventbus.EventBus;

import java.util.Collection;

public class EventControllerImpl implements EventController {

    private final EventBus eventBus;

    public EventControllerImpl(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void register(Object object) {
        eventBus.register(object);
    }

    @Override
    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    @Override
    public void post(Object event) {
        eventBus.post(event);
    }

    @Override
    public void post(Collection<?> events) {
        for (Object event : events) {
            eventBus.post(event);
        }
    }

    @Override
    public void postEvent(Event event) {
        this.post(event);
    }

    @Override
    public void postEvent(Collection<? extends Event> events) {
        this.post(events);
    }
}