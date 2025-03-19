package com.rj.ecommerce_backend.messaging.common.producer;

import com.rj.ecommerce_backend.messaging.common.excepion.MessagePublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageProducer {
    protected final RabbitTemplate rabbitTemplate;

    public <T> void sendMessage(String exchange, String routingKey, T message, String correlationId) {
        try {
            MessagePostProcessor messagePostProcessor = msg -> {
                if (correlationId != null && !correlationId.isEmpty()) {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                }
                return msg;
            };

            rabbitTemplate.convertAndSend(exchange, routingKey, message, messagePostProcessor);
            log.info("Sent message to exchange: {}, routing key: {}, message: {}", exchange, routingKey, message);
        } catch (Exception e) {
            log.error("Failed to send message to exchange: {}, routing key: {}, message: {}", exchange, routingKey, message, e);
            throw new MessagePublishException("Failed to publish message", e);
        }

    }
}
