# E-commerce Backend

Spring Boot based e-commerce backend application with JWT authentication and email verification.

## Prerequisites

- Java JDK 17 or higher
- MySQL 8.0
- Maven 3.8+
- Git
- IDE (IntelliJ IDEA recommended)
- Fake SMTP Server (for email testing)

## Local Setup

### 1. Clone Repository
```bash
git clone https://github.com/rohithpothula/Ecommerce-backend.git
cd ecommerce-backend
```

### 2. Database Setup
```bash
mysql -u root -p create database ecommerce;
```
- Update `src/main/resources/application.properties` with your database credentials
- Run the application to create tables
- Verify tables in the database
- Run the SQL script in `src/main/resources/db/data.sql` to insert sample data
- Verify data in the tables


### 3. SMTP Server Setup
```bash
docker run -p 3000:80 -p 2525:25 rnwood/smtp4dev
```
- Open http://localhost:3000 in your browser for SMTP server UI

### 4. Configure Application Properties
```bash
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/ecommerce
spring.datasource.username=root
spring.datasource.password=your_password

spring.mail.host=localhost
spring.mail.port=2525
```
### 4. Build and Run Application
```bash
mvn clean install
mvn spring-boot:run
```
### 5. Test Application
- Open http://localhost:8080/swagger-ui.html in your browser
- Register a new user
- Verify email using fake SMTP server
- Login and get JWT token
- Use JWT token to access protected APIs
- Test APIs using Swagger UI
## Git Hooks Setup

### 1. Change to Git Hooks Directory
```bash
cd .git-hooks
```
### 2. Make Hooks Executable
```
chmod +x .git-hooks/pre-commit
chmod +x .git-hooks/commit-msg
```
### 3. Configure Git to Use Custom Hooks Path
```
git config core.hooksPath .git-hooks
```
### 4. Verify Installation
```
ls -la .git-hooks/
git config --get core.hooksPath
```
### 5. Commit Message Format

**type(scope): message**

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation changes
- style: Code style changes
- refactor: Code refactoring
- perf: Performance improvements
- test: Adding tests
- chore: Maintenance tasks

### 6. Example commit messages:
- feat(auth): add user registration
- fix(db): resolve connection timeout
- docs(api): update API documentation
