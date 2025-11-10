-- Clean start
DROP TABLE IF EXISTS users_auth CASCADE;

-- Main user table
CREATE TABLE users_auth (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'INSTRUCTOR', 'STUDENT')),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_login TIMESTAMP
);

-- Email Validation
ALTER TABLE users_auth
    ADD CONSTRAINT email_format_check
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Sample users (replace TEMP_HASH_* with real Argon2 hashes)
INSERT INTO users_auth (email, role, password_hash)
VALUES
    ('admin@iiitd.ac.in', 'ADMIN', 'TEMP_HASH_ADMIN'),
    ('ravi.sharma@iiitd.ac.in', 'INSTRUCTOR', 'TEMP_HASH_INSTRUCTOR'),
    ('meera.bansal@iiitd.ac.in', 'INSTRUCTOR', 'TEMP_HASH_INSTRUCTOR2'),
    ('aryan.khare@iiitd.ac.in', 'STUDENT', 'TEMP_HASH_STU1'),
    ('ananya.verma@iiitd.ac.in', 'STUDENT', 'TEMP_HASH_STU2'),
    ('rohan.mehra@iiitd.ac.in', 'STUDENT', 'TEMP_HASH_STU3');

-- Test
SELECT user_id, email, role, status FROM users_auth;