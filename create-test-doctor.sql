-- Create Test Doctor Account
-- This script creates a test doctor account for testing the doctor dashboard

USE gym;

-- 1. Create doctor role (if not exists)
INSERT IGNORE INTO roles (name) VALUES ('ROLE_DOCTOR');

-- 2. Create test doctor user
-- Password: password123 (bcrypt encoded)
INSERT INTO users (username, password, email, first_name, last_name, status) 
VALUES ('doctor1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'doctor1@gym.com', 'John', 'Smith', 'ACTIVE')
ON DUPLICATE KEY UPDATE username = username;

-- 3. Link user to doctor role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'doctor1' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- 4. Verify the account was created
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    r.name as role,
    u.status
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'doctor1';

-- Expected output:
-- +----+----------+-------------------+------------+-----------+-------------+--------+
-- | id | username | email             | first_name | last_name | role        | status |
-- +----+----------+-------------------+------------+-----------+-------------+--------+
-- | XX | doctor1  | doctor1@gym.com   | John       | Smith     | ROLE_DOCTOR | ACTIVE |
-- +----+----------+-------------------+------------+-----------+-------------+--------+

-- Login Credentials:
-- Username: doctor1
-- Password: password123

-- Next Steps:
-- 1. Login at: http://localhost:8080/login
-- 2. You'll be redirected to doctor dashboard
-- 3. Click "Create Profile Now"
-- 4. Fill in:
--    - Specialization: Nutritionist
--    - License Number: MD12345
--    - Consultation Fee: 50.00
-- 5. Click "Create Profile"
-- 6. You should see success message and full dashboard access
