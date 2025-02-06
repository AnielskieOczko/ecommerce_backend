package com.rj.ecommerce_backend.order.service;

import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.domain.OrderItem;
import com.rj.ecommerce_backend.order.mapper.OrderMapper;
import com.rj.ecommerce_backend.order.search.OrderSearchCriteria;
import com.rj.ecommerce_backend.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import com.rj.ecommerce_backend.order.exceptions.OrderCancellationException;
import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.order.exceptions.OrderServiceException;
import com.rj.ecommerce_backend.order.repository.OrderRepository;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.service.ProductService;
import com.rj.ecommerce_backend.product.exceptions.InsufficientStockException;
import com.rj.ecommerce_backend.product.exceptions.ProductNotFoundException;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.exceptions.UserNotFoundException;
import com.rj.ecommerce_backend.user.services.AdminService;
import com.rj.ecommerce_backend.user.valueobject.Address;
import com.rj.ecommerce_backend.user.valueobject.ZipCode;
import com.rj.ecommerce_backend.securityconfig.SecurityContextImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final AdminService adminService;
    private final OrderMapper orderMapper;
    private final OrderEmailService orderEmailService;

    @Override
    public OrderDTO createOrder(Long userId, OrderCreationRequest orderCreationRequest) {
        // Validate user access and existence
        securityContext.checkAccess(userId);
        User user = adminService.getUserForValidation(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate product stock before processing
        validateCartAvailability(orderCreationRequest.cart());

        // Create order entity
        Order order = createOrderEntity(
                user,
                orderCreationRequest.shippingAddress(),
                orderCreationRequest.shippingMethod(),
                orderCreationRequest.paymentMethod(),
                orderCreationRequest.cart());

        // Process order items and update inventory
        List<OrderItem> orderItems = createOrderItems(order, orderCreationRequest.cart());
        order.setOrderItems(orderItems);

        // Save and return
        Order savedOrder = orderRepository.save(order);

        // send request for order email confirmation
        orderEmailService.sendOrderConfirmationEmail(orderMapper.toDto(savedOrder));


        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Optional<Order> getOrderByIdAdmin(Long orderId) {

        securityContext.isAdmin();

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
    public Optional<Order> getOrderById(Long userId, Long orderId) {

        securityContext.checkAccess(userId);

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
    public Page<OrderDTO> getOrdersForUser(Pageable pageable, OrderSearchCriteria criteria) {

        Specification<Order> spec = criteria.toSpecification();

        securityContext.checkAccess(criteria.userId());
        log.debug("Fetching orders for user ID: {}", criteria.userId());
        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable, OrderSearchCriteria criteria) {
        // Verify admin permissions
        if (securityContext.isAdmin()) {
            log.warn("Unauthorized access attempt to all orders");
            throw new AccessDeniedException("Admin access required");
        }

        Specification<Order> orderSpecification = criteria.toSpecification();

        Page<Order> orders = orderRepository.findAll(orderSpecification, pageable);

        // Convert to DTOs
        return orders.map(orderMapper::toDto);
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order {} status updated to {}", orderId, newStatus);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Validate order belongs to user
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User " + userId + " is not authorized to cancel order " + orderId);
        }

        // Validate order status
        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new OrderCancellationException("Cannot cancel order with status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Order {} cancelled by user {}", orderId, userId);
    }

    @Override
    public void cancelOrderAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Order {} cancelled by admin", orderId);
    }

    @Override
    public BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // this version fetch actual product price from DB
//    @Override
//    public BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems) {
//        return cartItems.stream()
//                .map(item -> {
//                    Product product = productService.getProductEntityForValidation(item.id())
//                            .orElseThrow(() -> new ProductNotFoundException(item.id()));
//                    return product.getProductPrice().amount().value().multiply(BigDecimal.valueOf(item.quantity()));
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }

    private Order createOrderEntity(
            User user,
            ShippingAddressDTO shippingAddress,
            ShippingMethod shippingMethod,
            PaymentMethod paymentMethod,
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
        order.setShippingMethod(shippingMethod);
        order.setOrderStatus(OrderStatus.PENDING);
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
