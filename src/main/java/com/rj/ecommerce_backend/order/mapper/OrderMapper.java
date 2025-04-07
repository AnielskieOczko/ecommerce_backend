package com.rj.ecommerce_backend.order.mapper;

import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.domain.OrderItem;
import com.rj.ecommerce_backend.order.dtos.ShippingAddressDTO;
import com.rj.ecommerce_backend.order.dtos.OrderDTO;
import com.rj.ecommerce_backend.order.dtos.OrderItemDTO;
import com.rj.ecommerce_backend.order.enums.Currency;
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

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .email(order.getUser().getEmail().value())
                .orderItems(orderItemDTOs)
                .totalPrice(order.getTotalPrice())
                .currency(order.getCurrency().name())
                .shippingAddress(addressDTO)
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .checkoutSessionUrl(order.getCheckoutSessionUrl())
                .paymentStatus(order.getPaymentStatus())
                .paymentTransactionId(order.getPaymentTransactionId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .checkoutSessionExpiresAt(order.getCheckoutSessionExpiresAt())
                .receiptUrl(order.getReceiptUrl())
                .build();
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
        Address shippingAddress = new Address(
                orderDTO.shippingAddress().street(),
                orderDTO.shippingAddress().city(),
                new ZipCode(orderDTO.shippingAddress().zipCode()),
                orderDTO.shippingAddress().country()
        );

        return Order.builder()
                .id(orderDTO.id())
                .totalPrice(orderDTO.totalPrice())
                .currency(Currency.valueOf(orderDTO.currency()))
                .shippingAddress(shippingAddress)
                .shippingMethod(orderDTO.shippingMethod())
                .paymentMethod(orderDTO.paymentMethod())
                .paymentTransactionId(orderDTO.paymentTransactionId())
                .checkoutSessionUrl(orderDTO.checkoutSessionUrl())
                .checkoutSessionExpiresAt(orderDTO.checkoutSessionExpiresAt())
                .receiptUrl(orderDTO.receiptUrl())
                .paymentStatus(orderDTO.paymentStatus())
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

        return OrderItem.builder()
                .id(orderItemDTO.id())
                .quantity(orderItemDTO.quantity())
                .price(orderItemDTO.price())
                .build();
    }
}
