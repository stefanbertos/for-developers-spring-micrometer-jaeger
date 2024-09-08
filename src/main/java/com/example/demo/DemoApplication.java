package com.example.demo;

import com.example.demo.service.solace.SolaceQueueProducer;
import com.solacesystems.jcsmp.JCSMPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class DemoApplication {
    @Autowired
    private SolaceQueueProducer solaceQueueProducer;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)  // 10 seconds delay, and prevent re-run
    public void runOnceAfterStartup() throws JCSMPException {
        solaceQueueProducer.sendMessageToQueue("Hello, World!");
    }
}
