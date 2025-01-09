package com.rj.ecommerce_backend.domain.user.dtos;

import com.rj.ecommerce_backend.domain.user.Authority;
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
