package com.rj.ecommerce_backend.messaging.email;

import com.rj.ecommerce_backend.messaging.email.contract.v1.EcommerceEmailRequest;
import com.rj.ecommerce_backend.messaging.email.contract.v1.order.OrderEmailRequestDTO;
import com.rj.ecommerce_backend.messaging.email.producer.EmailMessageProducer;
import com.rj.ecommerce_backend.notification.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailServiceClient {

    private final EmailNotificationService emailNotificationService;
    private final EmailMessageProducer emailMessageProducer;

    /**
     * Send an email request to the email service
     */
    public void sendEmailRequest(EcommerceEmailRequest request) {
        log.info("Sending email request: {}", request.getMessageId());

        // Record the email being sent
        String entityType = determineEntityType(request);
        String entityId = extractEntityId(request);

        emailNotificationService.recordEmailSent(
                request.getMessageId(),
                request.getTo(),
                request.getSubject(),
                request.getTemplate().name(),
                entityType,
                entityId
        );

        // Send to email service
        emailMessageProducer.sendEmail(request, request.getMessageId());


        log.info("Email request sent: {}", request.getMessageId());
    }

    private String determineEntityType(EcommerceEmailRequest request) {
        if (request instanceof OrderEmailRequestDTO) {
            return "ORDER";
        }
        return "UNKNOWN";
    }

    private String extractEntityId(EcommerceEmailRequest request) {
        if (request instanceof OrderEmailRequestDTO orderRequest) {
            return orderRequest.orderId();
        }
        return null;
    }
}
