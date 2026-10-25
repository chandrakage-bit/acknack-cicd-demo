package com.demo.acknack.service;

import com.demo.acknack.events.PaymentEventPublisher;
import com.demo.acknack.model.PaymentRequest;
import com.demo.acknack.model.PaymentResponse;
import com.demo.acknack.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    static final Set<String> SUPPORTED_CURRENCIES = Set.of("AUD", "USD", "EUR", "GBP", "INR");
    static final BigDecimal LIMIT = new BigDecimal("1000000");

    private final Map<String, PaymentResponse> store = new ConcurrentHashMap<>();
    private final PaymentEventPublisher publisher;

    public PaymentService(PaymentEventPublisher publisher) {
        this.publisher = publisher;
    }

    public PaymentResponse process(PaymentRequest request) {
        String reason = validate(request);
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID().toString(),
                reason == null ? PaymentStatus.ACK : PaymentStatus.NACK,
                reason,
                Instant.now());
        store.put(response.paymentId(), response);
        publisher.publish(response);
        return response;
    }

    public Optional<PaymentResponse> find(String paymentId) {
        return Optional.ofNullable(store.get(paymentId));
    }

    /** Returns a NACK reason code, or null when the payment is acceptable. */
    private String validate(PaymentRequest r) {
        if (!SUPPORTED_CURRENCIES.contains(r.currency().toUpperCase())) {
            return "UNSUPPORTED_CURRENCY";
        }
        if (r.amount().compareTo(LIMIT) > 0) {
            return "LIMIT_EXCEEDED";
        }
        if (r.debtorBank().equalsIgnoreCase(r.creditorBank())) {
            return "SAME_BANK_TRANSFER";
        }
        return null;
    }
}
