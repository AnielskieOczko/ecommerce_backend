package com.rj.ecommerce_backend.messaging.payment.listener;

import com.rj.ecommerce_backend.messaging.payment.dto.PaymentIntentResponseDTO;
import com.rj.ecommerce_backend.payment.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.PAYMENT_RESPONSE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseListener {

    private final PaymentProcessingService paymentProcessingService;

    @RabbitListener(queues = PAYMENT_RESPONSE_QUEUE)
    public void handlePaymentResponse(PaymentIntentResponseDTO response) {
        log.info("Received payment intent response for order: {}, event: {}",
                response.orderId(), response.status());

        try {
            paymentProcessingService.processPaymentResponse(response);
        } catch (Exception e) {
            log.error("Error processing payment response for order {}: {}",
                    response.orderId(), e.getMessage(), e);
            // Consider adding a dead letter queue handling here
        }
    }
}
