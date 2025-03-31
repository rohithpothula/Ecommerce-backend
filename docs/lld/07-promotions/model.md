# Promotions Domain Models - Low Level Design

## Promotion Entity
```java
@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type", nullable = false, length = 50)
    private PromotionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "minimum_order_value", precision = 10, scale = 2)
    private BigDecimal minimumOrderValue;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser;

    @Column(name = "current_usage", nullable = false)
    private Integer currentUsage = 0;

    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Optional: For category-specific promotions
    @ManyToMany
    @JoinTable(
        name = "promotion_categories",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> applicableCategories = new HashSet<>();

    // Optional: For product-specific promotions
    @ManyToMany
    @JoinTable(
        name = "promotion_products",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> applicableProducts = new HashSet<>();
}
```

## PromotionUsage Entity
```java
@Entity
@Table(name = "promotion_usages", indexes = {
    @Index(name = "idx_promotion_user", columnList = "promotion_id,user_id")
})
@Data
@NoArgsConstructor
public class PromotionUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private WebOrder order;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt = LocalDateTime.now();
}
```

## Enums

### PromotionType
```java
public enum PromotionType {
    COUPON,         // User must enter code
    AUTOMATIC       // Applied automatically if conditions met
}
```

### DiscountType
```java
public enum DiscountType {
    PERCENTAGE,     // e.g., 10% off
    FIXED_AMOUNT    // e.g., $50 off
}
```

## Database Schema (Conceptual)
```mermaid
erDiagram
    PROMOTIONS ||--o{ PROMOTION_USAGES : "tracks usage"
    PROMOTIONS }o--o{ CATEGORIES : "applies to"
    PROMOTIONS }o--o{ PRODUCTS : "applies to"
    PROMOTION_USAGES }|--|| USERS : "used by"
    PROMOTION_USAGES }|--|| ORDERS : "applied to"

    PROMOTIONS {
        BIGINT id PK
        VARCHAR(100) name
        VARCHAR(50) code UK
        VARCHAR(50) promotion_type
        VARCHAR(50) discount_type
        DECIMAL(10,2) discount_value
        DECIMAL(10,2) minimum_order_value "Nullable"
        DECIMAL(10,2) max_discount_amount "Nullable"
        TIMESTAMP start_date
        TIMESTAMP end_date "Nullable"
        INT max_usage "Nullable"
        INT max_usage_per_user "Nullable"
        INT current_usage
        BOOLEAN active
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    PROMOTION_USAGES {
        BIGINT id PK
        BIGINT promotion_id FK
        BIGINT user_id FK
        BIGINT order_id FK
        DECIMAL(10,2) discount_amount
        TIMESTAMP used_at
    }

    PROMOTION_CATEGORIES {
        BIGINT promotion_id PK, FK
        BIGINT category_id PK, FK
    }

    PROMOTION_PRODUCTS {
        BIGINT promotion_id PK, FK
        BIGINT product_id PK, FK
    }

    CATEGORIES { BIGINT id PK }
    PRODUCTS { BIGINT id PK }
    USERS { BIGINT id PK }
    ORDERS { BIGINT id PK }
