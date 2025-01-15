package com.rj.ecommerce_backend.domain.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Data transfer object representing a product image with its metadata",
        title = "Product Image"
)
public record ImageDTO(
        @Schema(
                description = "Unique identifier of the image",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY,  // This indicates the ID is read-only, typically set by the system
                requiredMode = Schema.RequiredMode.NOT_REQUIRED  // ID might be null in creation requests
        )
        Long id,

        @Schema(
                description = "The file path or URL where the image is stored in the system",
                example = "/products/electronics/smartphone-x1-front.jpg",
                requiredMode = Schema.RequiredMode.REQUIRED,
                pattern = "^[\\w/-]+\\.[a-zA-Z]{3,4}$"  // Basic pattern for file paths
        )
        String path,

        @Schema(
                description = "Alternative text for the image, important for accessibility and SEO",
                example = "Front view of Smartphone X1 in midnight black color",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String altText,

        @Schema(
                description = "MIME type of the image file (e.g., image/jpeg, image/png)",
                example = "image/jpeg",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"image/jpeg", "image/png", "image/gif"}  // Common supported image formats
        )
        String mimeType
) {
}
