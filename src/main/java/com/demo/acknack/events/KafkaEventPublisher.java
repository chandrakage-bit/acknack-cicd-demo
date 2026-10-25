package com.demo.acknack.events;

import com.demo.acknack.model.PaymentResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Publishes ACK/NACK events to Kafka when app.kafka.enabled=true. */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaEventPublisher implements PaymentEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;
    private final String topic;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper,
                               @Value("${app.kafka.topic:payment-acknack}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }

    @Override
    public void publish(PaymentResponse response) {
        try {
            kafkaTemplate.send(topic, response.paymentId(), mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialise payment event", e);
        }
    }
}
