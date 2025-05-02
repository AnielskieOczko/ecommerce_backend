package com.rj.ecommerce_backend.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import com.rj.ecommerce_backend.order.exceptions.OrderCancellationException;
import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.order.mapper.OrderMapper;
import com.rj.ecommerce_backend.order.service.OrderService;
import com.rj.ecommerce_backend.sorting.SortValidator;
import com.rj.ecommerce_backend.testutil.OrderTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerExceptionHandlingTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SortValidator sortValidator;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private OrderDTO testOrderDTO;
    private OrderCreationRequest testOrderCreationRequest;

    @BeforeEach
    void setUp() {
        // Set up exception handler with controller advice
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new OrderControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules for LocalDateTime serialization

        // Set up test data
        testOrderDTO = OrderTestDataFactory.createValidOrderDTO();

        // Create test OrderCreationRequest
        ShippingAddressDTO shippingAddress = new ShippingAddressDTO(
                "123 Test St", "Test City", "12-345", "Test Country");

        List<CartItemDTO> cartItems = List.of(
                new CartItemDTO(1L, 1L, 1L, "Test Product", 2, new BigDecimal("99.99"))
        );

        CartDTO cart = new CartDTO(1L, 1L, cartItems, LocalDateTime.now(), LocalDateTime.now());

        testOrderCreationRequest = new OrderCreationRequest(
                shippingAddress,
                PaymentMethod.CREDIT_CARD,
                ShippingMethod.INPOST,
                cart
        );
    }

    @Test
    void createOrder_ShouldReturnForbidden_WhenAccessDenied() throws Exception {
        // Given
        Long userId = 1L;

        when(orderService.createOrder(eq(userId), any(OrderCreationRequest.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        // When & Then
        mockMvc.perform(post("/api/v1/users/{userId}/orders", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderCreationRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelOrder_ShouldReturnNotFound_WhenOrderNotFound() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 999L;

        doThrow(new OrderNotFoundException(orderId))
                .when(orderService).cancelOrder(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelOrder_ShouldReturnBadRequest_WhenOrderCannotBeCancelled() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 1L;

        doThrow(new OrderCancellationException("Order cannot be cancelled"))
                .when(orderService).cancelOrder(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrder_ShouldReturnForbidden_WhenAccessDenied() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 1L;

        doThrow(new AccessDeniedException("Access denied"))
                .when(orderService).cancelOrder(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isForbidden());
    }
}
