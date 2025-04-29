package com.rj.ecommerce_backend.messaging.email.listener;

import com.rj.ecommerce_backend.messaging.email.contract.v1.notification.EmailDeliveryStatusDTO;
import com.rj.ecommerce_backend.notification.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.EMAIL_NOTIFICATION_QUEUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailStatusListener {

    private final EmailNotificationService emailNotificationService;

    @RabbitListener(queues = EMAIL_NOTIFICATION_QUEUE)
    public void handleEmailStatus(EmailDeliveryStatusDTO status) {
        log.info("Received email status: {} for message: {}",
                status.status(), status.originalMessageId());

        // Update notification status in the database
        emailNotificationService.updateEmailStatus(
                status.originalMessageId(),
                status.status().name(),
                status.errorMessage()
        );
    }
}
