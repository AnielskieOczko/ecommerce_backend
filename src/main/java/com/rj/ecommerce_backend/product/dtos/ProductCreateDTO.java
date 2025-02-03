package com.rj.ecommerce_backend.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(
        description = "Data transfer object for creating a new product",
        title = "Product Creation Request"
)
public record ProductCreateDTO(
        @Schema(
                description = "The name of the product - must not be empty",
                example = "Smartphone X1",
                minLength = 1,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name cannot be blank")
        String name,

        @Schema(
                description = "Detailed description of the product - provides key features and specifications",
                example = "Latest smartphone model featuring 6.7-inch OLED display, 5G connectivity, and 128GB storage",
                minLength = 10,
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Size(min = 10, max = 200, message = "Description must be between 10 and 200 characters")
        String description,

        @Schema(
                description = "Product price in the specified currency - must be greater than 0",
                example = "999.99",
                minimum = "0.01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Price cannot be null")
        @DecimalMin("0.01")
        BigDecimal price,

        @Schema(
                description = "ISO 4217 currency code for the price (e.g., USD, EUR, GBP)",
                example = "USD",
                pattern = "^[A-Z]{3}$",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Currency code cannot be null")
        String currencyCode,

        @Schema(
                description = "Initial stock quantity - must be at least 1",
                example = "100",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Quantity cannot be null")
        @Min(1)
        Integer quantity,

        @Schema(
                description = "List of category IDs this product belongs to. A product can be in multiple categories",
                example = "[1, 2, 3]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        List<Long> categoryIds,

        @Schema(
                description = "List of images associated with the product",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        List<ImageDTO> imageList
) {
}
