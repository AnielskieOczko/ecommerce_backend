package com.rj.ecommerce_backend.messaging.email.producer;

import com.rj.ecommerce_backend.messaging.common.producer.AbstractMessageProducer;
import com.rj.ecommerce_backend.messaging.email.dto.EmailNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.EMAIL_EXCHANGE;
import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.ROUTING_KEY;

@Component
@Slf4j
public class EmailMessageProducer extends AbstractMessageProducer {

    public EmailMessageProducer(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void sendEmail(EmailNotificationRequest emailRequest, String correlationId) {
        sendMessage(
                EMAIL_EXCHANGE,
                ROUTING_KEY,
                emailRequest,
                correlationId
        );
    }
}


