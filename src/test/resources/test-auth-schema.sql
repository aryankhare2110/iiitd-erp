-- Test Auth Schema (Clean version without sample data)
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
