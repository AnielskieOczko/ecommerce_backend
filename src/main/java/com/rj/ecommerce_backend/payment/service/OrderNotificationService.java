package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.email.dto.EmailNotificationRequest;
import com.rj.ecommerce_backend.messaging.email.producer.EmailMessageProducer;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationService {
    // handles order notifications
    private final EmailMessageProducer emailMessageProducer;

    public void sendOrderConfirmationEmail(OrderDTO order) {
        log.info("Sending order confirmation email for order ID: {}", order.id());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("order", order);
        templateData.put("estimatedDeliveryDate", calculateEstimatedDeliveryDate(order));

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .to(order.email())
                .subject("Order Confirmation - Order #" + order.id())
                .template("order-confirmation")
                .data(templateData)
                .build();

        emailMessageProducer.sendEmail(emailRequest, order.id().toString());
    }

    public void sendShipmentNotificationEmail(OrderDTO order, String trackingNumber, String trackingUrl) {
        log.info("Sending shipment notification email for order ID: {}", order.id());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("order", order);
        templateData.put("trackingNumber", trackingNumber);
        templateData.put("trackingUrl", trackingUrl);
        templateData.put("estimatedDeliveryDate", calculateEstimatedDeliveryDate(order));

        EmailNotificationRequest emailRequest = EmailNotificationRequest.builder()
                .to(order.email())
                .subject("Your Order #" + order.id() + " Has Been Shipped!")
                .template("order-shipment")
                .data(templateData)
                .build();

        emailMessageProducer.sendEmail(emailRequest, order.id().toString());
    }

    private LocalDateTime calculateEstimatedDeliveryDate(OrderDTO order) {
        // TODO: Basic estimation logic, should be changed later
        return switch (order.shippingMethod()) {
            case DHL -> order.orderDate().plusDays(2);
            case INPOST -> order.orderDate().plusDays(1);
        };
    }
}
