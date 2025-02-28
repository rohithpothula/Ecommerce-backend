-- Script to insert dummy dev data into the database.
use ecommerce;

-- Create users with hashed passwords
INSERT INTO local_user (user_name, password, email, first_name, last_name, email_verified) VALUES
                                                                                               ('user1', '$2a$10$randomhash12345abc', 'user1@example.com', 'User', 'One', 1),
                                                                                               ('user2', '$2a$10$randomhash67890def', 'user2@example.com', 'User', 'Two', 1);

SET @userId1 = 1;
SET @userId2 = 2;

-- Insert products
INSERT INTO product (name, short_description, long_description, price) VALUES
                                                                           ('Product #1', 'Product short description', 'Product long description', 100.00),
                                                                           ('Product #1', 'Product one short description.', 'Long description...', 5.50),
                                                                           ('Product #2', 'Product two short description.', 'Long description...', 10.56),
                                                                           ('Product #3', 'Product three short description.', 'Long description...', 2.74),
                                                                           ('Product #4', 'Product four short description.', 'Long description...', 15.69),
                                                                           ('Product #5', 'Product five short description.', 'Long description...', 42.59);

-- Inputs for subsequent tables
SELECT id INTO @product1 FROM product WHERE name = 'Product #1';
SELECT id INTO @product2 FROM product WHERE name = 'Product #2';
SELECT id INTO @product3 FROM product WHERE name = 'Product #3';
SELECT id INTO @product4 FROM product WHERE name = 'Product #4';
SELECT id INTO @product5 FROM product WHERE name = 'Product #5';

-- Insert inventory
INSERT INTO inventory (product_id, quantity) VALUES
                                                 (@product1, 5),
                                                 (@product2, 8),
                                                 (@product3, 12),
                                                 (@product4, 73),
                                                 (@product5, 2);

-- Insert addresses
INSERT INTO address (address_line_1, city, country, user_id) VALUES
                                                                 ('123 Tester Hill', 'Testerton', 'England', @userId1),
                                                                 ('312 Spring Boot', 'Hibernate', 'England', @userId2);

-- Insert web_orders
SELECT id INTO @address1 FROM address WHERE user_id = @userId1 ORDER BY id DESC LIMIT 1;
SELECT id INTO @address2 FROM address WHERE user_id = @userId2 ORDER BY id DESC LIMIT 1;

INSERT INTO web_order (address_id, user_id) VALUES
                                                (@address1, @userId1),
                                                (@address1, @userId1),
                                                (@address1, @userId1),
                                                (@address2, @userId2),
                                                (@address2, @userId2);

-- Insert web_order_quantities
SELECT id INTO @order1 FROM web_order WHERE address_id = @address1 AND user_id = @userId1 ORDER BY id DESC LIMIT 1;
SELECT id INTO @order2 FROM web_order WHERE address_id = @address1 AND user_id = @userId1 ORDER BY id DESC LIMIT 1 OFFSET 1;
SELECT id INTO @order3 FROM web_order WHERE address_id = @address1 AND user_id = @userId1 ORDER BY id DESC LIMIT 1 OFFSET 1;
SELECT id INTO @order4 FROM web_order WHERE address_id = @address2 AND user_id = @userId2 ORDER BY id DESC LIMIT 1;
SELECT id INTO @order5 FROM web_order WHERE address_id = @address2 AND user_id = @userId2 ORDER BY id DESC LIMIT 1 OFFSET 1;

INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES
                                                                      (@order1, @product1, 5),
                                                                      (@order1, @product2, 5),
                                                                      (@order2, @product3, 5),
                                                                      (@order2, @product2, 5),
                                                                      (@order2, @product5, 5),
                                                                      (@order3, @product3, 5),
                                                                      (@order4, @product4, 5),
                                                                      (@order4, @product2, 5),
                                                                      (@order5, @product3, 5),
                                                                      (@order5, @product1, 5);
