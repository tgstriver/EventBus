package com.nepxion.eventbus.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.nepxion.eventbus.annotation.EventBus;
import com.nepxion.eventbus.core.EventControllerFactory;

public class EventBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private EventControllerFactory eventControllerFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(EventBus.class)) {
            EventBus eventBusAnnotation = bean.getClass().getAnnotation(EventBus.class);
            String identifier = eventBusAnnotation.identifier();
            boolean async = eventBusAnnotation.async();

            eventControllerFactory.getController(identifier, async).register(bean);
        }

        return bean;
    }
}
