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

-- Sample users
INSERT INTO users_auth (email, role, password_hash)
VALUES
    ('admin@iiitd.ac.in', 'ADMIN', '$argon2id$v=19$m=15360,t=3,p=2$IfHmAZBcBHDAn9i2B3SrE9XWfZ1IjY65nehjwKonPhsgTUF32S5qYrAsQjn7xIo0DmC1pQsIbc3+tn6oi8zM3A$m81/pMRlL7KeWmsVdvJUqMpyeqfBDKoTrJxuzsca8m0'),
    ('ravi.sharma@iiitd.ac.in', 'INSTRUCTOR', '$argon2id$v=19$m=15360,t=3,p=2$xXgKWCiX60GBCppYTXCFREGEF3wJ6lbQgrBreufQN5LJWsDWWAei1PKdNlmfVzoSilW1jNZu3m2FtFAXLGq61w$WM7Lz7m6U6kqiL0UjnI8+gm8SEGvhLBkM6wHg8n/bew'),
    ('meera.bansal@iiitd.ac.in', 'INSTRUCTOR', '$argon2id$v=19$m=15360,t=3,p=2$v1on3tF87ug9atr1W61GoGe6EiDUlN9dR1Sg/rTQBvkAYWTl98q5fCC7S2o4+2kyYMOhsg8evsfbqf2+4WJNKw$juSsTSVCBaGJj8lRqMKV3Y+bQ165n0BiUjEM1e/3N7Y'),
    ('aryan24124@iiitd.ac.in', 'STUDENT', '$argon2id$v=19$m=15360,t=3,p=2$GNQSRBF5ICIj8Es7avYTjW833PMHZrUVKfJ6D2vRPctkmrWO4emVZ5Vlkm54EGcCb7qxemraBmftAg/mWXg8aQ$OJMIQKdoaJvKnYz9SYptEYMxRv/SBrU2MbzUUBv3NzY'),
    ('ananya23104@iiitd.ac.in', 'STUDENT', '$argon2id$v=19$m=15360,t=3,p=2$bD/8p70PblhCR5VDxywk3jZc8gfYMuGArTvUtxoPQ8cNOYw8N7lLW9SX2kFTFAUUZY42VePh3wDraJ6hPY5lmw$fgSIgyOsa66VdtYr3Y7lgmlqM6+BZdrEA2t74R0CuBo'),
    ('rohan22457@iiitd.ac.in', 'STUDENT', '$argon2id$v=19$m=15360,t=3,p=2$xwSp+HWPzxni3+NfLPJUasWznUihzlZ57TLkBgkobbUC8Vv5OBJPyjULy7zOY3a2awfWhUeKFbbqZ/u67/qMWw$69MeAneCKmxZBLKlgVS60IRLv4ZYWS1F5b2KAndQAb0');

-- Test
SELECT user_id, email, role, status FROM users_auth;