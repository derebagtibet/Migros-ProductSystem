package com.inventory.barcode.generator;

import org.springframework.stereotype.Component;

@Component

public class ScaleBarcodeGenerator {

    public String generate(String productCode, long sequence) {

        String sequencePart = String.format("%03d", sequence);

        return productCode + sequencePart;

    }

}
