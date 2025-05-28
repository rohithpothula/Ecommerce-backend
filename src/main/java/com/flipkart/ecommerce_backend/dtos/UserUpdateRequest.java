package com.flipkart.ecommerce_backend.dtos;

import com.flipkart.ecommerce_backend.models.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * Data Transfer Object for updating an existing user's details. Fields are typically optional,
 * allowing partial updates. Use validation annotations to enforce constraints on provided values.
 */
public record UserUpdateRequest(
    @Size(max = 50, message = "First name cannot exceed 50 characters") String firstName,
    @Size(max = 50, message = "Last name cannot exceed 50 characters") String lastName,
    @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,
    Boolean enabled,
    Set<ERole> roles) {}
