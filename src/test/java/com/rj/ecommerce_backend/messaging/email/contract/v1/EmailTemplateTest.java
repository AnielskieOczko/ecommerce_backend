package com.rj.ecommerce_backend.messaging.email.contract.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTemplateTest {

    @Test
    void shouldReturnCorrectTemplateId() {
        // Given & When & Then
        assertEquals("customer-welcome", EmailTemplate.CUSTOMER_WELCOME.getTemplateId());
        assertEquals("order-confirmation", EmailTemplate.ORDER_CONFIRMATION.getTemplateId());
        assertEquals("payment-failed", EmailTemplate.PAYMENT_FAILED.getTemplateId());
        assertEquals("test-message-template", EmailTemplate.TEST_MESSAGE.getTemplateId());
    }

    @Test
    void shouldFindTemplateByTemplateId() {
        // Given
        String templateId = "customer-welcome";

        // When
        EmailTemplate template = EmailTemplate.fromTemplateId(templateId);

        // Then
        assertEquals(EmailTemplate.CUSTOMER_WELCOME, template);
    }

    @Test
    void shouldFindAllTemplatesByTheirIds() {
        // Test all enum values can be found by their IDs
        for (EmailTemplate template : EmailTemplate.values()) {
            assertEquals(template, EmailTemplate.fromTemplateId(template.getTemplateId()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-template", "", "unknown", "welcome"})
    void shouldThrowExceptionForInvalidTemplateId(String invalidTemplateId) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailTemplate.fromTemplateId(invalidTemplateId)
        );
        
        assertEquals("Invalid templateId: " + invalidTemplateId, exception.getMessage());
    }
}
