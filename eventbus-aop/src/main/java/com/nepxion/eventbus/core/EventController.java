package com.nepxion.eventbus.core;

import java.util.Collection;

public interface EventController {

    void register(Object object);

    void unregister(Object object);

    void post(Object event);

    void post(Collection<?> event);

    void postEvent(Event event);

    void postEvent(Collection<? extends Event> events);
}