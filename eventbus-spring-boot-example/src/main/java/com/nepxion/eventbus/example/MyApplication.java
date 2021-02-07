package com.nepxion.eventbus.example;

import com.nepxion.eventbus.annotation.EnableEventBus;
import com.nepxion.eventbus.example.service.MyPublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableEventBus
public class MyApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication.class, args);
        MyPublisher myPublisher = applicationContext.getBean(MyPublisher.class);
        myPublisher.publish();
    }
}