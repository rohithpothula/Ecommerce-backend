# Promotions Controller - Low Level Design

## REST Controller (`PromotionController.java`)
- Endpoints for retrieving available promotions, validating coupon codes, checking promotion eligibility.
- Publicly accessible for viewing active promotions.
- User authentication required for applying/validating codes.

## Admin REST Controller (`AdminPromotionController.java`)
- Endpoints for creating, updating, deleting promotions, viewing usage statistics.
- Secured with admin role checks.

## Request DTOs
- `ValidateCouponRequest` (cartId, couponCode)
- `CreatePromotionRequest` (Admin: type, code, discountType, value, minimumOrderValue, startDate, endDate, maxUsage, etc.)
- `UpdatePromotionRequest` (Admin: similar to create but partial)

## Response DTOs
- `PromotionDto` (General promotion details)
- `CouponValidationResponse` (isValid, discount, message)
- `PromotionSummaryDto` (For listing: id, code, type, status, usage)
- `PromotionDetailsDto` (Full details including rules and usage stats)

## Exception Handling
- Handling `InvalidCouponException`.
- Handling `ExpiredCouponException`.
- Handling `MinimumOrderValueNotMetException`.
- Handling `CouponAlreadyUsedException`.
- Handling `MaxUsageReachedException`.
- Handling authorization failures.
