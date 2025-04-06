package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.email.dto.EmailNotificationRequest;
import com.rj.ecommerce_backend.messaging.email.producer.EmailMessageProducer;
import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionResponseDTO;
import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentNotificationService {

    private final EmailMessageProducer emailMessageProducer;

    public void sendPaymentNotification(CheckoutSessionResponseDTO response) {
        log.info("Sending payment notification for order: {}", response.orderId());

        PaymentStatus status = response.paymentStatus();

        switch (status) {
            case PAID, SUCCEEDED -> sendPaymentSuccessNotification(response);
            case UNPAID, FAILED -> sendPaymentFailureNotification(response);
            default -> log.warn("Unhandled payment status: {}", status);
        }
    }


    private void sendPaymentCancellationNotification(CheckoutSessionResponseDTO response) {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("orderId", response.orderId());
        if (response.amountTotal() != null) {
            emailData.put("amount", response.amountTotal());
        }
        if (response.currency() != null) {
            emailData.put("currency", response.currency());
        }

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .template("payment-canceled")
                .subject("Payment Canceled - Order #" + response.orderId())
                .data(emailData)
                .build();
        emailMessageProducer.sendEmail(emailRequest, response.orderId());
    }

    private void sendPaymentSuccessNotification(CheckoutSessionResponseDTO response) {
        // Extract amount and currency from metadata if available
        String amount = response.amountTotal() != null ? response.amountTotal().toString() : "";
        String currency = response.currency() != null ? response.currency() : "";
        String customerEmail = response.customerEmail() != null ? response.customerEmail() : "";

        // Build data map with optional receipt URL
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("orderId", response.orderId());
        emailData.put("amount", amount);
        emailData.put("currency", currency);
        emailData.put("customerEmail", customerEmail);

        // Add receipt URL if available
        if (response.additionalDetails() != null && response.additionalDetails().containsKey("receiptUrl")) {
            emailData.put("receiptUrl", response.additionalDetails().get("receiptUrl"));
        }

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .template("payment-confirmation")
                .subject("Payment Confirmed - Order #" + response.orderId())
                .data(emailData)
                .build();

        emailMessageProducer.sendEmail(emailRequest, response.orderId());
    }

    private void sendPaymentFailureNotification(CheckoutSessionResponseDTO response) {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("orderId", response.orderId());
        emailData.put("retryUrl", "https://yourstore.com/orders/" + response.orderId() + "/retry-payment");
        emailData.put("supportEmail", "support@yourstore.com");

        // Add any additional details that might be helpful
        if (response.additionalDetails() != null) {
            response.additionalDetails().forEach((key, value) -> {
                if (value != null) {
                    emailData.put(key, value);
                }
            });
        }

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .template("payment-failed-customer")
                .subject("Payment Failed - Order #" + response.orderId())
                .data(emailData)
                .build();

        emailMessageProducer.sendEmail(emailRequest, response.orderId());
    }

    public void sendPaymentErrorNotification(CheckoutSessionResponseDTO response, Exception e) {
        // Admin notification with technical details
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("orderId", response.orderId());
        adminData.put("errorMessage", e.getMessage() != null ? e.getMessage() : "Unknown error");
        adminData.put("sessionId", response.sessionId() != null ? response.sessionId() : "Unknown session");
        adminData.put("timestamp", LocalDateTime.now().toString());

        // Add any additional details that might help debugging
        if (response.additionalDetails() != null) {
            response.additionalDetails().forEach((key, value) -> {
                if (value != null) {
                    adminData.put(key, value);
                }
            });
        }

        EmailNotificationRequest adminNotification = EmailNotificationRequest.builder()
                .template("payment-error-admin")
                .subject("Payment Processing Error - Order #" + response.orderId())
                .data(adminData)
                .build();

        emailMessageProducer.sendEmail(adminNotification, response.orderId());

        // Customer notification with friendly message
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("orderId", response.orderId());
        customerData.put("supportEmail", "support@yourstore.com");
        customerData.put("helpUrl", "https://yourstore.com/payment-help");

        EmailNotificationRequest customerNotification = EmailNotificationRequest.builder()
                .template("payment-error-customer")
                .subject("Payment Processing Update - Order #" + response.orderId())
                .data(customerData)
                .build();

        emailMessageProducer.sendEmail(customerNotification, response.orderId());
    }


}
