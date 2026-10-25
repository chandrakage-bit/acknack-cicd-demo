package com.demo.acknack;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void validPaymentReturnsAck() throws Exception {
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content("""
                {"debtorBank":"ANZ","creditorBank":"NAB","amount":150.00,"currency":"AUD"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACK"));
    }

    @Test
    void badCurrencyReturnsNack() throws Exception {
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content("""
                {"debtorBank":"ANZ","creditorBank":"NAB","amount":150.00,"currency":"XYZ"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NACK"))
                .andExpect(jsonPath("$.reason").value("UNSUPPORTED_CURRENCY"));
    }

    @Test
    void malformedRequestReturns400() throws Exception {
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content("""
                {"debtorBank":"","creditorBank":"NAB","amount":-5,"currency":"AUD"}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownPaymentReturns404() throws Exception {
        mvc.perform(get("/api/payments/does-not-exist")).andExpect(status().isNotFound());
    }
}
