package com.rj.ecommerce_backend.messaging.email.listener;

import com.rj.ecommerce_backend.messaging.email.config.RabbitMQConfig;
import com.rj.ecommerce_backend.messaging.email.dto.EmailNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationListener {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleEmailNotification(EmailNotification notification) {
        log.info("Received email notification for order: {}, status: {}",
                notification.orderId(), notification.status());

        // Handle the notification (update order status, notify user, etc.)
    }
}
