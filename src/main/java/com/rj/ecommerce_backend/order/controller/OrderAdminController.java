package com.rj.ecommerce_backend.order.controller;

import com.rj.ecommerce_backend.order.mapper.OrderMapper;
import com.rj.ecommerce_backend.order.search.OrderSearchCriteria;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.dtos.StatusUpdateRequest;
import com.rj.ecommerce_backend.order.service.OrderService;
import com.rj.ecommerce_backend.sorting.OrderSortFilter;
import com.rj.ecommerce_backend.sorting.SortValidator;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final SortValidator sortValidator;

    @GetMapping()
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @Min(0) BigDecimal minTotal,
            @RequestParam(required = false) @Min(0) BigDecimal maxTotal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) Boolean hasTransactionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id:asc") String sort
    ) {

        log.info("Received request to retrieve all orders with filters. search={}, status={}, minTotal={}, maxTotal={}, startDate={}, endDate={}",
                search, status, minTotal, maxTotal, startDate, endDate);

        Sort validatedSort = sortValidator.validateAndBuildSort(sort, OrderSortFilter.class);
        Pageable pageable = PageRequest.of(page, size, validatedSort);
        OrderSearchCriteria criteria = new OrderSearchCriteria(
                search,
                status,
                minTotal,
                maxTotal,
                startDate,
                endDate,
                userId,
                paymentMethod,
                hasTransactionId
        );

        Page<OrderDTO> orders = orderService.getAllOrders(pageable, criteria);

        log.info("Successfully retrieved all orders with filters. search={}, status={}, minTotal={}, maxTotal={}, startDate={}, endDate={}",
                search, status, minTotal, maxTotal, startDate, endDate);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable Long orderId
    ) {
        log.info("Retrieving order {}", orderId);

        return orderService.getOrderByIdAdmin(orderId)
                .map(order -> ResponseEntity.ok(orderMapper.toDto(order)))
                .orElse(ResponseEntity.notFound().build());
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
