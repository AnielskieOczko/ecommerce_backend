package com.rj.ecommerce_backend.messaging.email.listener;

import com.rj.ecommerce_backend.messaging.email.dto.EmailNotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rj.ecommerce_backend.messaging.config.RabbitMQConfig.EMAIL_NOTIFICATION_QUEUE;

@Component
@Slf4j
public class EmailNotificationListener {

    @RabbitListener(queues = EMAIL_NOTIFICATION_QUEUE)
    public void handleEmailNotification(EmailNotificationResponse notification) {
        log.info("Received email notification for order: {}, status: {}",
                notification.orderId(), notification.status());

        // Handle the notification (update order status, notify user, etc.)
    }
}
