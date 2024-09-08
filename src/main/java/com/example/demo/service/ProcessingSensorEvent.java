package com.example.demo.service;

import com.example.demo.dto.SensorData;
import com.example.demo.dto.SensorReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingSensorEvent {
    private final MongoTemplate mongoTemplate;

    @EventListener
    @Async("propagatingContextExecutor")
    public void process(SensorReading sensorReading) {
        log.info("Processing sensor reading: {}", sensorReading);
        mongoTemplate.save(new SensorData(sensorReading.id(), sensorReading.temperature()));
    }
}
