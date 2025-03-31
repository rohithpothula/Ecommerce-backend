# Promotions and Discount System - High Level Design

## Overview
Implement a flexible system for creating and applying various types of promotions and discounts to carts and orders.

## System Architecture
```mermaid
graph TB
    UI[Frontend UI/Admin Panel] --> API[API Layer]
    API -- Apply Promo --> CS[Cart Service]
    API -- Manage Promo --> PromoAdminAPI[Promotion Admin API]
    CS --> PromoService[Promotion Service]
    PromoAdminAPI --> PromoService
    PromoService --> PromoRepo[Promotion Repository]
    PromoService --> OrderRepo[Order Repository] // To check conditions
    PromoRepo --> DB[(Database)]
    OrderRepo --> DB
```

## Core Components
1.  **Promotion Service:** Validates promotion codes, calculates discounts, applies promotions to carts/orders, manages promotion lifecycle and usage limits.
2.  **Promotion Repository:** Stores promotion definitions, rules, validity periods, and usage tracking.
3.  **Cart/Order Service Integration:** Cart and Order services call the Promotion Service to validate and apply discounts before finalizing totals.

## Key Features
- Support for various promotion types (percentage off, fixed amount off, BOGO, free shipping)
- Coupon code generation and validation
- Automatic promotions (e.g., spend $X get Y% off)
- Usage limits (total uses, per user)
- Date range validity
- Conditions (minimum purchase amount, specific products/categories, user segments)
- Stacking rules (allow/disallow combining promotions)
- Admin interface for managing promotions

## API Endpoints
- `POST /api/cart/apply-promo` (User: Apply promo code to cart)
- `DELETE /api/cart/remove-promo` (User: Remove promo code)
- `GET /api/promotions/active` (User: List currently active public promotions)
- `POST /api/admin/promotions` (Admin: Create promotion)
- `PUT /api/admin/promotions/{promoId}` (Admin: Update promotion)
- `GET /api/admin/promotions` (Admin: List promotions)
- `GET /api/admin/promotions/{promoId}/usage` (Admin: View usage stats)

## Security Considerations
- Prevent promo code abuse (brute-forcing, unauthorized use)
- Secure admin endpoints.
- Ensure discount calculations are accurate and cannot be manipulated.
- Validate promotion conditions server-side.

## Data Models (Conceptual)
- `Promotion` (Promo ID, Code, Type, Value, Start Date, End Date, Min Purchase, Usage Limit, Per User Limit, Status, Conditions JSON)
- `PromotionUsage` (Usage ID, Promo ID, User ID, Order ID, Timestamp)

## Future Enhancements
- Tiered promotions
- Loyalty program integration
- A/B testing for promotions
- More complex condition rules (customer segments, location)
