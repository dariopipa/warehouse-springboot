INSERT INTO roles(id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles(id, role) VALUES (2, 'ROLE_MANAGER');
INSERT INTO roles(id, role) VALUES (3, 'ROLE_USER');

INSERT INTO users(username, email, password)
VALUES ('admin', 'admin@example.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'admin'), 1
);

INSERT INTO product_types(id, name, description) VALUES (1, 'Electronics', 'Electronic devices and components');
INSERT INTO product_types(id, name, description) VALUES (2, 'Clothing', 'Apparel and fashion items');
INSERT INTO product_types(id, name, description) VALUES (3, 'Books', 'Books and educational materials');
INSERT INTO product_types(id, name, description) VALUES (4, 'Home & Garden', 'Home improvement and garden supplies');
INSERT INTO product_types(id, name, description) VALUES (5, 'Sports', 'Sports equipment and accessories');
