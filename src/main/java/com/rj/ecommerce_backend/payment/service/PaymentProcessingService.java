package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.payment.dto.PaymentIntentResponseDTO;
import com.rj.ecommerce_backend.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final OrderService orderService;
    private final PaymentNotificationService paymentNotificationService;

    @Transactional
    public void processPaymentResponse(PaymentIntentResponseDTO response) {
        try {
            // Update order status
            orderService.updateOrderPaymentDetails(response);

            // Handle notifications based on payment status
            paymentNotificationService.sendPaymentNotification(response);

        } catch (Exception e) {
            log.error("Payment processing failed for order {}", response.orderId(), e);
            handleProcessingError(response, e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }

    private void handleProcessingError(PaymentIntentResponseDTO response, Exception e) {
        try {
            orderService.updateOrderPaymentDetails(response);
            paymentNotificationService.sendPaymentErrorNotification(response, e);
        } catch (Exception notificationError) {
            log.error("Failed to handle payment processing error for order {}",
                    response.orderId(), notificationError);
        }
    }
}
