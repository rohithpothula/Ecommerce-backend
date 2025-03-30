package com.flipkart.ecommerce_backend.models;

/**
 * Enumeration defining the standard roles within the e-commerce application.
 * The 'ROLE_' prefix aligns with Spring Security's convention for hasRole() checks.
 */
public enum ERole {
    /**
     * Standard customer role with basic permissions to browse and order.
     */
    ROLE_USER,

    /**
     * Administrator role with full access to manage users, products, orders, etc.
     */
    ROLE_ADMIN,

    /**
     * Optional: Role for users managing specific content or community features.
     */
    ROLE_MODERATOR,

    /**
     * Optional: Role for users who list and manage products for sale.
     */
    ROLE_SELLER

    // Add any other roles specific to your e-commerce platform, for example:
    // ROLE_SUPPORT,
    // ROLE_WAREHOUSE_MANAGER
}
