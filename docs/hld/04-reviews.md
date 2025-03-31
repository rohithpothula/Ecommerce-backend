# Product Reviews and Ratings - High Level Design

## Overview
Allow users to submit reviews and ratings for products they have purchased, helping other customers make informed decisions.

## System Architecture
```mermaid
graph TB
    UI[Frontend UI] --> API[Review API Layer]
    API --> RS[Review Service]
    RS --> RR[Review Repository]
    RS --> OS[Order Service]  // To verify purchase
    RS --> US[User Service]
    RR --> DB[(Database)]
    OS --> OR[Order Repository]
    US --> UR[User Repository]
```

## Core Components
1.  **Review Service:** Handles submission, retrieval, moderation, and aggregation of reviews and ratings. Verifies if the user purchased the product.
2.  **Review Repository:** Stores review content, ratings, user information, product association, and moderation status.

## Key Features
- Submit reviews (text content) and ratings (e.g., 1-5 stars)
- Only allow reviews from users who purchased the product
- Display average rating and review count on product pages
- List reviews on product pages with sorting/filtering (e.g., by rating, date)
- Review moderation (Admin approval/rejection)
- Helpful review voting (optional)
- User profile showing their reviews (optional)

## API Endpoints
- `POST /api/products/{productId}/reviews` (Submit review)
- `GET /api/products/{productId}/reviews` (Get reviews for a product)
- `GET /api/reviews/{reviewId}` (Get specific review)
- `PUT /api/reviews/{reviewId}` (User: Edit own review - limited time?)
- `DELETE /api/reviews/{reviewId}` (User: Delete own review)
- `GET /api/users/{userId}/reviews` (Get reviews by a user)
- `POST /api/reviews/{reviewId}/vote` (Vote helpful/unhelpful)
- `PUT /api/admin/reviews/{reviewId}/status` (Admin: Moderate review)

## Security Considerations
- Prevent review spam (rate limiting, CAPTCHA if needed)
- Ensure only verified purchasers can review
- Prevent users from reviewing the same product multiple times
- Input validation and sanitization for review content
- Secure admin moderation endpoints

## Data Models (Conceptual)
- `ProductReview` (Review ID, Product ID, User ID, Rating, Title, Body, Status (Pending/Approved/Rejected), Created At, Updated At)
- `ReviewVote` (Vote ID, Review ID, User ID, Vote Type (Helpful/Unhelpful), Timestamp)

## Future Enhancements
- Allow photo/video uploads with reviews
- Q&A section for products
- Verified Purchase badge
- Review sentiment analysis
- Featured reviews
