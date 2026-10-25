package com.demo.acknack.events;

import com.demo.acknack.model.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Default publisher: just logs. Used when Kafka is not enabled. */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingEventPublisher implements PaymentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(PaymentResponse response) {
        log.info("PAYMENT_EVENT id={} status={} reason={}", response.paymentId(), response.status(), response.reason());
    }
}
