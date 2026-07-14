# Migros Product System - Presentation Test Guide

Bu dosya sunum sirasinda sistemi Docker ile ayaga kaldirip Product Service, Category Service ve Barcode Service akisini test etmek icin hazirlandi.

## 1. Proje Klasorune Git

```powershell
cd C:\Users\dereb\Desktop\inventory-management\Migros-ProductSystem
```

## 2. Servisleri Paketle

Docker image'lari `target/*.jar` dosyasini kopyaladigi icin once jar dosyalarini olustur.

```powershell
mvn -q -f category-service\pom.xml package -DskipTests
mvn -q -f product-service\pom.xml package -DskipTests
mvn -q -f barcode-service\pom.xml package -DskipTests
```

Beklenen sonuc: Komutlar hata vermeden biter.

## 3. Docker Servislerini Baslat

```powershell
docker compose up -d --build
```

Beklenen sonuc: Container'lar baslar.

Kontrol:

```powershell
docker compose ps
```

Beklenen durum:

```text
postgres-db       Up / healthy
category-service  Up
product-service   Up
barcode-service   Up
zipkin            Up
```

## 4. Product Service Kapanma Hatasini Kontrol Et

```powershell
docker compose logs --tail=80 product-service
```

Beklenen sonuc:

```text
Started ProductServiceApplication
Tomcat started on port 8081
```

Gorulmemesi gereken hata:

```text
Application run failed
fallbackExecutor
RxJava3FallbackDecorator
```

## 5. Health Check Testleri

```powershell
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

Beklenen sonuc:

```json
{"status":"UP"}
```

Not: Bir serviste actuator health kapaliysa bu adim yerine ilgili GET endpoint'i test edilebilir.

## 6. Category Olustur

Product Service, product olustururken Category Service'e gider. Bu yuzden once kategori olusturuyoruz.

```powershell
curl -X POST http://localhost:8082/api/v1/categories `
  -H "Content-Type: application/json" `
  -d "{\"code\":\"FR\",\"name\":\"Fruit\",\"active\":true}"
```

Beklenen sonuc:

```json
{
  "code": "FR",
  "name": "Fruit",
  "active": true
}
```

Kategori listesini kontrol et:

```powershell
curl http://localhost:8082/api/v1/categories
```

## 7. Product Olustur

Fruit + KILOGRAM icin barcode kurallarina gore PRODUCT ve CASE tipi uygundur.

```powershell
curl -X POST http://localhost:8081/api/v1/products `
  -H "Content-Type: application/json" `
  -d "{\"name\":\"Apple\",\"code\":\"FR001\",\"categoryCode\":\"FR\",\"brand\":\"Migros\",\"unit\":\"KILOGRAM\"}"
```

Beklenen sonuc:

```json
{
  "id": 1,
  "name": "Apple",
  "code": "FR001",
  "categoryCode": "FR",
  "brand": "Migros",
  "unit": "KILOGRAM"
}
```

Product listesini kontrol et:

```powershell
curl http://localhost:8081/api/v1/products
```

## 8. Product Service'in Category Service ile Konustugunu Goster

Olmayan kategoriyle product olusturmayi dene.

```powershell
curl -X POST http://localhost:8081/api/v1/products `
  -H "Content-Type: application/json" `
  -d "{\"name\":\"Invalid Product\",\"code\":\"XX001\",\"categoryCode\":\"XX\",\"brand\":\"Migros\",\"unit\":\"PIECE\"}"
```

Beklenen sonuc: Product olusmaz, hata doner.

Bu test Product Service'in Category Service'e gidip kategori kontrolu yaptigini gosterir.

## 9. Barcode Olustur

Once PRODUCT barcode olustur:

```powershell
curl -X POST http://localhost:8083/api/v1/barcodes `
  -H "Content-Type: application/json" `
  -d "{\"productId\":1,\"type\":\"PRODUCT\"}"
```

Beklenen sonuc:

```json
{
  "id": 1,
  "code": "PRD-FR001",
  "type": "PRODUCT",
  "productId": 1
}
```

Ayni product icin CASE barcode olustur:

```powershell
curl -X POST http://localhost:8083/api/v1/barcodes `
  -H "Content-Type: application/json" `
  -d "{\"productId\":1,\"type\":\"CASE\"}"
```

Beklenen sonuc:

```json
{
  "code": "CASE-FR001",
  "type": "CASE",
  "productId": 1
}
```

Product'a ait barcode'lari listele:

```powershell
curl http://localhost:8083/api/v1/barcodes/product/1
```

## 10. Barcode Business Rule Testi

Fruit + KILOGRAM icin SCALE barcode izinli degil. Bunu test et:

```powershell
curl -X POST http://localhost:8083/api/v1/barcodes `
  -H "Content-Type: application/json" `
  -d "{\"productId\":1,\"type\":\"SCALE\"}"
```

Beklenen sonuc: Barcode olusmaz, hata doner.

Bu test Barcode Service'in Product Service'ten product bilgisini alip is kurallarini uyguladigini gosterir.

## 11. Duplicate Barcode Testi

Ayni product icin ikinci kez PRODUCT barcode olusturmayi dene:

```powershell
curl -X POST http://localhost:8083/api/v1/barcodes `
  -H "Content-Type: application/json" `
  -d "{\"productId\":1,\"type\":\"PRODUCT\"}"
```

Beklenen sonuc:

```text
This product already has barcode type: PRODUCT
```

## 12. Sunumda Soylenebilecek Kisa Ozet

```text
Product Service Docker'da kapanmiyordu cunku Resilience4j dependency'leri farkli versiyonlardan geliyordu.
resilience4j-spring-boot3 2.3.0 kullanilirken alt moduller 2.1.0 geliyordu.
Resilience4j BOM eklenerek butun Resilience4j modulleri 2.3.0'a sabitlendi.
application-prod.yml dosyasindaki datasource ve JPA ayarlari da duzeltildi.
Su anda Product Service Docker'da ayakta kaliyor ve Category Service ile Feign uzerinden konusabiliyor.
```

## 13. Temiz Kapatma

Sunum bittikten sonra container'lari kapatmak icin:

```powershell
docker compose down
```

Database volume'unu da silmek istersen:

```powershell
docker compose down -v
```

