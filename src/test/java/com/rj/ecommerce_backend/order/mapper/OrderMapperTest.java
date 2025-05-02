package com.rj.ecommerce_backend.order.mapper;

import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.domain.OrderItem;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.dtos.OrderItemDTO;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.enums.Currency;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.valueobject.ProductName;
import com.rj.ecommerce_backend.testutil.OrderTestDataFactory;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.valueobject.Address;
import com.rj.ecommerce_backend.user.valueobject.Email;
import com.rj.ecommerce_backend.user.valueobject.ZipCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        testUser = OrderTestDataFactory.createTestUser();
    }

    @Test
    void shouldMapOrderToOrderDTO() {
        // Given
        Order order = createTestOrder();

        // When
        OrderDTO orderDTO = orderMapper.toDto(order);

        // Then
        assertNotNull(orderDTO);
        assertEquals(order.getId(), orderDTO.id());
        assertEquals(order.getUser().getId(), orderDTO.userId());
        assertEquals(order.getUser().getEmail().value(), orderDTO.email());
        assertEquals(order.getTotalPrice(), orderDTO.totalPrice());
        assertEquals(order.getCurrency().name(), orderDTO.currency());
        assertEquals(order.getShippingMethod(), orderDTO.shippingMethod());
        assertEquals(order.getPaymentMethod(), orderDTO.paymentMethod());
        assertEquals(order.getPaymentStatus(), orderDTO.paymentStatus());
        assertEquals(order.getOrderStatus(), orderDTO.orderStatus());

        // Check shipping address
        ShippingAddressDTO addressDTO = orderDTO.shippingAddress();
        assertNotNull(addressDTO);
        assertEquals(order.getShippingAddress().street(), addressDTO.street());
        assertEquals(order.getShippingAddress().city(), addressDTO.city());
        assertEquals(order.getShippingAddress().zipCode().value(), addressDTO.zipCode());
        assertEquals(order.getShippingAddress().country(), addressDTO.country());

        // Check order items
        List<OrderItemDTO> orderItemDTOs = orderDTO.orderItems();
        assertNotNull(orderItemDTOs);
        assertEquals(1, orderItemDTOs.size());

        OrderItemDTO itemDTO = orderItemDTOs.get(0);
        OrderItem orderItem = order.getOrderItems().get(0);
        assertEquals(orderItem.getId(), itemDTO.id());
        assertEquals(orderItem.getOrder().getId(), itemDTO.orderId());
        assertEquals(orderItem.getProduct().getId(), itemDTO.productId());
        assertEquals(orderItem.getProduct().getProductName().value(), itemDTO.productName());
        assertEquals(orderItem.getQuantity(), itemDTO.quantity());
        assertEquals(orderItem.getPrice(), itemDTO.price());
    }

    @Test
    void shouldReturnNullWhenOrderIsNull() {
        // Given
        Order order = null;

        // When
        OrderDTO orderDTO = orderMapper.toDto(order);

        // Then
        assertNull(orderDTO);
    }

    @Test
    void shouldMapOrderItemToOrderItemDTO() {
        // Given
        Order order = createTestOrder();
        OrderItem orderItem = order.getOrderItems().get(0);

        // When
        OrderItemDTO orderItemDTO = orderMapper.toDto(orderItem);

        // Then
        assertNotNull(orderItemDTO);
        assertEquals(orderItem.getId(), orderItemDTO.id());
        assertEquals(orderItem.getOrder().getId(), orderItemDTO.orderId());
        assertEquals(orderItem.getProduct().getId(), orderItemDTO.productId());
        assertEquals(orderItem.getProduct().getProductName().value(), orderItemDTO.productName());
        assertEquals(orderItem.getQuantity(), orderItemDTO.quantity());
        assertEquals(orderItem.getPrice(), orderItemDTO.price());
    }

    @Test
    void shouldReturnNullWhenOrderItemIsNull() {
        // Given
        OrderItem orderItem = null;

        // When
        OrderItemDTO orderItemDTO = orderMapper.toDto(orderItem);

        // Then
        assertNull(orderItemDTO);
    }

    @Test
    void shouldHandleNullProductInOrderItem() {
        // Given
        Order order = createTestOrder();
        OrderItem orderItem = order.getOrderItems().get(0);
        orderItem.setProduct(null);

        // When
        OrderItemDTO orderItemDTO = orderMapper.toDto(orderItem);

        // Then
        assertNotNull(orderItemDTO);
        assertNull(orderItemDTO.productId());
        assertNull(orderItemDTO.productName());
    }

    @Test
    void shouldHandleNullProductNameInOrderItem() {
        // Given
        Order order = createTestOrder();
        OrderItem orderItem = order.getOrderItems().get(0);
        Product product = orderItem.getProduct();
        product.setProductName(null);

        // When
        OrderItemDTO orderItemDTO = orderMapper.toDto(orderItem);

        // Then
        assertNotNull(orderItemDTO);
        assertNull(orderItemDTO.productName());
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setTotalPrice(new BigDecimal("199.99"));
        order.setCurrency(Currency.PLN);

        Address address = new Address(
                "123 Test St",
                "Test City",
                new ZipCode("12-345"),
                "Test Country"
        );
        order.setShippingAddress(address);

        order.setShippingMethod(ShippingMethod.INPOST);
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);

        Product product = new Product();
        product.setId(1L);
        product.setProductName(new ProductName("Test Product"));
        orderItem.setProduct(product);

        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("99.99"));

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);

        return order;
    }
}
