package com.inventory.product.feign;

import com.inventory.product.dto.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "category-service",
        url = "${services.category.url}"
)
public interface CategoryServiceClient {

    @GetMapping("/api/v1/categories/{code}")
    CategoryResponse getCategoryByCode(@PathVariable("code") String code);
}