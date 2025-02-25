package com.rj.ecommerce_backend.order.mapper;

import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.domain.OrderItem;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.dtos.OrderItemDTO;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.user.valueobject.Address;
import com.rj.ecommerce_backend.user.valueobject.ZipCode;
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

        ShippingAddressDTO addressDTO = new ShippingAddressDTO(order.getShippingAddress().street(),
                order.getShippingAddress().city(), order.getShippingAddress().zipCode().value(),
                order.getShippingAddress().country());


        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail().value(),
                orderItemDTOs,
                order.getTotalPrice(),
                addressDTO,
                order.getShippingMethod(),
                order.getPaymentMethod(),
                order.getPaymentTransactionId(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt());
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
                .updatedAt(orderDTO.updatedAt())
                .build();
    }

    public OrderItem toEntity(OrderItemDTO orderItemDTO) {
        if (orderItemDTO == null) {
            return null;
        }

        return OrderItem.builder()
                .id(orderItemDTO.id())
                .quantity(orderItemDTO.quantity())
                .price(orderItemDTO.price())
                .build();
    }
}
