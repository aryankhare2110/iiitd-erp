-- Test ERP Schema (Clean version without sample data)
DROP TABLE IF EXISTS grades CASCADE;
DROP TABLE IF EXISTS component_scores CASCADE;
DROP TABLE IF EXISTS section_components CASCADE;
DROP TABLE IF EXISTS component_types CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS sections CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS faculty CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS settings CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;

--  DEPARTMENTS
CREATE TABLE departments (
    department_id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL
);

--  FACULTY
CREATE TABLE faculty (
    faculty_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE,
    department_id INT REFERENCES departments(department_id) ON DELETE SET NULL,
    designation VARCHAR(50) CHECK (designation IN (
        'Professor',
        'Associate Professor',
        'Assistant Professor',
        'Visiting Professor',
        'Adjunct Faculty',
        'Emeritus Professor',
        'Department Head'
    )),
    full_name VARCHAR(100) NOT NULL
);

--  STUDENTS
CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE,
    degree_level VARCHAR(20) CHECK (degree_level IN ('B.Tech', 'M.Tech', 'PhD')),
    branch VARCHAR(20) CHECK (branch IN ('CSE', 'CSD', 'CSAI', 'CSAM', 'CSECON', 'CSB', 'ECE', 'EVE', 'CSSS')),
    year INT CHECK (year BETWEEN 1 AND 6),
    term VARCHAR(10) CHECK (term IN ('Winter', 'Monsoon', 'Summer')),
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

--  COURSES
CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    department_id INT REFERENCES departments(department_id) ON DELETE SET NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(150) NOT NULL,
    credits INT CHECK (credits > 0),
    prerequisites TEXT
);

--  SECTIONS
CREATE TABLE sections (
    section_id SERIAL PRIMARY KEY,
    course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
    instructor_id INT REFERENCES faculty(faculty_id) ON DELETE SET NULL,
    term VARCHAR(10) CHECK (term IN ('Winter', 'Monsoon', 'Summer')),
    year INT,
    room VARCHAR(50),
    capacity INT CHECK (capacity > 0)
);

--  COMPONENT TYPES
CREATE TABLE component_types (
    type_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

--  SECTION COMPONENTS
CREATE TABLE section_components (
    component_id SERIAL PRIMARY KEY,
    section_id INT REFERENCES sections(section_id) ON DELETE CASCADE,
    type_id INT REFERENCES component_types(type_id) ON DELETE SET NULL,
    day VARCHAR(10),
    start_time TIME,
    end_time TIME,
    weight NUMERIC(5,2) DEFAULT 0,
    description TEXT
);

--  ENROLLMENTS
CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    section_id INT REFERENCES sections(section_id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'ENROLLED',
    UNIQUE (student_id, section_id)
);

--  COMPONENT SCORES
CREATE TABLE component_scores (
    score_id SERIAL PRIMARY KEY,
    enrollment_id INT NOT NULL REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    component_id INT NOT NULL REFERENCES section_components(component_id) ON DELETE CASCADE,
    score NUMERIC(7,2) CHECK (score >= 0 AND score <= 100),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (enrollment_id, component_id)
);

--  FINAL GRADES
CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    enrollment_id INT UNIQUE NOT NULL REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    total_score NUMERIC(7,2),
    grade_label VARCHAR(10),
    computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--  SETTINGS
CREATE TABLE settings (
    key VARCHAR(50) PRIMARY KEY,
    value VARCHAR(50)
);

-- NOTIFICATIONS
CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    sent_by_email VARCHAR(200) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT NOW()
);
