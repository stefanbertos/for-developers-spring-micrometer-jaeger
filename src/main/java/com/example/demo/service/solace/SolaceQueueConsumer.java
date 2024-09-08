package com.example.demo.service.solace;

import com.example.demo.service.AnotherService;
import com.solacesystems.jcsmp.*;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolaceQueueConsumer {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JCSMPSession jcsmpSession;
    private final Tracer tracer; // Inject the tracer
    private final AnotherService anotherService; // Inject the other service
    @Value("${solace.java.queueName}")
    private String queueName;

    @Async
    @EventListener
    public void startConsumer(ApplicationStartedEvent ignored) throws JCSMPException {
        // Lookup the queue
        Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        // Create consumer flow properties
        ConsumerFlowProperties flowProps = new ConsumerFlowProperties();
        flowProps.setEndpoint(queue);
        flowProps.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);  // Client acknowledges messages


        //get the instrumented id

        // Create a flow receiver with the given properties
        FlowReceiver flowReceiver = jcsmpSession.createFlow(new XMLMessageListener() {
            @SneakyThrows
            @Override
            public void onReceive(BytesXMLMessage msg) {
                var traceId = msg.getProperties().getString("traceId");
                var spanId = msg.getProperties().getString("spanId");


                if (traceId != null && spanId != null) {
                    // Create a new span with the extracted traceId and spanId

                    // Create a custom TextMap to simulate context propagation

                    // Create a new span
                    Span newSpan = tracer.nextSpan().name("custom-trace-span").start();



                    try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
                        var spanCustomizer = tracer.currentSpanCustomizer();
                        spanCustomizer.tag("traceId", traceId);

                        // Process the message
                        log.info("Received message from queue: " + queueName
                                + " [traceId: " + traceId + ", spanId: " + spanId + "]");
                        // applicationEventPublisher.publishEvent(new SensorReading("1", 1.24));
                        // Call the another service method with tracing context
                        anotherService.processMessage(msg.dump());
                    } finally {
                        newSpan.end();
                    }
                } else {
                    // Process the message without trace context
                    System.out.println("Received message from queue: " + queueName + " with content: " + msg.dump());
                }
                // Acknowledge the message
                msg.ackMessage();
            }

            @Override
            public void onException(JCSMPException e) {
                System.err.println("Consumer received exception: " + e.getMessage());
            }
        }, flowProps, null);

        // Start the flow receiver
        flowReceiver.start();
    }
}

