package com.demo.acknack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demo.acknack.model.PaymentRequest;
import com.demo.acknack.model.PaymentResponse;
import com.demo.acknack.model.PaymentStatus;
import com.demo.acknack.service.PaymentService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {

    private final List<PaymentResponse> published = new ArrayList<>();
    private final PaymentService service = new PaymentService(published::add);

    @Test
    void validPaymentIsAcked() {
        PaymentResponse r = service.process(new PaymentRequest("ANZ", "NAB", new BigDecimal("150.00"), "AUD"));
        assertEquals(PaymentStatus.ACK, r.status());
        assertEquals(1, published.size());
    }

    @Test
    void unsupportedCurrencyIsNacked() {
        PaymentResponse r = service.process(new PaymentRequest("ANZ", "NAB", BigDecimal.TEN, "XYZ"));
        assertEquals(PaymentStatus.NACK, r.status());
        assertEquals("UNSUPPORTED_CURRENCY", r.reason());
    }

    @Test
    void amountOverLimitIsNacked() {
        PaymentResponse r = service.process(new PaymentRequest("ANZ", "NAB", new BigDecimal("1000000.01"), "USD"));
        assertEquals("LIMIT_EXCEEDED", r.reason());
    }

    @Test
    void sameBankIsNacked() {
        PaymentResponse r = service.process(new PaymentRequest("ANZ", "anz", BigDecimal.TEN, "AUD"));
        assertEquals("SAME_BANK_TRANSFER", r.reason());
    }

    @Test
    void processedPaymentCanBeFound() {
        PaymentResponse r = service.process(new PaymentRequest("ANZ", "NAB", BigDecimal.TEN, "AUD"));
        assertTrue(service.find(r.paymentId()).isPresent());
    }
}
