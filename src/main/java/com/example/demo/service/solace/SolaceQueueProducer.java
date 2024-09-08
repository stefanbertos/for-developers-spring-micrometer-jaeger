package com.example.demo.service.solace;

import com.solacesystems.common.util.ByteArray;
import com.solacesystems.jcsmp.*;
import com.solacesystems.jcsmp.impl.sdt.MapImpl;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class SolaceQueueProducer {

    private final JCSMPSession jcsmpSession;
    private final Tracer tracer; // Inject the tracer

    @Value("${solace.java.queueName}")
    private String queueName;


    public void sendMessageToQueue(String messageContent) throws JCSMPException {
        try {
            // Lookup or create the queue
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

            // Create a producer with a streaming publish event handler for acknowledgments and errors
            XMLMessageProducer producer = jcsmpSession.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
                @Override
                public void responseReceived(String messageID) {
                    // Acknowledgment callback for successful publish
                    log.info("Message acknowledged by Solace with ID: " + messageID);
                }

                @Override
                public void handleError(String messageID, JCSMPException cause, long timestamp) {
                    // Handle any errors that occur during publishing
                    log.error("Error publishing message with ID: " + messageID + ". Cause: " + cause.getMessage());
                }
            });

            // Create a text message
            TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
            message.setText(messageContent);


            // Add tracing information to the message
            String traceId = tracer.currentSpan().context().traceId();
            String spanId = tracer.currentSpan().context().spanId();
            // Set custom headers with traceId and spanId

            SDTMap messageProperties = new MapImpl();
            messageProperties.putString("traceId", traceId);
            messageProperties.putString("spanId", spanId);
            message.setProperties(messageProperties);

            // Send the message to the queue in non-blocking mode
            producer.send(message, queue);

           log.info("Message sent to queue: " + queueName + " with content: " + messageContent
                    + " [traceId: " + traceId + ", spanId: " + spanId + "]");

        } catch (JCSMPException e) {
           log.error("Error sending message to queue: " + e.getMessage());

        }
    }
}