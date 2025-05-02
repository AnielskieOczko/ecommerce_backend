package com.rj.ecommerce_backend.messaging.email.contract.v1.customer;

import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WelcomeEmailRequestDTOTest {

    @Test
    void shouldCreateValidInstance() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String subject = "Welcome to Our Store!";
        String customerName = "John Doe";
        String couponCode = "WELCOME10";
        Map<String, Object> additionalData = Map.of("key1", "value1");
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        WelcomeEmailRequestDTO dto = new WelcomeEmailRequestDTO(
                messageId, version, to, subject, customerName, couponCode, additionalData, timestamp
        );

        // Then
        assertEquals(messageId, dto.messageId());
        assertEquals(version, dto.version());
        assertEquals(to, dto.to());
        assertEquals(subject, dto.subject());
        assertEquals(customerName, dto.customerName());
        assertEquals(couponCode, dto.couponCode());
        assertEquals(additionalData, dto.additionalData());
        assertEquals(timestamp, dto.timestamp());
        assertEquals(EmailTemplate.CUSTOMER_WELCOME, dto.getTemplate());
    }

    @Test
    void shouldSetDefaultSubjectWhenNull() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When
        WelcomeEmailRequestDTO dto = new WelcomeEmailRequestDTO(
                messageId, version, to, null, customerName, null, null, null
        );

        // Then
        assertEquals("Welcome to Our Store!", dto.subject());
    }

    @Test
    void shouldSetDefaultSubjectWhenBlank() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When
        WelcomeEmailRequestDTO dto = new WelcomeEmailRequestDTO(
                messageId, version, to, "", customerName, null, null, null
        );

        // Then
        assertEquals("Welcome to Our Store!", dto.subject());
    }

    @Test
    void shouldSetDefaultTimestampWhenNull() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When
        WelcomeEmailRequestDTO dto = new WelcomeEmailRequestDTO(
                messageId, version, to, "Subject", customerName, null, null, null
        );

        // Then
        assertNotNull(dto.timestamp());
    }

    @Test
    void shouldSetEmptyMapWhenAdditionalDataIsNull() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When
        WelcomeEmailRequestDTO dto = new WelcomeEmailRequestDTO(
                messageId, version, to, "Subject", customerName, null, null, LocalDateTime.now()
        );

        // Then
        assertNotNull(dto.additionalData());
        assertTrue(dto.additionalData().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenMessageIdIsEmpty() {
        // Given
        String messageId = "";
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenMessageIdIsBlank() {
        // Given
        String messageId = " ";
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenVersionIsEmpty() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenVersionIsBlank() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = " ";
        String to = "customer@example.com";
        String customerName = "John Doe";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenToIsEmpty() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "";
        String customerName = "John Doe";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenToIsBlank() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = " ";
        String customerName = "John Doe";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenCustomerNameIsEmpty() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenCustomerNameIsBlank() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = " ";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new WelcomeEmailRequestDTO(
                    messageId, version, to, "Subject", customerName, null, null, null
            );
        });
    }

    @Test
    void shouldReturnCorrectTemplateData() {
        // Given
        String messageId = UUID.randomUUID().toString();
        String version = "1.0";
        String to = "customer@example.com";
        String customerName = "John Doe";
        String couponCode = "WELCOME10";
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("key1", "value1");
        additionalData.put("key2", 123);

        // When
        WelcomeEmailRequestDTO dto = new WelcomeEmailRequestDTO(
                messageId, version, to, "Subject", customerName, couponCode, additionalData, LocalDateTime.now()
        );
        Map<String, Object> templateData = dto.getTemplateData();

        // Then
        assertEquals(customerName, templateData.get("customerName"));
        assertEquals(couponCode, templateData.get("couponCode"));
        assertEquals("value1", templateData.get("key1"));
        assertEquals(123, templateData.get("key2"));
    }

    @Test
    void shouldCreateInstanceWithDefaultBuilder() {
        // When
        WelcomeEmailRequestDTO dto = WelcomeEmailRequestDTO.defaultBuilder()
                .to("customer@example.com")
                .customerName("John Doe")
                .build();

        // Then
        assertNotNull(dto.messageId());
        assertEquals("1.0", dto.version());
        assertEquals("customer@example.com", dto.to());
        assertEquals("Welcome to Our Store!", dto.subject());
        assertEquals("John Doe", dto.customerName());
        assertNotNull(dto.timestamp());
        assertNotNull(dto.additionalData());
    }

    @Test
    void shouldCreateImmutableCopyWithWith() {
        // Given
        WelcomeEmailRequestDTO original = WelcomeEmailRequestDTO.defaultBuilder()
                .to("customer@example.com")
                .customerName("John Doe")
                .build();

        // When
        WelcomeEmailRequestDTO modified = original.withCustomerName("Jane Doe")
                .withTo("jane@example.com")
                .withCouponCode("JANE20");

        // Then
        assertEquals("John Doe", original.customerName());
        assertEquals("customer@example.com", original.to());
        assertNull(original.couponCode());

        assertEquals("Jane Doe", modified.customerName());
        assertEquals("jane@example.com", modified.to());
        assertEquals("JANE20", modified.couponCode());
    }
}
