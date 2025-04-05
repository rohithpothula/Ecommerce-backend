# Wishlist Domain Models - Low Level Design

## WishlistItem Entity
```java
@Entity
@Table(name = "wishlist_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "product_id"}) // One entry per user per product
})
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @CreatedDate
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    // Note: A separate Wishlist entity might be overkill if it's always 1-to-1 with User.
    // We can directly link WishlistItem to LocalUser.
}
```

## Database Schema (Conceptual)
```mermaid
erDiagram
    WISHLIST_ITEMS }|--|| USERS : belongs_to
    WISHLIST_ITEMS }|--|| PRODUCTS : references

    WISHLIST_ITEMS {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT product_id FK
        TIMESTAMP added_at
        UNIQUE (user_id, product_id)
    }

    USERS { BIGINT id PK }
    PRODUCTS { BIGINT id PK }
