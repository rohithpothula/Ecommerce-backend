package com.flipkart.ecommerce_backend.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object representing a specific Product Variant (SKU)
 * for API responses.
 */
public record ProductVariantDto(
        UUID id, // The unique ID of this specific variant record

        @NotBlank // SKU should always be present
        String sku,

        @NotNull
        @DecimalMin(value = "0.00") // Price can be zero (e.g., included item) but not negative
        BigDecimal price, // Price specific to this variant

        @NotNull
        @Min(0) // Quantity cannot be negative
        Integer quantity, // Available stock for this variant

        // Variant Attributes (Examples - match your ProductVariant entity)
        String color, // Nullable if not applicable
        String size,  // Nullable if not applicable
        // String material, // Add other attributes as needed

        String imageUrl // Optional image specific to this variant
) {

}
