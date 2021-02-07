package com.nepxion.eventbus.aop;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.nepxion.eventbus.annotation.EnableEventBus;
import com.nepxion.eventbus.constant.EventConstant;
import com.nepxion.matrix.selector.AbstractImportSelector;
import com.nepxion.matrix.selector.RelaxedPropertyResolver;

@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EventBusImportSelector extends AbstractImportSelector<EnableEventBus> {

    @Override
    protected boolean isEnabled() {
        return new RelaxedPropertyResolver(super.getEnvironment()).getProperty(EventConstant.EVENTBUS_ENABLED, Boolean.class, Boolean.TRUE);
    }
}