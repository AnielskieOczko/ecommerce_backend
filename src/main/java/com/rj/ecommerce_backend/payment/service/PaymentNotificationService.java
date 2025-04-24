package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.email.EmailServiceClient;
import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.payment.PaymentEmailRequestDTO;
import com.rj.ecommerce_backend.messaging.email.producer.EmailMessageProducer;
import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionResponseDTO;
import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.rj.ecommerce_backend.messaging.email.contract.MessageVersioning.CURRENT_VERSION;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentNotificationService {


    private final EmailServiceClient emailServiceClient;

    public void sendPaymentNotification(CheckoutSessionResponseDTO response) {
        log.info("Sending payment notification for order: {}", response.orderId());

        PaymentStatus status = response.paymentStatus();

        switch (status) {
            case PAID, SUCCEEDED -> sendPaymentSuccessNotification(response);
            case UNPAID, FAILED -> sendPaymentFailureNotification(response);
            default -> log.warn("Unhandled payment status: {}", status);
        }
    }

    private void sendPaymentSuccessNotification(CheckoutSessionResponseDTO response) {
        log.info("Sending payment success notification for order: {}", response.orderId());

        // Create payment success email request
        PaymentEmailRequestDTO emailRequest = PaymentEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version(CURRENT_VERSION)
                .to(response.customerEmail())
                .template(EmailTemplate.PAYMENT_CONFIRMATION)
                .orderId(response.orderId())
                .paymentId(response.sessionId())
                .paymentStatus("SUCCEEDED")
                .paymentAmount(createMoneyDTO(response))
                .additionalData(createAdditionalData(response))
                .timestamp(LocalDateTime.now())
                .build();

        // Send email request
        emailServiceClient.sendEmailRequest(emailRequest);
    }

    private void sendPaymentFailureNotification(CheckoutSessionResponseDTO response) {
        log.info("Sending payment failure notification for order: {}", response.orderId());

        // Create payment failure email request
        PaymentEmailRequestDTO emailRequest = PaymentEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version(CURRENT_VERSION)
                .to(response.customerEmail())
                .template(EmailTemplate.PAYMENT_FAILED)
                .orderId(response.orderId())
                .paymentId(response.sessionId())
                .paymentStatus("FAILED")
                .paymentAmount(createMoneyDTO(response))
                .additionalData(createAdditionalData(response, Map.of(
                        "retryUrl", response.checkoutUrl(),
                        "supportEmail", "support@yourstore.com"
                )))
                .timestamp(LocalDateTime.now())
                .build();

        // Send email request
        emailServiceClient.sendEmailRequest(emailRequest);
    }

    public void sendPaymentErrorNotification(CheckoutSessionResponseDTO response, Exception e) {
        log.info("Sending payment error notifications for order: {}", response.orderId());

        // Admin notification
        PaymentEmailRequestDTO adminNotification = PaymentEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version(CURRENT_VERSION)
                .to("admin@yourstore.com")
                .template(EmailTemplate.PAYMENT_ERROR_ADMIN)
                .orderId(response.orderId())
                .paymentId(response.sessionId())
                .paymentStatus("ERROR")
                .paymentAmount(createMoneyDTO(response))
                .additionalData(createAdditionalData(response, Map.of(
                        "errorMessage", e.getMessage() != null ? e.getMessage() : "Unknown error",
                        "timestamp", LocalDateTime.now().toString()
                )))
                .timestamp(LocalDateTime.now())
                .build();

        // Customer notification
        PaymentEmailRequestDTO customerNotification = PaymentEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version(CURRENT_VERSION)
                .to(response.customerEmail())
                .template(EmailTemplate.PAYMENT_ERROR_CUSTOMER)
                .orderId(response.orderId())
                .paymentId(response.sessionId())
                .paymentStatus("ERROR")
                .paymentAmount(createMoneyDTO(response))
                .additionalData(createAdditionalData(response, Map.of(
                        "supportEmail", "support@yourstore.com",
                        "helpUrl", "https://yourstore.com/payment-help"
                )))
                .timestamp(LocalDateTime.now())
                .build();

        // Send email requests
        emailServiceClient.sendEmailRequest(adminNotification);
        emailServiceClient.sendEmailRequest(customerNotification);
    }

    // Helper methods

    private MoneyDTO createMoneyDTO(CheckoutSessionResponseDTO response) {
        if (response.amountTotal() == null) {
            return null;
        }

        return MoneyDTO.builder()
                .amount(new BigDecimal(response.amountTotal()))
                .currencyCode(response.currency() != null ? response.currency() : "USD")
                .build();
    }

    private Map<String, Object> createAdditionalData(CheckoutSessionResponseDTO response) {
        return createAdditionalData(response, Map.of());
    }

    private Map<String, Object> createAdditionalData(CheckoutSessionResponseDTO response, Map<String, Object> extraData) {
        Map<String, Object> data = new HashMap<>();

        // Add basic data
        data.put("orderId", response.orderId());

        // Add receipt URL if available
        if (response.additionalDetails() != null && response.additionalDetails().containsKey("receiptUrl")) {
            data.put("receiptUrl", response.additionalDetails().get("receiptUrl"));
        }

        // Add any additional details from the response
        if (response.additionalDetails() != null) {
            response.additionalDetails().forEach((key, value) -> {
                if (value != null) {
                    data.put(key, value);
                }
            });
        }

        // Add extra data provided by the caller
        data.putAll(extraData);

        return data;
    }
}