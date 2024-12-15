package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.order.dtos.AddressDTO;
import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDTO createOrder(Long userId, AddressDTO shippingAddress, String paymentMethod, CartDTO cartDTO);
    Optional<Order> getOrderById(Long orderId);
    Page<OrderDTO> getOrdersForUser(Long userId, Pageable pageable);
    Page<OrderDTO> getAllOrders(Pageable pageable); // (admin)
    OrderDTO updateOrderStatus(Long orderId, String newStatus);
    void cancelOrder(Long orderId);
    BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems);

    // TODO: optional
    // searchOrders(OrderSearchCriteria criteria);
    // getOrderHistory(Long userId);
    // (Long orderId);
}
