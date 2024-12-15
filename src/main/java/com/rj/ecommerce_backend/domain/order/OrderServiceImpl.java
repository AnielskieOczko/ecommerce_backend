package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.order.dtos.AddressDTO;
import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.exceptions.OrderCancellationException;
import com.rj.ecommerce_backend.domain.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.domain.order.exceptions.OrderServiceException;
import com.rj.ecommerce_backend.domain.product.Product;
import com.rj.ecommerce_backend.domain.product.ProductMapper;
import com.rj.ecommerce_backend.domain.product.ProductService;
import com.rj.ecommerce_backend.domain.product.exceptions.InsufficientStockException;
import com.rj.ecommerce_backend.domain.product.exceptions.ProductNotFoundException;
import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.exceptions.UserNotFoundException;
import com.rj.ecommerce_backend.domain.user.services.UserService;
import com.rj.ecommerce_backend.domain.user.valueobject.Address;
import com.rj.ecommerce_backend.domain.user.valueobject.ZipCode;
import com.rj.ecommerce_backend.securityconfig.SecurityContextImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final SecurityContextImpl securityContext;
    private final ProductService productService;
    private final UserService userService;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderDTO createOrder(Long userId, AddressDTO shippingAddress, String paymentMethod, CartDTO cartDTO) {
        // Validate user access and existence
        securityContext.checkAccess(userId);
        User user = userService.getUserForValidation(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate product stock before processing
        validateCartAvailability(cartDTO);

        // Create order entity
        Order order = createOrderEntity(user, shippingAddress, paymentMethod, cartDTO);

        // Process order items and update inventory
        List<OrderItem> orderItems = createOrderItems(order, cartDTO);
        order.setOrderItems(orderItems);

        // Save and return
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {

        if (orderId == null) {
            log.warn("Attempted to retrieve order with null ID");
            return Optional.empty();
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        // Handle case where order doesn't exist
        if (orderOptional.isEmpty()) {
            log.info("Order not found with ID: {}", orderId);
            return Optional.empty();
        }

        Order order = orderOptional.get();

        // Check user access permissions
        try {
            User orderUser = order.getUser();
            securityContext.checkAccess(orderUser.getId());

            log.info("Successfully retrieved order with ID: {}", orderId);
            return Optional.of(order);
        } catch (AccessDeniedException e) {
            log.warn("Access denied for order ID: {} for user", orderId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error retrieving order with ID: {}", orderId, e);
            throw new OrderServiceException("Error processing order retrieval", e);
        }
    }

    @Override
    public Page<OrderDTO> getOrdersForUser(Long userId, Pageable pageable) {
        securityContext.checkAccess(userId);
        log.debug("Fetching orders for user ID: {}", userId);
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        // Verify admin permissions
        if (!securityContext.isAdmin()) {
            log.warn("Unauthorized access attempt to all orders");
            throw new AccessDeniedException("Admin access required");
        }

        // Fetch all orders with pagination, sorted by most recent first
        Page<Order> orders = orderRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by("createdAt").descending())
        );

        // Convert to DTOs
        return orders.map(orderMapper::toDto);
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order {} status updated to {}", orderId, newStatus);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        securityContext.checkAccess(order.getUser().getId());

        // Validate order can be cancelled
        if (!order.getOrderStatus().equals("PENDING")) {
            throw new OrderCancellationException("Cannot cancel order with status: " + order.getOrderStatus());
        }

        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);

        log.info("Order {} cancelled", orderId);
    }

    @Override
    public BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order createOrderEntity(
            User user,
            AddressDTO shippingAddress,
            String paymentMethod,
            CartDTO cartDTO
    ) {
        Address address = new Address(
                shippingAddress.street(),
                shippingAddress.city(),
                new ZipCode(shippingAddress.zipCode()),
                shippingAddress.country()
        );

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setOrderStatus("PENDING");
        order.setPaymentMethod(paymentMethod);
        order.setTotalPrice(calculateOrderTotal(cartDTO.cartItems()));
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    private void validateCartAvailability(CartDTO cartDTO) {
        for (CartItemDTO item : cartDTO.cartItems()) {
            Product product = productService.getProductEntityForValidation(item.productId())
                    .orElseThrow(() -> new ProductNotFoundException(item.productId()));

            if (product.getStockQuantity().value() < item.quantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }
        }
    }

    private List<OrderItem> createOrderItems(Order order, CartDTO cartDTO) {
        return cartDTO.cartItems().stream()
                .map(cartItemDTO -> {
                    Product product = productService.getProductEntityForValidation(cartItemDTO.productId())
                            .orElseThrow(() -> new ProductNotFoundException(cartItemDTO.productId()));

                    // Reduce inventory atomically
                    productService.reduceProductQuantity(
                            product.getId(),
                            cartItemDTO.quantity()
                    );

                    return createOrderItem(order, product, cartItemDTO);
                })
                .toList();
    }

    private OrderItem createOrderItem(Order order, Product product, CartItemDTO cartItemDTO) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItemDTO.quantity());
        orderItem.setPrice(product.getProductPrice().amount().value());
        return orderItem;
    }
}
