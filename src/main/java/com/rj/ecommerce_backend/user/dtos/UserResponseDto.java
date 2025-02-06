package com.rj.ecommerce_backend.user.dtos;

import java.time.LocalDate;
import java.util.Set;

public record UserResponseDto(
        Long userId,
        String firstName,
        String lastName,
        String email,
        AddressDto address,
        PhoneNumberDto phoneNumber,
        LocalDate dateOfBirth,
        Set<String> authorities,
        Boolean isActive
) { }
