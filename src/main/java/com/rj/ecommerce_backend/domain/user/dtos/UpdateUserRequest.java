package com.rj.ecommerce_backend.domain.user.dtos;

import jakarta.validation.constraints.Email;


import java.time.LocalDate;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        @Email String email,
        String password,
        AddressDto address,
        PhoneNumberDto phoneNumber,
        LocalDate dateOfBirth
) { }
