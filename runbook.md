# 🚀 Inventory Management - Runbook

This document explains how to build and run the Inventory Management Microservices project.

---

# Prerequisites

Before running the project, make sure the following software is installed.

- Java 21
- Maven 3.9+
- Docker Desktop
- Git

Verify installation:

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

# Project Structure

```
inventory-management
│
├── product-service
├── category-service
├── barcode-service
└── docker-compose.yml
```

---

# Step 1 - Clone Repository

```bash
git clone <repository-url>

cd inventory-management
```

---

# Step 2 - Build Services

Build every microservice.

Product Service

```bash
cd product-service
./mvnw clean package -DskipTests
```

Category Service

```bash
cd ../category-service
./mvnw clean package -DskipTests
```

Barcode Service

```bash
cd ../barcode-service
./mvnw clean package -DskipTests
```

Return to project root.

```bash
cd ..
```

---

# Step 3 - Start Docker Desktop

Make sure Docker Desktop is running.

Verify:

```bash
docker info
```

---

# Step 4 - Run Application

Build Docker images and start all containers.

```bash
docker compose up --build
```

To run in detached mode:

```bash
docker compose up --build -d
```

---

# Step 5 - Verify Containers

Check running containers.

```bash
docker compose ps
```

Expected containers:

- postgres-db
- product-service
- category-service
- barcode-service

---

# Step 6 - Verify APIs

## Product Service

```
http://localhost:8081/swagger-ui/index.html
```

## Category Service

```
http://localhost:8082/swagger-ui/index.html
```

## Barcode Service

```
http://localhost:8083/swagger-ui/index.html
```

---

# Step 7 - Verify PostgreSQL

Enter PostgreSQL container.

```bash
docker exec -it postgres-db psql -U postgres -d product_db
```

Show tables

```sql
\dt
```

List products

```sql
SELECT * FROM products;
```

Exit PostgreSQL

```sql
\q
```

---

# Useful Docker Commands

Build

```bash
docker compose build
```

Start

```bash
docker compose up
```

Stop

```bash
docker compose stop
```

Remove containers

```bash
docker compose down
```

Remove containers and database volume

```bash
docker compose down -v
```

Restart

```bash
docker compose restart
```

Show logs

```bash
docker compose logs
```

Product Service logs

```bash
docker compose logs product-service
```

Barcode Service logs

```bash
docker compose logs barcode-service
```

Category Service logs

```bash
docker compose logs category-service
```

PostgreSQL logs

```bash
docker compose logs postgres
```

---

# Common Problems

## Port Already in Use

Check ports.

```bash
lsof -i :8081
lsof -i :8082
lsof -i :8083
lsof -i :5432
```

Kill process.

```bash
kill -9 <PID>
```

Or stop all Java processes.

```bash
pkill -f java
```

---

## Docker Daemon Not Running

Start Docker Desktop.

Verify:

```bash
docker info
```

---

## Rebuild Containers

```bash
docker compose down

docker compose up --build
```

---

## Rebuild Maven Project

```bash
./mvnw clean package
```

---

## Clean Docker Cache

```bash
docker system prune -a
```

---

# API Test Examples

Create Product

```bash
curl -X POST http://localhost:8081/api/v1/products \
-H "Content-Type: application/json" \
-d '{
"name":"Apple",
"code":"FR001",
"categoryCode":"FR",
"brand":"Migros",
"unit":"KILOGRAM"
}'
```

Get Products

```bash
curl http://localhost:8081/api/v1/products
```

Create Barcode

```bash
curl -X POST http://localhost:8083/api/v1/barcodes \
-H "Content-Type: application/json" \
-d '{
"productId":1,
"type":"PRODUCT"
}'
```

Get Product Barcodes

```bash
curl http://localhost:8083/api/v1/barcodes/product/1
```

---

# Development Profiles

| Profile | Database |
|----------|----------|
| dev | H2 |
| prod | PostgreSQL |

---

# Shutdown

Stop all services.

```bash
docker compose down
```

If database reset is required:

```bash
docker compose down -v
```

---

# Maintainer

Tibet Derebağ

Inventory Management Microservices