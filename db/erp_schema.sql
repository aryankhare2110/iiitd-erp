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
DROP TABLE IF EXISTS notifications CASCADE;

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

-- SEED: 100 COURSES FOR TESTING
INSERT INTO courses (department_id, code, title, credits, prerequisites) VALUES
    (2, 'CSE101', 'Introduction to Computing', 4, NULL),
    (2, 'CSE102', 'Data Structures', 4, 'CSE101'),
                                                                             (2, 'CSE103', 'Algorithms', 4, 'CSE102'),
                                                                             (2, 'CSE104', 'Computer Architecture', 4, 'CSE101'),
                                                                             (2, 'CSE105', 'Discrete Mathematics', 4, NULL),
                                                                             (2, 'CSE106', 'Theory of Computation', 4, 'CSE103'),
                                                                             (2, 'CSE107', 'Operating Systems Concepts', 4, 'CSE102'),
                                                                             (2, 'CSE108', 'Database Systems', 4, 'CSE102'),
                                                                             (2, 'CSE109', 'Computer Networks', 4, 'CSE102'),
                                                                             (2, 'CSE110', 'Machine Learning Foundations', 4, 'CSE103'),
                                                                             (2, 'CSE111', 'Artificial Intelligence', 4, 'CSE110'),
                                                                             (2, 'CSE112', 'Software Engineering', 4, 'CSE102'),
                                                                             (2, 'CSE113', 'Distributed Systems', 4, 'CSE107'),
                                                                             (2, 'CSE114', 'Web Technologies', 4, NULL),
                                                                             (2, 'CSE115', 'Cloud Computing', 4, 'CSE109'),
                                                                             (2, 'CSE116', 'Advanced Algorithms', 4, 'CSE103'),
                                                                             (2, 'CSE117', 'Cyber Security', 4, 'CSE109'),
                                                                             (2, 'CSE118', 'Parallel Computing', 4, 'CSE104'),
                                                                             (2, 'CSE119', 'Compilers', 4, 'CSE106'),
                                                                             (2, 'CSE120', 'Deep Learning', 4, 'CSE110'),

-- ECE (Dept 3)
                                                                             (3, 'ECE101', 'Basic Electronics', 4, NULL),
                                                                             (3, 'ECE102', 'Digital Logic Design', 4, NULL),
                                                                             (3, 'ECE103', 'Signals and Systems', 4, 'ECE102'),
                                                                             (3, 'ECE104', 'Microprocessors', 4, 'ECE102'),
                                                                             (3, 'ECE105', 'Analog Circuits', 4, 'ECE101'),
                                                                             (3, 'ECE106', 'Communication Systems', 4, 'ECE103'),
                                                                             (3, 'ECE107', 'Control Systems', 4, 'ECE103'),
                                                                             (3, 'ECE108', 'VLSI Design', 4, 'ECE105'),
                                                                             (3, 'ECE109', 'Embedded Systems', 4, 'ECE104'),
                                                                             (3, 'ECE110', 'Wireless Networks', 4, 'ECE106'),

-- Math (Dept 4)
                                                                             (4, 'MATH101', 'Calculus I', 4, NULL),
                                                                             (4, 'MATH102', 'Calculus II', 4, 'MATH101'),
                                                                             (4, 'MATH103', 'Linear Algebra', 4, NULL),
                                                                             (4, 'MATH104', 'Probability and Statistics', 4, NULL),
                                                                             (4, 'MATH105', 'Numerical Methods', 4, 'MATH102'),
                                                                             (4, 'MATH106', 'Differential Equations', 4, 'MATH101'),
                                                                             (4, 'MATH107', 'Graph Theory', 4, 'CSE105'),
                                                                             (4, 'MATH108', 'Optimization Techniques', 4, 'MATH103'),
                                                                             (4, 'MATH109', 'Discrete Mathematics II', 4, 'CSE105'),
                                                                             (4, 'MATH110', 'Game Theory', 4, 'MATH103'),

-- HCD (Dept 5)
                                                                             (5, 'DES101', 'Intro to Design', 4, NULL),
                                                                             (5, 'DES102', 'Design Thinking', 4, NULL),
                                                                             (5, 'DES103', 'Visual Communication', 4, NULL),
                                                                             (5, 'DES104', 'User Experience Design', 4, 'DES102'),
                                                                             (5, 'DES105', 'Human-Computer Interaction', 4, 'DES101'),
                                                                             (5, 'DES106', 'Design Research Methods', 4, NULL),
                                                                             (5, 'DES107', 'Typography and Layouts', 4, NULL),
                                                                             (5, 'DES108', '3D Modeling Basics', 4, NULL),
                                                                             (5, 'DES109', 'Interactive Systems', 4, 'DES104'),
                                                                             (5, 'DES110', 'Design Processes', 4, NULL),

-- SSH (Dept 6)
                                                                             (6, 'SSH101', 'Sociology', 4, NULL),
                                                                             (6, 'SSH102', 'Psychology', 4, NULL),
                                                                             (6, 'SSH103', 'Economics', 4, NULL),
                                                                             (6, 'SSH104', 'Political Science', 4, NULL),
                                                                             (6, 'SSH105', 'Philosophy', 4, NULL),
                                                                             (6, 'SSH106', 'Ethics and Society', 4, NULL),
                                                                             (6, 'SSH107', 'Communication Skills', 4, NULL),
                                                                             (6, 'SSH108', 'Cognitive Science', 4, 'SSH102'),
                                                                             (6, 'SSH109', 'History of Technology', 4, NULL),
                                                                             (6, 'SSH110', 'Cultural Studies', 4, NULL),

-- Add more filler CSE electives (40 more)
                                                                             (2, 'CSE121', 'Blockchain Technologies', 4, NULL),
                                                                             (2, 'CSE122', 'Internet of Things', 4, NULL),
                                                                             (2, 'CSE123', 'Reinforcement Learning', 4, 'CSE110'),
                                                                             (2, 'CSE124', 'Natural Language Processing', 4, 'CSE110'),
                                                                             (2, 'CSE125', 'Computer Vision', 4, 'CSE110'),
                                                                             (2, 'CSE126', 'Big Data Analytics', 4, NULL),
                                                                             (2, 'CSE127', 'Game Development', 4, NULL),
                                                                             (2, 'CSE128', 'Mobile App Development', 4, NULL),
                                                                             (2, 'CSE129', 'Ethical Hacking', 4, 'CSE117'),
                                                                             (2, 'CSE130', 'Information Retrieval', 4, NULL),
                                                                             (2, 'CSE131', 'Linux System Programming', 4, 'CSE107'),
                                                                             (2, 'CSE132', 'DevOps Engineering', 4, 'CSE107'),
                                                                             (2, 'CSE133', 'AI Safety & Ethics', 4, 'CSE111'),
                                                                             (2, 'CSE134', 'Computational Geometry', 4, 'CSE103'),
                                                                             (2, 'CSE135', 'Quantum Computing', 4, NULL),
                                                                             (2, 'CSE136', 'Digital Forensics', 4, NULL),
                                                                             (2, 'CSE137', 'High-Performance Computing', 4, 'CSE118'),
                                                                             (2, 'CSE138', 'Functional Programming', 4, NULL),
                                                                             (2, 'CSE139', 'Rust Programming', 4, NULL),
                                                                             (2, 'CSE140', 'Data Mining', 4, NULL),
                                                                             (2, 'CSE141', 'Neural Networks', 4, 'CSE110'),
                                                                             (2, 'CSE142', 'Robotics Programming', 4, NULL),
                                                                             (2, 'CSE143', 'Cloud Architecture', 4, NULL),
                                                                             (2, 'CSE144', 'Software Testing', 4, 'CSE112'),
                                                                             (2, 'CSE145', 'E-Commerce Systems', 4, NULL),
                                                                             (2, 'CSE146', 'Operating Systems II', 4, 'CSE107'),
                                                                             (2, 'CSE147', 'Compiler Optimization', 4, 'CSE119'),
                                                                             (2, 'CSE148', 'System Design', 4, NULL),
                                                                             (2, 'CSE149', 'Distributed AI', 4, 'CSE111'),
                                                                             (2, 'CSE150', 'Network Security', 4, 'CSE117'),
                                                                             (2, 'CSE151', 'AR/VR Technologies', 4, NULL),
                                                                             (2, 'CSE152', 'Edge Computing', 4, NULL),
                                                                             (2, 'CSE153', 'Information Security Management', 4, NULL),
                                                                             (2, 'CSE154', 'Digital Image Processing', 4, 'CSE110'),
                                                                             (2, 'CSE155', 'Cyber-Physical Systems', 4, NULL),
                                                                             (2, 'CSE156', 'Systems for ML', 4, 'CSE110'),
                                                                             (2, 'CSE157', 'Graph Mining', 4, 'CSE107'),
                                                                             (2, 'CSE158', 'Search Engines', 4, NULL),
                                                                             (2, 'CSE159', 'Formal Methods', 4, 'CSE106'),
                                                                             (2, 'CSE160', 'Programming Languages', 4, 'CSE105');

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

INSERT INTO settings (key, value)
VALUES
    ('maintenance_mode', 'OFF'),
    ('add_drop_deadline', '2025-12-31')
    ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value;

-- NOTIFICATIONS
CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    sent_by_email VARCHAR(200) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT NOW()
);


--  NOTES
-- weight = 0 → component is NOT used in grade calculation
-- component_types allows admin to add any assessment type (ATTENDANCE, JOURNAL, etc.)