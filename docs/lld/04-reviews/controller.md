# Review Controller - Low Level Design

## Review Flow Sequence

```mermaid
sequenceDiagram
    participant Client
    participant ReviewController
    participant AdminReviewController
    participant ReviewService
    participant ValidationService
    participant ModerationService
    participant Repository
    
    %% Submit Review Flow
    Client->>ReviewController: POST /api/products/{id}/reviews
    ReviewController->>ReviewService: submitReview(request, userId)
    ReviewService->>ValidationService: validateReviewSubmission(request)
    ValidationService->>Repository: hasUserPurchasedProduct()
    ValidationService->>Repository: existingReview()
    ReviewService->>Repository: save(review)
    ReviewService-->>ReviewController: ReviewDto
    ReviewController-->>Client: 201 Created
    
    %% Get Reviews Flow
    Client->>ReviewController: GET /api/products/{id}/reviews
    ReviewController->>ReviewService: getReviewsForProduct(id, pageable)
    ReviewService->>Repository: findByProductId()
    ReviewService-->>ReviewController: Page<ReviewSummaryDto>
    ReviewController-->>Client: 200 OK
    
    %% Vote Review Flow
    Client->>ReviewController: POST /api/reviews/{id}/vote
    ReviewController->>ReviewService: voteReview(id, request, userId)
    ReviewService->>ValidationService: validateReviewVote(id, userId)
    ReviewService->>Repository: updateVoteCounts()
    ReviewService-->>ReviewController: void
    ReviewController-->>Client: 200 OK
    
    %% Moderation Flow
    Client->>AdminReviewController: PUT /api/admin/reviews/{id}/status
    AdminReviewController->>ModerationService: approveReview(id, adminId)
    ModerationService->>Repository: updateStatus()
    ModerationService-->>AdminReviewController: ReviewDto
    AdminReviewController-->>Client: 200 OK
```

## REST Controller (`ReviewController.java`)
- Endpoints for submitting a review, getting reviews for a product, getting a specific review, editing/deleting own review, voting on a review.
- Authorization checks (user purchased product to review, user owns review to edit/delete).

## Admin REST Controller (`AdminReviewController.java`)
- Endpoints for listing reviews needing moderation, approving/rejecting reviews.
- Secured with admin role checks.

## Request DTOs
- `SubmitReviewRequest` (productId, rating, title, body)
- `UpdateReviewRequest` (rating, title, body)
- `ReviewVoteRequest` (voteType: HELPFUL/UNHELPFUL)
- `ModerateReviewRequest` (Admin only: status: APPROVED/REJECTED, reason)

## Response DTOs
- `ReviewDto` (Includes review details, user info, product info, votes)
- `ReviewSummaryDto` (For listing: rating, title snippet, user name, date)
- `ProductRatingStatsDto` (averageRating, totalReviews, ratingCounts)

## Exception Handling
- Handling `ReviewNotFoundException`.
- Handling `UserNotPurchasedProductException`.
- Handling `DuplicateReviewException`.
- Handling authorization failures.
- Handling invalid input (e.g., rating out of range).
