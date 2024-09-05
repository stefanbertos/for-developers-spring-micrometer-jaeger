package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SomeService2 {
    public void process() {
        log.info("process2");
    }
}
