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
- H2 Database (Development)
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

- H2 Database

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
# MigrosCaseStudy
# MigrosCaseStudy
