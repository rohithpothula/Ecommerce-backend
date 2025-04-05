package com.flipkart.ecommerce_backend.dtos;

import com.flipkart.ecommerce_backend.models.ProductStatus;

import com.flipkart.ecommerce_backend.models.ProductStatus;

import java.math.BigDecimal;
import java.util.List; // Import List
import java.util.UUID;

public record ProductDto(
    UUID id,
    String name,
    String shortDescription,
    String longDescription,
    BigDecimal price,
    String category,
    String brand,
    String imageUrl,
    ProductStatus status,
    List<ProductVariantDto> variants // Add variants list
) {}
