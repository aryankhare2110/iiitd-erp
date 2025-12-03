# IIITD ERP System

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-336791?style=flat-square&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=flat-square&logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

A comprehensive Enterprise Resource Planning system for IIITD built with Java Swing and PostgreSQL.

## Technology Stack

**Core Technologies**
- Java 21
- PostgreSQL 12+
- Apache Maven 3.6+

**Libraries and Frameworks**
- FlatLaf 3.3 (Modern UI Look and Feel)
- HikariCP 6.1. 0 (Database Connection Pooling)
- Password4j 1.7.3 (Argon2 Password Hashing)
- JFreeChart 1.5.5 (Data Visualization)
- PostgreSQL JDBC Driver 42.7.3

## Prerequisites

Ensure you have the following installed before proceeding:

- Java Development Kit (JDK) 21 or higher
- PostgreSQL 12 or higher
- Apache Maven 3.6 or higher
- Git

## Installation

### Clone the Repository

```bash
git clone https://github.com/aryankhare2110/iiitd-erp.git
cd iiitd-erp
```

### Database Setup

Create two PostgreSQL databases:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE erp_db;
```

### Initialise Database Schemas

Run the SQL schema files to create the required tables:

```bash
psql -U postgres -d auth_db -f db/auth_schema.sql
psql -U postgres -d erp_db -f db/erp_schema. sql
```

The schema files will initialise:
- **auth_db**: User authentication and authorisation tables
- **erp_db**: Academic records including students, faculty, courses, enrollments, and grades

### Configure Database Connection

Update database credentials in `src/main/java/edu/univ/erp/data/DBConnection.java`:

```java
private static final String username = "postgres";
private static final String password = "your_password";
private static final String url1 = "jdbc:postgresql://localhost:5432/auth_db";
private static final String url2 = "jdbc:postgresql://localhost:5432/erp_db";
```


### Build the Application

```bash
mvn clean install
```

### Run the Application

Using Maven:

```bash
mvn clean compile exec: java
```

Or build and run the JAR file:

```bash
mvn clean package
java -jar target/iiitd-erp-1.0-SNAPSHOT.jar
```

## Usage

### Default Credentials

The system includes pre-configured accounts for testing:

| Role | Email | Password |
|------|-------|----------|
| Administrator | admin@iiitd.ac.in | Admin123 |
| Faculty | ravi.sharma@iiitd.ac.in | Ravi&123 |
| Faculty | meera.bansal@iiitd.ac.in | Meera123$ |
| Student | aryan24124@iiitd.ac.in | Aryan123 |
| Student | ananya23104@iiitd.ac.in | AnanyaVerma#123 |
| Student | rohan22457@iiitd.ac.in | Mehra@789 |

### Login Process

1. Launch the application
2. Enter your institutional email address
3. Enter your password
4. Click "Sign In"
5. Access the portal corresponding to your role

## Project Structure

```
iiitd-erp/
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/
│       │       └── univ/
│       │           └── erp/
│       │               ├── Main.java              # Application entry point
│       │               ├── auth/                  # Authentication and session
│       │               ├── dao/                   # Data Access Objects
│       │               ├── data/                  # Database connections
│       │               ├── domain/                # Domain models
│       │               ├── service/               # Business logic
│       │               └── ui/                    # User interface
│       │                   ├── admin/             # Administrator portal
│       │                   ├── faculty/           # Faculty portal
│       │                   ├── student/           # Student portal
│       │                   ├── auth/              # Login interface
│       │                   └── common/            # Shared UI components
│       └── resources/
│           └── Images/                            # Application images
├── db/
│   ├── auth_schema.sql                           # Auth database schema
│   └── erp_schema.sql                            # ERP database schema
├── pom.xml                                       # Maven configuration
└── README.md
```

## Security Features

**Password Security**
All passwords are hashed using Argon2id algorithm with the following parameters:
- Memory: 15360 KB
- Iterations: 3
- Parallelism: 2

**Database Security**
- Separate databases for authentication and application data
- Connection pooling with HikariCP
- Prepared statements to prevent SQL injection

**Application Security**
- Role-based access control (RBAC)
- Session management
- Account lockout after failed login attempts
- Input validation and sanitisation

## Configuration

**Database Connection Pool**

Default HikariCP settings configured in `DBConnection.java`:
- Maximum pool size: 10 connections per database
- Pool names: authDBPool and erpDBPool

## License

This project is licensed under the MIT License. 

## Acknowledgments

Built using open source technologies:
- FlatLaf for modern UI components
- PostgreSQL for reliable data storage
- HikariCP for efficient connection pooling
- Password4j for secure password hashing

---
**Built by Aryan Khare and Rigzin Gyalpo**
