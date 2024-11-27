package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.dtos.AddressDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDTO createOrder(Long userId, AddressDTO shippingAddress, String paymentMethod, CartDTO cartDTO);
    Optional<Order> getOrderById(Long orderId);
    Page<OrderDTO> getOrdersForUser(Long userId);
    Page<OrderDTO> getAllOrders(); // (admin)
    OrderDTO updateOrderStatus(Long orderId, String newStatus);
    void cancelOrder(Long orderId);
    BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems);
}
