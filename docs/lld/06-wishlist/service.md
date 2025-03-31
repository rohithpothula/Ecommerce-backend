# Wishlist Service - Low Level Design

## Service Interfaces

### WishlistService Interface
```java
public interface WishlistService {
    WishlistDto getWishlist(Long userId);
    WishlistItemDto addItem(Long userId, Long productId);
    void removeItem(Long userId, Long productId);
    CartDto moveToCart(Long userId, Long productId, MoveWishlistItemToCartRequest request); // Returns updated cart
    boolean isProductInWishlist(Long userId, Long productId);
}
```

## Implementation Classes

### WishlistServiceImpl
```java
@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Assuming UserRepository exists
    private final CartService cartService; // To move items to cart
    private final WishlistDtoMapper mapper;

    @Override
    public WishlistDto getWishlist(Long userId) {
        List<WishlistItem> items = wishlistItemRepository.findByUser_IdOrderByAddedAtDesc(userId);
        return mapper.toWishlistDto(items);
    }

    @Override
    public WishlistItemDto addItem(Long userId, Long productId) {
        LocalUser user = findUser(userId);
        Product product = findProduct(productId);

        if (wishlistItemRepository.existsByUserAndProduct(user, product)) {
            throw new ItemAlreadyInWishlistException("Product already in wishlist.");
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        // addedAt is set by @CreatedDate

        WishlistItem savedItem = wishlistItemRepository.save(item);
        return mapper.toWishlistItemDto(savedItem);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        LocalUser user = findUser(userId);
        Product product = findProduct(productId);
        int deletedCount = wishlistItemRepository.deleteByUserAndProduct(user, product);
        if (deletedCount == 0) {
            throw new ItemNotInWishlistException("Product not found in wishlist.");
        }
    }

    @Override
    public CartDto moveToCart(Long userId, Long productId, MoveWishlistItemToCartRequest request) {
        LocalUser user = findUser(userId);
        Product product = findProduct(productId);

        // Find and remove from wishlist
        WishlistItem wishlistItem = wishlistItemRepository.findByUserAndProduct(user, product)
            .orElseThrow(() -> new ItemNotInWishlistException("Product not found in wishlist."));
        wishlistItemRepository.delete(wishlistItem);

        // Add to cart
        // Assuming user has one active cart, find or create it
        Cart userCart = cartService.findOrCreateUserCart(userId);
        CartItemRequest cartRequest = new CartItemRequest();
        cartRequest.setUserId(userId);
        cartRequest.setProductId(productId);
        cartRequest.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);

        return cartService.addItem(userCart.getId(), cartRequest);
    }

    @Override
    public boolean isProductInWishlist(Long userId, Long productId) {
        // Simple check without loading entities if possible
        return wishlistItemRepository.existsByUserIdAndProductId(userId, productId);
    }

    // Helper methods findUser, findProduct
    // ...
}
```

## DTO Mappers

### WishlistDtoMapper
```java
@Component
public class WishlistDtoMapper {
    // Inject necessary mappers or services (e.g., InventoryService for stock status)

    public WishlistDto toWishlistDto(List<WishlistItem> items) {
        List<WishlistItemDto> itemDtos = items.stream()
            .map(this::toWishlistItemDto)
            .collect(Collectors.toList());
        return new WishlistDto(itemDtos);
    }

    public WishlistItemDto toWishlistItemDto(WishlistItem item) {
        WishlistItemDto dto = new WishlistItemDto();
        Product product = item.getProduct();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductImageUrl(product.getImageUrl()); // Assuming Product has imageUrl
        dto.setPrice(product.getPrice()); // Assuming Product has price
        dto.setAddedDate(item.getAddedAt());
        // dto.setInStockStatus(inventoryService.isInStock(product.getId())); // Check stock
        return dto;
    }
}
