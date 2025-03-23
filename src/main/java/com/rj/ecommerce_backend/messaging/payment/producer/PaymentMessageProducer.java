package com.rj.ecommerce_backend.messaging.payment.producer;

import com.rj.ecommerce_backend.messaging.common.producer.AbstractMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.*;

@Slf4j
@Component
public class PaymentMessageProducer extends AbstractMessageProducer {

    public PaymentMessageProducer(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public <T> void sendPaymentIntentRequest(T request, String correlationId) {
        sendMessage(
                PAYMENT_INTENT_EXCHANGE,
                PAYMENT_INTENT_ROUTING_KEY,
                request,
                correlationId
        );
    }

    public <T> void sendPaymentVerificationRequest(T request, String correlationId) {
        sendMessage(
                PAYMENT_VERIFICATION_EXCHANGE,
                PAYMENT_VERIFICATION_ROUTING_KEY,
                request,
                correlationId
        );
    }
}
