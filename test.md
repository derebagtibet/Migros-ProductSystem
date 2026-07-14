# Migros Product System - macOS zsh Test Guide

This guide contains copy-pasteable commands for testing the three services from a macOS zsh terminal.

## 1. Open and Start the Project

Use the actual local path on your Mac:

```bash
cd /path/to/Migros-ProductSystem
docker compose down
docker compose build --no-cache
docker compose up -d
docker compose ps
```

## 2. Health Checks

```bash
curl -sS http://localhost:8081/actuator/health
curl -sS http://localhost:8082/actuator/health
curl -sS http://localhost:8083/actuator/health
```

Expected: every response contains `"status":"UP"`.

```bash
curl -sS http://localhost:8082/actuator
curl -sS http://localhost:8082/actuator/info
curl -sS http://localhost:8083/actuator
curl -sS http://localhost:8083/actuator/info
```

## 3. Category API

`CategoryResponse` has no Bean Validation annotations. Its fields are `code`, `name`, and `active`.

### Create Category

Single line:

```bash
curl -i -X POST 'http://localhost:8082/api/v1/categories' -H 'Content-Type: application/json' -d '{"code":"FR","name":"Fruit","active":true}'
```

Multi-line:

```bash
curl -i -X POST 'http://localhost:8082/api/v1/categories' \
  -H 'Content-Type: application/json' \
  -d '{"code":"FR","name":"Fruit","active":true}'
```

### Get All Categories

```bash
curl -i 'http://localhost:8082/api/v1/categories'
```

### Get Category by Code

```bash
curl -i 'http://localhost:8082/api/v1/categories/FR'
```

Category currently has no `PUT` or `DELETE` mapping, so those operations are not included as valid tests.

## 4. Product API

Valid product payload:

```json
{"name":"Apple","code":"FR001","categoryCode":"FR","brand":"Migros","unit":"KILOGRAM"}
```

Validation rules:

```text
name:         @NotBlank
code:         @NotBlank and exactly 5 characters
categoryCode: @NotBlank and exactly 2 characters
brand:        @NotBlank
unit:         @NotNull, either PIECE or KILOGRAM
```

Create the `FR` category first. The category must be active.

### Create Product

Single line:

```bash
curl -i -X POST 'http://localhost:8081/api/v1/products' -H 'Content-Type: application/json' -d '{"name":"Apple","code":"FR001","categoryCode":"FR","brand":"Migros","unit":"KILOGRAM"}'
```

Multi-line:

```bash
curl -i -X POST 'http://localhost:8081/api/v1/products' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Apple","code":"FR001","categoryCode":"FR","brand":"Migros","unit":"KILOGRAM"}'
```

Save the returned `id`. The examples below use `1`; replace it with the actual ID when necessary.

### Get All Products

```bash
curl -i 'http://localhost:8081/api/v1/products'
```

### Get Product by ID

```bash
curl -i 'http://localhost:8081/api/v1/products/1'
```

There is no Product `GET BY CODE` endpoint. The controller exposes only `GET /api/v1/products/{id}`.

### Update Product

Single line:

```bash
curl -i -X PUT 'http://localhost:8081/api/v1/products/1' -H 'Content-Type: application/json' -d '{"name":"Apple Updated","code":"FR001","categoryCode":"FR","brand":"Migros","unit":"KILOGRAM"}'
```

Multi-line:

```bash
curl -i -X PUT 'http://localhost:8081/api/v1/products/1' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Apple Updated","code":"FR001","categoryCode":"FR","brand":"Migros","unit":"KILOGRAM"}'
```

### Delete Product

```bash
curl -i -X DELETE 'http://localhost:8081/api/v1/products/1'
```

## 5. Barcode API

`BarcodeCreateRequest` requires non-null `productId` and `type`. Valid types are `PRODUCT`, `SCALE`, and `CASE`.

For an `FR` product with `KILOGRAM`, the current barcode business rules allow `PRODUCT`.

### Create Barcode

Single line:

```bash
curl -i -X POST 'http://localhost:8083/api/v1/barcodes' -H 'Content-Type: application/json' -d '{"productId":1,"type":"PRODUCT"}'
```

Multi-line:

```bash
curl -i -X POST 'http://localhost:8083/api/v1/barcodes' \
  -H 'Content-Type: application/json' \
  -d '{"productId":1,"type":"PRODUCT"}'
```

### Get All Barcodes

```bash
curl -i 'http://localhost:8083/api/v1/barcodes'
```

### Get Barcodes by Product

```bash
curl -i 'http://localhost:8083/api/v1/barcodes/product/1'
```

### Get Barcode by ID

```bash
curl -i 'http://localhost:8083/api/v1/barcodes/1'
```

### Delete Barcode

```bash
curl -i -X DELETE 'http://localhost:8083/api/v1/barcodes/1'
```

## 6. Negative Validation Tests

Invalid product: `code` is not five characters, `categoryCode` is not two characters, and `unit` is not a valid enum value.

```bash
curl -i -X POST 'http://localhost:8081/api/v1/products' -H 'Content-Type: application/json' -d '{"name":"Invalid Product","code":"BAD1","categoryCode":"F","brand":"Migros","unit":"BOX"}'
```

Invalid barcode: both required fields are null.

```bash
curl -i -X POST 'http://localhost:8083/api/v1/barcodes' -H 'Content-Type: application/json' -d '{"productId":null,"type":null}'
```

## 7. Why zsh Printed command not found

This is incorrect:

```bash
curl -X POST http://localhost:8081/api/v1/products
-H "Content-Type: application/json"
-d '{"name":"Apple"}'
```

Without a trailing `\\`, zsh treats `-H` and `-d` as new commands. Use one line or use a backslash at the end of every continued line:

```bash
curl -X POST 'http://localhost:8081/api/v1/products' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Apple","code":"FR001","categoryCode":"FR","brand":"Migros","unit":"KILOGRAM"}'
```

## 8. Stop Docker

```bash
docker compose down
```
