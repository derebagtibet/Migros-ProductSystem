# Inventory Management Runbook

This runbook is written for macOS zsh and Docker Desktop.

## Prerequisites

```bash
java -version
mvn -version
docker --version
docker compose version
```

## Start the Project

From the repository root:

```bash
cd /path/to/Migros-ProductSystem
docker compose down
docker compose build --no-cache
docker compose up -d
```

Check containers:

```bash
docker compose ps
```

Expected services: `postgres-db`, `product-service`, `category-service`, `barcode-service`, and `zipkin`.

## Service URLs

```text
Product Swagger:  http://localhost:8081/swagger-ui/index.html
Category Swagger: http://localhost:8082/swagger-ui/index.html
Barcode Swagger:  http://localhost:8083/swagger-ui/index.html
```

## Actuator Health

```bash
curl -sS http://localhost:8081/actuator/health
curl -sS http://localhost:8082/actuator/health
curl -sS http://localhost:8083/actuator/health
```

Each response must contain `{"status":"UP"}`. The endpoint index and info endpoint are also available at `/actuator` and `/actuator/info`.

## API Smoke Test

These commands are valid for macOS zsh. JSON is wrapped in single quotes so its double quotes are preserved.

### Category

```bash
curl -i -X POST 'http://localhost:8082/api/v1/categories' -H 'Content-Type: application/json' -d '{"code":"FR","name":"Fruit","active":true}'
curl -i 'http://localhost:8082/api/v1/categories'
curl -i 'http://localhost:8082/api/v1/categories/ME'
```

The service already contains these active categories: `ME`, `BA`, `ET`, `SU`, and `TM`. The POST command above adds `FR` for an optional custom category test.

Category currently has no `PUT` or `DELETE` mapping.

### Product

The product request requires a non-blank name and brand, a five-character code, a two-character category code, and `PIECE` or `KILOGRAM` as unit. The category must exist and be active.

Use this valid Swagger example. Do not leave generated placeholder values such as `"code":"string"` or `"categoryCode":"st"` in the request:

```json
{
  "name": "Tibet Product",
  "code": "ME001",
  "categoryCode": "ME",
  "brand": "Migros",
  "unit": "PIECE"
}
```

```bash
curl -i -X POST 'http://localhost:8081/api/v1/products' -H 'Content-Type: application/json' -d '{"name":"Tibet Product","code":"ME001","categoryCode":"ME","brand":"Migros","unit":"PIECE"}'
curl -i 'http://localhost:8081/api/v1/products'
curl -i 'http://localhost:8081/api/v1/products/1'
curl -i -X PUT 'http://localhost:8081/api/v1/products/1' -H 'Content-Type: application/json' -d '{"name":"Tibet Product Updated","code":"ME001","categoryCode":"ME","brand":"Migros","unit":"PIECE"}'
curl -i -X DELETE 'http://localhost:8081/api/v1/products/1'
```

Product has `GET BY ID`, not `GET BY CODE`. Replace `1` with the actual product ID returned by POST.

If Product Service returns `503 Category Service is temporarily unavailable`, first check the category code. This message can occur when a non-existing code such as `st` is sent from Swagger. It does not necessarily mean that Docker networking is down.

### Barcode

The product must already exist. Valid enum values are `PRODUCT`, `SCALE`, and `CASE`; business rules also depend on the product category and unit. For the sample `ME` product with `PIECE`, use `PRODUCT`.

```bash
curl -i -X POST 'http://localhost:8083/api/v1/barcodes' -H 'Content-Type: application/json' -d '{"productId":1,"type":"PRODUCT"}'
curl -i 'http://localhost:8083/api/v1/barcodes'
curl -i 'http://localhost:8083/api/v1/barcodes/product/1'
curl -i 'http://localhost:8083/api/v1/barcodes/1'
curl -i -X DELETE 'http://localhost:8083/api/v1/barcodes/1'
```

## Multi-line zsh Example

```bash
curl -i -X POST 'http://localhost:8081/api/v1/products' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Tibet Product","code":"ME001","categoryCode":"ME","brand":"Migros","unit":"PIECE"}'
```

The backslash must be the final character on every continued line.

## Logs and Ports

```bash
docker compose logs --tail=100 category-service
docker compose logs --tail=100 product-service
docker compose logs --tail=100 barcode-service
lsof -i :8081
lsof -i :8082
lsof -i :8083
```

## Stop the Project

```bash
docker compose down
```

To also delete the PostgreSQL volume:

```bash
docker compose down -v
```
