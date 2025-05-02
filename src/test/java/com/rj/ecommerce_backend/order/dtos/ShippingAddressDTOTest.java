package com.rj.ecommerce_backend.order.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShippingAddressDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidShippingAddressDTO() {
        // Given
        String street = "123 Main St";
        String city = "New York";
        String zipCode = "10001";
        String country = "USA";

        // When
        ShippingAddressDTO addressDTO = new ShippingAddressDTO(street, city, zipCode, country);

        // Then
        assertEquals(street, addressDTO.street());
        assertEquals(city, addressDTO.city());
        assertEquals(zipCode, addressDTO.zipCode());
        assertEquals(country, addressDTO.country());

        Set<ConstraintViolation<ShippingAddressDTO>> violations = validator.validate(addressDTO);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldFailValidationWhenStreetIsInvalid(String street) {
        // Given
        ShippingAddressDTO addressDTO = new ShippingAddressDTO(street, "New York", "10001", "USA");

        // When
        Set<ConstraintViolation<ShippingAddressDTO>> violations = validator.validate(addressDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("street", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldFailValidationWhenCityIsInvalid(String city) {
        // Given
        ShippingAddressDTO addressDTO = new ShippingAddressDTO("123 Main St", city, "10001", "USA");

        // When
        Set<ConstraintViolation<ShippingAddressDTO>> violations = validator.validate(addressDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("city", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldFailValidationWhenZipCodeIsInvalid(String zipCode) {
        // Given
        ShippingAddressDTO addressDTO = new ShippingAddressDTO("123 Main St", "New York", zipCode, "USA");

        // When
        Set<ConstraintViolation<ShippingAddressDTO>> violations = validator.validate(addressDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("zipCode", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldFailValidationWhenCountryIsInvalid(String country) {
        // Given
        ShippingAddressDTO addressDTO = new ShippingAddressDTO("123 Main St", "New York", "10001", country);

        // When
        Set<ConstraintViolation<ShippingAddressDTO>> violations = validator.validate(addressDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("country", violations.iterator().next().getPropertyPath().toString());
    }
}
