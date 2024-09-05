package com.example.demo;

import com.example.demo.service.SomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class DemoApplication {
    @Autowired
    private SomeService someService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent ignored) {
        System.out.println("Hello World!");
        someService.process();
    }

    @Bean(name = "propagatingContextExecutor")
    public TaskExecutor propagatingContextExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        // decorate task execution with a decorator that supports context propagation
        taskExecutor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        return taskExecutor;
    }
}
