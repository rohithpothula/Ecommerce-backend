-- Create local_user table
CREATE TABLE IF NOT EXISTS local_user (
                                          id BINARY(16) PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(1000) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE
    );

-- Create verification_token table
CREATE TABLE IF NOT EXISTS verification_token (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  token VARCHAR(512) NOT NULL UNIQUE,
    created_timestamp TIMESTAMP NOT NULL,
    user_id BINARY(16) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES local_user(id) ON DELETE CASCADE
    );

-- Create address table with BIGINT id
CREATE TABLE IF NOT EXISTS address (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       address_line_1 VARCHAR(512) NOT NULL,
    address_line_2 VARCHAR(512),
    city VARCHAR(255) NOT NULL,
    country VARCHAR(75) NOT NULL,
    user_id BINARY(16) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES local_user(id) ON DELETE CASCADE
    );

-- Create product table
CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL UNIQUE,
    short_description VARCHAR(255) NOT NULL,
    long_description TEXT,
    price DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create inventory table
CREATE TABLE IF NOT EXISTS inventory (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         product_id BIGINT NOT NULL UNIQUE,
                                         quantity INT NOT NULL DEFAULT 0,
                                         FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
    );

-- Create web_order table with address_id
CREATE TABLE IF NOT EXISTS web_order (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         user_id BINARY(16) NOT NULL,
    address_id BIGINT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES local_user(id) ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES address(id)
    );

-- Create web_order_quantities table
CREATE TABLE IF NOT EXISTS web_order_quantities (
                                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                    order_id BIGINT NOT NULL,
                                                    product_id BIGINT NOT NULL,
                                                    quantity INT NOT NULL,
                                                    FOREIGN KEY (order_id) REFERENCES web_order(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
    );
