package com.rj.ecommerce_backend.user.dtos;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.util.Set;

public record AdminUpdateUserRequest(
        String firstName,
        String lastName,
        @Email String email,
        AddressDto address,
        PhoneNumberDto phoneNumber,
        LocalDate dateOfBirth,
        Set<String> authorities,
        Boolean isActive
) { }
