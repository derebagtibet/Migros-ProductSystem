package com.inventory.barcode.validation;

import com.inventory.barcode.dto.ProductResponse;
import com.inventory.barcode.enums.BarcodeType;
import com.inventory.barcode.exception.BusinessException;
import com.inventory.barcode.repository.BarcodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BarcodeRuleValidator {

    private final BarcodeRepository barcodeRepository;

    public void validate(ProductResponse product, BarcodeType requestedType) {
        validateDuplicateType(product.id(), requestedType);
        validateBusinessRule(product, requestedType);
    }

    private void validateDuplicateType(Long productId, BarcodeType type) {
        if (barcodeRepository.existsByProductIdAndType(productId, type)) {
            throw new BusinessException("This product already has barcode type: " + type);
        }
    }

    private void validateBusinessRule(ProductResponse product, BarcodeType requestedType) {
        String category = product.categoryCode();
        String unit = product.unit();

        if ("ME".equalsIgnoreCase(category) && "KILOGRAM".equalsIgnoreCase(unit)) {
            allowOnly(requestedType, BarcodeType.PRODUCT, BarcodeType.CASE);
            return;
        }

        if ("BA".equalsIgnoreCase(category) && "KILOGRAM".equalsIgnoreCase(unit)) {
            allowOnly(requestedType, BarcodeType.PRODUCT, BarcodeType.SCALE);
            return;
        }

        if ("BA".equalsIgnoreCase(category) && "PIECE".equalsIgnoreCase(unit)) {
            allowOnly(requestedType, BarcodeType.CASE);
            return;
        }

        if ("ET".equalsIgnoreCase(category)) {
            allowOnly(requestedType, BarcodeType.SCALE);
            return;
        }

        allowOnly(requestedType, BarcodeType.PRODUCT);
    }

    private void allowOnly(BarcodeType requestedType, BarcodeType... allowedTypes) {
        for (BarcodeType allowedType : allowedTypes) {
            if (requestedType == allowedType) {
                return;
            }
        }

        throw new BusinessException("Barcode type " + requestedType + " is not allowed for this product");
    }
}