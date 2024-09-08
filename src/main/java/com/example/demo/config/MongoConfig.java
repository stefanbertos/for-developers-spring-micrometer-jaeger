package com.example.demo.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.observability.ContextProviderFactory;
import org.springframework.data.mongodb.observability.MongoObservationCommandListener;

@Configuration
public class MongoConfig {
    @Bean
    MongoClientSettingsBuilderCustomizer mongoMetricsSynchronousContextProvider(ObservationRegistry registry) {
        return (clientSettingsBuilder) -> {
            clientSettingsBuilder.contextProvider(ContextProviderFactory.create(registry))
                    .addCommandListener(new MongoObservationCommandListener(registry));
        };
    }
}
