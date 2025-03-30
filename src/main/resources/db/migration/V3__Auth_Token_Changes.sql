-- Flyway Migration V3: Create Product, Inventory, and Order Related Tables (Placeholders)
-- These are basic structures based on typical needs and existing schema hints.
-- !! ADJUST definitions based on your actual entity relationships and fields !!

-- Create product table (assuming basic fields)
CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         short_description VARCHAR(500),
                         long_description TEXT,
                         price DECIMAL(10, 2) NOT NULL,
    -- Add created_at/updated_at if needed
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- Add other fields like SKU, brand, category foreign keys, etc.
) ENGINE=InnoDB COMMENT='Stores product information';


-- Create inventory table (assuming relationship with product)
CREATE TABLE inventory (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           product_id BIGINT NOT NULL UNIQUE COMMENT 'FK to product.id', -- Assuming 1-to-1 inventory per product
                           quantity INT NOT NULL DEFAULT 0,
    -- Add other fields if needed (e.g., warehouse location)
                           last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Stores product inventory levels';


-- Create web_order table (assuming relationships)
CREATE TABLE web_order (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BINARY(16) NOT NULL COMMENT 'FK to local_users.id',
                           address_id BINARY(16) NOT NULL COMMENT 'FK to address.id (Must match address.id type)',
                           order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           total DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
                           order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

                           CONSTRAINT fk_web_order_user FOREIGN KEY (user_id) REFERENCES local_users(id) ON DELETE RESTRICT, -- Prevent deleting user with orders?
                           CONSTRAINT fk_web_order_address FOREIGN KEY (address_id) REFERENCES address(id) ON DELETE RESTRICT -- Prevent deleting address used in orders?
) ENGINE=InnoDB COMMENT='Stores customer order headers';


-- Create web_order_quantities table (Junction table for order items)
CREATE TABLE web_order_quantities (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      order_id BIGINT NOT NULL COMMENT 'FK to web_order.id',
                                      product_id BIGINT NOT NULL COMMENT 'FK to product.id',
                                      quantity INT NOT NULL,
    -- Optional: Store variantPrice at time of order if product prices can change
    -- price_per_unit DECIMAL(10, 2) NOT NULL,

                                      CONSTRAINT fk_woq_order FOREIGN KEY (order_id) REFERENCES web_order(id) ON DELETE CASCADE, -- If order deleted, items deleted
                                      CONSTRAINT fk_woq_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE RESTRICT -- Prevent deleting product if in an order?
) ENGINE=InnoDB COMMENT='Stores individual items (products and quantities) within an order';

CREATE INDEX idx_woq_order_id ON web_order_quantities(order_id);
CREATE INDEX idx_woq_product_id ON web_order_quantities(product_id);
