package com.example.demo.function;

import com.example.demo.dto.SensorReading;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ConsumeSensorReadingFunction {

    @Bean
    public Consumer<SensorReading> sink(ApplicationEventPublisher applicationEventPublisher) {
        return applicationEventPublisher::publishEvent;
    }
}
