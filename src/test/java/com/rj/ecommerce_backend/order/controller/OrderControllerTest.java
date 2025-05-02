package com.rj.ecommerce_backend.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import com.rj.ecommerce_backend.order.mapper.OrderMapper;
import com.rj.ecommerce_backend.order.search.OrderSearchCriteria;
import com.rj.ecommerce_backend.order.service.OrderService;
import com.rj.ecommerce_backend.sorting.OrderSortFilter;
import com.rj.ecommerce_backend.sorting.SortValidator;
import com.rj.ecommerce_backend.testutil.OrderTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

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
    private Order testOrder;
    private OrderCreationRequest testOrderCreationRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules for LocalDateTime serialization

        // Set up test data
        testOrderDTO = OrderTestDataFactory.createValidOrderDTO();
        testOrder = OrderTestDataFactory.createValidOrder(OrderTestDataFactory.createTestUser());
        
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
    void getAllOrdersForUser_ShouldReturnPageOfOrders() throws Exception {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<OrderDTO> orderPage = new PageImpl<>(Collections.singletonList(testOrderDTO), pageable, 1);
        
        when(sortValidator.validateAndBuildSort(any(), eq(OrderSortFilter.class)))
                .thenReturn(Sort.by("id").ascending());
        when(orderService.getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class)))
                .thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders", userId)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(testOrderDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].userId", is(testOrderDTO.userId().intValue())))
                .andExpect(jsonPath("$.content[0].email", is(testOrderDTO.email())))
                .andExpect(jsonPath("$.content[0].orderStatus", is(testOrderDTO.orderStatus().name())));
        
        verify(orderService, times(1)).getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class));
    }

    @Test
    void getOrder_ShouldReturnOrder_WhenOrderExists() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        
        when(orderService.getOrderById(userId, orderId)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testOrderDTO.id().intValue())))
                .andExpect(jsonPath("$.userId", is(testOrderDTO.userId().intValue())))
                .andExpect(jsonPath("$.email", is(testOrderDTO.email())))
                .andExpect(jsonPath("$.orderStatus", is(testOrderDTO.orderStatus().name())));
        
        verify(orderService, times(1)).getOrderById(userId, orderId);
    }

    @Test
    void getOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 999L;
        
        when(orderService.getOrderById(userId, orderId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isNotFound());
        
        verify(orderService, times(1)).getOrderById(userId, orderId);
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder() throws Exception {
        // Given
        Long userId = 1L;
        
        when(orderService.createOrder(eq(userId), any(OrderCreationRequest.class))).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/users/{userId}/orders", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderCreationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testOrderDTO.id().intValue())))
                .andExpect(jsonPath("$.userId", is(testOrderDTO.userId().intValue())))
                .andExpect(jsonPath("$.email", is(testOrderDTO.email())))
                .andExpect(jsonPath("$.orderStatus", is(testOrderDTO.orderStatus().name())));
        
        verify(orderService, times(1)).createOrder(eq(userId), any(OrderCreationRequest.class));
    }

    @Test
    void createOrder_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        // Given
        Long userId = 1L;
        
        // Create invalid request (missing required fields)
        OrderCreationRequest invalidRequest = new OrderCreationRequest(
                null, // Missing shipping address
                PaymentMethod.CREDIT_CARD,
                ShippingMethod.INPOST,
                null  // Missing cart
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/{userId}/orders", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrder_ShouldReturnNoContent_WhenOrderIsCancelled() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        
        doNothing().when(orderService).cancelOrder(userId, orderId);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isNoContent());
        
        verify(orderService, times(1)).cancelOrder(userId, orderId);
    }

    @Test
    void getAllOrdersForUser_ShouldFilterByOrderStatus() throws Exception {
        // Given
        Long userId = 1L;
        OrderStatus status = OrderStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<OrderDTO> orderPage = new PageImpl<>(Collections.singletonList(testOrderDTO), pageable, 1);
        
        when(sortValidator.validateAndBuildSort(any(), eq(OrderSortFilter.class)))
                .thenReturn(Sort.by("id").ascending());
        when(orderService.getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class)))
                .thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders", userId)
                .param("status", status.name())
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
        
        verify(orderService, times(1)).getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class));
    }

    @Test
    void getAllOrdersForUser_ShouldFilterByPriceRange() throws Exception {
        // Given
        Long userId = 1L;
        BigDecimal minTotal = new BigDecimal("50.00");
        BigDecimal maxTotal = new BigDecimal("200.00");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<OrderDTO> orderPage = new PageImpl<>(Collections.singletonList(testOrderDTO), pageable, 1);
        
        when(sortValidator.validateAndBuildSort(any(), eq(OrderSortFilter.class)))
                .thenReturn(Sort.by("id").ascending());
        when(orderService.getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class)))
                .thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders", userId)
                .param("minTotal", minTotal.toString())
                .param("maxTotal", maxTotal.toString())
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
        
        verify(orderService, times(1)).getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class));
    }

    @Test
    void getAllOrdersForUser_ShouldFilterByDateRange() throws Exception {
        // Given
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<OrderDTO> orderPage = new PageImpl<>(Collections.singletonList(testOrderDTO), pageable, 1);
        
        when(sortValidator.validateAndBuildSort(any(), eq(OrderSortFilter.class)))
                .thenReturn(Sort.by("id").ascending());
        when(orderService.getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class)))
                .thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders", userId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
        
        verify(orderService, times(1)).getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class));
    }

    @Test
    void getAllOrdersForUser_ShouldFilterByPaymentMethod() throws Exception {
        // Given
        Long userId = 1L;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<OrderDTO> orderPage = new PageImpl<>(Collections.singletonList(testOrderDTO), pageable, 1);
        
        when(sortValidator.validateAndBuildSort(any(), eq(OrderSortFilter.class)))
                .thenReturn(Sort.by("id").ascending());
        when(orderService.getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class)))
                .thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/orders", userId)
                .param("paymentMethod", paymentMethod.name())
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id:asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
        
        verify(orderService, times(1)).getOrdersForUser(any(Pageable.class), any(OrderSearchCriteria.class));
    }
}
