# Wishlist Controller - Low Level Design

## REST Controller (`WishlistController.java`)
- Endpoints for getting the user's wishlist, adding an item, removing an item, moving an item to the cart.
- Requires user authentication for all endpoints.

## Request DTOs
- `AddItemToWishlistRequest` (productId)
- `MoveWishlistItemToCartRequest` (Optional: quantity, if different from 1)

## Response DTOs
- `WishlistDto` (List of `WishlistItemDto`)
- `WishlistItemDto` (productId, productName, productImageUrl, price, addedDate, inStockStatus)

## Exception Handling
- Handling `ProductNotFoundException`.
- Handling `ItemAlreadyInWishlistException`.
- Handling `ItemNotInWishlistException`.
- Handling authorization failures.
