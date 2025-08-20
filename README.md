# Warehouse Product Management System

A comprehensive Spring Boot REST API application for managing products in a warehouse environment. This system provides full CRUD operations for products, user authentication, audit logging, and email notifications.

## 📋 Project Overview

This application is designed to manage warehouse inventory with features including:

- Product management (create, read, update, delete)
- Product types and categorization
- Stock level monitoring and alerts
- User authentication and authorization (JWT-based)
- Audit logging for all operations
- Email notifications
- RESTful API with OpenAPI/Swagger documentation

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Security** (JWT authentication)
- **Spring Data JPA** (Database persistence)
- **PostgreSQL** (Production database)
- **H2** (Test database)
- **Maven** (Build tool)
- **Docker & Docker Compose** (Containerization)
- **JaCoCo** (Code coverage)
- **JUnit 5 & Mockito** (Testing)
- **OpenAPI/Swagger** (API documentation)
- **MailHog** (Email testing)

## 📋 Requirements

### System Requirements

- **Java 17** or higher
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git**

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/dariopipa/warehouse-springboot.git
cd warehouse-springboot
```

### 2. Environment Setup

The .env has been provided, and that's why its public.
Please change the .gitignore to include the .env file after starting development.

```env
# Server Configuration
SERVER_PORT=6565

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/warehouse
DB_USERNAME=warehouse_user
DB_PASSWORD=warehouse_pass
DB_NAME=warehouse

# Mail Configuration (for testing)
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=test@example.com
MAIL_PASSWORD=password
MAIL_UI=8025
```

### 3. Run with Docker (Recommended)

```bash
docker-compose up -d

mvn clean package -P docker
```

### 4. Run Locally (Development)

```bash
docker-compose up app-db mailhog -d

mvn spring-boot:run
```

## 🧪 Testing

### Run All Unit Tests

```bash
# Run all unit tests (excludes integration tests that require Docker)
mvn test -Dtest="*ControllerTest,*UtilsTest"
```

### Run Tests with Coverage

```bash
# Generate coverage report (focuses on meaningful business logic)
mvn clean test -Dtest="*ControllerTest,*UtilsTest"

# View coverage report
open target/site/jacoco/index.html
```

### Run All Tests (Including Integration)

```bash
# Requires Docker to be running
mvn test
```

### Test Coverage

- Coverage report excludes boilerplate code (entities, DTOs, enums, configs)
- Focuses on business logic: controllers, services, utilities
- Current coverage: ~94% for ProductsController, 100% for utilities

## 📚 API Documentation

### Swagger UI

- **URL**: http://localhost:6565/swagger-ui/html
- **API Docs**: http://localhost:6565/v1/api-docs

### Key Endpoints

```
POST   /api/v1/auth/login      - User authentication
POST   /api/v1/auth/register   - User registration (Admin/Manager only)
GET    /api/v1/products        - Get products (paginated)
POST   /api/v1/products        - Create product
GET    /api/v1/products/{id}   - Get product by ID
PUT    /api/v1/products/{id}   - Update product
DELETE /api/v1/products/{id}   - Delete product
PATCH  /api/v1/products/{id}/quantity - Update product quantity
```

## 🔧 Development

### Project Structure

```
src/
├── main/java/io/github/dariopipa/warehouse/
│   ├── controllers/     # REST controllers
│   ├── services/        # Business logic
│   ├── repositories/    # Data access layer
│   ├── entities/        # JPA entities
│   ├── dtos/           # Data transfer objects
│   ├── security/       # JWT authentication
│   ├── config/         # Spring configuration
│   ├── utils/          # Utility classes
│   └── exceptions/     # Custom exceptions
└── test/java/          # Unit and integration tests
```

### Key Features

- **JWT Authentication**: Secure API access
- **Role-based Authorization**: Admin, Manager, User roles
- **Audit Logging**: Track all data changes
- **Soft Delete**: Products are marked as deleted, not physically removed
- **Stock Alerts**: Monitor low stock levels
- **Email Notifications**: Automated alerts
- **Pagination**: Efficient data retrieval
- **HATEOAS**: Hypermedia-driven API responses

## 🗄️ Database

### Default Users (created via data.sql)

- **Admin**: admin@warehouse.com / admin123
- **Manager**: manager@warehouse.com / manager123

### Entity Relationships

- Users have Roles (Many-to-Many)
- Products belong to ProductTypes (Many-to-One)
- Products have StockAlerts (One-to-Many)
- All operations are logged in AuditLog

## 🐳 Docker Support

### Services

- **backend**: Spring Boot application
- **app-db**: PostgreSQL database
- **mailhog**: Email testing service

### Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up --build
```

## 📊 Monitoring

### Actuator Endpoints

- **Health**: http://localhost:6565/api/health
- **Metrics**: http://localhost:6565/api/metrics

### Email Testing

- **MailHog UI**: http://localhost:8025
