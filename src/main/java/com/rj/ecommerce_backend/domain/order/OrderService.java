package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.domain.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDTO createOrder(Long userId, OrderCreationRequest orderCreationRequest);
    Optional<Order> getOrderById(Long userId, Long orderId);
    Page<OrderDTO> getAllOrders(Pageable pageable, OrderSearchCriteria criteria);
    Page<OrderDTO> getOrdersForUser(Pageable pageable, OrderSearchCriteria criteria);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
    void cancelOrder(Long userId, Long orderId);
    BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems);


    // Admin only methods
//    Page<OrderDTO> getAllOrders(Pageable pageable);
    void cancelOrderAdmin(Long orderId);
    // TODO: think on this method, maybe admin should also have in path {userId}
    Optional<Order> getOrderByIdAdmin(Long orderId);
    // TODO: optional
    // searchOrders(OrderSearchCriteria criteria);
    // getOrderHistory(Long userId);
    // (Long orderId);
}
