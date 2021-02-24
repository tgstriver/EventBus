package com.nepxion.eventbus.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.Subscribe;
import com.nepxion.eventbus.annotation.EventBus;
import com.nepxion.eventbus.core.Event;

@EventBus
@Service
public class MySubscriber1 {

    private static final Logger LOG = LoggerFactory.getLogger(MySubscriber1.class);

    @Subscribe
    public void subscribe(String event) {
        LOG.info("子线程接收异步事件 - {}，String类型", event);
    }

    @Subscribe
    public void subscribe(Long event) {
        LOG.info("子线程接收异步事件 - {}，Long类型", event);
    }

    @Subscribe
    public void subscribe(Boolean event) {
        LOG.info("子线程接收异步事件 - {}，Boolean类型", event);
    }

    @Subscribe
    public void subscribe(Event event) {
        LOG.info("子线程接收异步事件 - {}，内置类型Event", event);
    }
}