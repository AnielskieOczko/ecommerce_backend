package com.rj.ecommerce_backend.domain.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record CreateUserRequest(
        @NotEmpty String firstName,
        String lastName,
        @NotEmpty @Email String email,
        @NotEmpty String password,
        @NotNull AddressDto address,
        PhoneNumberDto phoneNumber,
        LocalDate dateOfBirth,
        Set<String> authorities
) { }
