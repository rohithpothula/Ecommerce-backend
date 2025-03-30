package com.flipkart.ecommerce_backend.dtos;


import java.util.UUID;

/**
 * Data Transfer Object representing an Address for API responses.
 */
public record AddressDTO(
        UUID id,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        String postalCode
) { }
