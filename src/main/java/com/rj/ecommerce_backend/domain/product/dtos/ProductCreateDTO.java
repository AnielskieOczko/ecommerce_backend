package com.rj.ecommerce_backend.domain.product.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateDTO(
        @NotBlank(message = "Name cannot be blank") String name,
        @Size(min = 10, max = 200, message = "Description must be between 10 and 200 characters") String description,
        @NotNull(message = "Price cannot be null") @DecimalMin("0.01") BigDecimal price,
        @NotNull(message = "Currency code cannot be null") String currencyCode,
        @NotNull(message = "Quantity cannot be null") @Min(1) Integer quantity,
        List<Long> categoryIds,
        List<ImageDTO> imageList)
{
}
