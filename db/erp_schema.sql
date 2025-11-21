-- Clean start (drop in dependency order)
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

--  DEPARTMENTS
CREATE TABLE departments (
    department_id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL
);

INSERT INTO departments (code, name) VALUES
    ('CB', 'Computational Biology'),
    ('CSE', 'Computer Science and Engineering'),
    ('ECE', 'Electronics and Communication Engineering'),
    ('MATH', 'Mathematics'),
    ('HCD', 'Human Centred Design'),
    ('SSH', 'Social Science and Humanities');

--  FACULTY
CREATE TABLE faculty (
    faculty_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE,   -- logical FK to auth_db.users_auth(user_id)
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

INSERT INTO faculty (user_id, department_id, designation, full_name) VALUES
    (2, 2, 'Department Head', 'Dr. Ravi Sharma'),
    (3, 5, 'Associate Professor', 'Dr. Meera Bansal');

--  STUDENTS
CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE,  -- logical FK to auth_db.users_auth(user_id)
    degree_level VARCHAR(20) CHECK (degree_level IN ('B.Tech', 'M.Tech', 'PhD')),
    branch VARCHAR(20) CHECK (branch IN ('CSE', 'CSD', 'CSAI', 'CSAM', 'CSECON', 'CSB', 'ECE', 'EVE', 'CSSS')),
    year INT CHECK (year BETWEEN 1 AND 6),
    term VARCHAR(10) CHECK (term IN ('Winter', 'Monsoon', 'Summer')),
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

INSERT INTO students (user_id, degree_level, branch, year, term, roll_no, full_name) VALUES
    (4, 'B.Tech', 'CSD', 2, 'Monsoon', '2024124', 'Aryan Khare'),
    (5, 'B.Tech', 'CSAI', 3, 'Monsoon', '2023102', 'Ananya Verma'),
    (6, 'B.Tech', 'CSE', 4, 'Monsoon', '2022457', 'Rohan Mehra');


--  COURSES
CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    department_id INT REFERENCES departments(department_id) ON DELETE SET NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(150) NOT NULL,
    credits INT CHECK (credits > 0),
    prerequisites TEXT
);

INSERT INTO courses (department_id, code, title, credits, prerequisites) VALUES
    (2, 'CSE231', 'Operating Systems', 4, 'CSE101, CSE102'),
    (2, 'CSE201', 'Advanced Programming', 4, 'CSE101, CSE102'),
    (5, 'DES201', 'Design Processes and Perspectives', 4, NULL);

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

INSERT INTO sections (course_id, instructor_id, term, year, room, capacity) VALUES
    (1, 1, 'Monsoon', 2025, 'C-101', 40),
    (2, 1, 'Monsoon', 2025, 'C-102', 35),
    (3, 2, 'Monsoon', 2025, 'B-105', 25);

--  COMPONENT TYPES
CREATE TABLE component_types (
    type_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO component_types (name) VALUES
    ('LECTURE'),
    ('TUTORIAL'),
    ('LAB'),
    ('QUIZ'),
    ('MIDSEM'),
    ('ENDSEM'),
    ('PROJECT'),
    ('ASSIGNMENT');

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

INSERT INTO section_components (section_id, type_id, day, start_time, end_time, weight, description)
VALUES
    (1, 1, 'Mon', '10:00', '11:30', 0, 'Lecture 1'),
    (1, 1, 'Wed', '10:00', '11:30', 0, 'Lecture 2'),
    (1, 3, 'Fri', '14:00', '16:00', 0, 'Weekly Lab'),
    (1, 4, NULL, NULL, NULL, 10, '4 Quizzes'),
    (1, 5, NULL, NULL, NULL, 30, 'Midsem Exam'),
    (1, 6, NULL, NULL, NULL, 40, 'Endsem Exam'),
    (1, 7, NULL, NULL, NULL, 20, 'OS Project');

--  ENROLLMENTS
CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    section_id INT REFERENCES sections(section_id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'ENROLLED',
    UNIQUE (student_id, section_id)
);

--  COMPONENT SCORES (per student × component)
CREATE TABLE component_scores (
    score_id SERIAL PRIMARY KEY,
    enrollment_id INT NOT NULL REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    component_id INT NOT NULL REFERENCES section_components(component_id) ON DELETE CASCADE,
    score NUMERIC(7,2) CHECK (score >= 0 AND score <= 100),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (enrollment_id, component_id)
);


--  FINAL GRADES (aggregated)
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

INSERT INTO settings (key, value) VALUES ('maintenance_mode', 'OFF');


--  NOTES
-- weight = 0 → component is NOT used in grade calculation
-- component_types allows admin to add any assessment type (ATTENDANCE, JOURNAL, etc.)