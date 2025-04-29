package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionRequestDTO;
import com.rj.ecommerce_backend.messaging.payment.producer.PaymentMessageProducer;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.order.service.OrderService;
import com.rj.ecommerce_backend.payment.dto.CheckoutSessionDTO;
import com.rj.ecommerce_backend.payment.dto.PaymentStatusDTO;
import com.rj.ecommerce_backend.securityconfig.SecurityContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService {


    private final OrderService orderService;
    private final PaymentMessageProducer paymentMessageProducer;
    private final SecurityContext securityContext;


    @Transactional
    public PaymentStatusDTO getOrderPaymentStatus(Long orderId) {

        Long userId = securityContext.getCurrentUser().getId();

        Order order = orderService.getOrderById(userId, orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return PaymentStatusDTO.builder()
                .orderId(order.getId())
                .paymentStatus(order.getPaymentStatus())
                .lastUpdated(order.getUpdatedAt())
                .build();
    }

    @Transactional
    public CheckoutSessionDTO createOrGetCheckoutSession(
            Long userId,
            Long orderId,
            String successUrl,
            String cancelUrl) {

        Order order = orderService.getOrderByIdWithOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Verify the order belongs to the user
        if (!order.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to access order {} belonging to user {}",
                    userId, orderId, order.getUser().getId());
            throw new OrderNotFoundException(orderId);
        }

        // If there's an existing valid session, return it
        if (isValidCheckoutSession(order)) {
            return CheckoutSessionDTO.builder()
                    .orderId(order.getId())
                    .paymentStatus(order.getPaymentStatus())
                    .sessionId(order.getPaymentTransactionId())
                    .sessionUrl(order.getCheckoutSessionUrl())
                    .expiresAt(order.getCheckoutSessionExpiresAt())
                    .build();
        }

        // Create new checkout session
        CheckoutSessionRequestDTO checkoutRequest = buildCheckoutRequest(userId, order, successUrl, cancelUrl);
        paymentMessageProducer.sendCheckoutSessionRequest(checkoutRequest, order.getId().toString());
        orderService.updatePaymentDetailsOnInitiation(order);

        return CheckoutSessionDTO.builder()
                .orderId(order.getId())
                .paymentStatus(order.getPaymentStatus())
                .sessionId(order.getPaymentTransactionId())
                .sessionUrl(order.getCheckoutSessionUrl())
                .build();
    }

    private boolean isValidCheckoutSession(Order order) {
        // First check if basic session data exists
        // payment transaction ID = Stripe session ID
        if (order.getPaymentTransactionId() == null ||
                order.getCheckoutSessionUrl() == null ||
                order.getPaymentStatus() == PaymentStatus.FAILED) {
            return false;
        }

        // Then check expiration
        if (order.getCheckoutSessionExpiresAt() != null) {
            return order.getCheckoutSessionExpiresAt().isBefore(LocalDateTime.now());
        }

        return true;
    }

    private CheckoutSessionRequestDTO buildCheckoutRequest(
            Long userId,
            Order order,
            String successUrl,
            String cancelUrl) {

        // Create metadata
        Map<String, String> metadata = Map.of(
                "orderId", order.getId().toString(),
                "userId", userId.toString(),
                "customerEmail", order.getUser().getEmail().value()
        );

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
                // Use Collectors.toCollection(ArrayList::new) instead of .toList()
                .collect(Collectors.toCollection(ArrayList::new)); // <<<--- MODIFIED LINE

        // Create checkout session request
        return CheckoutSessionRequestDTO.builder()
                .orderId(order.getId().toString())
                .customerEmail(order.getUser().getEmail().value())
                .successUrl(successUrl)
                .cancelUrl(cancelUrl)
                .lineItems(lineItems)
//                .currency(currency)
                .metadata(metadata)
                .build();
    }
}
