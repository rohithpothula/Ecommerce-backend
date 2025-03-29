package com.flipkart.ecommerce_backend.models;

public enum ProductStatus {
    DRAFT("DRAFT"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    DELETED("DELETED");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductStatus fromValue(String value) {
        for (ProductStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid product status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
