package com.rj.ecommerce_backend.payment.service;


import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionResponseDTO;
import com.rj.ecommerce_backend.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final OrderService orderService;
    private final PaymentNotificationService paymentNotificationService;

    @Transactional
    public void processCheckoutSessionResponse(CheckoutSessionResponseDTO response) {
        try {
            // Update order status
            orderService.updateOrderWithCheckoutSession(response);

            // Handle notifications based on payment status
            paymentNotificationService.sendPaymentNotification(response);

        } catch (Exception e) {
            log.error("Payment processing failed for order {}", response.orderId(), e);
            handleProcessingError(response, e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }

    @Transactional
    private void handleProcessingError(CheckoutSessionResponseDTO response, Exception e) {
        try {
            orderService.updateOrderWithCheckoutSession(response);
            paymentNotificationService.sendPaymentErrorNotification(response, e);
        } catch (Exception notificationError) {
            log.error("Failed to handle payment processing error for order {}",
                    response.orderId(), notificationError);
        }
    }


}
