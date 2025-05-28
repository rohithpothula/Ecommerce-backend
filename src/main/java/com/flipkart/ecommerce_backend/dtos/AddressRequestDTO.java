package com.flipkart.ecommerce_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating an Address. Contains necessary fields and
 * validation constraints.
 */
public record AddressRequestDTO(
    @NotBlank(message = "Address Line 1 cannot be blank")
        @Size(max = 255, message = "Address Line 1 cannot exceed 255 characters")
        String addressLine1,
    @Size(max = 255, message = "Address Line 2 cannot exceed 255 characters")
        String addressLine2, // Optional
    @NotBlank(message = "City cannot be blank")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,
    @NotBlank(message = "State cannot be blank")
        @Size(max = 100, message = "State cannot exceed 100 characters")
        String state,
    @NotBlank(message = "Country cannot be blank")
        @Size(max = 100, message = "Country cannot exceed 100 characters")
        String country,
    @NotBlank(message = "Postal Code cannot be blank")
        @Size(max = 20, message = "Postal Code cannot exceed 20 characters")
        String postalCode) {}
