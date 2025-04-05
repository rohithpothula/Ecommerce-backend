# Payment Controller - Low Level Design

## Payment Flow Sequence

```mermaid
sequenceDiagram
    participant Client
    participant PaymentController
    participant PaymentService
    participant GatewayAdapter
    participant PaymentRepository
    participant OrderService
    participant Gateway
    
    %% Payment Initiation
    Client->>PaymentController: POST /api/payments/initiate
    PaymentController->>PaymentService: initiatePayment(request)
    PaymentService->>OrderService: getOrder(orderId)
    PaymentService->>GatewayAdapter: createPaymentIntent(amount, currency)
    GatewayAdapter->>Gateway: API Call
    Gateway-->>GatewayAdapter: Payment Intent
    PaymentService->>PaymentRepository: save(transaction)
    PaymentService-->>PaymentController: PaymentResponse
    PaymentController-->>Client: {transactionId, clientSecret}
    
    %% Payment Confirmation
    Client->>PaymentController: POST /api/payments/confirm
    PaymentController->>PaymentService: confirmPayment(request)
    PaymentService->>PaymentRepository: findTransaction(id)
    PaymentService->>GatewayAdapter: handleConfirmation(params)
    GatewayAdapter->>Gateway: Verify Payment
    Gateway-->>GatewayAdapter: Payment Status
    PaymentService->>PaymentRepository: updateStatus(transaction)
    PaymentService->>OrderService: updateOrderStatus(orderId, status)
    PaymentService-->>PaymentController: PaymentStatus
    PaymentController-->>Client: {status, message}
    
    %% Webhook Handling
    Gateway->>PaymentController: POST /api/payments/webhook
    PaymentController->>PaymentService: handleWebhook(payload)
    PaymentService->>GatewayAdapter: verifySignature(payload)
    PaymentService->>PaymentRepository: updateTransaction(id)
    PaymentService->>OrderService: updateOrderStatus(orderId)
    PaymentController-->>Gateway: 200 OK
```

## REST Controller (`PaymentController.java`)
- Endpoints for initiating payment, confirming payment, handling webhooks, processing refunds.
- Request/Response DTOs.
- Validation rules.
- Security considerations (authentication, authorization).

## Request DTOs
- `InitiatePaymentRequest`
- `ConfirmPaymentRequest`
- `RefundRequest`
- `WebhookPayload` (Structure depends on gateway)

## Response DTOs
- `PaymentResponse` (e.g., transaction ID, status, redirect URL)
- `RefundResponse`

## Exception Handling
- Handling payment gateway errors.
- Handling invalid payment states.
- Handling webhook signature verification failures.
