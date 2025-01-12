package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.order.dtos.OrderCreationRequest;
import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.dtos.StatusUpdateRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import com.rj.ecommerce_backend.securityconfig.SecurityContext;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<OrderDTO>> getAllUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        log.info("Received request to retrieve orders for userId:  {}", userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        // TODO: implement search feature
        Page<OrderDTO> orders = orderService.getOrdersForUser(userId, pageable);

        log.info("Successfully retrieve orders for userId:  {}", userId);
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


    @DeleteMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId
    ) {
        log.info("Cancelling order {}", orderId);

        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }

}
