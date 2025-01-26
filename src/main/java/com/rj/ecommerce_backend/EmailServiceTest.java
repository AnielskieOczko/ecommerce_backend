package com.rj.ecommerce_backend;

import com.rj.ecommerce_backend.domain.order.Order;
import com.rj.ecommerce_backend.domain.order.OrderItem;
import com.rj.ecommerce_backend.domain.order.OrderStatus;
import com.rj.ecommerce_backend.domain.order.PaymentMethod;
import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.valueobject.Address;
import com.rj.ecommerce_backend.domain.user.valueobject.Email;
import com.rj.ecommerce_backend.domain.user.valueobject.ZipCode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class EmailServiceTest {

    private final RabbitTemplate rabbitTemplate;

    public void processTestEmailMessage() {

        Order order = Order.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .email(new Email("john.doe@example.com"))
                        .address(new Address("new string", "new city", new ZipCode("1114"), "PL"))
                        .build())
                .orderItems(Arrays.asList(
                        OrderItem.builder().id(1L).quantity(2).price(BigDecimal.valueOf(10.50)).build(),
                        OrderItem.builder().id(2L).quantity(1).price(BigDecimal.valueOf(25.00)).build()
                ))
                .totalPrice(BigDecimal.valueOf(46.00))
                .shippingAddress(Address.builder()
                        .street("123 Main St")
                        .city("Anytown")
                        .zipCode(new ZipCode("11111"))
                        .country("USA")
                        .build())
                .paymentMethod(PaymentMethod.BLIK)
                .paymentTransactionId("txn_1234567890abcdef")
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("System")
                .lastModifiedBy("System")
                .build();

        EmailMessageDTO emailMessageDTO = EmailMessageDTO.builder()
                .to("rafaljankowski7@gmail.com")
                .subject("Test message" + LocalDateTime.now())
                .template("test-message-template")
                .data(Map.of(
                        "email", order.getUser().getEmail().value(),
                        "Footer", order.getUser().getAddress().city()))
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                emailMessageDTO);
    }
}
