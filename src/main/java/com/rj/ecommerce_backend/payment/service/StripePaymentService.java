package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionRequestDTO;
import com.rj.ecommerce_backend.messaging.payment.producer.PaymentMessageProducer;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.order.service.OrderService;
import com.rj.ecommerce_backend.payment.service.dto.CheckoutSessionDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService {


    private final OrderService orderService;
    private final PaymentMessageProducer paymentMessageProducer;

    @Transactional()
    public CheckoutSessionDTO getCheckoutSessionForOrder(Long userId, Long orderId) {
        // Retrieve order and validate ownership
        Order order = orderService.getOrderById(userId, orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Create DTO from order data
        return CheckoutSessionDTO.builder()
                .orderId(order.getId())
                .paymentStatus(order.getPaymentStatus())
                .sessionId(order.getPaymentTransactionId())
                .sessionUrl(order.getCheckoutSessionUrl()) // Using the stored session URL
                .build();
    }

    @Transactional
    public void createCheckoutSessionForOrder(Long userId, Long orderId, String successUrl, String cancelUrl) {
        // Retrieve order and validate ownership
        Order order = orderService.getOrderById(userId, orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Get the currency from the order
        String currency = String.valueOf(order.getCurrency());

        // Get order items and convert to line items
        List<CheckoutSessionRequestDTO.CheckoutLineItemDTO> lineItems = order.getOrderItems().stream()
                .map(item -> CheckoutSessionRequestDTO.CheckoutLineItemDTO.builder()
                        .name(item.getProduct().getProductName().value())
                        .description("Product ID: " + item.getProduct().getId())
                        .unitAmount(item.getPrice().longValue())
                        .quantity(item.getQuantity())
                        .currency(currency)
                        .build())
                .toList();

        // Create metadata
        Map<String, String> metadata = Map.of(
                "orderId", order.getId().toString(),
                "userId", userId.toString(),
                "customerEmail", order.getUser().getEmail().value()
        );

        // Create checkout session request
        CheckoutSessionRequestDTO checkoutRequest = CheckoutSessionRequestDTO.builder()
                .orderId(order.getId().toString())
                .customerEmail(order.getUser().getEmail().value())
                .successUrl(successUrl)
                .cancelUrl(cancelUrl)
                .lineItems(lineItems)
                .currency(currency)
                .metadata(metadata)
                .build();

        // Send checkout session request
        paymentMessageProducer.sendCheckoutSessionRequest(checkoutRequest, order.getId().toString());

        // Update order status
        orderService.updatePaymentDetailsOnInitiation(order);
    }

}
