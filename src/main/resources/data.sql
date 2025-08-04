INSERT INTO roles(id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles(id, role) VALUES (2, 'ROLE_MANAGER');
INSERT INTO roles(id, role) VALUES (3, 'ROLE_USER');

INSERT INTO users(username, email, password)
VALUES ('admin', 'admin@example.com', '$2a$12$25ZJmLq2MzAKmWLz27AFquCJ9FTbk47jtDoNipYBQ9FNVPib.N5zq');

INSERT INTO user_roles(user_id, role_id) VALUES (
  (SELECT id FROM users WHERE username = 'admin'), 1
);
