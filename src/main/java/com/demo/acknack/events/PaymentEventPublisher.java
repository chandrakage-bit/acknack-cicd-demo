package com.demo.acknack.events;

import com.demo.acknack.model.PaymentResponse;

@FunctionalInterface
public interface PaymentEventPublisher {
    void publish(PaymentResponse response);
}
