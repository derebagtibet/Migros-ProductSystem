package com.inventory.barcode.client;

import com.inventory.barcode.dto.ProductResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(

        name = "product-service",

        url = "${services.product.url}"

)

public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")

    ProductResponse getProductById(@PathVariable Long id);

}
