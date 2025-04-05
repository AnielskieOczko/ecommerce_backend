package com.rj.ecommerce_backend.order.service;

import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.messaging.payment.dto.CheckoutSessionResponseDTO;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.domain.OrderItem;
import com.rj.ecommerce_backend.order.enums.*;
import com.rj.ecommerce_backend.order.mapper.OrderMapper;
import com.rj.ecommerce_backend.order.search.OrderSearchCriteria;
import com.rj.ecommerce_backend.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.exceptions.OrderCancellationException;
import com.rj.ecommerce_backend.order.exceptions.OrderNotFoundException;
import com.rj.ecommerce_backend.order.exceptions.OrderServiceException;
import com.rj.ecommerce_backend.order.repository.OrderRepository;
import com.rj.ecommerce_backend.payment.service.OrderNotificationService;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
@Slf4j
public class OrderServiceImpl implements OrderService {

    @PersistenceContext
    private EntityManager entityManager;

    private final OrderRepository orderRepository;
    private final SecurityContextImpl securityContext;
    private final ProductService productService;
    private final AdminService adminService;
    private final OrderMapper orderMapper;
    private final OrderNotificationService orderNotificationService;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, OrderCreationRequest orderCreationRequest) {
        // First create and save the order to get an ID
        try {
            Order order = createInitialOrder(userId, orderCreationRequest);

            // Add order items
            List<OrderItem> orderItems = createOrderItems(order, orderCreationRequest.cart());
            order.setOrderItems(orderItems);

            // Send notification
            orderNotificationService.sendOrderConfirmationEmail(orderMapper.toDto(order));

            return orderMapper.toDto(order);
        } catch (Exception e) {
            log.error("Error creating order for user {}", userId, e);
            throw new OrderServiceException("Error creating order", e);
        }
    }



    @Override
    @Transactional
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
    @Transactional
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

        return Optional.of(order);
    }

    @Override
    @Transactional
    public Page<OrderDTO> getOrdersForUser(Pageable pageable, OrderSearchCriteria criteria) {

        Specification<Order> spec = criteria.toSpecification();

        securityContext.checkAccess(criteria.userId());
        log.debug("Fetching orders for user ID: {}", criteria.userId());
        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional
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
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order {} status updated to {}", orderId, newStatus);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    @Transactional
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
    @Transactional
    public void cancelOrderAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Order {} cancelled by admin", orderId);
    }

    @Override
    @Transactional
    public BigDecimal calculateOrderTotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markOrderPaymentFailed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setOrderStatus(OrderStatus.FAILED);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderWithCheckoutSession(CheckoutSessionResponseDTO response) {
        Long orderId = Long.valueOf(response.orderId());
        String sessionId = response.sessionId();
        String sessionUrl = response.checkoutUrl();
        String paymentStatus = response.paymentStatus();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setPaymentTransactionId(sessionId);
        order.setPaymentStatus(PaymentStatus.fromStripeEvent(response.status()));
        order.setCheckoutSessionUrl(sessionUrl);
        order.setPaymentStatus(PaymentStatus.fromStripeEvent(paymentStatus));

        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("Updated order {} with checkout session {}, URL: {}", orderId, sessionId, sessionUrl);
    }

    public void updatePaymentDetailsOnInitiation(Order order) {
        // Update payment fields
        order.setPaymentTransactionId(order.getId().toString());
        order.setCheckoutSessionUrl(order.getId().toString());
        order.setPaymentStatus(PaymentStatus.PROCESSING);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    @Transactional
    private Order createInitialOrder(Long userId, OrderCreationRequest request) {
        // Validate user access and existence
        securityContext.checkAccess(userId);
        User user = adminService.getUserForValidation(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate product stock before processing
        validateCartAvailability(request.cart());

        // Create order entity
        Order order = createOrderEntity(
                user,
                request.shippingAddress(),
                request.shippingMethod(),
                request.paymentMethod(),
                request.cart());

        // Set initial status
        order.setPaymentStatus(PaymentStatus.PROCESSING);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setLastModifiedBy(user.getEmail().value());

        Order savedOrder = orderRepository.save(order);

        // Detach order from persistence context after initial save.
        // Prevents Hibernate's merge process in processPaymentForOrder from causing
        // ImmutableCollectionException due to persistence context conflicts across transactions.
        entityManager.detach(savedOrder);
        // Save and return
        return savedOrder;
    }

    private Order createOrderEntity(
            User user,
            ShippingAddressDTO shippingAddress,
            ShippingMethod shippingMethod,
            PaymentMethod paymentMethod,
            CartDTO cartDTO
    ) {
        Order order = new Order();
        order.setUser(user);
        order.setCreatedBy(user.getEmail().value());


        // Create Address from DTO
        Address deliveryAddress = new Address(
                shippingAddress.street(),
                shippingAddress.city(),
                new ZipCode(shippingAddress.zipCode()),
                shippingAddress.country()
        );

        order.setShippingAddress(deliveryAddress);
        order.setShippingMethod(shippingMethod);
        order.setPaymentMethod(paymentMethod);

        // Calculate total price
        BigDecimal totalPrice = calculateOrderTotal(cartDTO.cartItems());
        order.setCurrency(Currency.PLN);
        order.setTotalPrice(totalPrice);

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
