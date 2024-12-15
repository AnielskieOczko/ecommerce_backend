package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.order.dtos.AddressDTO;
import com.rj.ecommerce_backend.domain.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.domain.order.dtos.OrderItemDTO;
import com.rj.ecommerce_backend.domain.product.Product;
import com.rj.ecommerce_backend.domain.user.valueobject.Address;
import com.rj.ecommerce_backend.domain.user.valueobject.ZipCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::toDto)
                .toList();

        AddressDTO addressDTO = new AddressDTO(order.getShippingAddress().street(),
                order.getShippingAddress().city(), order.getShippingAddress().zipCode().value(),
                order.getShippingAddress().country());


        return new OrderDTO(order.getId(), order.getUser().getId(), orderItemDTOs, order.getTotalPrice(),
                addressDTO, order.getPaymentMethod(), order.getPaymentTransactionId(),
                order.getOrderDate(), order.getOrderStatus(), order.getCreatedAt(), order.getUpdatedAt());
    }

    public OrderItemDTO toDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        Product product = orderItem.getProduct();
        String productName = (product != null && product.getProductName() != null)
                ? product.getProductName().value() : null;


        return new OrderItemDTO(orderItem.getId(), orderItem.getOrder().getId(),
                (product != null) ? product.getId() : null, productName, orderItem.getQuantity(),
                orderItem.getPrice());
    }

    public Order toEntity(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }

        // Create Address value object from AddressDTO
        Address shippingAddress = new Address(orderDTO.shippingAddress().street(), orderDTO.shippingAddress().city(),
                new ZipCode(orderDTO.shippingAddress().zipCode()), orderDTO.shippingAddress().country());

        //                .user(userService.findById(orderDTO.userId())) // Fetch user in service
        // Set the Address value object


        // Handle orderItems in the service to manage bidirectional relationship
        return Order.builder()
                .id(orderDTO.id())
//                .user(userService.findById(orderDTO.userId())) // Fetch user in service
                .totalPrice(orderDTO.totalPrice())
                .shippingAddress(shippingAddress) // Set the Address value object
                .paymentMethod(orderDTO.paymentMethod())
                .paymentTransactionId(orderDTO.paymentTransactionId())
                .orderDate(orderDTO.orderDate())
                .orderStatus(orderDTO.orderStatus())
                .createdAt(orderDTO.createdAt())
                .updatedAt(orderDTO.updatedAt())
                .build();
    }

    public OrderItem toEntity(OrderItemDTO orderItemDTO) {
        if (orderItemDTO == null) {
            return null;
        }

        // .order(orderRepository.findById(orderItemDTO.orderId()).orElse(null)) // Fetch in service
        // .product(productRepository.findById(orderItemDTO.productId()).orElse(null))  // Fetch in service
        return OrderItem.builder()
                .id(orderItemDTO.id())
                // .order(orderRepository.findById(orderItemDTO.orderId()).orElse(null)) // Fetch in service
                // .product(productRepository.findById(orderItemDTO.productId()).orElse(null))  // Fetch in service
                .quantity(orderItemDTO.quantity())
                .price(orderItemDTO.price())
                .build();
    }
}
