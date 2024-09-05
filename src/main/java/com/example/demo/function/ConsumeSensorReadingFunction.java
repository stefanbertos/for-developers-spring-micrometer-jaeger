package com.example.demo.function;

import com.example.demo.dto.SensorReading;
import com.example.demo.service.ProcessingSensorEvent;
import io.micrometer.observation.annotation.Observed;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ConsumeSensorReadingFunction {


    @Bean
    public Consumer<SensorReading> sink(/*ApplicationEventPublisher applicationEventPublisher*/ProcessingSensorEvent processingSensorEvent){
       // return applicationEventPublisher::publishEvent;
               return processingSensorEvent::process;
    }
}
