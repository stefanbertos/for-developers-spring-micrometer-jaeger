package com.example.demo;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.Observation.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ObservabilityAspect {

    private final ObservationRegistry observationRegistry;

    public ObservabilityAspect(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Around("execution(* com.example..*(..))")  // Adjust the package as needed
    public Object observeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        // Start an observation for the current method
        Observation observation = Observation.start(methodName, observationRegistry);

        // Open a new scope for this observation to ensure proper hierarchy
        try (Scope scope = observation.openScope()) {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            observation.error(throwable);
            throw throwable;
        } finally {
            // Stop the observation once the method execution is complete
            observation.stop();
        }
    }
}