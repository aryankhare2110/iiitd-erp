# IIITD University ERP System

<img src="https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
<img src="https://img.shields.io/badge/PostgreSQL-12+-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
<img src="https://img.shields.io/badge/Maven-3.6+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License"/>

**A comprehensive Enterprise Resource Planning system for academic institutions**

[Features](#features) • [Installation](#installation) • [Usage](#usage) • [Documentation](#documentation) • [License](#license)

</div>

---

## Overview

IIITD University ERP is a full-featured academic management system designed for educational institutions. It provides role-based access control for administrators, faculty members, and students, enabling efficient management of courses, enrollments, grades, and academic records.

The system is built using Java Swing for the user interface and PostgreSQL for data persistence, offering a robust and scalable solution for academic administration. 

## Features

### Administrator Portal
- **User Management**: Create, update, and manage student and faculty accounts
- **Course Management**: Define courses, sections, and prerequisites
- **System Configuration**: Toggle maintenance mode and set academic deadlines
- **Notifications**: Broadcast system-wide announcements to all users
- **Dashboard Analytics**: View real-time statistics on students, faculty, and courses

### Faculty Portal
- **Course Sections**: View assigned sections and enrolled students
- **Grade Management**: Input and update component scores and final grades
- **Student Performance**: Track individual student progress across components
- **Academic Calendar**: View important dates and deadlines
- **Profile Management**: Update personal information and credentials

### Student Portal
- **Course Enrollment**: Browse available courses and register for sections
- **Grade Viewing**: Access current grades and component scores
- **Academic Records**: View enrollment history and transcripts
- **Schedule Management**: Track class timings and academic commitments
- **Notifications**: Receive important announcements from the administration

### Technical Features
- Secure authentication using Argon2 password hashing
- Role-based access control (RBAC)
- Connection pooling with HikariCP for optimal database performance
- Export/Im functionality for academic records (CSV and PDF formats)
- Modern UI with FlatLaf look-and-feel
- Multi-database architecture for separation of concerns

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Programming Language | Java 21 |
| UI Framework | Java Swing with FlatLaf |
| Database | PostgreSQL 12+ |
| Connection Pool | HikariCP |
| Password Hashing | Password4j (Argon2) |
| Build Tool | Apache Maven |
| Charts | JFreeChart |

## Prerequisites

Before installing the IIITD ERP system, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **PostgreSQL 12** or higher
- **Apache Maven 3.6** or higher
- **Git** (for cloning the repository)

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/aryankhare2110/iiitd-erp.git
cd iiitd-erp
```

### 2. Database Setup

Create two PostgreSQL databases for the application:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE erp_db;
```

### 3. Initialize Database Schemas

Run the provided SQL schema files to set up the required tables:

```bash
psql -U postgres -d auth_db -f db/auth_schema.sql
psql -U postgres -d erp_db -f db/erp_schema.sql
```

The schema files will create:
- **auth_db**: User authentication and authorisation tables
- **erp_db**: Academic records including students, faculty, courses, enrollments, and grades

### 4. Configure Database Connection

Update the database connection settings in `src/main/java/edu/univ/erp/data/DBConnection.java`:

```java
private static final String AUTH_DB_URL = "jdbc:postgresql://localhost:5432/auth_db";
private static final String ERP_DB_URL = "jdbc:postgresql://localhost:5432/erp_db";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "your_password";
```

For production deployments, consider using environment variables:

```java
private static final String DB_USER = System.getenv("DB_USER");
private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
```

### 5. Build the Application

```bash
mvn clean install
```

### 6. Run the Application

```bash
mvn exec:java -Dexec.mainClass="edu.univ.erp.Main"
```

Alternatively, you can run the generated JAR file:

```bash
java -jar target/iiitd-erp-1.0-SNAPSHOT.jar
```

## Usage

### Default Credentials

The system comes pre-configured with the following default accounts:



**Security Note**: Change all default passwords immediately after first login. 

### Login Process

1. Launch the application
2. Enter your institutional email address
3. Enter your password
4. Click "Login"
5. You will be redirected to the appropriate portal based on your role

### Administrator Workflow

1. **Dashboard**: View system statistics and overall health
2. **Manage Students**: Add new students, update information, or disable accounts
3. **Manage Faculty**: Create faculty profiles and assign departments
4. **Manage Courses**: Define courses, set prerequisites, and create sections
5. **System Settings**: Configure maintenance mode and set academic deadlines
6. **Notifications**: Send announcements to all users

### Faculty Workflow

1. **Dashboard**: View assigned courses and student counts
2. **My Courses**: Access list of sections being taught
3. **Grade Entry**: Input component scores and calculate final grades
4. **Student Roster**: View enrolled students and their performance
5. **Profile**: Update personal information and change password

### Student Workflow

1.  **Dashboard**: View enrolled courses and academic standing
2. **Enroll in Courses**: Browse available sections and register
3. **My Courses**: View current enrollments and schedules
4. **Grades**: Check component scores and final grades
5. **Profile**: Update personal information and change password

## Database Schema

### Authentication Database (auth_db)

```sql
users_auth (
  user_id SERIAL PRIMARY KEY,
  email VARCHAR(100) UNIQUE NOT NULL,
  role VARCHAR(20) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  last_login TIMESTAMP
)
```

### ERP Database (erp_db)

Key tables include:

- **students**: Student profiles and academic information
- **faculty**: Faculty profiles and department assignments
- **courses**: Course catalog with prerequisites
- **sections**: Course sections with instructors and schedules
- **enrollments**: Student course registrations
- **component_scores**: Individual assessment scores
- **grades**: Final computed grades
- **departments**: Academic departments
- **settings**: System configuration parameters
- **notifications**: System announcements

Refer to `db/erp_schema.sql` for complete schema definitions.

## Project Structure

```
iiitd-erp/
├── src/
│   └── main/
│       └── java/
│           └── edu/
│               └── univ/
│                   └── erp/
│                       ├── auth/           # Authentication and session management
│                       ├── dao/            # Data Access Objects
│                       ├── data/           # Database connection management
│                       ├── domain/         # Domain models/entities
│                       ├── service/        # Business logic layer
│                       └── ui/             # User interface components
│                           ├── admin/      # Administrator portal
│                           ├── faculty/    # Faculty portal
│                           ├── student/    # Student portal
│                           ├── auth/       # Login interface
│                           └── common/     # Shared UI components
├── db/
│   ├── auth_schema.sql    # Authentication database schema
│   └── erp_schema.sql     # ERP database schema
├── pom.xml                # Maven configuration
└── README.md
```

## Configuration

### Database Connection Pool

The application uses HikariCP for connection pooling. Default settings:

- Maximum pool size: 10 connections
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes

Adjust these settings in `DBConnection.java` based on your deployment requirements.

### Logging

Logging configuration is managed through Logback. Log files are written to:

```
logs/application.log
```

Modify `src/main/resources/logback.xml` to adjust log levels and output destinations.

## Security Considerations

### Password Security
- All passwords are hashed using Argon2id algorithm
- Password hashing parameters: memory=15360, iterations=3, parallelism=2
- Never store passwords in plain text

### Database Security
- Use strong database passwords
- Limit database user privileges to only required operations
- Enable SSL/TLS for database connections in production
- Regular security audits and updates

### Application Security
- Implement rate limiting for login attempts
- Regular security patches and dependency updates
- Secure session management
- Input validation and sanitization

## Troubleshooting

### Database Connection Issues

**Problem**: Cannot connect to database

**Solution**:
1. Verify PostgreSQL service is running
2. Check database credentials in `DBConnection.java`
3. Ensure databases `auth_db` and `erp_db` exist
4. Verify PostgreSQL is listening on port 5432

### Login Failures

**Problem**: Cannot login with default credentials

**Solution**:
1.  Verify database schemas were initialised correctly
2. Check that default user records exist in `users_auth` table
3. Ensure password hashing is working correctly

### Build Errors

**Problem**: Maven build fails

**Solution**:
1.  Verify Java 21 is installed: `java -version`
2.  Verify Maven is installed: `mvn -version`
3. Clear Maven cache: `mvn clean`
4. Check internet connectivity for dependency downloads

## Development

### Running Tests

```bash
mvn test
```

### Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit changes: `git commit -am 'Add feature'`
4. Push to branch: `git push origin feature-name`
5.  Submit a pull request

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Acknowledgments

- IIIT Delhi for academic guidance
- FlatLaf for modern UI components
- PostgreSQL community for robust database system
- Apache Software Foundation for various libraries

---

**Built by Aryan Khare and Rigzin Gyalpo**

Copyright © 2025 IIITD ERP System

</div>
