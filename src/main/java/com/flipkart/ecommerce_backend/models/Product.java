package com.flipkart.ecommerce_backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "short_description", nullable = false)
  private String shortDescription;

  @Column(name = "long_description")
  private String longDescription;

  @Column(name = "price")
  private Double price;

  @Column(name = "category")
  private String category;

  @Column(name = "brand")
  private String brand;

  @Column(name = "image_url")
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ProductStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @OneToOne(
      mappedBy = "product",
      cascade = CascadeType.REMOVE,
      optional = false,
      orphanRemoval = true)
  private Inventory inventory;

}
