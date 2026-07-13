package com.inventory.product.gateway;

import com.inventory.product.dto.CategoryResponse;
import com.inventory.product.exception.ServiceUnavailableException;
import com.inventory.product.feign.CategoryServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryGateway {

    private final CategoryServiceClient categoryServiceClient;

    @Retry(name = "categoryService")
    @CircuitBreaker(name = "categoryService", fallbackMethod = "categoryFallback")
    public CategoryResponse getCategory(String code) {
        return categoryServiceClient.getCategoryByCode(code);
    }

    private CategoryResponse categoryFallback(String code, Throwable throwable) {
        log.warn("Category Service unavailable for categoryCode={}", code, throwable);
        throw new ServiceUnavailableException("Category Service is temporarily unavailable");
    }
}
