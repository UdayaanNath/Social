-- 1. Insert ADMIN user into users table (if not already present)
INSERT INTO users (username, email, password_hash)
SELECT 'admin', 'admin@example.com', '${admin_password_hash}'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin' OR email = 'admin@example.com'
);

-- 2. Insert ADMIN user role into user_roles table (if not already present)
INSERT INTO user_roles (user_id, username, role_id, role_name, status)
SELECT u.id, u.username, 1, 'ADMIN', 'ACTIVE'
FROM users u
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_name = 'ADMIN'
);