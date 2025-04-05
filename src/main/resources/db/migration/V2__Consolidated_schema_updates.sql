-- Migration Script: Consolidated schema updates

-- Update `inventory` table
ALTER TABLE inventory
    ADD COLUMN last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD COLUMN status VARCHAR(255) NOT NULL,
    ADD COLUMN threshold INT NULL,
    ADD COLUMN reserved_quantity INT DEFAULT 0;
-- Update `product` table
ALTER TABLE product
    ADD COLUMN status VARCHAR(255) NOT NULL,
    ADD COLUMN brand VARCHAR(255) NULL,
    ADD COLUMN category VARCHAR(255) NULL,
    ADD COLUMN image_url VARCHAR(255) NULL,
    ADD COLUMN updated_at TIMESTAMP NULL,
    ADD COLUMN deleted_at TIMESTAMP NULL;

