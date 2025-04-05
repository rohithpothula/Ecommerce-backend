# Order Controller - Low Level Design

## Order Flow Sequence

```mermaid
sequenceDiagram
    participant Client
    participant OrderController
    participant AdminOrderController
    participant OrderService
    participant InventoryService
    participant PaymentService
    participant Repository
    
    %% Create Order Flow
    Client->>OrderController: POST /api/orders
    OrderController->>OrderService: createOrder(request, userId)
    OrderService->>InventoryService: checkAndReserveInventory()
    OrderService->>PaymentService: processPayment()
    OrderService->>Repository: save(order)
    OrderService->>InventoryService: confirmInventoryReduction()
    OrderService-->>OrderController: OrderDto
    OrderController-->>Client: 201 Created
    
    %% Get User Orders
    Client->>OrderController: GET /api/orders
    OrderController->>OrderService: getUserOrders(userId, pageable)
    OrderService->>Repository: findByUserId()
    OrderService-->>OrderController: Page<OrderSummaryDto>
    OrderController-->>Client: 200 OK
    
    %% Cancel Order
    Client->>OrderController: POST /api/orders/{id}/cancel
    OrderController->>OrderService: cancelOrder(id, userId)
    OrderService->>Repository: findById()
    OrderService->>InventoryService: returnInventory()
    OrderService->>PaymentService: processRefund()
    OrderService->>Repository: updateStatus()
    OrderService-->>OrderController: OrderDto
    OrderController-->>Client: 200 OK
```

## Admin Order Flow

```mermaid
sequenceDiagram
    participant Admin
    participant AdminOrderController
    participant OrderService
    participant Repository
    
    %% List All Orders
    Admin->>AdminOrderController: GET /api/admin/orders
    AdminOrderController->>OrderService: getAllOrders(filters, pageable)
    OrderService->>Repository: findAll(spec)
    OrderService-->>AdminOrderController: Page<OrderSummaryDto>
    AdminOrderController-->>Admin: 200 OK
    
    %% Update Order Status
    Admin->>AdminOrderController: PUT /api/admin/orders/{id}/status
    AdminOrderController->>OrderService: updateOrderStatus(id, request)
    OrderService->>Repository: findById()
    OrderService->>Repository: save(order)
    OrderService-->>AdminOrderController: OrderDto
    AdminOrderController-->>Admin: 200 OK
    
    %% View Order History
    Admin->>AdminOrderController: GET /api/admin/orders/{id}/history
    AdminOrderController->>OrderService: getOrderStatusHistory(id)
    OrderService->>Repository: findOrderHistoryById()
    OrderService-->>AdminOrderController: List<OrderStatusHistoryDto>
    AdminOrderController-->>Admin: 200 OK
```

## REST Controller (`OrderController.java`)
- Endpoints for creating an order, retrieving user's orders, retrieving a specific order, cancelling an order.
- Uses `@PreAuthorize` or similar for authorization checks (user owns order).

## Admin REST Controller (`AdminOrderController.java`)
- Endpoints for listing all orders (with filtering/pagination), updating order status, viewing order history.
- Secured with admin role checks.

## Request DTOs
- `CreateOrderRequest` (e.g., cartId, shippingAddressId, billingAddressId, paymentMethodId)
- `UpdateOrderStatusRequest` (Admin only: newStatus, notes)
- `CancelOrderRequest` (User initiated: reason)

## Response DTOs
- `OrderDto` (Detailed order view including items, addresses, status, totals)
- `OrderSummaryDto` (For listing orders: id, date, total, status, primary product image)
- `OrderStatusHistoryDto`

## Exception Handling
- Handling `OrderNotFoundException`.
- Handling invalid status transitions (`IllegalOrderStatusTransitionException`).
- Handling authorization failures.
- Handling inventory/payment issues during order creation.
