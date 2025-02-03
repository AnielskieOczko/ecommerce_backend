package com.rj.ecommerce_backend.messaging.email.producer;

import com.rj.ecommerce_backend.messaging.email.excepion.EmailSendingException;
import com.rj.ecommerce_backend.messaging.email.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.email.config.RabbitMQConfig.EMAIL_EXCHANGE;
import static com.rj.ecommerce_backend.messaging.email.config.RabbitMQConfig.ROUTING_KEY;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmail(EmailRequest emailRequest) {
        try {
            log.info("Sending email request to queue. Template: {}, To: {}",
                    emailRequest.template(), emailRequest.to());

            rabbitTemplate.convertAndSend(
                    EMAIL_EXCHANGE,
                    ROUTING_KEY,
                    emailRequest
            );

            log.debug("Email request sent successfully to queue. Template: {}",
                    emailRequest.template());

        } catch (AmqpException e) {
            log.error("Failed to send email request to queue. Template: {}, Error: {}",
                    emailRequest.template(), e.getMessage(), e);
            throw new EmailSendingException("Failed to queue email request", e);
        }
    }
}
