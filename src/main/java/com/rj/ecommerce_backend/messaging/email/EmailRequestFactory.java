package com.rj.ecommerce_backend.messaging.email;

import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.AddressDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.CustomerDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.common.MoneyDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.order.OrderEmailRequestDTO;
import com.rj.ecommerce_backend.messaging.email.contract.v1.order.OrderItemDTO;
import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.valueobject.Address;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class EmailRequestFactory {

    /**
     * Create an order confirmation email request
     */
    public OrderEmailRequestDTO createOrderConfirmationRequest(Order order) {
        return OrderEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .to(order.getUser().getEmail().value())
                .template(EmailTemplate.ORDER_CONFIRMATION)
                .orderId(order.getId().toString())
                .orderNumber(order.getId().toString())
                .customer(mapCustomer(order.getUser()))
                .items(mapOrderItems(order))
                .totalAmount(mapMoney(order.getTotalPrice(), order.getCurrency().name()))
                .shippingAddress(mapAddress(order.getShippingAddress()))
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getCreatedAt())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    /**
     * Create an order shipment email request
     */
    public OrderEmailRequestDTO createOrderShipmentRequest(Order order, String trackingNumber, String trackingUrl) {
        return OrderEmailRequestDTO.builder()
                .messageId(UUID.randomUUID().toString())
                .version("1.0")
                .to(order.getUser().getEmail().value())
                .template(EmailTemplate.ORDER_SHIPMENT)
                .orderId(order.getId().toString())
                .orderNumber(order.getId().toString())
                .customer(mapCustomer(order.getUser()))
                .items(mapOrderItems(order))
                .totalAmount(mapMoney(order.getTotalPrice(), order.getCurrency().name()))
                .shippingAddress(mapAddress(order.getShippingAddress()))
                .shippingMethod(order.getShippingMethod())
                .orderDate(order.getCreatedAt())
                .orderStatus(order.getOrderStatus())
                .additionalData(Map.of(
                        "trackingNumber", trackingNumber,
                        "trackingUrl", trackingUrl
                ))
                .build();
    }

    // Mapping methods
    private CustomerDTO mapCustomer(User user) {
        return CustomerDTO.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail().value())
                .phoneNumber(user.getPhoneNumber().value())
                .build();
    }

    private List<OrderItemDTO> mapOrderItems(Order order) {


        return order.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId().toString())
                        .productId(item.getProduct().getId().toString())
                        .productName(item.getProduct().getProductName().value())
                        .quantity(item.getQuantity())
                        .unitPrice(mapMoney(item.getPrice(), order.getCurrency().name()))
                        .totalPrice(mapMoney(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())), order.getCurrency().name()))
                        .build())
                .toList();
    }

    private MoneyDTO mapMoney(BigDecimal amount, String currencyCode) {
        return MoneyDTO.builder()
                .amount(amount)
                .currencyCode(currencyCode)
                .build();
    }

    private AddressDTO mapAddress(Address address) {
        return AddressDTO.builder()
                .street(address.street())
                .city(address.city())
                .zipCode(address.zipCode().value())
                .country(address.country())
                .build();
    }
}
