# Wishlist Functionality - High Level Design

## Overview
Enable users to save products they are interested in for later viewing or purchase.

## System Architecture
```mermaid
graph TB
    UI[Frontend UI] --> API[Wishlist API Layer]
    API --> WS[Wishlist Service]
    WS --> WR[Wishlist Repository]
    WS --> PR[Product Repository] // To get product details
    WR --> DB[(Database)]
    PR --> DB
```

## Core Components
1.  **Wishlist Service:** Manages adding, removing, and retrieving wishlist items for users.
2.  **Wishlist Repository:** Stores the relationship between users and the products in their wishlists.

## Key Features
- Add products to a user-specific wishlist
- Remove products from the wishlist
- View all items in the wishlist
- Indicate if a product on a product page is already in the user's wishlist
- Move items from wishlist to cart (integration with Cart Service)
- Basic persistence of wishlist items

## API Endpoints
- `GET /api/wishlist` (Get user's wishlist)
- `POST /api/wishlist/items` (Add item to wishlist - body: { productId: ... })
- `DELETE /api/wishlist/items/{productId}` (Remove item from wishlist)
- `POST /api/wishlist/items/{productId}/move-to-cart` (Move item to cart)

## Security Considerations
- Ensure users can only access and modify their own wishlist.
- Rate limiting on adding/removing items.

## Data Models (Conceptual)
- `Wishlist` (Wishlist ID, User ID) - Potentially implicit if only one per user.
- `WishlistItem` (Item ID, Wishlist ID/User ID, Product ID, Added At Timestamp)

## Future Enhancements
- Multiple wishlists per user (e.g., "Birthday", "Home Decor")
- Wishlist sharing
- Price drop notifications for wishlist items
- Stock availability notifications for wishlist items
