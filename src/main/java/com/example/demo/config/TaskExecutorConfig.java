package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

@Configuration
public class TaskExecutorConfig {
    @Bean(name = "propagatingContextExecutor")
    public TaskExecutor propagatingContextExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        // decorate task execution with a decorator that supports context propagation which is needed for observability
        taskExecutor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        return taskExecutor;
    }
}
