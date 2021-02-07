package com.nepxion.eventbus.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.nepxion.eventbus.constant.EventConstant;
import com.nepxion.eventbus.thread.ThreadPoolFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public final class EventControllerFactory {

    @Autowired
    private ThreadPoolFactory threadPoolFactory;

    private volatile Map<String, EventController> syncControllerMap = new ConcurrentHashMap<>();
    private volatile Map<String, EventController> asyncControllerMap = new ConcurrentHashMap<>();

    public EventController getAsyncController() {
        return this.getAsyncController(EventConstant.SHARED_CONTROLLER);
    }

    public EventController getAsyncController(String identifier) {
        return this.getController(identifier, true);
    }

    public EventController getSyncController() {
        return this.getSyncController(EventConstant.SHARED_CONTROLLER);
    }

    public EventController getSyncController(String identifier) {
        return this.getController(identifier, false);
    }

    public EventController getController(String identifier, boolean async) {
        return this.getController(identifier, async ? EventType.ASYNC : EventType.SYNC);
    }

    public EventController getController(String identifier, EventType type) {
        switch (type) {
            case SYNC:
                EventController syncEventController = syncControllerMap.get(identifier);
                if (syncEventController == null) {
                    EventController newEventController = this.createSyncController(identifier);
                    syncEventController = syncControllerMap.putIfAbsent(identifier, newEventController);
                    if (syncEventController == null) {
                        syncEventController = newEventController;
                    }
                }
                return syncEventController;
            case ASYNC:
                EventController asyncEventController = asyncControllerMap.get(identifier);
                if (asyncEventController == null) {
                    EventController newEventController = this.createAsyncController(identifier, threadPoolFactory.getThreadPoolExecutor(identifier));
                    asyncEventController = asyncControllerMap.putIfAbsent(identifier, newEventController);
                    if (asyncEventController == null) {
                        asyncEventController = newEventController;
                    }
                }
                return asyncEventController;
            default:
                break;
        }

        return null;
    }

    public EventController createSyncController() {
        return new EventControllerImpl(new EventBus());
    }

    /**
     * 创建同步控制器
     *
     * @param identifier
     * @return
     */
    public EventController createSyncController(String identifier) {
        return new EventControllerImpl(new EventBus(identifier));
    }

    public EventController createSyncController(SubscriberExceptionHandler subscriberExceptionHandler) {
        return new EventControllerImpl(new EventBus(subscriberExceptionHandler));
    }

    /**
     * 创建异步控制器
     *
     * @param identifier
     * @param executor
     * @return
     */
    public EventController createAsyncController(String identifier, Executor executor) {
        return new EventControllerImpl(new AsyncEventBus(identifier, executor));
    }

    public EventController createAsyncController(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
        return new EventControllerImpl(new AsyncEventBus(executor, subscriberExceptionHandler));
    }

    public EventController createAsyncController(Executor executor) {
        return new EventControllerImpl(new AsyncEventBus(executor));
    }
}