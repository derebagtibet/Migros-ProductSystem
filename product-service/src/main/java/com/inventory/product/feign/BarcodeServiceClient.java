package com.inventory.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "barcode-service",
        url = "${services.barcode.url}"
)
public interface BarcodeServiceClient {

    @DeleteMapping("/api/v1/barcodes/product/{productId}")
    void deleteBarcodesByProductId(@PathVariable("productId") Long productId);
}
