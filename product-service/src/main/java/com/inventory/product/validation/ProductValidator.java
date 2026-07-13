package com.inventory.product.validation;

import com.inventory.product.dto.ProductCreateRequest;
import com.inventory.product.dto.ProductUpdateRequest;
import com.inventory.product.exception.BusinessException;
import com.inventory.product.feign.CategoryServiceClient;
import com.inventory.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;
    private final CategoryServiceClient categoryServiceClient;

    public void validateForCreate(ProductCreateRequest request) {
        validateCategory(request.categoryCode());
        
        if (productRepository.existsByName(request.name())) {
            throw new BusinessException("Product with name '" + request.name() + "' already exists");
        }
        if (productRepository.existsByCode(request.code())) {
            throw new BusinessException("Product with code '" + request.code() + "' already exists");
        }
    }

    public void validateForUpdate(Long id, String currentName, String currentCode, ProductUpdateRequest request) {
        validateCategory(request.categoryCode());
        
        if (!currentName.equals(request.name()) && productRepository.existsByName(request.name())) {
            throw new BusinessException("Product with name '" + request.name() + "' already exists");
        }
        if (!currentCode.equals(request.code()) && productRepository.existsByCode(request.code())) {
            throw new BusinessException("Product with code '" + request.code() + "' already exists");
        }
    }

    private void validateCategory(String categoryCode) {
        try {
            var category = categoryServiceClient.getCategoryByCode(categoryCode);
            if (!category.active()) {
                throw new BusinessException("Category '" + categoryCode + "' is not active");
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("Category '" + categoryCode + "' not found or not accessible");
        }
    }
}

