package com.inventory.product.validation;

import com.inventory.product.dto.ProductCreateRequest;
import com.inventory.product.dto.ProductUpdateRequest;
import com.inventory.product.exception.BusinessException;
import com.inventory.product.exception.ServiceUnavailableException;
import com.inventory.product.gateway.CategoryGateway;
import com.inventory.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;
    private final CategoryGateway categoryGateway;

    public void validateForCreate(ProductCreateRequest request) {

        String categoryCode = validateCategoryAndGetCode(
                request.categoryCode()
        );

        validateProductCodeMatchesCategory(
                request.code(),
                categoryCode
        );

        if (productRepository.existsByName(request.name())) {
            throw new BusinessException(
                    "Product with name '" + request.name() + "' already exists"
            );
        }

        if (productRepository.existsByCode(request.code())) {
            throw new BusinessException(
                    "Product with code '" + request.code() + "' already exists"
            );
        }
    }

    public void validateForUpdate(
            Long id,
            String currentName,
            String currentCode,
            ProductUpdateRequest request
    ) {

        String categoryCode = validateCategoryAndGetCode(
                request.categoryCode()
        );

        validateProductCodeMatchesCategory(
                request.code(),
                categoryCode
        );

        if (!currentName.equals(request.name())
                && productRepository.existsByName(request.name())) {

            throw new BusinessException(
                    "Product with name '" + request.name() + "' already exists"
            );
        }

        if (!currentCode.equals(request.code())
                && productRepository.existsByCode(request.code())) {

            throw new BusinessException(
                    "Product with code '" + request.code() + "' already exists"
            );
        }
    }

    /**
     * Category Service üzerinden kategoriyi kontrol eder.
     *
     * Kategori bulunursa ve aktifse Category Service tarafından dönen
     * gerçek kategori kodunu döndürür.
     */
    private String validateCategoryAndGetCode(String categoryCode) {

        if (categoryCode == null || categoryCode.isBlank()) {
            throw new BusinessException(
                    "Category code cannot be empty"
            );
        }

        try {
            var category = categoryGateway.getCategory(categoryCode);

            if (category == null) {
                throw new BusinessException(
                        "Category '" + categoryCode + "' not found"
                );
            }

            if (!category.active()) {
                throw new BusinessException(
                        "Category '" + categoryCode + "' is not active"
                );
            }

            return category.code();

        } catch (BusinessException | ServiceUnavailableException e) {
            throw e;

        } catch (Exception e) {
            throw new BusinessException(
                    "Category '" + categoryCode
                            + "' not found or not accessible"
            );
        }
    }


    private void validateProductCodeMatchesCategory(
            String productCode,
            String categoryCode
    ) {

        if (productCode == null || productCode.isBlank()) {
            throw new BusinessException(
                    "Product code cannot be empty"
            );
        }

        if (productCode.length() < 2) {
            throw new BusinessException(
                    "Product code must contain at least 2 characters"
            );
        }

        if (categoryCode == null || categoryCode.isBlank()) {
            throw new BusinessException(
                    "Category code cannot be empty"
            );
        }

        String productCategoryPrefix = productCode.substring(0, 2);

        if (!productCategoryPrefix.equalsIgnoreCase(categoryCode)) {
            throw new BusinessException(
                    "Product code must start with category code '"
                            + categoryCode + "'"
            );
        }
    }
}