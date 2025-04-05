package com.flipkart.ecommerce_backend.dtos;


import com.flipkart.ecommerce_backend.models.ProductStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductUpdateDto(
        @Size(max = 255) String name,
        @Size(max = 500) String shortDescription,
        String longDescription,
        @NotNull(message = "Price is required for update")
        @DecimalMin(value = "0.01") @Digits(integer=10, fraction=2)
        BigDecimal price,
        @Size(max = 100) String category,
        @Size(max = 100) String brand,
        @Size(max = 1024) String imageUrl,
        ProductStatus status
) {}
