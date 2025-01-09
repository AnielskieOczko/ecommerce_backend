package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.dtos.StatusUpdateRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import com.rj.ecommerce_backend.securityconfig.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final SecurityContext securityContext;

    @GetMapping()
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        log.info("Received request to retrieve all users.");

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        // TODO: implement search feature
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);

        log.info("Successfully retrieved all users.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orders);
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

    @GetMapping("/{userId}")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        log.info("Retrieving orders for user");
        Page<OrderDTO> orders = orderService.getOrdersForUser(userId, pageable);

        return ResponseEntity.ok(orders);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
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

        orderService.cancelOrderAdmin(orderId);

        return ResponseEntity.noContent().build();
    }
}
