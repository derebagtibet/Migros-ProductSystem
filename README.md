# 📦 Inventory Management Microservices

A Spring Boot based Inventory Management System developed using Microservice Architecture.

This project consists of three independent microservices that communicate with each other using OpenFeign.

---

# 🚀 Architecture

```
                    +------------------+
                    | Category Service |
                    +--------+---------+
                             ^
                             |
                         OpenFeign
                             |
+----------------+     +-----+------+      +----------------+
| Barcode Service|---->| Product    |----->| PostgreSQL DB |
+----------------+     | Service    |      +----------------+
        ^              +------------+
        |
    OpenFeign
```

---

# 🛠️ Technologies

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- Spring Cloud OpenFeign
- PostgreSQL
- Docker
- Docker Compose
- Swagger / OpenAPI
- Lombok
- Maven

---

# 📂 Project Structure

```
inventory-management
│
├── product-service
├── category-service
├── barcode-service
└── docker-compose.yml
```

---

# 📌 Microservices

## Product Service

Responsible for product management.

### Features

- Create Product
- Update Product
- Delete Product
- Get Product
- Get All Products
- Product Validation
- Category Validation (Feign Client)

Runs on

```
localhost:8081
```

---

## Category Service

Provides category information.

Current implementation uses mock data.

Runs on

```
localhost:8082
```

---

## Barcode Service

Responsible for barcode generation.

Supports

- Product Barcode
- Case Barcode
- Scale Barcode

Runs on

```
localhost:8083
```

---

# 📦 Product Model

```
Product

- id
- name
- code
- category
- brand
- unit
```

---

# 🏷 Barcode Model

```
Barcode

- id
- code
- type
- productId
- createdAt
```

---

# 📜 Business Rules

### Product

- Product name must be unique.
- Product code must be unique.
- Product code length = 5.
- First two characters of product code must match category code.
- Every product must have
  - Brand
  - Category
  - Unit

### Barcode

A product can have multiple barcodes.

Barcode belongs to only one product.

Rules

| Category | Unit | Allowed Barcode Types |
|----------|------|----------------------|
| Fruit | Kilogram | PRODUCT, CASE |
| Fish | Kilogram | PRODUCT, SCALE |
| Fish | Piece | CASE |
| Meat | Any | SCALE |
| Others | Any | PRODUCT |

---

# 🔗 Service Communication

```
Product Service
        |
        | OpenFeign
        ↓
Category Service

Barcode Service
        |
        | OpenFeign
        ↓
Product Service
```

---

# 🐳 Docker

# Product Service Resilience and Observability

Product Service does not call Category Service directly from validation code anymore.
The flow is:

```text
ProductValidator
        |
        v
CategoryGateway
        |
        v
Retry + Circuit Breaker
        |
        v
CategoryServiceClient
        |
        v
Category Service
```

## Category Gateway

- `CategoryGateway` wraps the existing OpenFeign client.
- `ProductValidator` keeps validation rules only.
- The gateway owns external Category Service communication and resilience behavior.
- Fallback does not return fake category data.
- If Category Service is unavailable, the fallback logs the failure and throws `ServiceUnavailableException`.
- `GlobalExceptionHandler` maps this exception to HTTP `503 Service Unavailable`.

Expected fallback response:

```json
{
  "timestamp": "...",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Category Service is temporarily unavailable"
}
```

## Resilience4j

Product Service uses:

- `resilience4j-spring-boot3`
- `spring-boot-starter-aop`
- `spring-boot-starter-actuator`

Configured instance name:

```yaml
categoryService
```

The same name is used by:

- `@Retry(name = "categoryService")`
- `@CircuitBreaker(name = "categoryService")`
- `resilience4j.retry.instances.categoryService`
- `resilience4j.circuitbreaker.instances.categoryService`

Retry is applied only to the Category Service lookup through Feign. It is not added directly to Product Service `POST`, `PUT`, or database write methods.

## Feign Timeout

Category Feign client timeout is configured to avoid long blocking calls:

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          category-service:
            connectTimeout: 1000
            readTimeout: 2000
```

## Externalized URLs

Category Service URL is externalized:

```yaml
services:
  category:
    url: ${SERVICES_CATEGORY_URL:http://category-service:8082}
```

Docker Compose sets:

```yaml
SERVICES_CATEGORY_URL: http://category-service:8082
```

## Tracing and Zipkin

Spring Cloud Sleuth is not used because it was removed for Spring Boot 3 based projects.
Product Service uses Micrometer Tracing with Brave and Zipkin reporter.

Zipkin endpoint:

```yaml
management:
  zipkin:
    tracing:
      endpoint: ${ZIPKIN_ENDPOINT:http://zipkin:9411/api/v2/spans}
```

Zipkin UI:

```text
http://localhost:9411
```

## Trace ID Logging

Product Service logs include trace and span IDs:

```yaml
logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

## Actuator

Product Service exposes health, info, metrics, and circuit breaker endpoints:

```text
http://localhost:8081/actuator/health
http://localhost:8081/actuator/metrics
```

## Manual Resilience Test

Start services:

```bash
docker compose up --build
```

Create product while Category Service is available:

```bash
curl -i -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Resilience Test Available\",\"code\":\"R1001\",\"categoryCode\":\"ME\",\"brand\":\"Migros\",\"unit\":\"PIECE\"}"
```

Stop Category Service:

```bash
docker compose stop category-service
```

Create product while Category Service is unavailable:

```bash
curl -i -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Resilience Test Unavailable\",\"code\":\"R1002\",\"categoryCode\":\"ME\",\"brand\":\"Migros\",\"unit\":\"PIECE\"}"
```

Expected result:

```text
HTTP/1.1 503
```

Restart Category Service:

```bash
docker compose start category-service
```

---

Build

```bash
docker compose build
```

Run

```bash
docker compose up
```

Stop

```bash
docker compose down
```

---

# 📖 Swagger

Product

```
http://localhost:8081/swagger-ui/index.html
```

Category

```
http://localhost:8082/swagger-ui/index.html
```

Barcode

```
http://localhost:8083/swagger-ui/index.html
```

---

# 🧪 Testing

The project includes

- Unit Tests
- Integration Tests

---

# 📚 API Examples

Create Product

```http
POST /api/v1/products
```

```json
{
  "name":"Apple",
  "code":"FR001",
  "categoryCode":"FR",
  "brand":"Migros",
  "unit":"KILOGRAM"
}
```

---

Create Barcode

```http
POST /api/v1/barcodes
```

```json
{
    "productId":1,
    "type":"PRODUCT"
}
```

---

# 📁 Profiles

Development

- PostgreSQL via Docker or local PostgreSQL

Production

- PostgreSQL

---

# ✨ Future Improvements

- API Gateway
- Eureka Discovery Server
- Config Server
- JWT Authentication
- Kafka Event Streaming
- Redis Cache
- Monitoring (Prometheus + Grafana)
- CI/CD Pipeline (GitHub Actions)
- Kubernetes Deployment

---

# 👨‍💻 Author

**Tibet Derebağ**

Software Engineering Student

Backend Developer

Java • Spring Boot • Microservices • Docker • PostgreSQL
