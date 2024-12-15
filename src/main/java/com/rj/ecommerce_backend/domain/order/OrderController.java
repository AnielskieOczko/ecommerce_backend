package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.dtos.StatusUpdateRequest;
import com.rj.ecommerce_backend.securityconfig.SecurityContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final SecurityContext securityContext;


    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderCreationRequest orderRequest
    ) {
        Long userId = securityContext.getCurrentUser().getId();
        log.info("Creating order for user: {}", userId);

        OrderDTO createdOrder = orderService.createOrder(
                userId,
                orderRequest.shippingAddress(),
                orderRequest.paymentMethod(),
                orderRequest.cart()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable Long orderId
    ) {
        log.info("Retrieving order {}", orderId);

        return orderService.getOrderById(orderId)
                .map(order -> ResponseEntity.ok(orderMapper.toDto(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            Pageable pageable
    ) {
        log.info("Retrieving orders for user");

        Long userId = securityContext.getCurrentUser().getId();
        Page<OrderDTO> orders = orderService.getOrdersForUser(userId, pageable);

        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest statusUpdate
    ) {
        log.info("Updating status for order {}", orderId);

        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, statusUpdate.newStatus());

        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId
    ) {
        log.info("Cancelling order {}", orderId);

        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }

}
