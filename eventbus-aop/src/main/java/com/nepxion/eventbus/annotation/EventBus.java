package com.nepxion.eventbus.annotation;

import com.nepxion.eventbus.constant.EventConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EventBus {

    /**
     * 事件标识
     *
     * @return identifier
     */
    String identifier() default EventConstant.SHARED_CONTROLLER;

    /**
     * 事件是否采用异步执行
     *
     * @return boolean
     */
    boolean async() default true;
}