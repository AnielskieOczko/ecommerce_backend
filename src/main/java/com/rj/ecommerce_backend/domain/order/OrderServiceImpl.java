package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.dtos.AddressDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public OrderDTO createOrder(Long userId, AddressDTO shippingAddress, String paymentMethod, CartDTO cartDTO) {
        return null;
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Page<OrderDTO> getOrdersForUser(Long userId) {
        return Page.empty();
    }

    @Override
    public Page<OrderDTO> getAllOrders() {
        return Page.empty();
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String newStatus) {
        return null;
    }

    @Override
    public void cancelOrder(Long orderId) {

    }

    @Override
    public BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems) {
        return null;
    }
}
