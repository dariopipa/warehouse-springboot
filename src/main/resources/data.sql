-- =====================================================
-- ROLES SETUP
-- =====================================================
INSERT INTO roles(id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles(id, role) VALUES (2, 'ROLE_MANAGER');
INSERT INTO roles(id, role) VALUES (3, 'ROLE_USER');

-- =====================================================
-- USERS SETUP
-- =====================================================
-- Admin user (password: admin123)
INSERT INTO users(username, email, password)
VALUES ('admin', 'admin@warehouse.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

-- Manager users (password: admin123)
INSERT INTO users(username, email, password)
VALUES ('manager1', 'manager1@warehouse.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

INSERT INTO users(username, email, password)
VALUES ('manager2', 'manager2@warehouse.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

-- Regular users (password: admin123)
INSERT INTO users(username, email, password)
VALUES ('user1', 'user1@warehouse.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

INSERT INTO users(username, email, password)
VALUES ('user2', 'user2@warehouse.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

INSERT INTO users(username, email, password)
VALUES ('user3', 'user3@warehouse.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

-- =====================================================
-- USER ROLES ASSIGNMENT
-- =====================================================
-- Admin role
INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'admin'), 1
);

-- Manager roles
INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'manager1'), 2
);

INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'manager2'), 2
);

-- User roles
INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'user1'), 3
);

INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'user2'), 3
);

INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'user3'), 3
);

-- =====================================================
-- PRODUCT TYPES SETUP
-- =====================================================
INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (1, 'Electronics', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (2, 'Clothing & Apparel', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (3, 'Books & Media', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (4, 'Home & Garden', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (5, 'Sports & Outdoors', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (6, 'Health & Beauty', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (7, 'Automotive', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (8, 'Food & Beverages', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (9, 'Office Supplies', 1, 1, NOW(), NOW(), false);

INSERT INTO product_type(id, name, created_by, updated_by, created_at, updated_at, deleted) 
VALUES (10, 'Toys & Games', 1, 1, NOW(), NOW(), false);

-- =====================================================
-- PRODUCTS SETUP - Electronics Category
-- =====================================================
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('ELE-LTP-001', 'Gaming Laptop Pro 15"', 'High-performance gaming laptop with RTX graphics', 45, 10, 2.5, 2.5, 35.0, 1, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('ELE-SMP-002', 'Smartphone Ultra 256GB', 'Latest flagship smartphone with advanced camera', 120, 20, 0.18, 0.8, 16.0, 1, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('ELE-TAB-003', 'Tablet Pro 12.9" WiFi', 'Professional tablet for creative work', 75, 15, 0.68, 0.6, 28.0, 1, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('ELE-HDT-004', 'Wireless Headphones Noise-Canceling', 'Premium wireless headphones with ANC', 200, 30, 0.25, 20.0, 18.0, 1, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('ELE-CAM-005', 'DSLR Camera 24MP', 'Professional digital camera with multiple lenses', 35, 8, 1.2, 10.0, 14.0, 1, 1, 1, NOW(), NOW(), false);

-- =====================================================
-- PRODUCTS SETUP - Clothing & Apparel Category
-- =====================================================
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('CLO-JST-001', 'Cotton T-Shirt Basic Blue', 'Comfortable cotton t-shirt in navy blue', 300, 50, 0.15, 0.5, 25.0, 2, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('CLO-JNS-002', 'Denim Jeans Classic Fit', 'Classic fit denim jeans in various sizes', 180, 25, 0.6, 1.0, 35.0, 2, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('CLO-JKT-003', 'Winter Jacket Waterproof', 'Insulated waterproof winter jacket', 95, 15, 1.2, 3.0, 45.0, 2, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('CLO-SNK-004', 'Running Sneakers Lightweight', 'Lightweight running shoes with air cushioning', 150, 20, 0.8, 12.0, 30.0, 2, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('CLO-DRS-005', 'Summer Dress Floral Pattern', 'Light summer dress with floral design', 85, 12, 0.3, 1.0, 40.0, 2, 2, 2, NOW(), NOW(), false);

-- =====================================================
-- PRODUCTS SETUP - Books & Media Category
-- =====================================================
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('BOO-FIC-001', 'Mystery Novel "The Silent Observer"', 'Bestselling mystery thriller novel', 250, 40, 0.4, 2.0, 20.0, 3, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('BOO-TEC-002', 'Programming Guide: Advanced Java', 'Comprehensive guide to advanced Java programming', 120, 20, 0.8, 3.0, 25.0, 3, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('BOO-COO-003', 'Cookbook: International Cuisine', 'Collection of international recipes and techniques', 90, 15, 1.2, 2.5, 28.0, 3, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('BOO-DVD-004', 'Documentary Series: Nature World', '10-disc nature documentary collection', 65, 10, 0.3, 1.5, 19.0, 3, 1, 1, NOW(), NOW(), false);

-- =====================================================
-- PRODUCTS SETUP - Home & Garden Category
-- =====================================================
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('HOM-FUR-001', 'Office Chair Ergonomic', 'Ergonomic office chair with lumbar support', 55, 8, 18.0, 120.0, 65.0, 4, 3, 3, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('HOM-LAM-002', 'LED Desk Lamp Adjustable', 'Energy-efficient LED desk lamp with dimmer', 140, 25, 1.8, 45.0, 20.0, 4, 3, 3, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('HOM-PLT-003', 'Indoor Plant Pot Ceramic', 'Decorative ceramic pot for indoor plants', 200, 30, 2.5, 25.0, 25.0, 4, 3, 3, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('HOM-TOL-004', 'Power Drill Set 18V', 'Cordless power drill with battery and accessories', 80, 12, 2.2, 25.0, 30.0, 4, 3, 3, NOW(), NOW(), false);

-- =====================================================
-- PRODUCTS SETUP - Sports & Outdoors Category
-- =====================================================
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('SPO-BIK-001', 'Mountain Bike 27.5" Frame', 'All-terrain mountain bike with 21-speed gearing', 25, 5, 15.0, 110.0, 180.0, 5, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('SPO-TEN-002', 'Tennis Racket Professional', 'Professional-grade tennis racket with grip tape', 75, 12, 0.3, 69.0, 32.0, 5, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('SPO-FIT-003', 'Yoga Mat Premium', 'Non-slip premium yoga mat with carrying strap', 160, 25, 1.0, 0.6, 183.0, 5, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('SPO-CAM-004', 'Camping Tent 4-Person', 'Waterproof 4-person camping tent with rain fly', 40, 6, 8.5, 20.0, 60.0, 5, 2, 2, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('HLT-SHA-001', 'Organic Shampoo Natural', 'Organic shampoo with natural ingredients', 180, 30, 0.5, 20.0, 6.0, 6, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('AUT-OIL-001', 'Motor Oil 5W-30 Synthetic', 'High-performance synthetic motor oil', 95, 15, 5.0, 30.0, 25.0, 7, 1, 1, NOW(), NOW(), false);
 
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('FOD-COF-001', 'Premium Coffee Beans 1kg', 'Single-origin premium coffee beans', 220, 35, 1.0, 15.0, 20.0, 8, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('OFF-PEN-001', 'Ballpoint Pen Set 12-Pack', 'Professional ballpoint pen set', 300, 50, 0.2, 15.0, 20.0, 9, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('TOY-LEG-001', 'Building Blocks Creative Set', 'Educational building blocks for creativity', 110, 18, 2.0, 30.0, 40.0, 10, 1, 1, NOW(), NOW(), false);

-- =====================================================
-- LOW STOCK PRODUCTS
-- =====================================================
INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('LOW-STK-001', 'Limited Edition Watch', 'Luxury limited edition wristwatch', 3, 5, 0.2, 1.0, 4.0, 1, 1, 1, NOW(), NOW(), false);

INSERT INTO product(sku, name, description, quantity, low_stock_threshold, weight, height, length, product_type_id, created_by, updated_by, created_at, updated_at, deleted)
VALUES ('LOW-STK-002', 'Rare Collectible Item', 'Rare collectible for enthusiasts', 1, 3, 0.5, 10.0, 15.0, 10, 1, 1, NOW(), NOW(), false);

-- =====================================================
-- SAMPLE AUDIT LOGS
-- =====================================================
INSERT INTO audit_log(user_id, action, entity_type, entity_id, details, logged_at)
VALUES (1, 'CREATE', 'PRODUCT_TYPE', 1, 'Created product type: Electronics', NOW() - INTERVAL '30 days');

INSERT INTO audit_log(user_id, action, entity_type, entity_id, details, logged_at)
VALUES (1, 'CREATE', 'PRODUCT', 1, 'Created product: Gaming Laptop Pro 15"', NOW() - INTERVAL '25 days');

INSERT INTO audit_log(user_id, action, entity_type, entity_id, details, logged_at)
VALUES (2, 'UPDATE', 'PRODUCT', 1, 'Updated product quantity from 50 to 45', NOW() - INTERVAL '20 days');

INSERT INTO audit_log(user_id, action, entity_type, entity_id, details, logged_at)
VALUES (1, 'CREATE', 'USER', 4, 'Created user: user1', NOW() - INTERVAL '15 days');

INSERT INTO audit_log(user_id, action, entity_type, entity_id, details, logged_at)
VALUES (3, 'UPDATE', 'PRODUCT', 5, 'Updated product description', NOW() - INTERVAL '10 days');

-- =====================================================
-- SAMPLE STOCK ALERTS
-- =====================================================
-- STOCK ALERTS SETUP
-- =====================================================
INSERT INTO stock_alert(product_id, triggered_at, email_sent)
VALUES ((SELECT id FROM product WHERE sku = 'LOW-STK-001'), NOW() - INTERVAL '5 days', true);

INSERT INTO stock_alert(product_id, triggered_at, email_sent)
VALUES ((SELECT id FROM product WHERE sku = 'LOW-STK-002'), NOW() - INTERVAL '2 days', false);
