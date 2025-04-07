package com.rj.ecommerce_backend.messaging.payment.listener;

import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionResponseDTO;
import com.rj.ecommerce_backend.payment.service.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.CHECKOUT_SESSION_RESPONSE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckoutSessionResponseListener {

    private final PaymentWebhookService paymentWebhookService;

    @RabbitListener(queues = CHECKOUT_SESSION_RESPONSE_QUEUE)
    public void handleCheckoutSessionResponse(CheckoutSessionResponseDTO response) {
        log.info("Received checkout session response for order: {}, session status: {}, payment status: {}",
                response.orderId(), response.sessionStatus(), response.paymentStatus());

        try {
            // Process the checkout session response directly
            paymentWebhookService.processCheckoutSessionResponse(response);
        } catch (Exception e) {
            log.error("Error processing checkout session response for order {}: {}",
                    response.orderId(), e.getMessage(), e);
            // Consider adding a dead letter queue handling here
        }
    }
}
