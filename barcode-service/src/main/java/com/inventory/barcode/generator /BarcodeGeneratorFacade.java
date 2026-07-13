package com.inventory.barcode.generator;

import com.inventory.barcode.enums.BarcodeType;
import com.inventory.barcode.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BarcodeGeneratorFacade {

    private final ProductBarcodeGenerator productBarcodeGenerator;
    private final CaseBarcodeGenerator caseBarcodeGenerator;
    private final ScaleBarcodeGenerator scaleBarcodeGenerator;

    public String generate(BarcodeType type, String productCode, long sequence) {
        return switch (type) {
            case PRODUCT -> productBarcodeGenerator.generate();
            case CASE -> caseBarcodeGenerator.generate();
            case SCALE -> scaleBarcodeGenerator.generate(productCode, sequence);
            default -> throw new BusinessException("Unsupported barcode type");
        };
    }
}