package com.rj.ecommerce_backend.domain.user.dtos;

import java.time.LocalDate;

public record UpdateBasicDetailsRequest(
        String firstName,
        String lastName,
        AddressDto address,
        PhoneNumberDto phoneNumber,
        LocalDate dateOfBirth
) {}
