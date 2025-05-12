package com.flipkart.ecommerce_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "product_variant",
    indexes = {
      @Index(name = "idx_variant_product_id", columnList = "product_id"),
      @Index(name = "idx_variant_sku", columnList = "sku", unique = true)
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_variant_attributes",
          columnNames = {"product_id", "color", "size"})
    })
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @NotBlank(message = "SKU cannot be blank")
  @Size(max = 100)
  @Column(length = 100, unique = true, nullable = false)
  private String sku;

  @NotNull(message = "Variant variantPrice cannot be null")
  @DecimalMin(value = "0.00", inclusive = false, message = "Price must be positive")
  @Digits(integer = 10, fraction = 2)
  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal price;

  @NotNull(message = "Quantity cannot be null")
  @Min(value = 0, message = "Quantity cannot be negative")
  @Column(nullable = false)
  private Integer quantity = 0;

  @Size(max = 50)
  @Column(length = 50)
  private String color;

  @Size(max = 50)
  @Column(length = 50)
  private String size;

  @Column(length = 1024)
  private String imageUrl;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private Instant updatedAt = Instant.now();

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}
