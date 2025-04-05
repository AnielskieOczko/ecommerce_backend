package com.rj.ecommerce_backend.payment.service;

import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.payment.service.dto.CheckoutSessionDTO;
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
    public ResponseEntity<CheckoutSessionDTO> getCheckoutSession(@PathVariable Long orderId) {
        try {
            Long userId = securityContext.getCurrentUser().getId();
            CheckoutSessionDTO sessionDTO = stripePaymentService.getCheckoutSessionForOrder(userId, orderId);
            return ResponseEntity.ok(sessionDTO);
        } catch (OrderNotFoundException e) {
            log.error("Order not found: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found", e);
        } catch (Exception e) {
            log.error("Error getting checkout session for order: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting checkout session", e);
        }
    }

    @PostMapping("/checkout/create/{orderId}")
    public ResponseEntity<String> createCheckoutSession(
            @PathVariable Long orderId,
            @RequestBody CheckoutUrlsRequest request) {
        try {
            log.info("Creating checkout session for order: {}", orderId);
            Long userId = securityContext.getCurrentUser().getId();

            stripePaymentService.createCheckoutSessionForOrder(
                    userId,
                    orderId,
                    request.successUrl(),
                    request.cancelUrl()
            );

            return ResponseEntity.accepted().body("Checkout session creation initiated");
        } catch (OrderNotFoundException e) {
            log.error("Order not found: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found", e);
        } catch (Exception e) {
            log.error("Error creating checkout session for order: {}", orderId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating checkout session", e);
        }
    }

    /**
     * Record for checkout URLs request
     */
    public record CheckoutUrlsRequest(String successUrl, String cancelUrl) {
    }

}
