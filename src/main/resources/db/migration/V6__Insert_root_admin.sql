-- First insert the user
INSERT INTO users (email, password, first_name, last_name, created_at, updated_at, is_active)
VALUES (
    'admin@ecommerce.com',
    '$2a$10$5Q/UQNN27YkJqonO5z.YU.yBxMeNnxqWvU0hnJyPQ5xgvZOXxJH7C', -- this is BCrypt hash of 'admin123'
    'Root',
    'Admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
);

-- Then get the user_id and role_id to create the users_authorities relationship
INSERT INTO users_authorities (user_id, authority_id)
SELECT 
    (SELECT id FROM users WHERE email = 'admin@ecommerce.com'),
    (SELECT id FROM authorities WHERE name = 'ROLE_ADMIN');