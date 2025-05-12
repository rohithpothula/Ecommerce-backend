package com.flipkart.ecommerce_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(
    name = "product",
    indexes = {@Index(name = "idx_product_name", columnList = "name")})
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @NotBlank(message = "Product name cannot be blank")
  @Size(max = 255)
  @Column(length = 255, nullable = false)
  private String name;

  @Size(max = 500)
  @Column(name = "short_description", length = 500, nullable = false)
  private String shortDescription;

  @Lob
  @Column(name = "long_description", columnDefinition = "TEXT")
  private String longDescription;

  @NotNull(message = "Base variantPrice cannot be null")
  @DecimalMin(value = "0.00", inclusive = false, message = "Price must be positive")
  @Digits(integer = 10, fraction = 2)
  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal price;

  @NotBlank
  @Size(max = 100)
  @Column(length = 100)
  private String category; // Consider making this a @ManyToOne to a Category entity

  @NotBlank
  @Size(max = 100)
  @Column(length = 100)
  private String brand; // Consider making this a @ManyToOne to a Brand entity

  @Column(name = "image_url", length = 1024)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ProductStatus status;

  @OneToMany(
      mappedBy = "product",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ProductVariant> variants = new ArrayList<>();

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private Instant updatedAt = Instant.now();

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  //  @OneToOne(
  //      mappedBy = "product",
  //      cascade = CascadeType.REMOVE,
  //      optional = false,
  //      orphanRemoval = true)
  //  private Inventory inventory;

  public void addVariant(ProductVariant variant) {
    variants.add(variant);
    variant.setProduct(this);
  }

  public void removeVariant(ProductVariant variant) {
    variants.remove(variant);
    variant.setProduct(null);
  }
}
