package com.flipkart.ecommerce_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", unique = true)
  private UUID id;

  @JsonIgnore
  @OneToOne(optional = false, orphanRemoval = true)
  @JoinColumn(name = "product_id", nullable = false, unique = true)
  private Product product;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "reserved_quantity", nullable = false)
  private Integer reservedQuantity;

  @Column(name = "threshold", nullable = false)
  private Integer threshold;

  @Column(name = "last_updated", nullable = false)
  private LocalDateTime lastUpdated;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private InventoryStatus status;
}
