# Warehouse Product Management System

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=bugs)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=coverage)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=dariopipa_warehouse-springboot&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=dariopipa_warehouse-springboot)

A Spring Boot application for warehouse product management with JWT authentication, email notifications, and comprehensive API documentation.

## Requirements

To run this application, you need:

- **Java 21** (for building the Docker image)
- **Maven 3.8+** (for building the Docker image)
- **Docker** (version 20.0 or higher)
- **Docker Compose** (version 2.0 or higher)

## Quick Start

1. Clone the repository

   ```bash
   git clone https://github.com/dariopipa/warehouse-springboot.git
   ```

2. Navigate to the project directory

   ```bash
   cd warehouse-springboot
   ```

3. Build the Docker image

   ```bash
   mvn clean package -P docker
   ```

4. Start the application:

   ```bash
   docker compose up -d
   ```

The application can be accessed at `http://localhost:6565`

## Architecture

The application consists of:

- **Spring Boot API** - port 6565
- **PostgreSQL Database** - port 5432
- **MailHog** - Email testing server (UI port 8025, SMTP port 1025)

## Available Endpoints

Once the application is running, you can access:

### API Documentation

- **Swagger UI**: [http://localhost:6565/swagger-ui/index.html](http://localhost:6565/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:6565/v1/api-docs](http://localhost:6565/v1/api-docs)

### Email Testing

- **MailHog Web UI**: [http://localhost:8025](http://localhost:8025)
  - View all emails sent by the application
  - Test email notifications

### Health Monitoring (Only if logged in as admin)

- **Health Check**: [http://localhost:6565/actuator/health](http://localhost:6565/actuator/health)
- **Application Info**: [http://localhost:6565/actuator/info](http://localhost:6565/actuator/info)

#### Admin Login Example

First, get the JWT token by logging in:

```bash
# Login and get JWT token
curl -X POST "http://localhost:6565/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Then use the token to access protected endpoints:

```bash
# Access health endpoint with JWT token (replace YOUR_JWT_TOKEN with actual token)
curl -X GET "http://localhost:6565/actuator/health" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## For Developers

### Prerequisites

- **Java 21**
- **Maven 3.8+**
- **Docker & Docker Compose**

### Running Tests

```bash
# Run unit tests
mvn test

# Run all tests (unit + integration)
mvn clean verify
```

### Code Quality & Coverage

```bash
# Generate code coverage report
mvn clean test jacoco:report

# Run SonarQube analysis (requires SONAR_TOKEN)
mvn clean verify sonar:sonar
```

### Building the Application

```bash
# Build JAR file
mvn clean package

# Build Docker image
mvn clean package -P docker

# Skip tests during build
mvn clean package -DskipTests
```

### Development Workflow

1. **Start local development environment:**

   ```bash
   docker compose up -d app-db mailhog
   ```

2. **Run the application locally:**

   ```bash
   mvn spring-boot:run
   ```

3. **Run integration tests:**

   ```bash
   mvn clean verify
   ```

### Load Testing

The project includes K6 performance tests. Install K6 first: [K6 Installation Guide](https://k6.io/docs/get-started/installation/)

```bash
# Navigate to k6 directory
cd k6

# Run smoke test
k6 run smoke-test.js

# Run load test
k6 run load-test.js

# Run stress test
k6 run stress-test.js

# Run spike test
k6 run spike-test.js
```

### Docker Commands

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Stop all services
docker compose down

# Stop and remove volumes
docker compose down -v

# Rebuild and start
docker compose up -d --build
```

## Tech Stack

- **Backend**: Spring Boot 3.5.3, Java 21
- **Database**: PostgreSQL 17
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI 3 with Swagger UI
- **Testing**: JUnit 5, Testcontainers, K6
- **Build**: Maven
- **Deployment**: Docker, Docker Compose
