package com.example.demo.service.solace;

import brave.Tracing;
import com.example.demo.dto.SensorReading;
import com.example.demo.service.ProcessingSensorEvent;
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
import brave.propagation.TraceContext;
@Slf4j
@Service
@RequiredArgsConstructor
public class SolaceQueueConsumer {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JCSMPSession jcsmpSession;
    private final Tracer tracer; // Inject the tracer
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
                // Check the type of message received

                log.info("Received message: " + msg.dump());

                var traceId = msg.getProperties().getString("traceId");
                var spanId = msg.getProperties().getString("spanId");
// Update the tracing context

                if (traceId != null && spanId != null) {
                    // Create a new span with the extracted traceId and spanId
                    Span span = tracer.nextSpan()
                            .name("SolaceQueueConsumer")
                            .tag("traceId", traceId)
                            .tag("spanId", spanId)
                            .start();
                    try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
                        // Process the message
                        System.out.println("Received message from queue: " + queueName + " with content: " + msg.dump()
                                + " [traceId: " + traceId + ", spanId: " + spanId + "]");
                        applicationEventPublisher.publishEvent(new SensorReading("1", 1.24));


                    } finally {
                        span.end();
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

