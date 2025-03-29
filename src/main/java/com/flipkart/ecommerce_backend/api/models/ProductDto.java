package com.flipkart.ecommerce_backend.api.models;

public record ProductDto(
    Long id,
    String name,
    String shortDescription,
    String longDescription,
    Double price,
    String category,
    String brand,
    String imageUrl,
    String status
) {}
