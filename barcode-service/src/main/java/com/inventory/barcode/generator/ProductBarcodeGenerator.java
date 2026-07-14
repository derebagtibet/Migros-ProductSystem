package com.inventory.barcode.generator;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class ProductBarcodeGenerator {

    public String generate() {
        long number = ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);
        return String.valueOf(number);
    }
}
