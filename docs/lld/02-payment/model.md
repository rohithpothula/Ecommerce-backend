# Payment Domain Models - Low Level Design

## PaymentTransaction Entity
```java
@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private WebOrder order;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "gateway", nullable = false, length = 50)
    private String gateway; // e.g., "STRIPE", "PAYPAL"

    @Column(name = "gateway_transaction_id", length = 255) // ID from the payment gateway
    private String gatewayTransactionId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency; // e.g., "USD", "INR"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentStatus status;

    @Column(name = "payment_method_details", length = 255) // e.g., "Visa **** 4242"
    private String paymentMethodDetails;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

## PaymentStatus Enum
```java
public enum PaymentStatus {
    PENDING,      // Payment initiated but not confirmed
    SUCCESSFUL,   // Payment completed successfully
    FAILED,       // Payment failed
    REFUNDED,     // Payment was refunded
    PARTIALLY_REFUNDED,
    CANCELLED,    // Payment was cancelled before completion
    REQUIRES_ACTION // e.g., 3D Secure authentication needed
}
```

## PaymentMethod Entity (Optional - If storing payment methods)
```java
@Entity
@Table(name = "user_payment_methods")
@Data
@NoArgsConstructor
public class UserPaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "gateway", nullable = false, length = 50)
    private String gateway;

    @Column(name = "gateway_token", nullable = false, length = 255) // Token representing the payment method at the gateway
    private String gatewayToken;

    @Column(name = "type", nullable = false, length = 50) // e.g., "CARD", "PAYPAL"
    private String type;

    @Column(name = "details", length = 255) // e.g., "Visa **** 4242", "user@example.com"
    private String details;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Column(name = "expiry_date", length = 7) // MM/YYYY for cards
    private String expiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

## Database Schema (Conceptual)
```mermaid
erDiagram
    PAYMENT_TRANSACTIONS }|--|| ORDERS : relates_to
    PAYMENT_TRANSACTIONS }|--|| USERS : belongs_to
    USER_PAYMENT_METHODS }|--|| USERS : belongs_to

    PAYMENT_TRANSACTIONS {
        BIGINT id PK
        BIGINT order_id FK
        BIGINT user_id FK
        VARCHAR(50) gateway
        VARCHAR(255) gateway_transaction_id
        DECIMAL(10,2) amount
        VARCHAR(3) currency
        VARCHAR(50) status
        VARCHAR(255) payment_method_details
        TEXT error_message
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    USER_PAYMENT_METHODS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR(50) gateway
        VARCHAR(255) gateway_token
        VARCHAR(50) type
        VARCHAR(255) details
        BOOLEAN is_default
        VARCHAR(7) expiry_date
        TIMESTAMP created_at
    }

    ORDERS {
        BIGINT id PK
        # ... other order fields
    }

    USERS {
        BIGINT id PK
        # ... other user fields
    }
