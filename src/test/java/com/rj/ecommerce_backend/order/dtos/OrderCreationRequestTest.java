package com.rj.ecommerce_backend.order.dtos;

import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidOrderCreationRequest() {
        // Given
        ShippingAddressDTO shippingAddress = new ShippingAddressDTO(
                "123 Main St", "New York", "10001", "USA");
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        ShippingMethod shippingMethod = ShippingMethod.INPOST;
        CartDTO cart = new CartDTO(1L, 1L, List.of(
                new CartItemDTO(1L, 1L, 1L, "Test Product", 2, new BigDecimal("99.99"))
        ), LocalDateTime.now(), LocalDateTime.now());

        // When
        OrderCreationRequest request = new OrderCreationRequest(
                shippingAddress, paymentMethod, shippingMethod, cart);

        // Then
        assertEquals(shippingAddress, request.shippingAddress());
        assertEquals(paymentMethod, request.paymentMethod());
        assertEquals(shippingMethod, request.shippingMethod());
        assertEquals(cart, request.cart());

        Set<ConstraintViolation<OrderCreationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenShippingAddressIsNull() {
        // Given
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        ShippingMethod shippingMethod = ShippingMethod.INPOST;
        CartDTO cart = new CartDTO(1L, 1L, List.of(
                new CartItemDTO(1L, 1L, 1L, "Test Product", 2, new BigDecimal("99.99"))
        ), LocalDateTime.now(), LocalDateTime.now());

        // When
        OrderCreationRequest request = new OrderCreationRequest(
                null, paymentMethod, shippingMethod, cart);
        Set<ConstraintViolation<OrderCreationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("shippingAddress", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAllowNullPaymentMethod() {
        // Given
        ShippingAddressDTO shippingAddress = new ShippingAddressDTO(
                "123 Main St", "New York", "10001", "USA");
        ShippingMethod shippingMethod = ShippingMethod.INPOST;
        CartDTO cart = new CartDTO(1L, 1L, List.of(
                new CartItemDTO(1L, 1L, 1L, "Test Product", 2, new BigDecimal("99.99"))
        ), LocalDateTime.now(), LocalDateTime.now());

        // When
        OrderCreationRequest request = new OrderCreationRequest(
                shippingAddress, null, shippingMethod, cart);
        Set<ConstraintViolation<OrderCreationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenShippingMethodIsNull() {
        // Given
        ShippingAddressDTO shippingAddress = new ShippingAddressDTO(
                "123 Main St", "New York", "10001", "USA");
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        CartDTO cart = new CartDTO(1L, 1L, List.of(
                new CartItemDTO(1L, 1L, 1L, "Test Product", 2, new BigDecimal("99.99"))
        ), LocalDateTime.now(), LocalDateTime.now());

        // When
        OrderCreationRequest request = new OrderCreationRequest(
                shippingAddress, paymentMethod, null, cart);
        Set<ConstraintViolation<OrderCreationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("shippingMethod", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenCartIsNull() {
        // Given
        ShippingAddressDTO shippingAddress = new ShippingAddressDTO(
                "123 Main St", "New York", "10001", "USA");
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        ShippingMethod shippingMethod = ShippingMethod.INPOST;

        // When
        OrderCreationRequest request = new OrderCreationRequest(
                shippingAddress, paymentMethod, shippingMethod, null);
        Set<ConstraintViolation<OrderCreationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("cart", violations.iterator().next().getPropertyPath().toString());
    }
}
