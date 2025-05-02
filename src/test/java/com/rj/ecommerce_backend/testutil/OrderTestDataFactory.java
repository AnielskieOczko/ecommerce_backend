package com.rj.ecommerce_backend.testutil;

import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.domain.OrderItem;
import com.rj.ecommerce_backend.order.dtos.OrderCreationRequest;
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
import com.rj.ecommerce_backend.product.valueobject.StockQuantity;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.valueobject.Address;
import com.rj.ecommerce_backend.user.valueobject.Email;
import com.rj.ecommerce_backend.user.valueobject.ZipCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating test order data
 */
public class OrderTestDataFactory {

    /**
     * Creates a valid Order entity with default values
     */
    public static Order createValidOrder(User user) {
        Order order = Order.builder()
                .user(user)
                .totalPrice(new BigDecimal("199.99"))
                .currency(Currency.PLN)
                .shippingAddress(new Address("123 Test St", "Test City", new ZipCode("12-345"), "Test Country"))
                .shippingMethod(ShippingMethod.INPOST)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Add order items
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(createOrderItem(order, 1L, "Test Product 1", 2, new BigDecimal("99.99")));
        order.setOrderItems(orderItems);

        return order;
    }

    /**
     * Creates a valid OrderDTO with default values
     */
    public static OrderDTO createValidOrderDTO() {
        return OrderDTO.builder()
                .id(1L)
                .userId(1L)
                .email("test@example.com")
                .orderItems(List.of(
                        new OrderItemDTO(1L, 1L, 1L, "Test Product 1", 2, new BigDecimal("99.99"))
                ))
                .totalPrice(new BigDecimal("199.99"))
                .currency("PLN")
                .shippingAddress(new ShippingAddressDTO("123 Test St", "Test City", "12-345", "Test Country"))
                .shippingMethod(ShippingMethod.INPOST)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a valid OrderCreationRequest with default values
     */
    public static OrderCreationRequest createValidOrderCreationRequest() {
        ShippingAddressDTO shippingAddress = new ShippingAddressDTO(
                "123 Test St",
                "Test City",
                "12-345",
                "Test Country"
        );

        List<CartItemDTO> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDTO(1L, 1L, 1L, "Test Product 1", 2, new BigDecimal("99.99")));

        CartDTO cart = new CartDTO(1L, 1L, cartItems, LocalDateTime.now(), LocalDateTime.now());

        return new OrderCreationRequest(
                shippingAddress,
                PaymentMethod.CREDIT_CARD,
                ShippingMethod.INPOST,
                cart
        );
    }

    /**
     * Creates an OrderItem for testing
     */
    public static OrderItem createOrderItem(Order order, Long productId, String productName, int quantity, BigDecimal price) {
        Product product = new Product();
        product.setId(productId);
        product.setProductName(new ProductName(productName));
        product.setStockQuantity(new StockQuantity(100));

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }

    /**
     * Creates a test User for orders
     */
    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail(new Email("test@example.com"));
        return user;
    }
}
