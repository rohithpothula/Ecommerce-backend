# Promotions Service - Low Level Design

## Service Interfaces

### PromotionService Interface
```java
public interface PromotionService {
    List<PromotionDto> getActivePromotions();
    CouponValidationResponse validateCoupon(ValidateCouponRequest request);
    DiscountCalculationResult calculateDiscount(Cart cart, String couponCode);
    void recordPromotionUsage(Long promotionId, Long userId, Long orderId, BigDecimal discountAmount);
    boolean isPromotionApplicable(Long promotionId, Cart cart);
    int getUserPromotionUsageCount(Long promotionId, Long userId);
}
```

### AdminPromotionService Interface
```java
public interface AdminPromotionService {
    PromotionDto createPromotion(CreatePromotionRequest request);
    PromotionDto updatePromotion(Long promotionId, UpdatePromotionRequest request);
    void deactivatePromotion(Long promotionId);
    PromotionDetailsDto getPromotionDetails(Long promotionId);
    Page<PromotionSummaryDto> getAllPromotions(PromotionFilterCriteria criteria, Pageable pageable);
    List<PromotionUsageDto> getPromotionUsageStats(Long promotionId);
}
```

## Implementation Classes

### PromotionServiceImpl
```java
@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository usageRepository;
    private final CartService cartService;
    private final PromotionDtoMapper mapper;

    @Override
    public CouponValidationResponse validateCoupon(ValidateCouponRequest request) {
        Cart cart = cartService.getCartEntity(request.getCartId());
        Promotion promotion = findPromotionByCode(request.getCouponCode());

        // Validate basic conditions
        validatePromotionStatus(promotion);
        validateDateRange(promotion);
        validateUsageLimits(promotion, cart.getUser().getId());
        validateMinimumOrderValue(promotion, cart.getTotal());

        // Calculate potential discount
        DiscountCalculationResult discount = calculateDiscount(cart, promotion);

        return new CouponValidationResponse(true, discount.getDiscountAmount(), 
            "Coupon valid: " + promotion.getName());
    }

    @Override
    public DiscountCalculationResult calculateDiscount(Cart cart, String couponCode) {
        Promotion promotion = findPromotionByCode(couponCode);
        return calculateDiscount(cart, promotion);
    }

    private DiscountCalculationResult calculateDiscount(Cart cart, Promotion promotion) {
        BigDecimal cartTotal = cart.getTotal();
        BigDecimal discountAmount;

        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = cartTotal.multiply(promotion.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discountAmount = promotion.getDiscountValue();
        }

        // Apply max discount cap if exists
        if (promotion.getMaxDiscountAmount() != null && 
            discountAmount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discountAmount = promotion.getMaxDiscountAmount();
        }

        return new DiscountCalculationResult(discountAmount, promotion.getId());
    }

    @Override
    public void recordPromotionUsage(Long promotionId, Long userId, Long orderId, BigDecimal discountAmount) {
        Promotion promotion = findPromotion(promotionId);
        
        // Create usage record
        PromotionUsage usage = new PromotionUsage();
        usage.setPromotion(promotion);
        usage.setUser(userService.getUserEntity(userId));
        usage.setOrder(orderService.getOrder(orderId));
        usage.setDiscountAmount(discountAmount);
        
        // Update promotion usage counter
        promotion.setCurrentUsage(promotion.getCurrentUsage() + 1);
        
        // Save both entities
        usageRepository.save(usage);
        promotionRepository.save(promotion);
    }

    private void validatePromotionStatus(Promotion promotion) {
        if (!promotion.isActive()) {
            throw new InvalidCouponException("This promotion is no longer active.");
        }
    }

    private void validateDateRange(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate()) || 
            (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate()))) {
            throw new ExpiredCouponException("This promotion is not valid at this time.");
        }
    }

    private void validateUsageLimits(Promotion promotion, Long userId) {
        // Check global usage limit
        if (promotion.getMaxUsage() != null && 
            promotion.getCurrentUsage() >= promotion.getMaxUsage()) {
            throw new MaxUsageReachedException("This promotion has reached its maximum usage limit.");
        }

        // Check per-user limit
        if (promotion.getMaxUsagePerUser() != null) {
            int userUsage = getUserPromotionUsageCount(promotion.getId(), userId);
            if (userUsage >= promotion.getMaxUsagePerUser()) {
                throw new MaxUsageReachedException("You have reached the maximum usage limit for this promotion.");
            }
        }
    }

    private void validateMinimumOrderValue(Promotion promotion, BigDecimal cartTotal) {
        if (promotion.getMinimumOrderValue() != null && 
            cartTotal.compareTo(promotion.getMinimumOrderValue()) < 0) {
            throw new MinimumOrderValueNotMetException(
                "Order total must be at least " + promotion.getMinimumOrderValue() + 
                " to use this promotion.");
        }
    }
}
```

## Data Objects (Helper)

```java
@Data
public class DiscountCalculationResult {
    private final BigDecimal discountAmount;
    private final Long promotionId;
}

@Data
public class PromotionFilterCriteria {
    private Boolean active;
    private PromotionType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String searchTerm; // For searching by name/code
}
