package com.demo.acknack.model;

import java.time.Instant;

public record PaymentResponse(String paymentId, PaymentStatus status, String reason, Instant processedAt) {
}
