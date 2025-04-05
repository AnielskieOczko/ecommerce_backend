package com.rj.ecommerce_backend.messaging.payment.producer;

import com.rj.ecommerce_backend.messaging.common.producer.AbstractMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.CHECKOUT_SESSION_EXCHANGE;
import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.CHECKOUT_SESSION_ROUTING_KEY;

@Slf4j
@Component
public class PaymentMessageProducer extends AbstractMessageProducer {

    public PaymentMessageProducer(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public <T> void sendCheckoutSessionRequest(T request, String correlationId) {
        sendMessage(
                CHECKOUT_SESSION_EXCHANGE,
                CHECKOUT_SESSION_ROUTING_KEY,
                request,
                correlationId
        );
    }
}
