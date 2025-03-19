package com.rj.ecommerce_backend.messaging.payment.listener;

import com.rj.ecommerce_backend.messaging.payment.dto.PaymentVerificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.VERIFICATION_RESPONSE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidationListener {

    @RabbitListener(queues = VERIFICATION_RESPONSE_QUEUE)
    public void handleVerificationResponse(PaymentVerificationResponseDTO response) {
        log.info("Received verification response for order: {}, status: {}",
                response.orderId(), response.status());
        // Handle payment verification response
    }
}
