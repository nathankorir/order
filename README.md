# 🧪 Orders Service

This microservice handles order management for the e-commerce platform. It allows creating and retrieving order records with pagination and filtering support.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Mapstruct
- Swagger (OpenAPI)
- Docker / Kubernetes (optional)
- JUnit 5 + MockMvc for testing

---

## ✅ Prerequisites

Ensure the following are installed and properly configured on your system:

- Java 17
- Maven
- Docker
  > Make sure Docker is installed and **running** before starting the application.

---

## 🚀 How to Run the Project

Follow these steps to run the application locally:

### 1. Clone the project
```bash
https://github.com/nathankorir/order.git
```

### 2. Install dependencies
```bash
mvn clean install
```

### 3. Start the postgres database and spring boot application
```bash
docker-compose up
```

### 4. Run the application
```bash
./mvnw spring-boot:run  
```

## Documentation

### Swagger UI
```bash
http://localhost:8081/swagger-ui/index.html
```

### OpenAPI JSON Spec
```bash
http://localhost:8081/v3/api-docs
```

## 🔐 Authentication
To access secured endpoints, you need to obtain a JWT token.

### Login Endpoint
```bash
POST http://localhost:8081/auth/login
```
Use this endpoint to get a token.

Note:
Authentication credentials are hardcoded for development/demo purposes using Spring Security. Update it with real user management in production.

### Default user credentials
Username: user
Password: password