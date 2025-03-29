package com.flipkart.ecommerce_backend.api.models;

public record ProductCreateDto(
    String name,
    String shortDescription,
    String longDescription,
    Double price,
    String category,
    String brand,
    String imageUrl
) {}
