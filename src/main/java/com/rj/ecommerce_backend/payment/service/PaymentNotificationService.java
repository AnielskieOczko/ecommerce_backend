package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.email.dto.EmailNotificationRequest;
import com.rj.ecommerce_backend.messaging.email.producer.EmailMessageProducer;
import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionResponseDTO;
import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentNotificationService {

    private final EmailMessageProducer emailMessageProducer;

    public void sendPaymentNotification(CheckoutSessionResponseDTO response) {
        log.info("Sending payment notification for order: {}", response.orderId());

        PaymentStatus status = PaymentStatus.fromStripeEvent(response.status());

        switch (status) {
            case SUCCEEDED -> sendPaymentSuccessNotification(response);
            case FAILED -> sendPaymentFailureNotification(response);
            case CANCELED -> sendPaymentCancellationNotification(response);
            default -> log.warn("Unhandled payment status: {}", status);
        }
    }


    private void sendPaymentCancellationNotification(CheckoutSessionResponseDTO response) {
        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .template("payment-canceled")
                .subject("Payment Canceled - Order #" + response.orderId())
                .data(Map.of(
                        "orderId", response.orderId(),
                        "amount", response.amountTotal(),
                        "currency", response.currency()
                ))
                .build();
        emailMessageProducer.sendEmail(emailRequest, response.orderId());
    }

    private void sendPaymentSuccessNotification(CheckoutSessionResponseDTO response) {
        // Extract amount and currency from metadata if available
        String amount = response.amountTotal().toString();
        String currency = response.currency();
        String customerEmail = response.customerEmail();

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .template("payment-confirmation")
                .subject("Payment Confirmed - Order #" + response.orderId())
                .data(Map.of(
                        "orderId", response.orderId(),
                        "amount", amount,
                        "currency", currency,
                        "customerEmail", customerEmail
                ))
                .build();

        emailMessageProducer.sendEmail(emailRequest, response.orderId());
    }

    private void sendPaymentFailureNotification(CheckoutSessionResponseDTO response) {
        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .template("payment-failed-customer")
                .subject("Payment Failed - Order #" + response.orderId())
                .data(Map.of(
                        "orderId", response.orderId(),
                        "retryUrl", "https://yourstore.com/orders/" + response.orderId() + "/retry-payment",
                        "supportEmail", "support@yourstore.com"
                ))
                .build();

        emailMessageProducer.sendEmail(emailRequest, response.orderId());
    }

    public void sendPaymentErrorNotification(CheckoutSessionResponseDTO response, Exception e) {
        // Admin notification with technical details
        EmailNotificationRequest adminNotification = EmailNotificationRequest.builder()
                .template("payment-error-admin")
                .subject("Payment Processing Error - Order #" + response.orderId())
                .data(Map.of(
                        "orderId", response.orderId(),
                        "errorMessage", e.getMessage(),
                        "sessionId", response.sessionId(),
                        "timestamp", LocalDateTime.now()
                ))
                .build();

        emailMessageProducer.sendEmail(adminNotification, response.orderId());

        // Customer notification with friendly message
        EmailNotificationRequest customerNotification = EmailNotificationRequest.builder()
                .template("payment-error-customer")
                .subject("Payment Processing Update - Order #" + response.orderId())
                .data(Map.of(
                        "orderId", response.orderId(),
                        "supportEmail", "support@yourstore.com",
                        "helpUrl", "https://yourstore.com/payment-help"
                ))
                .build();

        emailMessageProducer.sendEmail(customerNotification, response.orderId());
    }


}
