package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.payment.dto.CheckoutSessionDTO;
import com.rj.ecommerce_backend.payment.dto.PaymentStatusDTO;
import com.rj.ecommerce_backend.securityconfig.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripePaymentController {

    private final SecurityContext securityContext;
    private final StripePaymentService stripePaymentService;

    @GetMapping("/checkout/session/{orderId}")
    public ResponseEntity<PaymentStatusDTO> getPaymentStatus(@PathVariable Long orderId) {
        try {
            log.info("Getting payment status for order: {}", orderId);
            stripePaymentService.getOrderPaymentStatus(orderId);
            return ResponseEntity.ok(stripePaymentService.getOrderPaymentStatus(orderId));
        } catch (Exception e) {
            log.error("Error getting payment status for order: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting payment status", e);
        }
    }

    @PostMapping("/checkout/session/{orderId}")
    public ResponseEntity<CheckoutSessionDTO> createOrGetCheckoutSession(
            @PathVariable Long orderId,
            @RequestBody CheckoutUrlsRequest request) {
        try {
            log.info("Processing checkout session for order: {}", orderId);
            Long userId = securityContext.getCurrentUser().getId();

            // Get existing session or create new one
            CheckoutSessionDTO sessionDTO = stripePaymentService.createOrGetCheckoutSession(
                    userId,
                    orderId,
                    request.successUrl(),
                    request.cancelUrl()
            );

            return ResponseEntity.ok(sessionDTO);
        } catch (OrderNotFoundException e) {
            log.error("Order not found: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found", e);
        } catch (Exception e) {
            log.error("Error processing checkout session for order: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing checkout session", e);
        }
    }

    /**
     * Record for checkout URLs request
     */
    public record CheckoutUrlsRequest(String successUrl, String cancelUrl) {
    }
}
