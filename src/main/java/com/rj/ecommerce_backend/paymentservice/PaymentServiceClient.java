package com.rj.ecommerce_backend.paymentservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${payment.service.url}")
    private final String paymentServiceUrl;

    public PaymentIntentDTO createPaymentIntent(PaymentRequestDTO paymentRequestDTO) {
        log.info("Creating payment intent for order");
        try {
            return restTemplate.postForObject(
                    paymentServiceUrl + "/api/v1/payments/payment-intent",
                    paymentRequestDTO,
                    PaymentIntentDTO.class);
        } catch (Exception e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw new PaymentServiceException("Error creating payment intent", e);
        }

    }

    public PaymentIntentDTO getPaymentStatus(String paymentIntentId) {
        log.info("Getting payment status for payment intent: {}", paymentIntentId);
        try {
            return restTemplate.getForObject(
                    paymentServiceUrl + "/api/v1/payments/payment-intent/" + paymentIntentId,
                    PaymentIntentDTO.class);
        } catch (Exception e) {
            log.error("Error getting payment status: {}", e.getMessage());
            throw new PaymentServiceException("Error getting payment status", e);
        }
    }
}
